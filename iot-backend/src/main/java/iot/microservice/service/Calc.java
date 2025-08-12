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

  public String estimateDuration(String topic, ZonedDateTime start,  ZonedDateTime end) {
    List<SleepData> sessionData = mqttDataRepository.findByTopicAndTimestampBetween(topic, start, end);
    if (sessionData.isEmpty()) {
      return Duration.ZERO.toString();
    }
    sessionData.sort(Comparator.comparing(SleepData::getTimestamp));
    final Duration maxGap = Duration.ofMinutes(30);
    Duration totalSleep = Duration.ZERO;

    ZonedDateTime segmentStart = null;   // start of a contiguous in-bed segment
    ZonedDateTime prevTs = null;         // previous sample timestamp

    for (SleepData s : sessionData) {
      final ZonedDateTime ts = s.getTimestamp();
      final int inBed = s.getInBed();

      // If there is a large gap, close any open segment at the previous timestamp
      if (prevTs != null && ts.isAfter(prevTs.plus(maxGap)) && segmentStart != null) {
          totalSleep = totalSleep.plus(Duration.between(segmentStart, prevTs));
          segmentStart = null;
        }


      if (inBed == 1) {
        // Open a segment if not already open
        if (segmentStart == null) {
          segmentStart = ts;
        }
      } else { // inBed == 0
        // Close an active segment at the last known timestamp (prevTs) to avoid over-counting
        if (segmentStart != null) {
          ZonedDateTime endTs = (prevTs != null && !prevTs.isBefore(segmentStart)) ? prevTs : ts;
          totalSleep = totalSleep.plus(Duration.between(segmentStart, endTs));
          segmentStart = null;
        }
      }

      prevTs = ts;
    }

    // If the last samples are still in-bed, close at the last timestamp we saw
    if (segmentStart != null && prevTs != null) {
      totalSleep = totalSleep.plus(Duration.between(segmentStart, prevTs));
    }

    return totalSleep.isNegative() ? Duration.ZERO.toString() : totalSleep.toString();
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
  public String timeSeries(String topic, ZonedDateTime start, ZonedDateTime end) {
    List<SleepData> sessionData = mqttDataRepository.findByTopicAndTimestampBetween(topic, start, end);
    if (sessionData == null || sessionData.isEmpty()) {
      return "[]";
    }

    sessionData.sort(Comparator.comparing(SleepData::getTimestamp));

    List<java.util.Map<String, Object>> payload = new java.util.ArrayList<>(sessionData.size());

    for (SleepData s : sessionData) {
      java.util.Map<String, Object> event = new java.util.LinkedHashMap<>();
      event.put("turnoverNumber", s.getTurnoverNumber());
      event.put("largeBodyMove", s.getLargeBodyMove());
      event.put("minorBodyMove", s.getMinorBodyMove());
      event.put("apneaEvents", s.getApneaEvents());

      java.util.Map<String, Object> point = new java.util.LinkedHashMap<>();
      point.put("event", event);
      point.put("timestamp", s.getTimestamp());

      payload.add(point);
    }

    // Serialize using Jackson (ISO-8601 for ZonedDateTime)
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    try {
      return mapper.writeValueAsString(payload);
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      return "[]";
    }
  }

  /*
   * return JSON with
   * - New sleepState
   * - Time stamp
   */
  public String sleepStateSeries(String topic, ZonedDateTime start, ZonedDateTime end) {
    List<SleepData> sessionData = mqttDataRepository.findByTopicAndTimestampBetween(topic, start, end);
    if (sessionData == null || sessionData.isEmpty()) {
      return "[]";
    }

    // Sort chronologically
    sessionData.sort(Comparator.comparing(SleepData::getTimestamp));

    List<java.util.Map<String, Object>> changes = new java.util.ArrayList<>();

    Integer prev = null;
    for (SleepData s : sessionData) {
      Integer curr = s.getSleepState();
      if (curr == null) {
        continue; // skip malformed samples
      }
      if (prev == null) {
        prev = curr; // initialize baseline without emitting an event
        continue;
      }
      if (!curr.equals(prev)) {
        java.util.Map<String, Object> change = new java.util.LinkedHashMap<>();
        change.put("NewState", curr);
        change.put("TimeStamp", s.getTimestamp());
        changes.add(change);
        prev = curr;
      }
    }

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    try {
      return mapper.writeValueAsString(changes);
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      return "[]";
    }
  }

  public ZonedDateTime getTimeDelta() {
    return ZonedDateTime.now().minusDays(1).withHour(16).withMinute(0).withSecond(0).withNano(0);
  }
}
