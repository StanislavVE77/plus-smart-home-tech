package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.configuration.EventClient;
import ru.yandex.practicum.telemetry.aggregator.configuration.EventTopics;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private final EventClient client;
    private final InMemorySensorEvents service;

    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    public void start() {

        try {
            client.getSensorConsumer().subscribe(List.of(EventTopics.TELEMETRY_SENSOR_TOPIC));

            int count = 0;

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = client.getSensorConsumer().poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    log.info("Обновление снапшота");
                    Optional<SensorsSnapshotAvro> snapshotAvro = service.updateState((SensorEventAvro) record.value());
                    if (snapshotAvro.isEmpty()) {
                        log.info("Обновление снапшота не произошло.");
                    } else {
                        log.info("Новый снапшот: {}", snapshotAvro.get());
                        ProducerRecord<String, SpecificRecordBase> snapshot = new ProducerRecord<>(EventTopics.TELEMETRY_SNAPSHOT_TOPIC, snapshotAvro.get());
                        client.getProducer().send(snapshot);
                        log.info("Снапшот отправлен в топик {}", EventTopics.TELEMETRY_SNAPSHOT_TOPIC);
                    }
                    manageOffsets(record, count, client.getSensorConsumer());
                    count++;
                }

                client.getSensorConsumer().commitAsync();
            }

        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                client.getProducer().flush();
                client.getSensorConsumer().commitSync(currentOffsets);
            } finally {
                client.stop();
            }
        }
    }

    private static void manageOffsets(ConsumerRecord<String, SpecificRecordBase> record, int count, Consumer<String, SpecificRecordBase> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if(count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if(exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }

}