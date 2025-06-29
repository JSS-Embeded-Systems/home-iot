package iot.microservice.service;

import iot.microservice.entity.MqttDataEntity;
import iot.microservice.repository.MqttDataRepository;
import iot.microservice.structure.RGBRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MicroService {
  /*@Value("${internal.api}")
  private final String led;

  @Value("${internal.api}")
  private final String lamp;*/

  //private final RestTemplate restTemplate;
  private final MqttDataRepository repository;

  public String ledStringService(RGBRequest rgbRequest) {
    return "Success";
  }
  public String shellyRelaySwitch(String status) {
    return "Success";
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
