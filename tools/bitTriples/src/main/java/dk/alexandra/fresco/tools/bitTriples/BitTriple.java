package dk.alexandra.fresco.tools.bitTriples;

import dk.alexandra.fresco.framework.network.Network;
import dk.alexandra.fresco.framework.util.StrictBitVector;
import dk.alexandra.fresco.tools.bitTriples.cointossing.CoinTossingMpc;
import dk.alexandra.fresco.tools.bitTriples.elements.MultiplicationTriple;
import dk.alexandra.fresco.tools.bitTriples.prg.BytePrg;
import dk.alexandra.fresco.tools.bitTriples.prg.BytePrgImpl;
import dk.alexandra.fresco.tools.bitTriples.triple.TripleGeneration;
import java.util.List;

/**
 * Implementation of the main MASCOT protocol (<a href="https://eprint.iacr.org/2016/505.pdf">https://eprint.iacr.org/2016/505.pdf</a>)
 * which can be used for the SPDZ pre-processing phase. <br> Supports generation of multiplication
 * triples, random authenticated elements, and random authenticated bits.
 */
public class BitTriple {

  private final TripleGeneration tripleGeneration;
  //private final BitConverter bitConverter;
  private final BitTripleResourcePool resourcePool;

  /**
   * Creates new {@link BitTriple}.
   */
  public BitTriple(BitTripleResourcePool resourcePool, Network network, StrictBitVector macKeyShareLeft,StrictBitVector macKeyShareRight) {
    this.resourcePool = resourcePool;
    // agree on joint seed
    StrictBitVector jointSeed = new CoinTossingMpc(resourcePool, network)
        .generateJointSeed(resourcePool.getPrgSeedBitLength());
    BytePrg jointSampler = new BytePrgImpl(jointSeed);
    this.tripleGeneration =
        new TripleGeneration(
            resourcePool, network, resourcePool.getComputationalSecurityBitParameter(), jointSampler, macKeyShareLeft, macKeyShareRight);
  }

  /**
   * Generates a batch of multiplication triples.
   *
   * It only possible to generate a set number of triples, dependant on the statistical security parameter(SSP):
   *  SSP | Number of triples
   *  40  | 163 | 2047 | 16389 | 223016 | 1864191
   *  60  | 63  | 511  | 7281  | 83885  | 671088 | 5368708
   *
   * @param numTriples number of triples in batch
   * @return multiplication triples
   */
  public List<MultiplicationTriple> getTriples(int numTriples) {
    return tripleGeneration.triple(triplesToCreate(numTriples,resourcePool.getStatisticalSecurityByteParameter()));
  }

  protected static int triplesToCreate(int atLeast, int securityParameter){
    if (securityParameter <= 40) {
      if (atLeast <= 163) {
        return 163;
      } else if (atLeast <= 2047) {
        return 2047;
      } else if (atLeast <= 16389) {
        return 16389;
      } else if (atLeast <= 233016) {
        return 233016;
      } else if (atLeast <= 1864191){
        return 1864191;
      } else {
        throw new IllegalArgumentException("The number of triples should be below 1864191, "
            + "if the statistical security parameter is 40");
      }
    } else {
      if (atLeast <= 63) {
        return 63;
      } else if (atLeast <= 511) {
        return 511;
      } else if (atLeast <= 7281) {
        return 7281;
      } else if (atLeast <= 83885) {
        return 83885;
      } else if (atLeast <= 671088) {
        return 671088;
      } else if (atLeast <= 5368708) {
        return 5368708;
      } else {
        throw new IllegalArgumentException("The number of triples should be below 5368708, "
            + "if the statistical security parameter is 64");
      }
    }
  }
}
