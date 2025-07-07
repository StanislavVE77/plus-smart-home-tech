package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProduser;

import java.time.Instant;

public class DeviceHubRemovedEventHandler extends BaseHubEventHandler {
    protected final KafkaEventProduser producer;

    public DeviceHubRemovedEventHandler(KafkaEventProduser producer) {
        this.producer = producer;
    }

    @Override
    protected HubEventAvro mapToAvro(HubEventProto event) {
        DeviceRemovedEventProto record = event.getDeviceRemoved();
        DeviceRemovedEventAvro drEvent = DeviceRemovedEventAvro.newBuilder()
                .setId(record.getId())
                .build();
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(drEvent)
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public void handle(HubEventProto event) {
        HubEventAvro record = mapToAvro(event);
        producer.sendHubEventToKafka(record);
    }
}
