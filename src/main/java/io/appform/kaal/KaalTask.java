package io.appform.kaal;

import java.util.Date;
import java.util.function.BiFunction;

/**
 *
 */
public interface KaalTask<T extends KaalTask<T, R>, R> extends BiFunction<Date, KaalTaskData<T, R>, R> {
    String id();

    long delayToNextRun(Date currentTime);

}
