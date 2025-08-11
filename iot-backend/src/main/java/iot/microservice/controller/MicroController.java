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

  /**
   *
   * @param rgbRequest  The LED values
   * @return            The response status message
   */
  @PostMapping("/bedroom/led")
  public ResponseEntity<Response> ledStripe(@RequestBody RGBRequest rgbRequest) {
    Response res = new Response();
    res.setMessage(service.ledStringService(rgbRequest));
    System.out.println("Calling internal device with output: "+res.getMessage());
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
    res.setMessage(service.retrieveSleep("sensors/sleep",timeframe));
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @GetMapping("/bedroom/sleep/quality-score")
  public ResponseEntity<Response> sleepQualityScore() {
    Response res = new Response();
    res.setMessage(service.retrieveSleepQualityScore("sensors/sleep"));
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @GetMapping("/bedroom/sleep/total-duration")
  public ResponseEntity<Response> sleepTotalDuration() {
    Response res = new Response();
    res.setMessage(service.retrieveSleepTotalDuration());
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @GetMapping("/bedroom/sleep/time-series")
  public ResponseEntity<Response> sleepTimeSeries() {
    Response res = new Response();
    res.setMessage(service.retrieveSleepTimeSeries());
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @GetMapping("/bedroom/sleep/median-bpm")
  public ResponseEntity<Response> sleepMedianBpm() {
    Response res = new Response();
    res.setMessage(service.retrieveSleepMedianBpm("sensors/sleep"));
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @GetMapping("/bedroom/sleep/median-rpm")
  public ResponseEntity<Response> sleepMedianRpm() {
    Response res = new Response();
    res.setMessage(service.retrieveSleepMedianRPM("Sensors/sleep"));
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @GetMapping("/bedroom/sleep/states-series")
  public ResponseEntity<Response> sleepStatesSeries() {
    Response res = new Response();
    res.setMessage(service.retrieveSleepStateSeries());
    return new ResponseEntity<>(res, HttpStatus.OK);
  }
}