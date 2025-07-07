package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProduser;

import java.time.Instant;

@Component
public class DeviceHubAddedEventHandler extends BaseHubEventHandler {
    protected final KafkaEventProduser producer;

    public DeviceHubAddedEventHandler(KafkaEventProduser producer) {
        this.producer = producer;
    }


    @Override
    protected HubEventAvro mapToAvro(HubEventProto event) {
        DeviceAddedEventProto record = event.getDeviceAdded();
        DeviceAddedEventAvro daEvent = DeviceAddedEventAvro.newBuilder()
                .setId(record.getId())
                .setType(DeviceTypeAvro.valueOf(record.getType().name()))
                .build();
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(daEvent)
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        HubEventAvro record = mapToAvro(event);
        producer.sendHubEventToKafka(record);
    }
}
