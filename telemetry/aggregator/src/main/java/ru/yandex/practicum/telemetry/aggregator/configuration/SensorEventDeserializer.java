package ru.yandex.practicum.telemetry.aggregator.configuration;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public class SensorEventDeserializer extends BaseAvroDeserializer<SensorEventAvro> {
    public SensorEventDeserializer() {

        super(SensorEventAvro.getClassSchema());
    }
}