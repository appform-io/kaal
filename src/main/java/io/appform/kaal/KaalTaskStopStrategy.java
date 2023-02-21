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
