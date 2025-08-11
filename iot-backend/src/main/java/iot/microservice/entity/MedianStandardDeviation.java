package iot.microservice.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MedianStandardDeviation {
  private double median;
  private double SD;
}
