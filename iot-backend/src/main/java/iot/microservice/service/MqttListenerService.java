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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MqttListenerService {
  private IMqttClient iMqttClient;
  private final MqttDataRepository repository;

  @Value("${mqtt.broker}")
  private String brokerUrl;

  @PostConstruct
  public void init() {
    try {
      String clientId = UUID.randomUUID().toString();
      iMqttClient = new MqttClient(brokerUrl, clientId);

      MqttConnectOptions options = new MqttConnectOptions();
      options.setAutomaticReconnect(true);
      options.setCleanSession(true);
      options.setConnectionTimeout(60);
      options.setKeepAliveInterval(30);

      iMqttClient.connect(options);
      System.out.println("✅ MQTT client connected.");

      iMqttClient.subscribe("sensor/sleep", this::handleMessage);
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }

  private void handleMessage(String topic, MqttMessage message) throws MqttException {
    String payload = new String(message.getPayload());

    MqttDataEntity data = new MqttDataEntity();
    data.setTopic(topic);
    data.setPayload(payload);
    data.setTimestamp(new java.util.Date());
    repository.save(data);
  }
}
