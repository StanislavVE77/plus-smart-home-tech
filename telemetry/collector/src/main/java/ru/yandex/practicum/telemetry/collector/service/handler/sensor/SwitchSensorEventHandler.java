package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.telemetry.collector.model.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.SensorEventType;
import ru.yandex.practicum.telemetry.collector.model.SwitchSensorEvent;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProduser;

@Component
public class SwitchSensorEventHandler extends BaseSensorEventHandler {

    public SwitchSensorEventHandler(KafkaEventProduser producer) {
        super(producer);
    }

    @Override
    protected SensorEventAvro mapToAvro(SensorEvent event) {
        SwitchSensorEvent record = (SwitchSensorEvent) event;
        SwitchSensorAvro ssEvent = SwitchSensorAvro.newBuilder()
                .setState(record.isState())
                .build();
        return SensorEventAvro.newBuilder()
                .setId(record.getId())
                .setHubId(record.getHubId())
                .setTimestamp(record.getTimestamp())
                .setPayload(ssEvent)
                .build();
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEvent event) {
        SensorEventAvro record = mapToAvro(event);
        producer.sendSensorEventToKafka(record);
    }
}
