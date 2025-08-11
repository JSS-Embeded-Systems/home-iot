package iot.microservice.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class SleepData {
  private int inBed;
  private int sleepState;
  private int averageRespiration;
  private int averageHeartbeat;
  private int turnoverNumber;
  private int largeBodyMove;
  private int minorBodyMove;
  private int apneaEvents;
  private int sleepDuration;
  private int sleepQualityScore;

  private ZonedDateTime timestamp;
}
