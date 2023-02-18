package io.appform.kaal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class KaalSchedulerBuilder <T extends KaalTask<T, R>, R> {

    private static final long DEFAULT_CHECK_DELAY = 100;

    private long pollingInterval = DEFAULT_CHECK_DELAY;
    private KaalTaskIdGenerator<T, R> taskIdGenerator;
    private KaalTaskStopStrategy<T,R> stopStrategy;
    private ExecutorService executorService;

    public KaalSchedulerBuilder<T,R> withPollingInterval(long pollingInterval) {
        this.pollingInterval = pollingInterval;
        return this;
    }

    public KaalSchedulerBuilder<T,R> withTaskIdGenerator(final KaalTaskIdGenerator<T,R> taskIdGenerator) {
        this.taskIdGenerator = taskIdGenerator;
        return this;
    }

    public KaalSchedulerBuilder<T,R> withTaskStopStrategy(final KaalTaskStopStrategy<T,R> stopStrategy) {
        this.stopStrategy = stopStrategy;
        return this;
    }

    public  KaalSchedulerBuilder<T,R> withExecutorService(final ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public KaalScheduler<T,R> build() {
        return new KaalScheduler<>(pollingInterval <= 0 ? DEFAULT_CHECK_DELAY : pollingInterval,
                                   Objects.requireNonNullElseGet(taskIdGenerator, RandomKaalTaskIdGenerator::new),
                                   Objects.requireNonNullElseGet(stopStrategy, KaalDefaultTaskStopStrategy::new),
                                   Objects.requireNonNullElseGet(executorService, Executors::newCachedThreadPool));
    }
}
