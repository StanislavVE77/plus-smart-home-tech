package ru.yandex.practicum.telemetry.analyzer.configuration;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public class HubEventDeserializer extends BaseAvroDeserializer<HubEventAvro> {
    public HubEventDeserializer() {

        super(HubEventAvro.getClassSchema());
    }
}