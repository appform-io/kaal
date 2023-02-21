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
