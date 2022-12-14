package dk.alexandra.fresco.tools.mascot;

import dk.alexandra.fresco.framework.Party;
import dk.alexandra.fresco.framework.builder.numeric.field.BigIntegerFieldDefinition;
import dk.alexandra.fresco.framework.builder.numeric.field.FieldDefinition;
import dk.alexandra.fresco.framework.builder.numeric.field.FieldElement;
import dk.alexandra.fresco.framework.configuration.NetworkConfiguration;
import dk.alexandra.fresco.framework.configuration.NetworkConfigurationImpl;
import dk.alexandra.fresco.framework.network.Network;
import dk.alexandra.fresco.framework.network.socket.SocketNetwork;
import dk.alexandra.fresco.framework.util.AesCtrDrbgFactory;
import dk.alexandra.fresco.framework.util.Drbg;
import dk.alexandra.fresco.framework.util.ExceptionConverter;
import dk.alexandra.fresco.framework.util.ModulusFinder;
import dk.alexandra.fresco.tools.mascot.field.MultiplicationTriple;
import dk.alexandra.fresco.tools.ot.base.BigIntNaorPinkas;
import dk.alexandra.fresco.tools.ot.base.Ot;
import dk.alexandra.fresco.tools.ot.otextension.RotList;
import java.io.Closeable;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class MascotDemo {

  private final Mascot mascot;
  private final Closeable toClose;
  private final MascotSecurityParameters parameters = new MascotSecurityParameters();
  private final FieldDefinition fieldDefinition =
      new BigIntegerFieldDefinition(ModulusFinder.findSuitableModulus(128));

  private MascotDemo(int myId, int noOfParties) {
    Network network =
        new SocketNetwork(defaultNetworkConfiguration(myId, noOfParties));
    MascotResourcePool resourcePool = defaultResourcePool(myId, noOfParties,
        network);
    FieldElement macKeyShare = resourcePool.getLocalSampler().getNext();
    toClose = (Closeable) network;
    mascot = new Mascot(resourcePool, network, macKeyShare);
  }

  private void run(int numIts, int numTriples) {
    for (int i = 0; i < numIts; i++) {
      System.out.println("Generating another triple batch.");
      long startTime = System.currentTimeMillis();
      List<MultiplicationTriple> triples = mascot.getTriples(numTriples);
      long endTime = System.currentTimeMillis();
      long total = endTime - startTime;
      System.out.println("Generated " + triples.size() + " triples in " + total + " ms");
    }
    Callable<Void> closeTask = () -> {
      toClose.close();
      return null;
    };
    ExceptionConverter.safe(closeTask, "Failed closing network");
  }

  private NetworkConfiguration defaultNetworkConfiguration(int myId, int noOfParties) {
    Map<Integer, Party> parties = new HashMap<>();
    for (int partyId = 1; partyId <= noOfParties; partyId++) {
      parties.put(partyId, new Party(partyId, "localhost", 8005 + partyId));
    }
    return new NetworkConfigurationImpl(myId, parties);
  }

  private MascotResourcePool defaultResourcePool(int myId, int noOfParties,
      Network network) {
    // generate random seed for local DRBG
    byte[] drbgSeed = new byte[parameters.getPrgSeedLength() / 8];
    new SecureRandom().nextBytes(drbgSeed);
    Drbg drbg = AesCtrDrbgFactory.fromDerivedSeed(drbgSeed);
    Map<Integer, RotList> seedOts = new HashMap<>();
    for (int otherId = 1; otherId <= noOfParties; otherId++) {
      if (myId != otherId) {
        Ot ot = new BigIntNaorPinkas(otherId, drbg, network);
        RotList currentSeedOts = new RotList(drbg, parameters.getPrgSeedLength());
        if (myId < otherId) {
          currentSeedOts.send(ot);
          currentSeedOts.receive(ot);
        } else {
          currentSeedOts.receive(ot);
          currentSeedOts.send(ot);
        }
        seedOts.put(otherId, currentSeedOts);
      }
    }
    int instanceId = 1;
    return new MascotResourcePoolImpl(
        myId, noOfParties, instanceId, drbg, seedOts, parameters, fieldDefinition);
  }

  /**
   * Runs demo.
   */
  public static void main(String[] args) {
    int myId = Integer.parseInt(args[0]);
    new MascotDemo(myId, 2).run(1, 9 * 1024);
  }
}
