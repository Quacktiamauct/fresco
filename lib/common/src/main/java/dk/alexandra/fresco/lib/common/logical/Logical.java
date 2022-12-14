package dk.alexandra.fresco.lib.common.logical;

import dk.alexandra.fresco.framework.DRes;
import dk.alexandra.fresco.framework.builder.ComputationDirectory;
import dk.alexandra.fresco.framework.builder.numeric.ProtocolBuilderNumeric;
import dk.alexandra.fresco.framework.value.SInt;
import java.math.BigInteger;
import java.util.List;

/**
 * Logical operators on secret arithmetic representations of boolean values. <p>NOTE: all inputs are
 * assumed to represent 0 or 1 values only. The result is undefined if other values are passed
 * in.</p>
 */
public interface Logical extends ComputationDirectory {
  // TODO: this is starting to look a lot like the Binary computation directory...

  public static Logical using(ProtocolBuilderNumeric builder) {
    return new DefaultLogical(builder);
  }

  /**
   * Computes logical AND of inputs. <p>NOTE: Inputs must represent 0 or 1 values only.</p>
   */
  DRes<SInt> and(DRes<SInt> bitA, DRes<SInt> bitB);

  /**
   * Computes logical OR of inputs. <p>NOTE: Inputs must represent 0 or 1 values only.</p>
   */
  DRes<SInt> or(DRes<SInt> bitA, DRes<SInt> bitB);

  /**
   * Computes logical OR of inputs but is only safe to use when at least one of the bits is 0.
   * <p>This allows to express and or as a linear operation and therefore far more efficient.</p>
   */
  DRes<SInt> halfOr(DRes<SInt> bitA, DRes<SInt> bitB);

  /**
   * Computes logical XOR of inputs. <p>NOTE: Inputs must represent 0 or 1 values only.</p>
   */
  DRes<SInt> xor(DRes<SInt> bitA, DRes<SInt> bitB);

  /**
   * Computes logical AND of inputs. <p>NOTE: Inputs must represent 0 or 1 values only.</p>
   */
  DRes<SInt> andKnown(BigInteger knownBit, DRes<SInt> secretBit);

  /**
   * Computes logical XOR of inputs. <p>NOTE: Inputs must represent 0 or 1 values only.</p>
   */
  DRes<SInt> xorKnown(BigInteger knownBit, DRes<SInt> secretBit);

  /**
   * Computes logical NOT of input. <p>NOTE: Input must represent 0 or 1 values only.</p>
   */
  DRes<SInt> not(DRes<SInt> secretBit);

  /**
   * Opens secret bits, possibly performing conversion before producing final open value. <p>NOTE:
   * Input must represent 0 or 1 values only.</p>
   */
  DRes<BigInteger> openAsBit(DRes<SInt> secretBit);

  /**
   * Batch opening of bits.
   */
  DRes<List<DRes<BigInteger>>> openAsBits(DRes<List<DRes<SInt>>> secretBits);

  /**
   * Negates all given bits.
   */
  DRes<List<DRes<SInt>>> batchedNot(DRes<List<DRes<SInt>>> bits);

  /**
   * Computes pairwise logical AND of input bits. <p>NOTE: Inputs must represent 0 or 1 values
   * only.</p>
   */
  DRes<List<DRes<SInt>>> pairWiseAndKnown(List<BigInteger> knownBits,
      DRes<List<DRes<SInt>>> secretBits);

  /**
   * Computes pairwise logical AND of input bits. <p>NOTE: Inputs must represent 0 or 1 values
   * only.</p>
   */
  DRes<List<DRes<SInt>>> pairWiseAnd(DRes<List<DRes<SInt>>> bitsA,
      DRes<List<DRes<SInt>>> bitsB);

  /**
   * Computes pairwise logical OR of input bits. <p>NOTE: Inputs must represent 0 or 1 values
   * only.</p>
   */
  DRes<List<DRes<SInt>>> pairWiseOr(DRes<List<DRes<SInt>>> bitsA,
      DRes<List<DRes<SInt>>> bitsB);

  /**
   * Computes pairwise logical XOR of input bits. <p>NOTE: Inputs must represent 0 or 1 values
   * only.</p>
   */
  DRes<List<DRes<SInt>>> pairWiseXor(DRes<List<DRes<SInt>>> bitsA,
      DRes<List<DRes<SInt>>> bitsB);

  /**
   * Computes pairwise logical XOR of input bits. <p>NOTE: Inputs must represent 0 or 1 values
   * only.</p>
   */
  DRes<List<DRes<SInt>>> pairWiseXorKnown(List<BigInteger> knownBits,
      DRes<List<DRes<SInt>>> secretBits);

  /**
   * Computes logical OR of all input bits. <p> NOTE: Inputs must represent 0 or 1 values only.
   * </p>
   */
  DRes<SInt> orOfList(DRes<List<DRes<SInt>>> bits);

  /**
   * Given a list of bits, computes or of each neighbor pair of bits, i.e., given b1, b2, b3, b4,
   * will output b1 OR b2, b3 OR b4. <p>Also handles uneven number of elements.</p>
   */
  DRes<List<DRes<SInt>>> orNeighbors(List<DRes<SInt>> bits);

}