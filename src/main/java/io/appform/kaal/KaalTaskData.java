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
