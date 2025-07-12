package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemorySensorEvents {
    private final Map<String, SensorsSnapshotAvro> snapshots;

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        log.info("...... Список снапшотов: {}", snapshots);
        if (snapshots.containsKey(event.getHubId())) {
            SensorsSnapshotAvro snapshot = snapshots.get(event.getHubId());
            log.info("...... снапшот найден {}", event.getHubId());
            if (snapshot.getSensorsState().containsKey(event.getId())) {
                SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());
                log.info("...... предыдущее состояние найдено {}", oldState);
                log.info("...... сравнение времени: старого {} и нового {}", oldState.getTimestamp(), event.getTimestamp());
                //int comparisonResult = oldState.getTimestamp().compareTo(event.getTimestamp());
                String oldStateStr = oldState.getData().toString();
                String newStateStr = event.getPayload().toString();
                log.info("...... сравнение данных состояния id={}: старого \"{}\" и нового \"{}\"", event.getId(), oldStateStr, newStateStr);
                if (oldState.getTimestamp().isAfter(event.getTimestamp()) || oldStateStr.equals(newStateStr)) {
                //if (comparisonResult >= 0 || oldState.getData() == event.getPayload()) {
                    log.info("...... предыдущее состояние не изменилось");
                    return Optional.empty();
                } else {
                    log.info("...... добавление обновленного состояния в снапшот");
                    SensorStateAvro newState = SensorStateAvro.newBuilder()
                            .setTimestamp(event.getTimestamp())
                            .setData(event.getPayload())
                            .build();
                    log.info("...... новое состояние теперь {}", newState);
                    snapshot.setTimestamp(event.getTimestamp());
                    log.info("...... обновление времени снапшота");
                    snapshot.getSensorsState().put(event.getId(), newState);
                    log.info("...... обновленный снапшот теперь {}", snapshot);
                    return Optional.of(snapshot);
                }
            } else {
                log.info("...... добавление нового состояния в снапшот");
               SensorStateAvro newState = SensorStateAvro.newBuilder()
                       .setTimestamp(event.getTimestamp())
                       .setData(event.getPayload())
                       .build();
               snapshot.setTimestamp(event.getTimestamp());
               snapshot.getSensorsState().put(event.getId(), newState);
               return Optional.of(snapshot);
            }
        } else {
            log.info("...... снапшот не найден");
            return Optional.of(addSnapshot(event));
        }
    }

    private SensorsSnapshotAvro addSnapshot(SensorEventAvro record) {
        String hubId = record.getHubId();
        Map<String, SensorStateAvro> sensorsState = new HashMap<>();
        SensorStateAvro state = SensorStateAvro.newBuilder()
                .setTimestamp(record.getTimestamp())
                .setData(record.getPayload())
                .build();
        sensorsState.put(record.getId(), state);
        SensorsSnapshotAvro event  = SensorsSnapshotAvro.newBuilder()
                .setTimestamp(record.getTimestamp())
                .setHubId(record.getHubId())
                .setSensorsState(sensorsState)
                .build();
        snapshots.put(hubId, event);
        return event;
    }

}
