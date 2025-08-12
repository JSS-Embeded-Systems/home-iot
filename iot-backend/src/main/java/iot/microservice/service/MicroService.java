package iot.microservice.service;

import iot.microservice.entity.MedianStandardDeviation;
import iot.microservice.entity.MqttDataEntity;
import iot.microservice.entity.SleepData;
import iot.microservice.repository.MqttDataRepository;
import iot.microservice.structure.RGBRequest;
import lombok.RequiredArgsConstructor;
import iot.microservice.component.AppProps;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.ToIntFunction;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MicroService {

  private final MqttDataRepository repository;
  private final AppProps appProps;
  private final Calc calc;

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

  public String shellyRelayStatus() {
    WebClient webClient = WebClient.create("http://"+appProps.getLampIp());
    return webClient.get()
            .uri("/relay/0")
            .retrieve()
            .bodyToMono(String.class)
            .block();
  }

  // Function here to retrieve data from Mongo DB
  // Format: topic, payload, timestamp
  public List<MqttDataEntity> retrieveSleep(String topic, int timeframe) {
    ZonedDateTime now = ZonedDateTime.now();
    now = now.minusHours(timeframe);
    return repository.findByTopicAndTimestampAfter(topic, now);
  }

  /*
  * return JSON with
  * - Sleep Quality Score
  */
  public MedianStandardDeviation retrieveSleepQualityScore(String topic) {
    ToIntFunction<SleepData> extractor = SleepData::getSleepQualityScore;
    return calc.anyMedianStandardDeviation(topic, extractor, ZonedDateTime.now(), calc.getTimeDelta());
  }

  /*
   * return JSON with
   * - Total Duration
   */
  public String retrieveSleepTotalDuration(String topic) {
    return calc.estimateDuration(topic, ZonedDateTime.now(), calc.getTimeDelta());
  }

  /*
   * return JSON with
   * - Event (turnoverNumber,
   *          largeBodyMove,
   *          minorBodyMove,
   *          apneaEvents,
   *          )
   * - Time stamp
   */
  public String retrieveSleepTimeSeries(String topic) {
    return calc.timeSeries(topic, ZonedDateTime.now(), calc.getTimeDelta());
  }

  /*
  * returns JSON with
  * - Median Heart Beats Per Minute
  * - Standard Deviation
  */
  public MedianStandardDeviation retrieveSleepMedianBpm(String topic) {
    ToIntFunction<SleepData> extractor = SleepData::getAverageHeartbeat;
    return calc.anyMedianStandardDeviation(topic, extractor, ZonedDateTime.now(), calc.getTimeDelta());
  }

  /*
  * returns JSON with
  * - Median Respiration per Minute
  * - Standard deviation
  */
  public MedianStandardDeviation retrieveSleepMedianRPM(String topic) {
    ToIntFunction<SleepData> extractor = SleepData::getAverageRespiration;
    return calc.anyMedianStandardDeviation(topic, extractor, ZonedDateTime.now(), calc.getTimeDelta());
  }

  /*
   * return JSON with
   * - New sleepState
   * - Time stamp
   */
  public String retrieveSleepStateSeries(String topic) {
    return calc.sleepStateSeries(topic, ZonedDateTime.now(), calc.getTimeDelta());
  }
}
