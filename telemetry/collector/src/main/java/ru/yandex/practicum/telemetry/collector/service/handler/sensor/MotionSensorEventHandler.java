package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.model.MotionSensorEvent;
import ru.yandex.practicum.telemetry.collector.model.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.SensorEventType;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProduser;

@Component
public class MotionSensorEventHandler extends BaseSensorEventHandler {

    public MotionSensorEventHandler(KafkaEventProduser producer) {
        super(producer);
    }

    @Override
    protected SensorEventAvro mapToAvro(SensorEvent event) {
        MotionSensorEvent record = (MotionSensorEvent) event;
        MotionSensorAvro msEvent = MotionSensorAvro.newBuilder()
                .setMotion(record.isMotion())
                .setLinkQuality(record.getLinkQuality())
                .setVoltage(record.getVoltage())
                .build();
        return SensorEventAvro.newBuilder()
                .setId(record.getId())
                .setHubId(record.getHubId())
                .setTimestamp(record.getTimestamp())
                .setPayload(msEvent)
                .build();
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEvent event) {
        SensorEventAvro record = mapToAvro(event);
        producer.sendSensorEventToKafka(record);
    }
}
