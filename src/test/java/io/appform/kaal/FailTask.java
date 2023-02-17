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
        return 2_000;
    }

    @Override
    public Void apply(Date date, KaalTaskData<FailTask, Void> failTaskVoidKaalTaskData) {
        throw new RuntimeException(new IllegalArgumentException("Forced failure"));
    }
}
