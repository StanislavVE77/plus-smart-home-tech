package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.VoidDeserializer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.configuration.SnapshotEventDeserializer;
import ru.yandex.practicum.telemetry.analyzer.dal.service.SnapshotHandleService;

import java.time.Duration;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(100);

    private final SnapshotHandleService snapshotHandleService;

    @Override
    public void run() {
        Properties config = getConsumerProperties();

        String topic = "telemetry.snapshots.v1";

        KafkaConsumer<String, SpecificRecordBase> consumer = new KafkaConsumer<>(config);

        try {
            consumer.subscribe(List.of(topic));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records =    consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                int count = 0;
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    snapshotHandleService.handleRecord((SensorsSnapshotAvro) record.value());
                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }

        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Ошибка во время обработки снапшотов", e);
        } finally {
            try {
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }

    private static Properties getConsumerProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "SnapshotConsumer");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-client-snapshot");
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, VoidDeserializer.class.getCanonicalName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SnapshotEventDeserializer.class.getCanonicalName());
        return properties;
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