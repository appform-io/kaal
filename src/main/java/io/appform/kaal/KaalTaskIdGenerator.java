package io.appform.kaal;

import java.util.Date;

/**
 *
 */
public interface KaalTaskIdGenerator<T extends KaalTask<T, R>, R> {
    String generateId(final T task, Date executionTime);
}
