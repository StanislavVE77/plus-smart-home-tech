package ru.yandex.practicum.telemetry.aggregator.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@Getter
@AllArgsConstructor
@ConfigurationProperties("aggregator.kafka.consumer")
public class EventConsumerConfiguration {
    private Properties properties;

    @Bean
    EventConsumerClient getConsumerClient() {
        return new EventConsumerClient() {

            private Consumer<String, SpecificRecordBase> sensorConsumer;

            @Override
            public Consumer<String, SpecificRecordBase> getSensorConsumer() {
                if (sensorConsumer == null) {
                    initSensorConsumer();
                }
                return sensorConsumer;
            }

            private void initSensorConsumer() {
                sensorConsumer = new KafkaConsumer<>(properties);
            }

            @Override
            public void stop() {
                if (sensorConsumer != null) {
                    sensorConsumer.close();
                }
            }
        };
    }

}