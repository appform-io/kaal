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

import lombok.Data;

import java.util.Date;
import java.util.Objects;

/**
 * Data received by the handler connected to {@link KaalScheduler#onTaskCompleted()} signal once a task run is complete.
 */
@Data
public class KaalTaskData<T extends KaalTask<T, R>, R> {
    /**
     * Unique ID for the current run
     */
    private final String runId;

    /**
     * Reference to the task
     */
    private final T task;

    /**
     * Time at which task was supposed to be executed
     */
    private final Date targetExecutionTime;

    /**
     * Time at which task was actually started
     */
    private Date actualStartTime;

    /**
     * Result of the task run
     */
    private R result;

    /**
     * Any exception thrown during the task run
     */
    private Throwable exception;

    /**
     * Calculate the drift between expected and the actual start time
     * @return The time drift in milliseconds
     */
    public long drift() {
        return Objects.requireNonNullElse(actualStartTime, new Date()).getTime() - targetExecutionTime.getTime();
    }
}
