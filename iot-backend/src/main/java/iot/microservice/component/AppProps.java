package iot.microservice.component;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Getter;

@Getter
@Setter
@Component
public class AppProps {
  @Value("${internal.api.lamp}")
  private String lampIp;

  @Value("${internal.api.led}")
  private String ledIp;
}