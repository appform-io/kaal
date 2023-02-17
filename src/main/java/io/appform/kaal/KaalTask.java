package io.appform.kaal;

import java.util.Date;
import java.util.function.BiFunction;

/**
 *
 */
public interface KaalTask<TT extends KaalTask<TT, R>, R> extends BiFunction<Date, KaalTaskData<TT, R>, R> {
    String id();

    long delayToNextRun(Date currentTime);
}
