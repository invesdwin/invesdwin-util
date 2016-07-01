package de.invesdwin.util.math.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.math3.dfp.Dfp;
import org.assertj.core.description.TextDescription;

import de.invesdwin.norva.marker.IDecimal;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.decimal.internal.impl.ADecimalImpl;

@Immutable
public abstract class ADecimal<E extends ADecimal<E>> extends Number implements Comparable<Object>, IDecimal {

    /**
     * Using HALF_UP here for commercial rounding.
     */
    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;
    public static final int DEFAULT_ROUNDING_SCALE = 9;
    public static final ADelegateComparator<ADecimal<?>> COMPARATOR = new ADelegateComparator<ADecimal<?>>() {
        @Override
        protected Comparable<?> getCompareCriteria(final ADecimal<?> e) {
            return e;
        }
    };

    public abstract ADecimalImpl getImpl();

    /**
     * http://stackoverflow.com/questions/2170872/does-java-casting-introduce-overhead-why
     */
    protected abstract E getGenericThis();

    protected abstract E newValueCopy(ADecimalImpl value);

    public abstract E fromDefaultValue(Decimal value);

    public abstract Decimal getDefaultValue();

    @Override
    public int hashCode() {
        return Objects.hashCode(getClass(), getImpl());
    }

    @Override
    public int compareTo(final Object other) {
        return getImpl().compareTo(other);
    }

    @Override
    public boolean equals(final Object other) {
        return other != null && getClass().isAssignableFrom(other.getClass()) && Objects.equals(getImpl(), other);
    }

    @Override
    public String toString() {
        return getImpl().toString();
    }

    @Override
    public int intValue() {
        return getImpl().intValue();
    }

    @Override
    public long longValue() {
        return getImpl().longValue();
    }

    @Override
    public float floatValue() {
        return getImpl().floatValue();
    }

    @Override
    public double doubleValue() {
        return getImpl().doubleValue();
    }

    /**
     * This gives the raw value, thus not getting rounded for precision.
     */
    public double doubleValueRaw() {
        return getImpl().doubleValueRaw();
    }

    @Override
    public byte byteValue() {
        return getImpl().byteValue();
    }

    @Override
    public short shortValue() {
        return getImpl().shortValue();
    }

    public BigDecimal bigDecimalValue() {
        return getImpl().bigDecimalValue();
    }

    public BigInteger bigIntegerValue() {
        return getImpl().bigIntegerValue();
    }

    public Dfp dfpValue() {
        return getImpl().dfpValue();
    }

    public Number numberValue() {
        return getImpl().numberValue();
    }

    /**
     * This value denotes how many digits are after the decimal point. E.g.: 12.0001 results in 4
     * 
     * Also called decimal scale.
     * 
     * Returns the real scale without trailing zeros.
     * 
     * WARNING: make sure this values gets cached, because this operation causes a large performance overhead!
     */
    public int getDecimalDigits() {
        return getImpl().getDecimalDigits();
    }

    /**
     * This value denotes how many digits are before the decimal point. E.g.: 12.0001 results in 2
     * 
     * Also called decimal precision minus decimal scale.
     * 
     * Returns the real scale without trailing zeros.
     * 
     * WARNING: make sure this values gets cached, because this operation causes a large performance overhead!
     */
    public int getWholeNumberDigits() {
        return getImpl().getWholeNumberDigits();
    }

    /**
     * This value denotes how many digits are after the decimal point. E.g.: 12.0001 results in 6
     * 
     * Also called decimal precision.
     * 
     * Returns the real precision without trailing zeros.
     * 
     * WARNING: make sure this values gets cached, because this operation causes a large performance overhead!
     */
    public int getDigits() {
        return getImpl().getDigits();
    }

    public boolean isZero() {
        return getImpl().isZero();
    }

    public final boolean isNotZero() {
        return !isZero();
    }

    /**
     * 0 is counted as positive aswell here to make things simpler.
     */
    public boolean isPositive() {
        return getImpl().isPositive();
    }

    /**
     * This one excludes 0 from positive.
     */
    public boolean isPositiveNonZero() {
        return isPositive() && !isZero();
    }

    public boolean isNegative() {
        return !isPositive();
    }

    public boolean isNegativeOrZero() {
        return !isPositiveNonZero();
    }

    public boolean isGreaterThan(final Number o) {
        if (o == null) {
            return false;
        }
        return compareTo(o) > 0;
    }

    public boolean isGreaterThanOrEqualTo(final Number o) {
        return !isLessThan(o);
    }

    public boolean isLessThan(final Number o) {
        if (o == null) {
            return false;
        }
        return compareTo(o) < 0;
    }

    public boolean isLessThanOrEqualTo(final Number o) {
        return !isGreaterThan(o);
    }

    public boolean isBetween(final Number lowerBound, final Number upperBound) {
        return isGreaterThanOrEqualTo(lowerBound) && isLessThanOrEqualTo(upperBound);
    }

    public E scaleByPowerOfTen(final int n) {
        return newValueCopy(getImpl().scaleByPowerOfTen(n));
    }

    public E round() {
        return round(DEFAULT_ROUNDING_SCALE);
    }

    public E round(final RoundingMode roundingMode) {
        return round(DEFAULT_ROUNDING_SCALE, roundingMode);
    }

    public E round(final int scale) {
        return round(scale, DEFAULT_ROUNDING_MODE);
    }

    public E round(final int scale, final RoundingMode roundingMode) {
        return newValueCopy(getImpl().round(scale, roundingMode));
    }

    /**
     * <ul>
     * <li>[growth rate] = ( [current] - [previous] ) / |[previous]|</li>
     * <ul>
     * 
     * @see <a href="http://www.chemieonline.de/forum/showthread.php?t=101560">Source</a>
     */
    public E growthRate(final E nextValue) {
        return nextValue.subtract(getGenericThis()).divide(abs());
    }

    public E abs() {
        return newValueCopy(getImpl().abs());
    }

    public E subtract(final E subtrahend) {
        if (subtrahend == null) {
            return getGenericThis();
        }
        return newValueCopy(getImpl().subtract(subtrahend));
    }

    public E add(final E augend) {
        if (augend == null) {
            return getGenericThis();
        }
        return newValueCopy(getImpl().add(augend));
    }

    public E multiply(final E multiplicant) {
        if (multiplicant == null) {
            return multiply(0);
        } else {
            return newValueCopy(getImpl().multiply(multiplicant));
        }
    }

    /**
     * returns the remainder of the division.
     */
    public E remainder(final E divisor) {
        if (divisor == null || divisor.isZero()) {
            return remainder(0);
        } else {
            return newValueCopy(getImpl().remainder(divisor));
        }
    }

    public E remainder(final Number divisor) {
        if (divisor == null || divisor.doubleValue() == 0D) {
            //results in 0, thus multiply by 0
            return newValueCopy(getImpl().remainder(0));
        } else {
            if (divisor instanceof AScaledDecimal) {
                throw new IllegalArgumentException(new TextDescription(
                        "Division between different types of %ss [%s=%s / %s=%s] does not make any sense.",
                        AScaledDecimal.class.getSimpleName(), this.getClass().getSimpleName(), this,
                        divisor.getClass().getSimpleName(), divisor).toString());
            }
            return newValueCopy(getImpl().remainder(divisor));
        }
    }

    /**
     * If the divisor is 0, 0 is returned. This goes against the mathematical rules, but makes a developers life easier.
     */
    public E divide(final E divisor) {
        if (divisor == null || divisor.isZero()) {
            return divide(0);
        } else {
            return newValueCopy(getImpl().divide(divisor));
        }
    }

    public E multiply(final Number multiplicant) {
        if (multiplicant == null) {
            return newValueCopy(getImpl().multiply(0));
        } else {
            if (multiplicant instanceof AScaledDecimal) {
                throw new IllegalArgumentException(new TextDescription(
                        "Multiplication between different types of %ss [%s=%s * %s=%s] does not make any sense.",
                        AScaledDecimal.class.getSimpleName(), this.getClass().getSimpleName(), this,
                        multiplicant.getClass().getSimpleName(), multiplicant).toString());
            }
            return newValueCopy(getImpl().multiply(multiplicant));
        }
    }

    /**
     * If the divisor is 0, 0 is returned. This goes against the mathematical rules, but makes a developers life easier.
     */
    public E divide(final Number divisor) {
        if (divisor == null || divisor.doubleValue() == 0D) {
            //results in 0, thus multiply by 0
            return newValueCopy(getImpl().multiply(0));
        } else {
            if (divisor instanceof AScaledDecimal) {
                throw new IllegalArgumentException(new TextDescription(
                        "Division between different types of %ss [%s=%s / %s=%s] does not make any sense.",
                        AScaledDecimal.class.getSimpleName(), this.getClass().getSimpleName(), this,
                        divisor.getClass().getSimpleName(), divisor).toString());
            }
            return newValueCopy(getImpl().divide(divisor));
        }
    }

    /**
     * With a step of 0.5: (Math.ceil(x * 2) / 2)
     */
    public E roundToStep(final E step) {
        return roundToStep(step, DEFAULT_ROUNDING_MODE);
    }

    public E roundToStep(final E step, final RoundingMode roundingMode) {
        final E stepReciprocal = step.reciprocal();
        return multiply(stepReciprocal).round(0, roundingMode).divide(stepReciprocal);
    }

    public E reciprocal() {
        return newValueCopy(Decimal.ONE.getImpl()).divide(getGenericThis());
    }

    public E orHigher(final E other) {
        if (other == null) {
            return getGenericThis();
        }

        if (compareTo(other) > 0) {
            return getGenericThis();
        } else {
            return other;
        }
    }

    public E orLower(final E other) {
        if (other == null) {
            return getGenericThis();
        }

        if (compareTo(other) < 0) {
            return getGenericThis();
        } else {
            return other;
        }
    }

    public E between(final E min, final E max) {
        if (min.isGreaterThan(max)) {
            throw new IllegalArgumentException("min [" + min + "] must not be larger than max [" + max + "]");
        }
        return orLower(max).orHigher(min);
    }

    public E pow(final E exponent) {
        return newValueCopy(getImpl().pow(exponent));
    }

    public E pow(final int exponent) {
        return newValueCopy(getImpl().pow(exponent));
    }

    public E sqrt() {
        return newValueCopy(getImpl().sqrt());
    }

    /**
     * Root = Value^1/n
     * 
     * @see <a href="http://www.ee.ucl.ac.uk/~mflanaga/java/Stat.html#geom2">Source with BigDecimal</a>
     */
    public E root(final int n) {
        return newValueCopy(getImpl().root(n));
    }

    public E distance(final E to) {
        return subtract(to).abs();
    }

    public E negate() {
        return multiply(-1);
    }

    public String getSign() {
        return getSign(false);
    }

    public String getSignInverted() {
        return getSign(true);
    }

    public String getSign(final boolean inverted) {
        if (isPositive()) {
            if (!inverted) {
                return "+";
            } else {
                return "-";
            }
        } else {
            if (!inverted) {
                return "-";
            } else {
                return "+";
            }
        }
    }

    public static <T extends ADecimal<T>> T zeroToNull(final T value) {
        if (value == null || value.isZero()) {
            return (T) null;
        } else {
            return value;
        }
    }

}