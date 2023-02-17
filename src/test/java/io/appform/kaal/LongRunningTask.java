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
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return taskData.getRunId();
    }
}
