package io.appform.kaal;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
@Slf4j
public class TestTask implements KaalTask<TestTask, String> {
    private static final String DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";

    @Getter
    private final int index;
    private final long delay;

    public TestTask(int index, long delay) {
        this.index = index;
        this.delay = delay;
    }

    @Override
    public String id() {
        return "TEST_TASK_" + index;
    }

    @Override
    public long delayToNextRun(final Date currentTime) {
        return delay;
    }

    @Override
    public String apply(Date date, KaalTaskData<TestTask, String> taskData) {
        log.info("Task run {}/{} called at {}. Current Time: {}. Drift: {} ms",
                 id(), taskData.getRunId(), taskData.getTargetExecutionTime(), date,
                 taskData.drift());
        return new SimpleDateFormat(DATE_FORMAT).format(date);
    }
}
