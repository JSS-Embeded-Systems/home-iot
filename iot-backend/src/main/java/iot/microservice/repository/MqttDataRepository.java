package iot.microservice.repository;

import iot.microservice.entity.MqttDataEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MqttDataRepository extends MongoRepository<MqttDataEntity, String> {
  public List<MqttDataEntity> findByTopic(String topic);
}
