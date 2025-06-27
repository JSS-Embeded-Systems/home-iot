package iot.microservice.structure;

import lombok.*;

@Getter @Setter @AllArgsConstructor
public class RGBRequest extends Request {
  private int red;
  private int green;
  private int blue;
}