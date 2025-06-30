package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.telemetry.collector.model.HubEvent;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProduser;
import ru.yandex.practicum.telemetry.collector.service.handler.HubEventHandler;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {
    protected final KafkaEventProduser producer;

    protected abstract T mapToAvro(HubEvent event);
}
