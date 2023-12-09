package io.appform.kaal;

/**
 * A strategy used by the {@link KaalScheduler} to determine if a retry run for the task needs to be scheduled or not
 * in case the current run ends up with an exception
 */
public interface KaalTaskRetryStrategy<T extends KaalTask<T, R>, R> {

    /**
     * Determines if a retry run for this task is needed or not
     * @param taskData Data for the recently completed run
     * @return True if a retry run is needed, false otherwise
     */
    boolean shouldRetry(final KaalTaskData<T, R> taskData);

}
