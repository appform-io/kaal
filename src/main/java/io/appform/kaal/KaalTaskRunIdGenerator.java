package io.appform.kaal;

import java.util.Date;

/**
 * Generates run id for a task run
 */
public interface KaalTaskRunIdGenerator<T extends KaalTask<T, R>, R> {

    /**
     * Return a unique Id for a task run
     * @param task reference to the task being run
     * @param executionTime time at which task is supposed to be executed
     * @return A unique string id for this run
     */
    String generateId(final T task, Date executionTime);
}
