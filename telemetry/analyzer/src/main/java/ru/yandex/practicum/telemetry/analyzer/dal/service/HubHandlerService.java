package ru.yandex.practicum.telemetry.analyzer.dal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Action;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Condition;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Sensor;
import ru.yandex.practicum.telemetry.analyzer.dal.repository.ActionRepository;
import ru.yandex.practicum.telemetry.analyzer.dal.repository.ConditionRepository;
import ru.yandex.practicum.telemetry.analyzer.dal.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.dal.repository.SensorRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubHandlerService {
    private final SensorRepository sensorRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final ScenarioRepository scenarioRepository;


    public void handleRecord(HubEventAvro record) throws InterruptedException {
        Class<?> cls = record.getPayload().getClass();
        String hubId = record.getHubId();
        log.info("Добавление записи \"{} \" в таблицу. Класс объекта: \"{} \"", record, cls);
        if(cls.getSimpleName().equals("DeviceAddedEventAvro")) {
            DeviceAddedEventAvro event = (DeviceAddedEventAvro) record.getPayload();
            String sensorId = event.getId();
            Sensor sensor = new Sensor();
            sensor.setId(sensorId);
            sensor.setHubId(hubId);
            sensorRepository.save(sensor);
        } else if(cls.getSimpleName().equals("DeviceRemovedEventAvro")) {
            DeviceRemovedEventAvro event = (DeviceRemovedEventAvro) record.getPayload();
            String sensorId = event.getId();
            Sensor sensor = new Sensor();
            sensor.setId(sensorId);
            sensor.setHubId(hubId);
            sensorRepository.delete(sensor);
        } else if(cls.getSimpleName().equals("ScenarioAddedEventAvro")) {
            ScenarioAddedEventAvro event = (ScenarioAddedEventAvro) record.getPayload();
            Map<String, Condition> scenarioConditions = new HashMap<>();
            List<ScenarioConditionAvro> conditions = event.getConditions();
            for (ScenarioConditionAvro condition : conditions) {
                String sensorId = condition.getSensorId();
                Condition newCondition = new Condition();
                newCondition.setType(condition.getType().name());
                newCondition.setOperation(condition.getOperation().name());
                if (condition.getValue() instanceof Integer) {
                    newCondition.setValue(((Number) condition.getValue()).longValue());
                } else {
                    Boolean boolValue = (Boolean) condition.getValue();
                    newCondition.setValue(boolValue ? 1L : 0L);
                }
                conditionRepository.save(newCondition);
                scenarioConditions.put(sensorId, newCondition);
            }
            Map<String, Action> scenarioActions = new HashMap<>();
            List<DeviceActionAvro> actions = event.getActions();
            for (DeviceActionAvro action : actions) {
                String sensorId = action.getSensorId();
                Action newAction = new Action();
                newAction.setType(action.getType().name());
                newAction.setValue(action.getValue().longValue());
                actionRepository.save(newAction);
                scenarioActions.put(sensorId, newAction);
            }
            Scenario scenario = new Scenario();
            scenario.setHubId(hubId);
            scenario.setName(event.getName());
            scenario.setConditions(scenarioConditions);
            scenario.setActions(scenarioActions);
            scenarioRepository.save(scenario);
        } else if (cls.getSimpleName().equals("ScenarioRemovedEventAvro")) {
            ScenarioRemovedEventAvro event = (ScenarioRemovedEventAvro) record.getPayload();
            Optional<Scenario> scenario = scenarioRepository.findByHubIdAndName(hubId, event.getName());
            scenario.ifPresent(scenarioRepository::delete);
        } else {
            log.info("Неизвестный класс объекта");
        }
    }

}
