package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.model.DeviceAddedEvent;
import ru.yandex.practicum.telemetry.collector.model.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.HubEventType;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProduser;

@Component
public class DeviceHubAddedEventHandler extends BaseHubEventHandler {

    public DeviceHubAddedEventHandler(KafkaEventProduser producer) {
        super(producer);
    }

    @Override
    protected HubEventAvro mapToAvro(HubEvent event) {
        DeviceAddedEvent record = (DeviceAddedEvent) event;
        DeviceAddedEventAvro daEvent = DeviceAddedEventAvro.newBuilder()
                .setId(record.getId())
                .setType(DeviceTypeAvro.valueOf(record.getDeviceType().name()))
                .build();
        return HubEventAvro.newBuilder()
                .setHubId(record.getHubId())
                .setTimestamp(record.getTimestamp())
                .setPayload(daEvent)
                .build();
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_ADDED;
    }

    @Override
    public void handle(HubEvent event) {
        HubEventAvro record = mapToAvro(event);
        producer.sendHubEventToKafka(record);
    }
}
