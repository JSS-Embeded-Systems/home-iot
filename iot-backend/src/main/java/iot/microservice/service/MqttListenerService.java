package iot.microservice.service;

import iot.microservice.entity.MqttDataEntity;
import iot.microservice.repository.MqttDataRepository;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

@Service
public class MqttListenerService {
  private final IMqttClient client;
  private final MqttDataRepository repository;

  public MqttListenerService(MqttDataRepository repo) throws Exception {
    String clientId = MqttClient.generateClientId();
    this.client = new MqttClient("tcp://localhost:1883", clientId);
    this.repository = repo;
  }

  @PostConstruct
  public void start() throws Exception {
    MqttConnectOptions options = new MqttConnectOptions();
    options.setAutomaticReconnect(true);
    options.setCleanSession(true);
    client.connect(options);

    // Subscribes to all data
    client.subscribe("sensors/#");
  }

  private void handleMessage(String topic, MqttMessage message) {
    String payload = new String(message.getPayload());

    MqttDataEntity data = new MqttDataEntity();
    data.setTopic(topic);
    data.setPayload(payload);
    data.setTimestamp(new java.util.Date());
    repository.save(data);
  }
}
