package io.appform.kaal;

public interface KaalTaskRetryStrategy<T extends KaalTask<T, R>, R> {

    boolean shouldRetry(final KaalTaskData<T, R> taskData);

}
