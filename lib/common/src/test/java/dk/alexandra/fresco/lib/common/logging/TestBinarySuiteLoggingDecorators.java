package dk.alexandra.fresco.lib.common.logging;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import dk.alexandra.fresco.framework.ProtocolEvaluator;
import dk.alexandra.fresco.framework.TestThreadRunner;
import dk.alexandra.fresco.framework.builder.binary.ProtocolBuilderBinary;
import dk.alexandra.fresco.framework.configuration.NetworkConfiguration;
import dk.alexandra.fresco.framework.configuration.NetworkUtil;
import dk.alexandra.fresco.framework.network.socket.SocketNetwork;
import dk.alexandra.fresco.framework.sce.SecureComputationEngine;
import dk.alexandra.fresco.framework.sce.SecureComputationEngineImpl;
import dk.alexandra.fresco.framework.sce.evaluator.BatchEvaluationStrategy;
import dk.alexandra.fresco.framework.sce.evaluator.BatchedProtocolEvaluator;
import dk.alexandra.fresco.framework.sce.evaluator.EvaluationStrategy;
import dk.alexandra.fresco.framework.sce.resources.ResourcePoolImpl;
import dk.alexandra.fresco.lib.common.compare.ComparisonBooleanTests;
import dk.alexandra.fresco.logging.BinarySuiteLogging;
import dk.alexandra.fresco.logging.PerformanceLogger;
import dk.alexandra.fresco.logging.binary.BinaryLoggingDecorator;
import dk.alexandra.fresco.suite.dummy.bool.DummyBooleanProtocolSuite;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class TestBinarySuiteLoggingDecorators {

  // Tests that a < b and b !< a for some 5-bit a and b
  private final long bitLength = 5;
  private final long amountOfGts = 2 * 1;
  private final long amountOfAndsInTest = amountOfGts * bitLength;
  private final long amountOfXorsInTest = amountOfGts * (1 + (bitLength - 1) * 3);

  @Test
  public void testBinaryLoggingDecorator() {

    TestThreadRunner.TestThreadFactory<ResourcePoolImpl, ProtocolBuilderBinary> f =
        new ComparisonBooleanTests.TestGreaterThan<>();

    int noOfParties = 2;
    EvaluationStrategy evalStrategy = EvaluationStrategy.SEQUENTIAL;

    List<Integer> ports = new ArrayList<>(noOfParties);
    for (int i = 1; i <= noOfParties; i++) {
      ports.add(9000 + i * (noOfParties - 1));
    }

    Map<Integer, NetworkConfiguration> netConf =
        NetworkUtil.getNetworkConfigurations(ports);
    Map<Integer,
        TestThreadRunner.TestThreadConfiguration<ResourcePoolImpl, ProtocolBuilderBinary>> conf =
        new HashMap<>();

    Map<Integer, BinarySuiteLogging<ResourcePoolImpl>> performanceLoggers = new HashMap<>();

    for (int playerId : netConf.keySet()) {
      NetworkConfiguration partyNetConf = netConf.get(playerId);
      BinarySuiteLogging<ResourcePoolImpl> ps =
          new BinarySuiteLogging<>(new DummyBooleanProtocolSuite());
      performanceLoggers.put(playerId, ps);

      BatchEvaluationStrategy<ResourcePoolImpl> strat = evalStrategy.getStrategy();

      ProtocolEvaluator<ResourcePoolImpl> evaluator = new BatchedProtocolEvaluator<>(strat, ps);
      SecureComputationEngine<ResourcePoolImpl, ProtocolBuilderBinary> sce =
          new SecureComputationEngineImpl<>(ps, evaluator);

      TestThreadRunner.TestThreadConfiguration<ResourcePoolImpl, ProtocolBuilderBinary> ttc =
          new TestThreadRunner.TestThreadConfiguration<>(sce,
              () -> new ResourcePoolImpl(playerId, noOfParties),
              () -> new SocketNetwork(partyNetConf));
      conf.put(playerId, ttc);
    }
    TestThreadRunner.run(f, conf);

    for (Integer id : netConf.keySet()) {
      PerformanceLogger pl = performanceLoggers.get(id);
      Map<String, Long> loggedValues = pl.getLoggedValues();
      assertThat(loggedValues.get(BinaryLoggingDecorator.BINARY_BASIC_XOR), is(amountOfXorsInTest));
      assertThat(loggedValues.get(BinaryLoggingDecorator.BINARY_BASIC_AND), is(amountOfAndsInTest));
      assertThat(loggedValues.get(BinaryLoggingDecorator.BINARY_BASIC_RANDOM), is((long) 0));

      pl.reset();
      loggedValues = pl.getLoggedValues();
      assertThat(loggedValues.get(BinaryLoggingDecorator.BINARY_BASIC_XOR), is((long) 0));
      assertThat(loggedValues.get(BinaryLoggingDecorator.BINARY_BASIC_AND), is((long) 0));
      assertThat(loggedValues.get(BinaryLoggingDecorator.BINARY_BASIC_RANDOM), is((long) 0));
    }
  }
}
