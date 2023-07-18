package io.appform.kaal;

/**
 * This {@link KaalTaskRetryStrategy} always returns false. As in the task will be executed only once for every runId, even
 * if it fails the execution
 */
public class KaalDefaultTaskRetryStrategy<T extends KaalTask<T, R>, R> implements KaalTaskRetryStrategy<T, R>{

    /**
     * Ensures task will only be executed once, even if it fails
     * @param taskData Data for the recently completed run
     * @return Always false
     */
    @Override
    public boolean shouldRetry(KaalTaskData<T, R> taskData) {
        return false;
    }
}
