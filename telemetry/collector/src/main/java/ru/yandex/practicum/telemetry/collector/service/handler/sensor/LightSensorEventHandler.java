package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProduser;

import java.time.Instant;

@Component
public class LightSensorEventHandler extends BaseSensorEventHandler {
    protected final KafkaEventProduser producer;

    public LightSensorEventHandler(KafkaEventProduser producer) {
        this.producer = producer;
    }

    @Override
    protected SensorEventAvro mapToAvro(SensorEventProto event) {
        LightSensorProto record = event.getLightSensorEvent();
        LightSensorAvro lsEvent = LightSensorAvro.newBuilder()
                .setLinkQuality(record.getLinkQuality())
                .setLuminosity(record.getLuminosity())
                .build();
        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(lsEvent)
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        SensorEventAvro record = mapToAvro(event);
        producer.sendSensorEventToKafka(record);
    }
}

