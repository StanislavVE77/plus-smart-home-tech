package ru.yandex.practicum.telemetry.aggregator.configuration;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class EventConfiguration {

    @Bean
    EventClient getClient() {
        return new EventClient() {

            private final AtomicInteger counter = new AtomicInteger(0);

            private Producer<String, SpecificRecordBase> producer;

            private Consumer<String, SpecificRecordBase> sensorConsumer;

            @Override
            public Consumer<String, SpecificRecordBase> getSensorConsumer() {
                if (sensorConsumer == null) {
                    initSensorConsumer();
                }
                return sensorConsumer;
            }

            private void initSensorConsumer() {

                Properties config = new Properties();
                config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
                config.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
                config.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "ru.yandex.practicum.telemetry.aggregator.configuration.SensorEventDeserializer");
                config.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "consumer-client-" + counter.getAndIncrement());
                config.setProperty(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "500");

                sensorConsumer = new KafkaConsumer<>(config);
            }

            @Override
            public Producer<String, SpecificRecordBase> getProducer() {
                if (producer == null) {
                    initProducer();
                }
                return producer;
            }

            private void initProducer() {

                Properties config = new Properties();
                config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
                config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
                config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "ru.yandex.practicum.telemetry.aggregator.configuration.EventAvroSerializer");

                producer = new KafkaProducer<>(config);
            }

            @Override
            public void stop() {
                if (sensorConsumer != null) {
                    sensorConsumer.close();
                }
                if (producer != null) {
                    producer.close();
                }
            }
        };
    }

}
