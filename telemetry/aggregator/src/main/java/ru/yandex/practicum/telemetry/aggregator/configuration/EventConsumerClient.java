package ru.yandex.practicum.telemetry.aggregator.configuration;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;

public interface EventConsumerClient {

    Consumer<String, SpecificRecordBase> getSensorConsumer();

    void stop();

}
