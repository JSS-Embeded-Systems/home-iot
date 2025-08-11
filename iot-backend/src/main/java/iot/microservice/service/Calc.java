package iot.microservice.service;

import iot.microservice.entity.MedianStandardDeviation;
import iot.microservice.entity.SleepData;
import iot.microservice.repository.MqttDataRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

@Service
public class Calc {
  private MqttDataRepository mqttDataRepository;
  public MedianStandardDeviation anyMedianStandardDeviation(String topic, ToIntFunction<SleepData> extractor, ZonedDateTime start, ZonedDateTime end) {
    List<SleepData> sessionData = mqttDataRepository.findByTopicAndTimestampBetween(topic, start, end);
    if (sessionData.isEmpty()) {
      return MedianStandardDeviation.builder()
              .median(-1)
              .SD(-1)
              .build();
    }

    List<Integer> qualityScores = sessionData.stream()
            .mapToInt(extractor)
            .sorted()
            .boxed()
            .collect(Collectors.toList());

    double median = this.medianCalculator(qualityScores);
    return MedianStandardDeviation.builder()
            .median(median)
            .SD(this.standardDeviationCalculator(qualityScores, median))
            .build();
  }


  public double medianCalculator(List<Integer> data) {
    double median;
    int size =  data.size();
    if (size == 1) {
      median = data.getFirst();
    } else if (size == 0) {
      median = -1;
    } else if (size%2 == 1) {
      median = data.get(size/2);
    }  else {
      median = data.get(size/2-1);
    }
    return median;
  }

  public double standardDeviationCalculator(List<Integer> data, double median) {
    double sd;
    sd = median - data.getFirst();
    return sd;
  }

  public Duration estimateDuration(String topic, ZonedDateTime start,  ZonedDateTime end) {
    List<SleepData> sessionData = mqttDataRepository.findByTopicAndTimestampBetween(topic, start, end);
    if (sessionData.isEmpty()) {
      return Duration.ZERO;
    }
    sessionData.sort(Comparator.comparing(SleepData::getTimestamp));
    Duration totalSleep = Duration.ZERO;
    // TODO...
  }
}
