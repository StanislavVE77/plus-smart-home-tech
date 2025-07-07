package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProduser;

import java.time.Instant;

@Component
public class ScenarioHubRemovedEventHandler extends BaseHubEventHandler {
    protected final KafkaEventProduser producer;

    public ScenarioHubRemovedEventHandler(KafkaEventProduser producer) {
        this.producer = producer;
    }

    @Override
    protected HubEventAvro mapToAvro(HubEventProto event) {
        ScenarioRemovedEventProto record = event.getScenarioRemoved();
        ScenarioRemovedEventAvro saEvent = ScenarioRemovedEventAvro.newBuilder()
                .setName(record.getName())
                .build();
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(saEvent)
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public void handle(HubEventProto event) {
        HubEventAvro record = mapToAvro(event);
        producer.sendHubEventToKafka(record);
    }
}
