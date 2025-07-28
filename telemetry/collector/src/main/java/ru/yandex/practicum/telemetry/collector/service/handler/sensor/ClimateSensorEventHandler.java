package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProduser;

import java.time.Instant;

@Component
public class ClimateSensorEventHandler extends BaseSensorEventHandler {

    protected final KafkaEventProduser producer;

    public ClimateSensorEventHandler(KafkaEventProduser producer) {
        this.producer = producer;
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        SensorEventAvro record = mapToAvro(event);
        producer.sendSensorEventToKafka(record);
    }

    @Override
    protected SensorEventAvro mapToAvro(SensorEventProto event) {
        ClimateSensorProto record = event.getClimateSensorEvent();
        ClimateSensorAvro csEvent = ClimateSensorAvro.newBuilder()
                .setTemperatureC(record.getTemperatureC())
                .setCo2Level(record.getCo2Level())
                .setHumidity(record.getHumidity())
                .build();
        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(csEvent)
                .build();
    }
}
