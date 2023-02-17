package io.appform.kaal;

import io.appform.signals.signals.ConsumingSyncSignal;
import io.appform.signals.signals.ScheduledSignal;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;

/**
 *
 */
@Slf4j
public class KaalScheduler<T extends KaalTask<T, R>, R> {

    private static final String HANDLER_NAME = "TASK_POLLER";
    private static final long DEFAULT_CHECK_DELAY = 1_000;

    private final KaalTaskIdGenerator<T, R> taskIdGenerator;
    private final ExecutorService executorService;

    private final PriorityBlockingQueue<KaalTaskData<T, R>> tasks
            = new PriorityBlockingQueue<>(1024,
                                          Comparator.comparing(e -> {
                                              val nextTime = e.getTargetExecutionTime().getTime();
                                              log.debug("Execution time: {}", nextTime);
                                              return nextTime;
                                          }));
    private final ScheduledSignal signalGenerator = ScheduledSignal.builder()
            .errorHandler(e -> log.error("Error running scheduled poll: " + e.getMessage(), e))
            .interval(Duration.ofMillis(DEFAULT_CHECK_DELAY))
            .build();

    private final Set<String> deleted = new ConcurrentSkipListSet<>();

    private final ConsumingSyncSignal<KaalTaskData<T, R>> taskCompleted = new ConsumingSyncSignal<>();

    public KaalScheduler(KaalTaskIdGenerator<T, R> taskIdGenerator, ExecutorService executorService) {
        this.taskIdGenerator = taskIdGenerator;
        this.executorService = executorService;
    }

    public void start() {
        taskCompleted.connect(this::handleTaskCompletion);
        signalGenerator.connect(this::processQueuedTask);
        clear();
        log.info("Started task scheduler");
    }

    public void stop() {
        signalGenerator.disconnect(HANDLER_NAME);
        signalGenerator.close();
        log.info("Kaal scheduler shut down");
    }

    public void clear() {
        tasks.clear();
        deleted.clear();
        log.info("Scheduler queue purged");
    }

    public ConsumingSyncSignal<KaalTaskData<T, R>> onTaskCompleted() {
        return taskCompleted;
    }

    public Optional<String> schedule(final T task) {
        return schedule(task, new Date());
    }

    public Optional<String> schedule(final T task, final Date currTime) {
        var delay = task.delayToNextRun(currTime);
        if(delay < DEFAULT_CHECK_DELAY) {
            log.warn("Provided delay of {} ms readjusted to lowest possible delay of {} ms",
                     delay, DEFAULT_CHECK_DELAY);
            delay = DEFAULT_CHECK_DELAY;
        }
        val executionTime = new Date(currTime.getTime() + delay);
        val runId = taskIdGenerator.generateId(task, executionTime);
        tasks.put(new KaalTaskData<>(runId, task, executionTime));
        log.debug("Scheduled task {} with delay of {} ms at {} with run id {}. Reference time: {}",
                  task.id(), delay, executionTime, runId, currTime);
        return Optional.of(runId);
    }

    public void delete(final String id) {
        deleted.add(id);
    }

    private void handleTaskCompletion(KaalTaskData<T, R> taskData) {
        val taskId = taskData.getTask().id();
        if (null == taskData.getException()) {
            log.info("Task run {}/{} is now complete.", taskId, taskData.getRunId());
        }
        else {
            log.warn("Task run {}/{} is now complete with error: {}",
                     taskId,
                     taskData.getRunId(),
                     errorMessage(taskData.getException()));
        }
        if(deleted.contains(taskId)) { //Will get hit if deleted during task execution
            log.debug("Looks like task {} has already been deleted .. no further scheduling necessary", taskId);
            deleted.remove(taskId);
            return;
        }
        val drift = taskData.drift();
        log.debug("Adjusting next run of {} for a drift of {} ms", taskId, drift);
        schedule(taskData.getTask(), Date.from(Instant.now().minus(drift, ChronoUnit.MILLIS)));
    }

    private void processQueuedTask(Date currentTime) {
        while (true) {
            val taskData = tasks.peek();
            var canContinue = false;
            if (taskData == null) {
                log.trace("Nothing queued... will sleep again");
            }
            else {
                val taskId = taskData.getTask().id();
                val runId = taskData.getRunId();
                log.trace("Received task {}/{}", taskId, runId);
                if (currentTime.before(taskData.getTargetExecutionTime())) {
                    log.trace("Found non-executable earliest task: {}/{}", taskId, runId);
                }
                else {
                    canContinue = true;
                }
            }
            if (!canContinue) {
                log.trace("Nothing to do now, will try again later.");
                break;
            }
            try {
                executorService.submit(() -> executeTask(taskData));
                val status = tasks.remove(taskData);
                log.trace("{}/{} submitted for execution with status {}",
                          taskData.getTask().id(),
                          taskData.getRunId(),
                          status);
            }
            catch (Exception e) {
                log.error("Error scheduling topology task: ", e);
            }
        }
    }

    private void executeTask(KaalTaskData<T, R> taskData) {
        val taskId = taskData.getTask().id();
        if(deleted.contains(taskId)) { //Will get hit if delete was called during pause
            log.debug("Looks like task {} has already been deleted .. task will be ignored", taskId);
            deleted.remove(taskId);
            return;
        }
        taskData.setActualStartTime(new Date());
        try {
            taskCompleted.dispatch(
                    taskData.setResult(taskData.getTask().apply(new Date(), taskData)));
        }
        catch (Throwable t) {
            taskCompleted.dispatch(taskData.setException(t));
        }
    }

    public static String errorMessage(Throwable t) {
        var root = t;
        while (null != root.getCause()) {
            root = root.getCause();
        }
        return null == root.getMessage()
               ? root.getClass().getSimpleName()
               : root.getMessage();
    }
}
