/*
 * Copyright 2023. Santanu Sinha
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 *  compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */

package io.appform.kaal.example;

import io.appform.kaal.KaalScheduler;
import io.appform.kaal.KaalTaskData;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;

/**
 * Runs a task 10 times
 */
@Slf4j
public class SampleTaskRunner {
    private final KaalScheduler<SampleTask, Date> scheduler;
    private final CountingStopStrategy stopStrategy = new CountingStopStrategy();
    private AtomicBoolean done = new AtomicBoolean();

    public SampleTaskRunner() {
        scheduler = KaalScheduler.<SampleTask, Date>builder()
                .withTaskStopStrategy(stopStrategy)
                .build();
        scheduler.onTaskCompleted().connect(this::handleTaskResult);
    }

    public void start() {
        scheduler.start();
        log.info("Scheduler started");
    }

    public void run() {
        scheduler.schedule(new SampleTask());
        await().atMost(Duration.ofSeconds(12))
                .pollInterval(Duration.ofSeconds(1))
                .until(() -> stopStrategy.getExecutionCount().get() == 10);
        done.set(true);
        log.info("All runs are done");
    }

    public boolean done() {
        return done.get();
    }

    public void stop() {
        scheduler.stop();
    }

    private void handleTaskResult(KaalTaskData<SampleTask, Date> taskData) {
        log.info("Received result from task run {}/{} is {}",
                 taskData.getTask().id(), taskData.getRunId(), taskData.getResult());
    }

}
