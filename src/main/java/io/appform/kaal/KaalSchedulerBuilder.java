package io.appform.kaal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Customize and build a {@link KaalScheduler} instance. Please calls {@link KaalScheduler#start()} to start the scheduler.
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class KaalSchedulerBuilder <T extends KaalTask<T, R>, R> {

    private static final long DEFAULT_CHECK_DELAY = 100;

    private long pollingInterval = DEFAULT_CHECK_DELAY;
    private KaalTaskRunIdGenerator<T, R> taskIdGenerator;
    private KaalTaskStopStrategy<T,R> stopStrategy;
    private ExecutorService executorService;

    /**
     * Set the interval at which scheduler looks for new runs to execute. Default is 100 ms.
     * @param pollingInterval The interval to be set
     * @return Reference to the builder
     */
    public KaalSchedulerBuilder<T,R> withPollingInterval(long pollingInterval) {
        this.pollingInterval = pollingInterval;
        return this;
    }

    /**
     * Provide a custom ID generator. If not provided, {@link KaalRandomTaskRunIdGenerator} is used to generate run id.
     * @param taskIdGenerator Custom implementation of {@link KaalRandomTaskRunIdGenerator}
     * @return Reference to the builder
     */
    public KaalSchedulerBuilder<T,R> withTaskIdGenerator(final KaalTaskRunIdGenerator<T,R> taskIdGenerator) {
        this.taskIdGenerator = taskIdGenerator;
        return this;
    }

    /**
     * Propvide a custom stop strategy. If not provided, {@link KaalDefaultTaskStopStrategy} is used.
     * @param stopStrategy Custom implementation of {@link KaalTaskStopStrategy}
     * @return Reference to the builder
     */
    public KaalSchedulerBuilder<T,R> withTaskStopStrategy(final KaalTaskStopStrategy<T,R> stopStrategy) {
        this.stopStrategy = stopStrategy;
        return this;
    }

    /**
     * Executor service to be used to execute a task. If not provided an unbounded cached thread pool is used.
     * @param executorService Custom executor service.
     * @return Reference to the builder
     */
    public  KaalSchedulerBuilder<T,R> withExecutorService(final ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    /**
     * Build the scheduler
     * @return instance of {@link KaalScheduler}
     */
    public KaalScheduler<T,R> build() {
        return new KaalScheduler<>(pollingInterval <= 0 ? DEFAULT_CHECK_DELAY : pollingInterval,
                                   Objects.requireNonNullElseGet(taskIdGenerator, KaalRandomTaskRunIdGenerator::new),
                                   Objects.requireNonNullElseGet(stopStrategy, KaalDefaultTaskStopStrategy::new),
                                   Objects.requireNonNullElseGet(executorService, Executors::newCachedThreadPool));
    }
}
