package io.appform.kaal;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 */
@Slf4j
class KaalSchedulerTest {

    @Test
    @SneakyThrows
    void testScheduler() {
        val called = new AtomicInteger();
        val scheduler = new KaalScheduler<TestTask, String>(new RandomKaalTaskIdGenerator<>(), Executors.newCachedThreadPool());
        scheduler.onTaskCompleted().connect(td -> {
            log.info("Result: {}", td.getResult());
            called.incrementAndGet();
        });
        scheduler.start();
        val delayMs = 2_000;
        val task = new TestTask(0, delayMs);
        assertTrue(scheduler.schedule(task).isPresent());

        await()
                .forever()
                .until(() -> called.get() == 3);
        assertEquals(3, called.get()); //3 executions have happened

        log.info("Testing for delete now");
        scheduler.delete(task.id());

        //Now wait for a period > delay to ensure no further runs have happened
        val future = Date.from(Instant.now().plus(2 * delayMs, ChronoUnit.MILLIS));

        await().forever().until(() -> new Date().after(future));
        assertEquals(3, called.get()); //No further increments means, no further executions have happened
        scheduler.stop();
    }

    @Test
    @SneakyThrows
    void testSchedulerLowDelayTask() {
        val called = new AtomicInteger();
        val scheduler = new KaalScheduler<TestTask, String>(new RandomKaalTaskIdGenerator<>(), Executors.newCachedThreadPool());
        scheduler.onTaskCompleted().connect(td -> {
            log.info("Result: {}", td.getResult());
            called.incrementAndGet();
        });
        scheduler.start();
        val delayMs = 1;
        val task = new TestTask(0, delayMs);
        assertTrue(scheduler.schedule(task).isPresent());
        val future = Date.from(Instant.now().plus(3_000, ChronoUnit.MILLIS));

        await()
                .forever()
                .until(() -> called.get() == 3);
        assertEquals(3, called.get()); //3 executions have happened
        assertTrue(new Date().after(future)); //Make sure this is readjusted to min 1 sec interval
        scheduler.stop();
    }

    @Test
    @SneakyThrows
    void testSchedulerLongRunningTaskDelete() {
        val called = new AtomicInteger();
        val scheduler = new KaalScheduler<LongRunningTask, String>(new RandomKaalTaskIdGenerator<>(), Executors.newCachedThreadPool());
        scheduler.onTaskCompleted().connect(td -> {
            log.info("Result: {}", td.getResult());
            called.incrementAndGet();
        });
        scheduler.start();
        val task = new LongRunningTask();
        assertTrue(scheduler.schedule(task).isPresent());
        val future = Date.from(Instant.now().plus(5_000, ChronoUnit.MILLIS));
        await().until(() -> task.getStarted().get());
        scheduler.delete(task.id());
        await()
                .forever()
                .until(() -> new Date().after(future));
        assertEquals(1, called.get()); //only 1, because other executions were cancelled

        scheduler.stop();
    }

    @Test
    @SneakyThrows
    void testSchedulerTaskException() {
        val called = new AtomicInteger();
        val scheduler = new KaalScheduler<FailTask, Void>(new RandomKaalTaskIdGenerator<>(), Executors.newCachedThreadPool());
        scheduler.onTaskCompleted().connect(td -> {
            if(null != td.getException()) {
                log.info("Failure: {}", td.getException().getMessage());
                called.incrementAndGet();
            }
        });
        scheduler.start();
        val delayMs = 2_000;
        val task = new FailTask();
        assertTrue(scheduler.schedule(task).isPresent());

        await()
                .forever()
                .until(() -> called.get() == 3);
        assertEquals(3, called.get()); //3 executions have happened

        scheduler.stop();
    }


    @Test
    @SneakyThrows
    void testSchedulerMulti() {
        val called = IntStream.range(0, 10)
                .mapToObj(i -> new AtomicInteger())
                .toList();
        val scheduler = new KaalScheduler<TestTask, String>(new RandomKaalTaskIdGenerator<>(), Executors.newCachedThreadPool());
        scheduler.onTaskCompleted().connect(td -> {
            log.info("Result: {}", td.getResult());
            called.get(td.getTask().getIndex()).incrementAndGet();
        });
        scheduler.start();
        val delayMs = 2_000;
        IntStream.range(0, 10)
                .forEach(i -> {
                    val task = new TestTask(i, delayMs);
                    assertTrue(scheduler.schedule(task).isPresent());
                });

        await()
                .forever()
                .until(() -> IntStream.range(0, 10)
                        .allMatch(i -> called.get(i).get() >= 3));
        assertTrue(IntStream.range(0, 10)
                           .allMatch(i -> called.get(i).get() >= 3));
        scheduler.stop();
    }
}