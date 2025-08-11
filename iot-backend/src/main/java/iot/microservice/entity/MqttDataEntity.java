package iot.microservice.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;
import java.time.ZonedDateTime;

@Getter
@Setter
@Document(collection = "IoT")
public class MqttDataEntity {
  @Id
  private String id;
  private String topic;
  private String Payload;
  private ZonedDateTime timestamp;

  // TODO: Implement some kind of getter system only for the Payload and the Timestamp
  public SleepData getParsedPayload() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(Payload, SleepData.class);
    } catch (IOException e) {
      throw new RuntimeException("MqttDataEntity, line 26 ff.", e);
    }
  }
}
