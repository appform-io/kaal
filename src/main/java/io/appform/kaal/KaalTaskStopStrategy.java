package io.appform.kaal;

/**
 *
 */
public interface KaalTaskStopStrategy<T extends KaalTask<T,R>, R> {
    boolean scheduleNext(final KaalTaskData<T,R> taskData);
}
