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

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class LongRunningTask implements KaalTask<LongRunningTask, String> {

    @Getter
    private final AtomicBoolean started = new AtomicBoolean();
    @Override
    public String id() {
        return "LONG_TASK";
    }

    @Override
    public long delayToNextRun(Date currentTime) {
        return 2_000;
    }

    @Override
    @SuppressWarnings("java:S2925")
    public String apply(Date date, KaalTaskData<LongRunningTask, String> taskData) {
        started.set(true);
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return taskData.getRunId();
    }
}
