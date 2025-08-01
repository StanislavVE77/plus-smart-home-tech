package ru.yandex.practicum.telemetry.collector.configuration;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;

public interface EventClient {

    Producer<String, SpecificRecordBase> getProducer();

    void stop();

}
