package io.appform.kaal;

/**
 *
 */
public class KaalDefaultTaskStopStrategy<T extends KaalTask<T, R>, R> implements KaalTaskStopStrategy<T, R> {
    @Override
    public boolean scheduleNext(KaalTaskData<T, R> taskData) {
        return true;
    }
}
