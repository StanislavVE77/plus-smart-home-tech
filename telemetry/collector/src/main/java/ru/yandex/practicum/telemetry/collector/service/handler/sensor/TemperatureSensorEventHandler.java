package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.telemetry.collector.model.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.SensorEventType;
import ru.yandex.practicum.telemetry.collector.model.TemperatureSensorEvent;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProduser;

@Component
public class TemperatureSensorEventHandler extends BaseSensorEventHandler {

    public TemperatureSensorEventHandler(KafkaEventProduser producer) {
        super(producer);
    }

    @Override
    protected SensorEventAvro mapToAvro(SensorEvent event) {
        TemperatureSensorEvent record = (TemperatureSensorEvent) event;
        TemperatureSensorAvro tsEvent = TemperatureSensorAvro.newBuilder()
                .setTemperatureC(record.getTemperatureC())
                .setTemperatureF(record.getTemperatureF())
                .build();
        return SensorEventAvro.newBuilder()
                .setId(record.getId())
                .setHubId(record.getHubId())
                .setTimestamp(record.getTimestamp())
                .setPayload(tsEvent)
                .build();
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEvent event) {
        SensorEventAvro record = mapToAvro(event);
        producer.sendSensorEventToKafka(record);
    }
}
