package ru.yandex.practicum.telemetry.aggregator.configuration;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;

public interface EventProducerClient {

    Producer<String, SpecificRecordBase> getProducer();

    void stop();

}
