package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProduser;

import java.time.Instant;

@Component
public class MotionSensorEventHandler extends BaseSensorEventHandler {
    protected final KafkaEventProduser producer;

    public MotionSensorEventHandler(KafkaEventProduser producer) {
        this.producer = producer;
    }

    @Override
    protected SensorEventAvro mapToAvro(SensorEventProto event) {
        MotionSensorProto record = event.getMotionSensorEvent();
        MotionSensorAvro msEvent = MotionSensorAvro.newBuilder()
                .setMotion(record.getMotion())
                .setLinkQuality(record.getLinkQuality())
                .setVoltage(record.getVoltage())
                .build();
        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(msEvent)
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        SensorEventAvro record = mapToAvro(event);
        producer.sendSensorEventToKafka(record);
    }
}
