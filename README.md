# Kaal

[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=appform-io_kaal&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=appform-io_kaal) [![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=appform-io_kaal&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=appform-io_kaal) [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=appform-io_kaal&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=appform-io_kaal) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=appform-io_kaal&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=appform-io_kaal) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=appform-io_kaal&metric=coverage)](https://sonarcloud.io/summary/new_code?id=appform-io_kaal)
[![javadoc](https://javadoc.io/badge2/io.appform.kaal/kaal/javadoc.svg)](https://javadoc.io/doc/io.appform.kaal/kaal)

An embeddable in-memory scheduler. It can be used to implement tasks that need to run multiple times at intervals.

* Provides simple interface to obtain and act on results.
* Provides full control to task to tune further executions
* Multithreaded execution

## Entities

* **KaalTask** - An implementation of a task
* **KaalScheduler** - The scheduler that runs the task
* **KaalTaskData** - Task data available to a task execution

## Getting Started

### Coordinates

Kaal is available on [Maven central](https://central.sonatype.com/artifact/io.appform.kaal/kaal/1.0.0). Please use the
required dependency based on your build system.

### Step 1 - Create the task

The first thing that needs to be done is to crate a custom type of task. For this example we create a task that
gets invoked every 1 second and returns the current time as result. This current time is handled by using a signal
handler and printed out. Task stops after 10 invocations.

```java
/**
 * Sample task that runs every one second and returns the invocation time as the result
 */
@Slf4j
public class SampleTask implements KaalTask<SampleTask, Date> {

    @Override
    public String id() {
        return "SAMPLE_TASK"; //Fixed ID for the task
    }

    @Override
    public long delayToNextRun(Date currentTime) {
        return 1_000; //Task runs every one second
    }

    @Override
    public Date apply(Date date, KaalTaskData<SampleTask, Date> sampleTaskDateKaalTaskData) {
        log.info("Sample task invoked");
        return date; //Just return the invocation date as the task result.
    }
}
```

## Step 2 - (Optional) Create a stop strategy

If you want the job to stop after some criterion are met, implement a stop strategy.
For our example, we want the execution to stop after 10 runs, so we implement a stop strategy accordingly.

```java
/**
 * Allows execution for only 10 iterations
 */
public class CountingStopStrategy implements KaalTaskStopStrategy<SampleTask, Date> {

    @Getter
    private final AtomicInteger executionCount = new AtomicInteger();

    @Override
    public boolean scheduleNext(KaalTaskData<SampleTask, Date> taskData) {
        return executionCount.incrementAndGet() < 10;
    }
}
```

## Step 3 - Using the KaalScheduler

To use the scheduler, the following needs to be done:

### Building the scheduler

The scheduler class is called `KaalScheduler` and can be built using the provided builder.

```java
    scheduler=KaalScheduler.<SampleTask, Date>builder()
        .withTaskStopStrategy(stopStrategy)
        .build();
```

### Handling a task run completion

Every run of a `KaalTask` is uniquely identified using a run Id. The ID generation logic can be customised by
implementing the `KaalTaskRunIdGenerator` interface.

On completion of a run, a [ConsumingSyncSignal](https://github.com/appform-io/signals) dispatch is invoked with an
instance of a `KaalTaskData`. The info is available inside the instance:

* **task** - A reference to the Task instance
* **runId** - The id for the current run invocation time
* **targetExecutionTime** - Time at which task is supposed to be executed
* **actualStartTime** - Time at which task was actually executed
* **result** - Result of the execution if actually returned. Null if null is returned or task threw an exception
* **exception** - Reference to the throwable thrown by the task run if any, or null otherwise
* A method called `drift()` is available in the `TaskData` instance to calculate the time drift between actual and
  expected execution times in milliseconds

**NOTE** - It is highly recommended to register a handler and at the very least, log or handle any exceptions
thrown from the task run

Implement the handler like following:

```java
    private void handleTaskResult(KaalTaskData<SampleTask, Date> taskData){
        log.info("Received result from task run {}/{} is {}",
        taskData.getTask().id(),taskData.getRunId(),taskData.getResult());
        }
```

And connect it to the signal in the following manner:

```java
    scheduler.onTaskCompleted().connect(this::handleTaskResult);
```

## Starting the scheduler

The scheduler can be started by invoking the `scheudler.start()` method.

## Submitting tasks(s) to Kaal

You can submit your tasks to Kaal in the following manner:

```java
    scheduler.schedule(new SampleTask());
```

## Deleting tasks

To delete a task, you need to pass the same id returned by `id()` method of your `KaalTask` implementation.

```java
    schduler.delete("SAMPLE_TASK");
```

## Cleaning up all tasks

To clean up all tasks in Kaal, please call the `schduler.cleanup()` method.

## Stopping the scheduler

The scheduler can can be stopped by calling `schduler.stop()` method.

## Bringing it all together

The following class shows a concrete wrapper around the above steps:

```java

@Slf4j
public class SampleTaskRunner {
    private final KaalScheduler<SampleTask, Date> scheduler;
    private final CountingStopStrategy stopStrategy = new CountingStopStrategy();

    public SampleTaskRunner() {
        scheduler = KaalScheduler.<SampleTask, Date>builder()
                .withTaskStopStrategy(stopStrategy)
                .build();
        scheduler.onTaskCompleted().connect(this::handleTaskResult);
    }

    private void handleTaskResult(KaalTaskData<SampleTask, Date> taskData) {
        log.info("Received result from task run {}/{} is {}",
                 taskData.getTask().id(), taskData.getRunId(), taskData.getResult());
    }

    public void start() {
        scheduler.start();
        log.info("Scheduler started");
    }

    public void run() {
        scheduler.schedule(new SampleTask());
        await().atMost(Duration.ofSeconds(12))
                .pollInterval(Duration.ofSeconds(1))
                .until(() -> stopStrategy.getExecutionCount().get() == 11);
    }

    public void stop() {
        scheduler.stop();
    }

}
```

Finally, a test for the runner:

```java
class SampleTaskTest {

    @Test
    void sampleTest() {
        val runner = new SampleTaskRunner();
        runner.start();
        try {
            runner.run();
            await().atMost(Duration.ofSeconds(15)).until(runner::done);
        }
        finally {
            runner.stop();
        }
    }
}
```

## Customizations

* **Task Stop Strategy** - can be overridden to evaluate and stop a task from further runs. By default, task runs
  forever. A custom implementation of the stop strategy can be passed to the builder using the `withTaskStopStrategy()`
  method.
* **Run ID Generation** - every run of a KaalTask implementation gets a unique run id. This ID is an UUID by
  default. If more meaningful ID is needed, an implementation of `KaalTaskRunIdGenerator` can be provided to the
  builder using the `withTaskIdGenerator()` method.
* **Polling Interval** - By default Kaal checks the task queue for execution every 100 milliseconds. If lower or higher
  resolution is needed, pass the same by using the `pollingInterval()` method in the builder.
* **Executor Service** - By default Kaal uses an unbounded cached thread pool that ensures that tasks always get a
  thread to execute. A custom executor service can be provided to Kaal using the `withExecutorService()` method of the
  builder.

## Drift and polling interval

Drift is caused due to the polling interval. For example: if a task is supposed to be run at time 1:00 pm,
the polling interval is 1 second, there might be a maximum drift of approx 1 second before th job kicks in.

Additionally, there is no concept of interval in Kaal. Once a run is complete, the next run of a task will be scheduled
at a delay returned by `KaalTask::delayToNextRun(currentDate)` and will be adjusted by the drift amount. the scheduling,
therefore, may or may not be uniform and can be precisely controlled by the task implementation, providing a lot of
flexibility in the implementation. this behaviour can be used to run tasks at fixed intervals, exponential backoff or
whatever suits the use-case.

Please make sure the delay returned by `delayToNextRun()` is more than the polling interval. As can be guessed, an
execution delay of less that polling interval will cause undefined behaviour.

## License

Apache 2