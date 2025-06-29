package iot.microservice.repository;

import iot.microservice.entity.MqttDataEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MqttDataRepository extends MongoRepository<MqttDataEntity, String> {
  List<MqttDataEntity> findByTopicAndTimestampAfter(String topic, Date after);
}
