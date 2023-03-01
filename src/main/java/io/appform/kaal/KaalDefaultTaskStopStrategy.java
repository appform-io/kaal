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
 * This {@link KaalTaskStopStrategy} always returns true. As in the task will continue to get scheduled at specified
 * intervals perpetually.
 */
public class KaalDefaultTaskStopStrategy<T extends KaalTask<T, R>, R> implements KaalTaskStopStrategy<T, R> {

    /**
     * Ensures task will always get scheduled
     * @param taskData Data for the recently completed run
     * @return Always true
     */
    @Override
    public boolean scheduleNext(KaalTaskData<T, R> taskData) {
        return true;
    }
}
