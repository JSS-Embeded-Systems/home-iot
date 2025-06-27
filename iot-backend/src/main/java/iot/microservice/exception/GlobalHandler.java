package iot.microservice.exception;

import iot.microservice.structure.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalHandler {
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Response> handleException(Exception e) {
    Response res = new Response<>();
    res.setMessage(e.getMessage());
    // TODO: I dont like internal... has to be dynamic
    return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
