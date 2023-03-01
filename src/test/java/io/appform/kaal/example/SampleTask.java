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

import io.appform.kaal.KaalTask;
import io.appform.kaal.KaalTaskData;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * Sample task that runs every one second and returns the invocation time as the result
 */
@Slf4j
public class SampleTask implements KaalTask<SampleTask, Date> {

    @Override
    public String id() {
        return "SAMPLE_TASK"; //Fixed ID for the task
    }

    @Override
    public long delayToNextRun(Date currentTime) {
        return 1_000; //Task runs every one second
    }

    @Override
    public Date apply(Date date, KaalTaskData<SampleTask, Date> sampleTaskDateKaalTaskData) {
        log.info("Sample task invoked");
        return date; //Just return the invocation date as the task result.
    }
}
