package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProduser;

import java.time.Instant;

@Component
public class TemperatureSensorEventHandler extends BaseSensorEventHandler {
    protected final KafkaEventProduser producer;

    public TemperatureSensorEventHandler(KafkaEventProduser producer) {
        this.producer = producer;
    }

    @Override
    protected SensorEventAvro mapToAvro(SensorEventProto event) {
        TemperatureSensorProto record = event.getTemperatureSensorEvent();
        TemperatureSensorAvro tsEvent = TemperatureSensorAvro.newBuilder()
                .setTemperatureC(record.getTemperatureC())
                .setTemperatureF(record.getTemperatureF())
                .build();
        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(tsEvent)
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        SensorEventAvro record = mapToAvro(event);
        producer.sendSensorEventToKafka(record);
    }
}
