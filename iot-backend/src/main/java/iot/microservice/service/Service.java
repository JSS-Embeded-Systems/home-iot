package iot.microservice.service;

import iot.microservice.structure.RGBRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

public class Service {
  @Value("${internal.api}")
  private String led;

  @Value("${internal.api}")
  private String lamp;

  private final RestTemplate restTemplate;

  public Service(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public String ledStringService(RGBRequest rgbRequest) {
    String url = led + "/set";
    return restTemplate.postForObject(url, rgbRequest, String.class);
  }
  public String shellyRelaySwitch(String status) {
    String url = lamp + "/relay/" + status;
    return restTemplate.postForObject(url, "", String.class);
  }

  public void mqttParser() {

  }
}
