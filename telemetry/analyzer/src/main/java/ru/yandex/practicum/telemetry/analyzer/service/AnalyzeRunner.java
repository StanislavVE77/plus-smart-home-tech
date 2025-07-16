package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalyzeRunner implements CommandLineRunner {
    final HubEventProcessor hubEventProcessor;
    final SnapshotProcessor snapshotProcessor;

    @Override
    public void run(String... args) throws Exception {
        Thread hubEventThread = new Thread(hubEventProcessor);
        hubEventThread.setName("HubEventHandlerThread");
        hubEventThread.start();

        snapshotProcessor.run();
    }
}
