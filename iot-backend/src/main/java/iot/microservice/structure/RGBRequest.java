package iot.microservice.structure;

import lombok.*;

@Getter @Setter @AllArgsConstructor
public class RGBRequest extends Request {
  private int r;
  private int g;
  private int b;
}