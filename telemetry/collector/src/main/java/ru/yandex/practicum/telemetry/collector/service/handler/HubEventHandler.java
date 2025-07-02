package ru.yandex.practicum.telemetry.collector.service.handler;

import ru.yandex.practicum.telemetry.collector.model.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.HubEventType;

public interface HubEventHandler {
    HubEventType getMessageType();

    void handle(HubEvent event);
}
