package iot.microservice.service;

import iot.microservice.entity.MqttDataEntity;
import iot.microservice.repository.MqttDataRepository;
import iot.microservice.structure.RGBRequest;
import lombok.RequiredArgsConstructor;
import iot.microservice.component.AppProps;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MicroService {

  private final MqttDataRepository repository;
  private final AppProps appProps;

  public String ledStringService(RGBRequest rgbRequest) {
    WebClient webClient = WebClient.create("http://"+appProps.getLedIp());
    return webClient.post()
            .uri("/set")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(rgbRequest)
            .retrieve()
            .bodyToMono(String.class)
            .block();
  }

  public String shellyRelaySwitch(String status) {
    WebClient webClient = WebClient.create("http://"+appProps.getLampIp());
    return webClient.get()
            .uri("/relay/0?turn="+status)
            .retrieve()
            .bodyToMono(String.class)
            .block();
  }

  // Function here to retrieve data from Mongo DB
  // Format: topic, payload, timestamp
  public List<MqttDataEntity> retrieveSleep(String topic, int timeframe) {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, -timeframe);
    Date startDate = calendar.getTime();
    return repository.findByTopicAndTimestampAfter(topic, startDate);
  }
}
