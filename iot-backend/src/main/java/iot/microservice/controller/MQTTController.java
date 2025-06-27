package iot.microservice.controller;


// source: https://github.com/gulteking/spring-boot-mqtt-sample/blob/master/src/main/java/com/gulteking/mqttbackendserver/config/Mqtt.java

import iot.microservice.service.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/v1/mqtt")
public class MQTTController {
  private Service service;

  @GetMapping("subscribe")
  public ResponseEntity<String> subscribeChannel(@RequestParam(value = "topic") String topic,
                                                 @RequestParam(value = "wait_millis") Integer waitMillis)
    throws Exception {
    String res = "We are still working on it";
    return new ResponseEntity<>(res, HttpStatus.FORBIDDEN);
  }
}
