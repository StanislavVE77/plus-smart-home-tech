package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.model.DeviceRemovedEvent;
import ru.yandex.practicum.telemetry.collector.model.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.HubEventType;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProduser;

public class DeviceHubRemovedEventHandler extends BaseHubEventHandler {

    public DeviceHubRemovedEventHandler(KafkaEventProduser producer) {
        super(producer);
    }

    @Override
    protected HubEventAvro mapToAvro(HubEvent event) {
        DeviceRemovedEvent record = (DeviceRemovedEvent) event;
        DeviceRemovedEventAvro drEvent = DeviceRemovedEventAvro.newBuilder()
                .setId(record.getId())
                .build();
        return HubEventAvro.newBuilder()
                .setHubId(record.getHubId())
                .setTimestamp(record.getTimestamp())
                .setPayload(drEvent)
                .build();
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_REMOVED;
    }

    @Override
    public void handle(HubEvent event) {
        HubEventAvro record = mapToAvro(event);
        producer.sendHubEventToKafka(record);
    }
}
