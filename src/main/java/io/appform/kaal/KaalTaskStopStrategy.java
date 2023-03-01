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

/**
 * A strategy used by the {@link KaalScheduler} to determine if a subsequent run for the task needs to be scheduled or not
 */
public interface KaalTaskStopStrategy<T extends KaalTask<T,R>, R> {

    /**
     * Determines if a subsequent run for this task is needed or not
     * @param taskData Data for the recently completed run
     * @return True if a subsequent run is needed, false otherwise
     */
    boolean scheduleNext(final KaalTaskData<T,R> taskData);
}
