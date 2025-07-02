package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.model.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.ScenarioAddedEvent;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProduser;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScenarioHubAddedEventHandler extends BaseHubEventHandler {

    public ScenarioHubAddedEventHandler(KafkaEventProduser producer) {
        super(producer);
    }

    @Override
    protected HubEventAvro mapToAvro(HubEvent event) {
        ScenarioAddedEvent record = (ScenarioAddedEvent) event;
        List<ScenarioConditionAvro> listConditions = record.getConditions()
                .stream()
                .map(condition -> new ScenarioConditionAvro(condition.getSensorId(), ConditionTypeAvro.valueOf(condition.getType().name()), ConditionOperationAvro.valueOf(condition.getOperation().name()), condition.getValue()))
                .collect(Collectors.toList());
        List<DeviceActionAvro> listActions = record.getActions()
                .stream()
                .map(action -> new DeviceActionAvro(action.getSensorId(), ActionTypeAvro.valueOf(action.getType().name()), action.getValue()))
                .collect(Collectors.toList());
        ScenarioAddedEventAvro saEvent = ScenarioAddedEventAvro.newBuilder()
                .setName(record.getName())
                .setConditions(listConditions)
                .setActions(listActions)
                .build();
        return HubEventAvro.newBuilder()
                .setHubId(record.getHubId())
                .setTimestamp(record.getTimestamp())
                .setPayload(saEvent)
                .build();
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEvent event) {
        HubEventAvro record = mapToAvro(event);
        producer.sendHubEventToKafka(record);
    }
}
