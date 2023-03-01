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

package io.appform.kaal;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
@Slf4j
public class TestTask implements KaalTask<TestTask, String> {
    private static final String DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";

    @Getter
    private final int index;
    private final long delay;

    public TestTask(int index, long delay) {
        this.index = index;
        this.delay = delay;
    }

    @Override
    public String id() {
        return "TEST_TASK_" + index;
    }

    @Override
    public long delayToNextRun(final Date currentTime) {
        return delay;
    }

    @Override
    public String apply(Date date, KaalTaskData<TestTask, String> taskData) {
        log.info("Task run {}/{} called at {}. Current Time: {}. Drift: {} ms",
                 id(), taskData.getRunId(), taskData.getTargetExecutionTime(), date,
                 taskData.drift());
        return new SimpleDateFormat(DATE_FORMAT).format(date);
    }
}
