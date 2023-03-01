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

/**
 *
 */
public class FailTask implements KaalTask<FailTask, Void> {
    @Override
    public String id() {
        return "FAIL_TASK";
    }

    @Override
    public long delayToNextRun(Date currentTime) {
        return 200;
    }

    @Override
    public Void apply(Date date, KaalTaskData<FailTask, Void> failTaskVoidKaalTaskData) {
        throw new RuntimeException(new IllegalArgumentException("Forced failure"));
    }
}
