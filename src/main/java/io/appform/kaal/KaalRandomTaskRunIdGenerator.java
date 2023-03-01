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

import java.util.Date;
import java.util.UUID;

/**
 * Generates a random task ID.
 */
public class KaalRandomTaskRunIdGenerator<T extends KaalTask<T, R>, R> implements KaalTaskRunIdGenerator<T, R> {

    /**
     * Generates a UUID based random task id.
     *
     * @param task Task metadata
     * @param executionTime The time when this task is supposed to be executed
     * @return The newly generated ID
     */
    @Override
    public String generateId(T task, Date executionTime) {
        return UUID.randomUUID().toString();
    }
}
