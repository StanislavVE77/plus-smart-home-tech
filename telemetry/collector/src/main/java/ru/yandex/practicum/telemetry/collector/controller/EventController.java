package ru.yandex.practicum.telemetry.collector.controller;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.telemetry.collector.model.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.SensorEventType;
import ru.yandex.practicum.telemetry.collector.service.handler.HubEventHandler;
import ru.yandex.practicum.telemetry.collector.service.handler.SensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/events")
public class EventController {
    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventType, HubEventHandler> hubEventHandlers;

    public EventController(Set<SensorEventHandler> sensorEventHandlers, Set<HubEventHandler> hubEventHandlers) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, sensorEventHandler -> sensorEventHandler));
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, hubEventHandler -> hubEventHandler));
    }

    @PostMapping("/sensors")
    @ResponseStatus(HttpStatus.CREATED)
    public void collectSensorEvent(@Valid @RequestBody SensorEvent request) {
        log.info("--> POST запрос /events/sensors с телом {}", request);
        if (sensorEventHandlers.containsKey(request.getType())) {
            sensorEventHandlers.get(request.getType()).handle(request);
        } else {
            throw new IllegalArgumentException("Не могу найти обработчик.");
        }
    }

    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.CREATED)
    public void collectHubEvent(@Valid @RequestBody HubEvent request) {
        log.info("--> POST запрос /events/hubs с телом {}", request);
        if (hubEventHandlers.containsKey(request.getType())) {
            hubEventHandlers.get(request.getType()).handle(request);
        } else {
            throw new IllegalArgumentException("Не могу найти обработчик.");
        }
    }
}
