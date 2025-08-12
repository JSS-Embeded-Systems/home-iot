package iot.microservice.repository;

import iot.microservice.entity.MqttDataEntity;
import iot.microservice.entity.SleepData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface MqttDataRepository extends MongoRepository<MqttDataEntity, String> {
  List<MqttDataEntity> findByTopicAndTimestampAfter(String topic, ZonedDateTime after);
  List<SleepData> findByTopicAndTimestampBetween(String topic, ZonedDateTime start, ZonedDateTime end);
}
