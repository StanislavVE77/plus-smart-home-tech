package ru.yandex.practicum.telemetry.collector.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.collector.configuration.EventClient;
import ru.yandex.practicum.telemetry.collector.configuration.EventTopics;

@Slf4j
@Service
@AllArgsConstructor
public class KafkaEventProduser {
    private final EventClient client;

    public void sendHubEventToKafka(SpecificRecordBase message) {
        String topic = EventTopics.TELEMETRY_HUB_TOPIC;
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, message);
        log.info("--> Отправка в Kafka сообщения (HubEventAvro): {}", record);
        client.getProducer().send(record);
    }

    public void sendSensorEventToKafka(SpecificRecordBase message) {
        String topic = EventTopics.TELEMETRY_SENSOR_TOPIC;
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, message);
        log.info("--> Отправка в Kafka сообщения (SensorEventAvro: {}", record);
        client.getProducer().send(record);
    }

}

