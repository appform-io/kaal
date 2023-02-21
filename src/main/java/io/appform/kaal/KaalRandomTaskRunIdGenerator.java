package io.appform.kaal;

import java.util.Date;
import java.util.UUID;

/**
 * Generates a random task ID.
 */
public class KaalRandomTaskRunIdGenerator<T extends KaalTask<T, R>, R> implements KaalTaskRunIdGenerator<T, R> {

    /**
     * Generates a UUID based random task id.
     *
     * @param task Task metadata
     * @param executionTime The time when this task is supposed to be executed
     * @return The newly generated ID
     */
    @Override
    public String generateId(T task, Date executionTime) {
        return UUID.randomUUID().toString();
    }
}
