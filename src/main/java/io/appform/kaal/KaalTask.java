package io.appform.kaal;

import java.util.Date;
import java.util.function.BiFunction;

/**
 * Interface for a Task to be executed.
 * The logic for the task is to be implemented in the BiFunction.apply(taskData, time) method.
 */
public interface KaalTask<T extends KaalTask<T, R>, R> extends BiFunction<Date, KaalTaskData<T, R>, R> {

    /**
     * Return an ID based on the parameters
     * @return Globally unique ID for a particular task
     */
    String id();

    /**
     * Return the delay for the next task run. Can be used to implement different strategies for execution.
     * NOTE: If a negative number is returned, no further scheduling will happen for the task
     * @param currentTime Time at which the method is being called
     * @return Delay to the next run
     */
    long delayToNextRun(Date currentTime);

}
