package io.appform.kaal;

import lombok.Data;

import java.util.Date;
import java.util.Objects;

/**
 *
 */
@Data
public class KaalTaskData<T extends KaalTask<T, R>, R> {
    private final String runId;
    private final T task;
    private final Date targetExecutionTime;

    private Date actualStartTime;

    private R result;

    private Throwable exception;

    public long drift() {
        return Objects.requireNonNullElse(actualStartTime, new Date()).getTime() - targetExecutionTime.getTime();
    }
}
