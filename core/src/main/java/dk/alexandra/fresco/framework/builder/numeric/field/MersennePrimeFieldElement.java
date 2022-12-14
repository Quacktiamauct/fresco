package dk.alexandra.fresco.framework.builder.numeric.field;

import dk.alexandra.fresco.framework.util.MathUtils;
import java.math.BigInteger;

final class MersennePrimeFieldElement implements FieldElement {

  private final BigInteger value;
  private final MersennePrimeModulus modulus;

  private MersennePrimeFieldElement(BigInteger value, MersennePrimeModulus modulus) {
    if (value.signum() < 0) {
      BigInteger positiveValue = modulus.ensureInField(value.abs());
      this.value = modulus.getPrime().subtract(positiveValue);
    } else {
      this.value = modulus.ensureInField(value);
    }
    this.modulus = modulus;
  }

  private FieldElement create(BigInteger value) {
    return new MersennePrimeFieldElement(value, modulus);
  }

  static FieldElement create(BigInteger value, MersennePrimeModulus modulus) {
    return new MersennePrimeFieldElement(value, modulus);
  }

  static FieldElement create(long value, MersennePrimeModulus modulus) {
    return create(BigInteger.valueOf(value), modulus);
  }

  static FieldElement create(String string, MersennePrimeModulus modulus) {
    return create(new BigInteger(string), modulus);
  }

  @Override
  public FieldElement add(FieldElement operand) {
    return create(value.add(operand.toBigInteger()));
  }

  @Override
  public FieldElement subtract(FieldElement operand) {
    return create(value.subtract(operand.toBigInteger()));
  }

  @Override
  public FieldElement negate() {
    return create(getModulus().subtract(value));
  }

  @Override
  public FieldElement multiply(FieldElement operand) {
    return create(value.multiply(operand.toBigInteger()));
  }

  @Override
  public FieldElement sqrt() {
    return create(MathUtils.modularSqrt(value, getModulus()));
  }

  @Override
  public FieldElement modInverse() {
    return create(modulus.inverse(value));
  }

  @Override
  public boolean isZero() {
    return BigInteger.ZERO.equals(value);
  }

  @Override
  public BigInteger toBigInteger() {
    return value;
  }

  private BigInteger getModulus() {
    return modulus.getPrime();
  }

  @Override
  public String toString() {
    return "MersennePrimeFieldElement{"
        + "value=" + value
        + ", modulus =" + modulus
        + '}';
  }
}
