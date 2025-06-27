package iot.microservice.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Document(collection = "IoT")
public class MqttDataEntity {

  @Id
  private String id;

  private String topic;
  private String Payload;
  private Date timestamp;

}
