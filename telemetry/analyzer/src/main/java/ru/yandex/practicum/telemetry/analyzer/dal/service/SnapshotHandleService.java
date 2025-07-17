package ru.yandex.practicum.telemetry.analyzer.dal.service;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Action;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Condition;
import ru.yandex.practicum.telemetry.analyzer.dal.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.dal.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.dal.repository.SensorRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SnapshotHandleService {
    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;

    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public SnapshotHandleService(@GrpcClient("hub-router")
                                 HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient, SensorRepository sensorRepository, ScenarioRepository scenarioRepository) {
        this.hubRouterClient = hubRouterClient;
        this.sensorRepository = sensorRepository;
        this.scenarioRepository = scenarioRepository;
    }

    public void handleRecord(SensorsSnapshotAvro record) throws InterruptedException {

        try {
            String hubId = record.getHubId();
            Map<String, SensorStateAvro> sensorsState = record.getSensorsState();
            List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);
            List<Integer> switchExecution;
            for (Scenario scenario : scenarios) {
                String scenarioName = scenario.getName();
                switchExecution = new ArrayList<>();
                Map<String, Condition> conditions = scenario.getConditions();
                if (sensorRepository.existsByIdInAndHubId(sensorsState.keySet(), hubId)) {
                    for (String sensorId : conditions.keySet()) {
                        if (sensorsState.keySet().contains(sensorId)) {
                            Condition condition = conditions.get(sensorId);
                            SensorStateAvro state = sensorsState.get(sensorId);
                            if (condition.getType().equals("SWITCH")) {
                                SwitchSensorAvro switchSensorAvro = (SwitchSensorAvro) state.getData();
                                if (condition.getOperation().equals("EQUALS") && ((condition.getValue() == 1 && switchSensorAvro.getState()) ||
                                        (condition.getValue() == 0 && !switchSensorAvro.getState()))) {
                                    switchExecution.add(1);
                                } else {
                                    switchExecution.add(0);
                                }
                            }
                            if (condition.getType().equals("MOTION")) {
                                MotionSensorAvro motionSensorAvro = (MotionSensorAvro) state.getData();
                                if (condition.getOperation().equals("EQUALS") && ((condition.getValue() == 1 && motionSensorAvro.getMotion()) ||
                                        (condition.getValue() == 0 && !motionSensorAvro.getMotion()))) {
                                    switchExecution.add(1);
                                } else {
                                    switchExecution.add(0);
                                }
                            }
                            if (condition.getType().equals("LUMINOSITY")) {
                                LightSensorAvro lightSensorAvro = (LightSensorAvro) state.getData();
                                if ((condition.getOperation().equals("EQUALS") && condition.getValue() == lightSensorAvro.getLuminosity()) ||
                                        (condition.getOperation().equals("GREATER_THAN") && condition.getValue() < lightSensorAvro.getLuminosity()) ||
                                        (condition.getOperation().equals("LOWER_THAN") && condition.getValue() > lightSensorAvro.getLuminosity())) {
                                    switchExecution.add(1);
                                } else {
                                    switchExecution.add(0);
                                }
                            }
                            if (condition.getType().equals("CO2LEVEL")) {
                                ClimateSensorAvro climateSensorAvro = (ClimateSensorAvro) state.getData();
                                if ((condition.getOperation().equals("EQUALS") && condition.getValue() == climateSensorAvro.getCo2Level()) ||
                                        (condition.getOperation().equals("GREATER_THAN") && condition.getValue() < climateSensorAvro.getCo2Level()) ||
                                        (condition.getOperation().equals("LOWER_THAN") && condition.getValue() > climateSensorAvro.getCo2Level())) {
                                    switchExecution.add(1);
                                } else {
                                    switchExecution.add(0);
                                }
                            }
                            if (condition.getType().equals("HUMIDITY")) {
                                ClimateSensorAvro climateSensorAvro = (ClimateSensorAvro) state.getData();
                                if ((condition.getOperation().equals("EQUALS") && condition.getValue() == climateSensorAvro.getHumidity()) ||
                                        (condition.getOperation().equals("GREATER_THAN") && condition.getValue() < climateSensorAvro.getHumidity()) ||
                                        (condition.getOperation().equals("LOWER_THAN") && condition.getValue() > climateSensorAvro.getHumidity())) {
                                    switchExecution.add(1);
                                } else {
                                    switchExecution.add(0);
                                }
                            }
                            if (condition.getType().equals("TEMPERATURE") && state.getData().getClass().getSimpleName().equals("TemperatureSensorAvro")) {
                                TemperatureSensorAvro temperatureSensorAvro = (TemperatureSensorAvro) state.getData();
                                if ((condition.getOperation().equals("EQUALS") && condition.getValue() == temperatureSensorAvro.getTemperatureC()) ||
                                        (condition.getOperation().equals("GREATER_THAN") && condition.getValue() < temperatureSensorAvro.getTemperatureC()) ||
                                        (condition.getOperation().equals("LOWER_THAN") && condition.getValue() > temperatureSensorAvro.getTemperatureC())) {
                                    switchExecution.add(1);
                                } else {
                                    switchExecution.add(0);
                                }
                            }
                            if (condition.getType().equals("TEMPERATURE") && state.getData().getClass().getSimpleName().equals("ClimateSensorAvro")) {
                                ClimateSensorAvro climateSensorAvro = (ClimateSensorAvro) state.getData();
                                if ((condition.getOperation().equals("EQUALS") && condition.getValue() == climateSensorAvro.getTemperatureC()) ||
                                        (condition.getOperation().equals("GREATER_THAN") && condition.getValue() < climateSensorAvro.getTemperatureC()) ||
                                        (condition.getOperation().equals("LOWER_THAN") && condition.getValue() > climateSensorAvro.getTemperatureC())) {
                                    switchExecution.add(1);
                                } else {
                                    switchExecution.add(0);
                                }
                            }

                        }
                    }
                } else {
                    log.info("в базе нет соответствующего сенсора для данного хаба");
                }
                if (!switchExecution.isEmpty() && !switchExecution.contains(0)) {
                    Map<String, Action> actions = scenario.getActions();
                    for (String sensorId : actions.keySet()) {
                        Action action = actions.get(sensorId);
                        Instant time = Instant.now();
                        Timestamp timestamp = Timestamp.newBuilder().setSeconds(time.getEpochSecond())
                                .setNanos(time.getNano()).build();

                        DeviceActionProto data;
                        if (action.getType().equals("SET_VALUE")) {
                            data = DeviceActionProto.newBuilder()
                                    .setSensorId(sensorId)
                                    .setType(ActionTypeProto.valueOf(action.getType()))
                                    .setValue(action.getValue().intValue())
                                    .build();
                        } else {
                            data = DeviceActionProto.newBuilder()
                                    .setSensorId(sensorId)
                                    .setType(ActionTypeProto.valueOf(action.getType()))
                                    .build();
                        }
                        DeviceActionRequest request = DeviceActionRequest.newBuilder()
                                .setHubId(hubId)
                                .setScenarioName(scenarioName)
                                .setAction(data)
                                .setTimestamp(timestamp)
                                .build();
                        Empty result = hubRouterClient.handleDeviceAction(request);
                    }
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }

    }

}
