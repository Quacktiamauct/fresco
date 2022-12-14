package dk.alexandra.fresco.tools.bitTriples;

import dk.alexandra.fresco.framework.util.ExceptionConverter;
import dk.alexandra.fresco.framework.util.Pair;
import java.io.Closeable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class TestRuntime {

  private Map<Integer, BitTriplesTestContext> contexts;
  private ExecutorService executor;
  private boolean executorInitialized;
  private long timeout;

  /**
   * Creates new test runtime.
   */
  TestRuntime() {
    this.contexts = new HashMap<>();
    this.executor = null;
    this.executorInitialized = false;
    this.timeout = 1200L;
  }

  /**
   * Closes the networks on the contexts and shuts down the executor. <br> Call this after test.
   */
  public void shutdown() {
    if (!executorInitialized) {
      throw new IllegalStateException("Executor not initialized, nothing to shut down.");
    }
    executorInitialized = false;
    for (BitTriplesTestContext context : contexts.values()) {
      ExceptionConverter.safe(() -> {
        ((Closeable) context.getNetwork()).close();
        return null;
      }, "Closing network failed");
    }
    executor.shutdown();
    ExceptionConverter.safe(() -> {
      executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
      return null;
    }, "Executor shutdown failed");
  }

  /**
   * Creates a new executor service with fixed-size thread pool.
   *
   * @param noOfParties number of threads in thread pool (one per party)
   */
  private void initializeExecutor(int noOfParties) {
    if (executorInitialized) {
      throw new IllegalStateException("Executor already initialized");
    }
    executorInitialized = true;
    executor = Executors.newFixedThreadPool(noOfParties);
  }

  /**
   * Invokes tasks and unwraps futures. <br> Uses {@link ExceptionConverter#safe(Callable, String)}
   * to convert checked exceptions.
   *
   * @param tasks task to invoke
   * @return results of tasks
   */
  private <T> List<T> safeInvokeAll(List<Callable<T>> tasks) {
    Callable<List<Future<T>>> runAll = () -> executor.invokeAll(tasks, timeout, TimeUnit.SECONDS);
    List<Future<T>> futures = ExceptionConverter.safe(runAll, "Invoke all failed");
    return futures.stream().map(future -> ExceptionConverter.safe(future::get, "Party task failed"))
        .collect(Collectors.toList());
  }

  /**
   * Given a ready executor, creates as BitTriple test context for each party.
   */
  public Map<Integer, BitTriplesTestContext> initializeContexts(
      int noOfParties, int instanceId,
      BitTripleSecurityParameters securityParameters) {
    initializeExecutor(noOfParties);
    List<Callable<Pair<Integer, BitTriplesTestContext>>> initializationTasks = new LinkedList<>();
    for (int partyId = 1; partyId <= noOfParties; partyId++) {
      int finalPartyId = partyId;
      initializationTasks.add(() -> initializeContext(finalPartyId, noOfParties,
          instanceId, securityParameters));
    }
    for (Pair<Integer, BitTriplesTestContext> pair : safeInvokeAll(initializationTasks)) {
      contexts.put(pair.getFirst(), pair.getSecond());
    }
    return contexts;
  }

  /**
   * Runs the task defined for each party. <br> Currently assumes that all parties receive the same
   * type of output. This method assumes that tasks are ordered by party.
   *
   * @param tasks tasks to run
   * @return result of tasks
   */
  public <T> List<T> runPerPartyTasks(List<Callable<T>> tasks) {
    if (!executorInitialized) {
      throw new IllegalStateException("Executor not initialized yet");
    }
    return safeInvokeAll(tasks);
  }

  /**
   * Initializes a single context for a party.
   */
  private Pair<Integer, BitTriplesTestContext> initializeContext(int myId, int noOfParties,
      int instanceId, BitTripleSecurityParameters securityParameters) {
    BitTriplesTestContext ctx = new BitTriplesTestContext(myId, noOfParties, instanceId,
        securityParameters);
    return new Pair<>(myId, ctx);
  }

  /**
   * Check if executor has been initialized.
   *
   * @return is initialized
   */
  public boolean isExecutorInitialized() {
    return executorInitialized;
  }

}
