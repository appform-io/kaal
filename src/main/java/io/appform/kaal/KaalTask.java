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
import java.util.function.BiFunction;

/**
 * Interface for a Task to be executed.
 * The logic for the task is to be implemented in the BiFunction.apply(taskData, time) method.
 */
public interface KaalTask<T extends KaalTask<T, R>, R> extends BiFunction<Date, KaalTaskData<T, R>, R> {

    /**
     * Return an ID based on the parameters
     * @return Globally unique ID for a particular task
     */
    String id();

    /**
     * Return the delay for the next task run. Can be used to implement different strategies for execution.
     * NOTE: If a negative number is returned, no further scheduling will happen for the task
     * @param currentTime Time at which the method is being called
     * @return Delay to the next run
     */
    long delayToNextRun(Date currentTime);

}
