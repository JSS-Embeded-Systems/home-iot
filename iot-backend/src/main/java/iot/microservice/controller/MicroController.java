package iot.microservice.controller;

/**
 * API V1
 */

import iot.microservice.service.MicroService;
import iot.microservice.structure.RGBRequest;
import iot.microservice.structure.Response;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/rest")
@RequiredArgsConstructor
public class MicroController {

  private final MicroService service;

  @PostConstruct
  public void init() {
    System.out.println("✅ MicroserviceRestController wurde geladen");
  }

  /**
   *
   * @param rgbRequest  The LED values
   * @return            The response status message
   */
  @PostMapping("/bedroom/led")
  public ResponseEntity<Response> ledStripe(@RequestBody RGBRequest rgbRequest) {
    Response res = new Response();
    res.setMessage(service.ledStringService(rgbRequest));
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @PostMapping("/bedroom/lamp/{val}")
  public ResponseEntity<Response> lamp(@PathVariable String val) {
    Response res = new Response();
    res.setMessage(service.shellyRelaySwitch(val));
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @GetMapping("/bedroom/sleep/{day}")
  public ResponseEntity<Response> sleep(@PathVariable("day") int timeframe) {
    Response res = new Response();
    res.setMessage(service.retrieveSleep("sensor/sleep",timeframe));
    return new ResponseEntity<>(res, HttpStatus.OK);
  }
}