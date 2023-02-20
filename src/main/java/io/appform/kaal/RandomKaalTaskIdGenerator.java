package io.appform.kaal;

import java.util.Date;
import java.util.UUID;

/**
 *
 */
public class RandomKaalTaskIdGenerator<T extends KaalTask<T, R>, R> implements KaalTaskIdGenerator<T, R> {
    @Override
    public String generateId(T task, Date executionTime) {
        return UUID.randomUUID().toString();
    }
}
