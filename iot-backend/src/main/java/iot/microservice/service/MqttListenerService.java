package iot.microservice.service;

import iot.microservice.entity.MqttDataEntity;
import iot.microservice.repository.MqttDataRepository;
import iot.microservice.structure.RGBRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class MqttListenerService {
  private IMqttClient iMqttClient;
  private final MqttDataRepository repository;
  private final MicroService microService;

  @Value("${mqtt.broker}")
  private String brokerUrl;

  @Value("${mqtt.username}")
  private String username;

  @Value("${mqtt.password}")
  private String password;

  @Value("${mqtt.topic.sleep}")
  private String topicSleep;

  @Value("${mqtt.topic.presence}")
  private String topicPresence;

  // Simple per-topic occupancy state and optional delayed-off scheduler
  private final Map<String, Boolean> occupied = new ConcurrentHashMap<>();
  private final Map<String, ScheduledFuture<?>> offTasks = new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  // Off delay in milliseconds (grace period). 0 = immediate off on absence
  @Value("${presence.graceMs:0}")
  private long presenceGraceMs;

  /**
   * Optional internal workflow callback. If registered, it will be called on presence events
   * parsed from incoming MQTT payloads.
   */
  private volatile PresenceWorkflow presenceWorkflow;

  /** Simple functional interface for internal workflows triggered by presence events. */
  @FunctionalInterface
  public interface PresenceWorkflow {
    /**
     * @param topic      Full MQTT topic the event was received on
     * @param presence   true if presence detected, false if explicitly absent; null if not parsable
     * @param rawPayload Original message payload
     */
    void onPresenceEvent(String topic, Boolean presence, String rawPayload);
  }

  /** Register (or replace) the internal presence workflow handler. */
  public void registerPresenceWorkflow(PresenceWorkflow workflow) {
    this.presenceWorkflow = workflow;
  }

  // Patterns to robustly detect presence in various formats: JSON and plain text
  private static final Pattern PRESENCE_JSON_TRUE = Pattern.compile("\\\"presence\\\"\\s*:\\s*(true|1)", Pattern.CASE_INSENSITIVE);
  private static final Pattern PRESENCE_TEXT_PAIR = Pattern.compile("presence\\s*[:=]\\s*(1|0|true|false)", Pattern.CASE_INSENSITIVE);

  /**
   * Try to interpret the payload for a presence boolean. Returns TRUE/FALSE if explicit, or null if unknown.
   */
  private static Boolean parsePresenceFromPayload(String payload) {
    if (payload == null) return null;
    String s = payload.trim();
    Matcher m1 = PRESENCE_JSON_TRUE.matcher(s);
    if (m1.find()) return Boolean.TRUE;
    Matcher m3 = PRESENCE_TEXT_PAIR.matcher(s);
    if (m3.find()) {
      String v = m3.group(1).toLowerCase();
      return ("1".equals(v) || "true".equals(v)) ? Boolean.TRUE : Boolean.FALSE;
    }
    return null; // not parsable
  }


  private static String deriveKeyFromTopic(String topic) {
    // If topic contains .../c4001/<room>/presence use '<room>' as key; otherwise use full topic
    try {
      String[] parts = topic.split("/");
      for (int i = 0; i < parts.length - 1; i++) {
        if ("c4001".equalsIgnoreCase(parts[i]) && i + 2 < parts.length && "presence".equalsIgnoreCase(parts[i + 2])) {
          return parts[i + 1];
        }
      }
    } catch (Exception ignored) {}
    return topic; // fallback: per-topic state
  }

  private void cancelOffTask(String key) {
    ScheduledFuture<?> f = offTasks.remove(key);
    if (f != null) f.cancel(false);
  }

  @PostConstruct
  public void init() {
    // Alle 30 Sekunden prüfen, ob noch verbunden
    new Thread(() -> {
      while (true) {
        try {
          if (iMqttClient == null || !iMqttClient.isConnected()) {
            System.out.println("[MQTT] Lost connection. Reconnecting...");

            String clientId = UUID.randomUUID().toString();
            System.out.println("[MQTT] " + username + ", "+password);
            iMqttClient = new MqttClient(brokerUrl, clientId);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(60);
            options.setKeepAliveInterval(30);

            if (username != null && !username.isEmpty()) {
              options.setUserName(username);
            }
            if (password != null) {
              options.setPassword(password.toCharArray());
            }

            System.out.println("[MQTT] "+options.getUserName()+" "+ Arrays.toString(options.getPassword()));

            iMqttClient.connect(options);
            System.out.println("[MQTT] Connected to " + brokerUrl + " as " + clientId + ", subscribing to '" + topicSleep + "'.");
            iMqttClient.subscribe(topicSleep, this::handleMessage);
            iMqttClient.subscribe(topicPresence, this::handleWorkflow);
            System.out.println("[MQTT] Subscribed to topics: '" + topicSleep + "' and '" + topicPresence + "'.");
            System.out.println("[MQTT] Reconnected and subscribed.");
          }

          Thread.sleep(30000); // Wait 30 sec
        } catch (Exception e) {
          if (e instanceof MqttException me) {
            System.err.println("[MQTT] ReasonCode=" + me.getReasonCode() + ", Message=" + me.getMessage());
          }
          e.printStackTrace();
          try {
            Thread.sleep(5000); // Retry after 5 sec
          } catch (InterruptedException ignored) {}
        }
      }
    }).start();
  }

  /**
   * Subscribe to an additional topic filter with a custom listener.
   * Useful to attach domain-specific handlers without changing the default listener.
   *
   * @param topicFilter MQTT topic filter (can include wildcards like "+" / "#")
   * @param listener    callback receiving messages for this subscription
   */
  public void subscribe(String topicFilter, IMqttMessageListener listener) throws MqttException {
    if (iMqttClient == null || !iMqttClient.isConnected()) {
      throw new MqttException(new Throwable("MQTT client not connected"));
    }
    iMqttClient.subscribe(topicFilter, 1, listener);
  }

  private void handleMessage(String topic, MqttMessage message) throws MqttException {
    String payload = new String(message.getPayload());
    ZonedDateTime timestamp = ZonedDateTime.now();
    MqttDataEntity data = new MqttDataEntity();
    data.setTopic(topic);
    data.setPayload(payload);
    data.setTimestamp(timestamp);
    repository.save(data);
  }

  private void handleWorkflow(String topic, MqttMessage message) throws MqttException {
    String payload = new String(message.getPayload());
    System.out.println("[MQTT] presence message on '" + topic + "' -> " + payload);

    Boolean presence = parsePresenceFromPayload(payload);
    if (presence == null) {
      System.out.println("[MQTT] presence not parsable (expected JSON {\"presence\": true|1} or 'presence: 1/true').");
      return;
    }

    // Notify optional workflow listener
    PresenceWorkflow wf = this.presenceWorkflow;
    if (wf != null) {
      try {
        wf.onPresenceEvent(topic, presence, payload);
      } catch (Exception ex) {
        System.err.println("[MQTT] Workflow error: " + ex.getMessage());
      }
    }

    // Per-topic/room state machine: turn on once per activity burst, turn off on absence (with optional grace)
    final String key = deriveKeyFromTopic(topic);
    final boolean wasOccupied = occupied.getOrDefault(key, false);

    if (presence) {
      // Cancel any pending off
      cancelOffTask(key);
      if (!wasOccupied) {
        System.out.println("[MQTT] [" + key + "] transition VACANT -> OCCUPIED (turn ON once)");
        occupied.put(key, true);
        RGBRequest rgbRequest = new RGBRequest();
        rgbRequest.setR(150);
        rgbRequest.setG(165);
        rgbRequest.setB(100);
        try { microService.ledStringService(rgbRequest); } catch (Exception e) { System.err.println("[MQTT] ledStringService error: " + e.getMessage()); }
      } else {
        // Absence: immediate or delayed off
        if (presenceGraceMs <= 0) {
          if (wasOccupied) {
            System.out.println("[MQTT] [" + key + "] OCCUPIED -> VACANT (turn OFF)");
            occupied.put(key, false);
            RGBRequest rgbRequest = new RGBRequest();
            rgbRequest.setR(0); rgbRequest.setG(0); rgbRequest.setB(0);
            try { microService.ledStringService(rgbRequest); } catch (Exception e) { System.err.println("[MQTT] ledStringService error: " + e.getMessage()); }
          } else {
            System.out.println("[MQTT] [" + key + "] already VACANT – no action");
          }
        } else {
          System.out.println("[MQTT] [" + key + "] absence detected – scheduling OFF in " + presenceGraceMs + " ms");
          cancelOffTask(key);
          ScheduledFuture<?> f = scheduler.schedule(() -> {
            try {
              if (occupied.getOrDefault(key, false)) {
                occupied.put(key, false);
                System.out.println("[MQTT] [" + key + "] grace elapsed -> turn OFF");
                RGBRequest rgbRequest = new RGBRequest();
                rgbRequest.setR(0); rgbRequest.setG(0); rgbRequest.setB(0);
                microService.ledStringService(rgbRequest);
              }
            } catch (Exception ex) {
              System.err.println("[MQTT] OFF task error: " + ex.getMessage());
            } finally {
              offTasks.remove(key);
            }
          }, presenceGraceMs, TimeUnit.MILLISECONDS);
          offTasks.put(key, f);
        }
      }
    } else {
      System.out.println("[MQTT] presence = 0 (false). No action.");
    }
  }
}
