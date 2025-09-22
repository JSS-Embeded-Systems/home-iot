package iot.microservice.service;

import iot.microservice.entity.MqttDataEntity;
import iot.microservice.repository.MqttDataRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MqttListenerService {
  private IMqttClient iMqttClient;
  private final MqttDataRepository repository;

  @Value("${mqtt.broker}")
  private String brokerUrl;

  @Value("${mqtt.username}")
  private String username;

  @Value("${mqtt.password}")
  private String password;

  @Value("${mqtt.topic}")
  private String topic;

  @PostConstruct
  public void init() {
    // Alle 30 Sekunden prüfen, ob noch verbunden
    new Thread(() -> {
      while (true) {
        try {
          if (iMqttClient == null || !iMqttClient.isConnected()) {
            System.out.println("[MQTT] Lost connection. Reconnecting...");

            String clientId = UUID.randomUUID().toString();
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

            iMqttClient.connect(options);
            System.out.println("[MQTT] Connected to " + brokerUrl + " as " + clientId + ", subscribing to '" + topic + "'.");
            iMqttClient.subscribe(topic, this::handleMessage);

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

  private void handleMessage(String topic, MqttMessage message) throws MqttException {
    String payload = new String(message.getPayload());
    ZonedDateTime timestamp = ZonedDateTime.now();
    MqttDataEntity data = new MqttDataEntity();
    data.setTopic(topic);
    data.setPayload(payload);
    data.setTimestamp(timestamp);
    repository.save(data);
  }
}
