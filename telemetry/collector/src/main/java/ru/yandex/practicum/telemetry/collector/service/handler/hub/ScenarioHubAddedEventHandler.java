package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProduser;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScenarioHubAddedEventHandler extends BaseHubEventHandler {

    protected final KafkaEventProduser producer;

    public ScenarioHubAddedEventHandler(KafkaEventProduser producer) {
        this.producer = producer;
    }

    @Override
    protected HubEventAvro mapToAvro(HubEventProto event) {
        ScenarioAddedEventProto record = event.getScenarioAdded();
        List<ScenarioConditionAvro> listConditions = record.getConditionList()
                .stream()
                .map(condition -> new ScenarioConditionAvro(condition.getSensorId(), ConditionTypeAvro.valueOf(condition.getType().name()),
                        ConditionOperationAvro.valueOf(condition.getOperation().name()), condition.hasIntValue() ? condition.getIntValue() : condition.getBoolValue()
                ))
                .collect(Collectors.toList());
        List<DeviceActionAvro> listActions = record.getActionList()
                .stream()
                .map(action -> new DeviceActionAvro(action.getSensorId(), ActionTypeAvro.valueOf(action.getType().name()), action.getValue()))
                .collect(Collectors.toList());
        ScenarioAddedEventAvro saEvent = ScenarioAddedEventAvro.newBuilder()
                .setName(record.getName())
                .setConditions(listConditions)
                .setActions(listActions)
                .build();
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(saEvent)
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        HubEventAvro record = mapToAvro(event);
        producer.sendHubEventToKafka(record);
    }
}
