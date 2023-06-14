package de.invesdwin.util.math.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.marker.IDecimal;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.lang.string.description.TextDescription;
import de.invesdwin.util.math.BigDecimals;
import de.invesdwin.util.math.BigIntegers;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Floats;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.math.Shorts;
import de.invesdwin.util.math.decimal.internal.DecimalDigitsInfo;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;
import io.netty.util.concurrent.FastThreadLocal;

@Immutable
public abstract class ADecimal<E extends ADecimal<E>> extends Number implements Comparable<Object>, IDecimal {

    public static final String NEGATIVE_SIGN = "-";
    public static final String POSITIVE_SIGN = "+";
    /**
     * Using HALF_UP here for commercial rounding.
     */
    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;
    public static final int DEFAULT_ROUNDING_SCALE = 9;
    public static final IComparator<ADecimal<?>> COMPARATOR = IComparator.getDefaultInstance();
    private static final FastThreadLocal<NumberFormat> NUMBER_FORMAT = new FastThreadLocal<NumberFormat>() {
        @Override
        protected NumberFormat initialValue() throws Exception {
            final NumberFormat format = NumberFormat.getNumberInstance(Locale.ENGLISH);
            format.setMaximumFractionDigits(MathContext.DECIMAL128.getPrecision());
            format.setRoundingMode(Decimal.DEFAULT_ROUNDING_MODE);
            format.setGroupingUsed(false);
            return format;
        }
    };

    /**
     * http://stackoverflow.com/questions/2170872/does-java-casting-introduce-overhead-why
     */
    protected abstract E getGenericThis();

    protected abstract E newValueCopy(double value);

    public abstract E fromDefaultValue(double value);

    public List<E> fromDefaultValueVector(final List<Double> vector) {
        if (vector == null) {
            return null;
        }
        final List<E> converted = new ArrayList<E>(vector.size());
        for (final Double value : vector) {
            converted.add(fromDefaultValue(value));
        }
        return converted;
    }

    public List<List<E>> fromDefaultValueMatrix(final List<List<Double>> matrix) {
        if (matrix == null) {
            return null;
        }
        final List<List<E>> converted = new ArrayList<List<E>>(matrix.size());
        for (final List<Double> vector : matrix) {
            converted.add(fromDefaultValueVector(vector));
        }
        return converted;
    }

    @SuppressWarnings("unchecked")
    public E[] fromDefaultValueVector(final double[] vector) {
        if (vector == null) {
            return null;
        }
        final E[] destination = (E[]) Arrays.newInstance(getGenericThis().getClass(), vector.length);
        return fromDefaultValueVector(vector, destination);
    }

    public E[] fromDefaultValueVector(final double[] vector, final E[] destination) {
        for (int i = 0; i < vector.length; i++) {
            destination[i] = fromDefaultValue(vector[i]);
        }
        return destination;
    }

    @SuppressWarnings("unchecked")
    public E[][] fromDefaultValueMatrix(final double[][] matrix) {
        if (matrix == null) {
            return null;
        }
        final E[][] destination = (E[][]) Arrays.newInstance(getGenericThis().getClass(), matrix.length,
                matrix[0].length);
        return fromDefaultValueMatrix(matrix, destination);
    }

    public E[][] fromDefaultValueMatrix(final double[][] values, final E[][] destination) {
        for (int i = 0; i < values.length; i++) {
            destination[i] = fromDefaultValueVector(values[i]);
        }
        return destination;
    }

    public List<E> toObjectVector(final List<Double> vector) {
        if (vector == null) {
            return null;
        }
        final List<E> converted = new ArrayList<E>(vector.size());
        for (final Double value : vector) {
            converted.add(toObject(value));
        }
        return converted;
    }

    public E toObject(final double value) {
        return fromDefaultValue(value);
    }

    public List<List<E>> toObjectMatrix(final List<List<Double>> matrix) {
        if (matrix == null) {
            return null;
        }
        final List<List<E>> converted = new ArrayList<List<E>>(matrix.size());
        for (final List<Double> vector : matrix) {
            converted.add(toObjectVector(vector));
        }
        return converted;
    }

    @SuppressWarnings("unchecked")
    public E[] toObjectVector(final double[] vector) {
        if (vector == null) {
            return null;
        }
        final E[] destination = (E[]) Arrays.newInstance(getGenericThis().getClass(), vector.length);
        return toObjectVector(vector, destination);
    }

    public E[] toObjectVector(final double[] vector, final E[] destination) {
        for (int i = 0; i < vector.length; i++) {
            destination[i] = toObject(vector[i]);
        }
        return destination;
    }

    @SuppressWarnings("unchecked")
    public E[][] toObjectMatrix(final double[][] matrix) {
        if (matrix == null) {
            return null;
        }
        if (matrix.length == 0) {
            return (E[][]) Arrays.newInstance(getGenericThis().getClass(), 0, 0);
        } else {
            final E[][] destination = (E[][]) Arrays.newInstance(getGenericThis().getClass(), matrix.length,
                    matrix[0].length);
            return toObjectMatrix(matrix, destination);
        }
    }

    public E[][] toObjectMatrix(final double[][] values, final E[][] destination) {
        for (int i = 0; i < values.length; i++) {
            destination[i] = toObjectVector(values[i]);
        }
        return destination;
    }

    public abstract double getDefaultValue();

    @Override
    public int hashCode() {
        //force explicit default rounding if not done yet
        return Objects.hashCode(getClass(), round().doubleValue());
    }

    public int compareTo(final Double other) {
        if (other == null) {
            return 1;
        }
        return Doubles.compare(getValue(), other.doubleValue());
    }

    protected abstract double getValue();

    public int compareTo(final double other) {
        return Doubles.compare(getValue(), other);
    }

    @Override
    public int compareTo(final Object other) {
        final double doubleOther;
        if (other instanceof ADecimal) {
            final ADecimal<?> cOther = (ADecimal<?>) other;
            doubleOther = cOther.getDefaultValue();
        } else if (other instanceof Number) {
            final Number cOther = (Number) other;
            doubleOther = cOther.doubleValue();
        } else {
            return 1;
        }
        return Doubles.compare(getDefaultValue(), doubleOther);
    }

    @Override
    public boolean equals(final Object other) {
        if (other != null && getClass().isAssignableFrom(other.getClass())) {
            final ADecimal<?> cOther = (ADecimal<?>) other;
            return Doubles.equals(getDefaultValue(), cOther.getDefaultValue());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return toString(getValue());
    }

    public static String toString(final double value) {
        return NUMBER_FORMAT.get().format(value);
    }

    @Override
    public int intValue() {
        return Integers.checkedCast(getValue());
    }

    @Override
    public long longValue() {
        return Longs.checkedCast(getValue());
    }

    @Override
    public float floatValue() {
        return Floats.checkedCast(getValue());
    }

    @Override
    public double doubleValue() {
        return getValue();
    }

    @Override
    public byte byteValue() {
        return Bytes.checkedCast(getValue());
    }

    @Override
    public short shortValue() {
        return Shorts.checkedCast(getValue());
    }

    public BigDecimal bigDecimalValue() {
        return BigDecimals.valueOf(getValue());
    }

    public BigInteger bigIntegerValue() {
        return BigIntegers.valueOf(getValue());
    }

    public Number numberValue() {
        return getValue();
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
        return new DecimalDigitsInfo(toString()).getDecimalDigits();
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
        return new DecimalDigitsInfo(toString()).getWholeNumberDigits();
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
        return new DecimalDigitsInfo(toString()).getDigits();
    }

    public boolean isZero() {
        return Doubles.isZero(getValue());
    }

    public final boolean isNotZero() {
        return Doubles.isNotZero(getValue());
    }

    /**
     * 0 is counted as positive as well here to make things simpler.
     */
    public boolean isPositive() {
        return Doubles.isPositive(getValue());
    }

    /**
     * This one excludes 0 from positive.
     */
    public boolean isPositiveNonZero() {
        return Doubles.isPositiveNonZero(getValue());
    }

    public boolean isNegative() {
        return Doubles.isNegative(getValue());
    }

    public boolean isNegativeOrZero() {
        return Doubles.isNegativeOrZero(getValue());
    }

    public boolean isGreaterThan(final double o) {
        return Doubles.isGreaterThan(getValue(), o);
    }

    public boolean isGreaterThan(final Double o) {
        return Doubles.isGreaterThan(getValue(), o);
    }

    public boolean isGreaterThan(final Number o) {
        return Doubles.isGreaterThan(getValue(), o);
    }

    public boolean isGreaterThan(final ADecimal<?> o) {
        if (o == null) {
            return false;
        }
        return Doubles.isGreaterThan(getDefaultValue(), o.getDefaultValue());
    }

    public boolean isGreaterThanOrEqualTo(final double o) {
        return Doubles.isGreaterThanOrEqualTo(getValue(), o);
    }

    public boolean isGreaterThanOrEqualTo(final Double o) {
        return Doubles.isGreaterThanOrEqualTo(getValue(), o);
    }

    public boolean isGreaterThanOrEqualTo(final Number o) {
        return Doubles.isGreaterThanOrEqualTo(getValue(), o);
    }

    public boolean isGreaterThanOrEqualTo(final ADecimal<?> o) {
        if (o == null) {
            return false;
        }
        return Doubles.isGreaterThanOrEqualTo(getDefaultValue(), o.getDefaultValue());
    }

    public boolean isLessThan(final double o) {
        return Doubles.isLessThan(getValue(), o);
    }

    public boolean isLessThan(final Double o) {
        return Doubles.isLessThan(getValue(), o);
    }

    public boolean isLessThan(final Number o) {
        return Doubles.isLessThan(getValue(), o);
    }

    public boolean isLessThan(final ADecimal<?> o) {
        if (o == null) {
            return false;
        }
        return Doubles.isLessThan(getDefaultValue(), o.getDefaultValue());
    }

    public boolean isLessThanOrEqualTo(final double o) {
        return Doubles.isLessThanOrEqualTo(getValue(), o);
    }

    public boolean isLessThanOrEqualTo(final Double o) {
        return Doubles.isLessThanOrEqualTo(getValue(), o);
    }

    public boolean isLessThanOrEqualTo(final Number o) {
        return Doubles.isLessThanOrEqualTo(getValue(), o);
    }

    public boolean isLessThanOrEqualTo(final ADecimal<?> o) {
        if (o == null) {
            return false;
        }
        return Doubles.isLessThanOrEqualTo(getDefaultValue(), o.getDefaultValue());
    }

    public boolean isBetween(final double lowerBound, final double upperBound) {
        return isGreaterThanOrEqualTo(lowerBound) && isLessThanOrEqualTo(upperBound);
    }

    public boolean isBetween(final Double lowerBound, final Double upperBound) {
        return isGreaterThanOrEqualTo(lowerBound) && isLessThanOrEqualTo(upperBound);
    }

    public boolean isBetween(final Number lowerBound, final Number upperBound) {
        return isGreaterThanOrEqualTo(lowerBound) && isLessThanOrEqualTo(upperBound);
    }

    public boolean isBetween(final ADecimal<?> lowerBound, final ADecimal<?> upperBound) {
        return isGreaterThanOrEqualTo(lowerBound) && isLessThanOrEqualTo(upperBound);
    }

    public E scaleByPowerOfTen(final int n) {
        return newValueCopy(Doubles.scaleByPowerOfTen(getValue(), n));
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
        return newValueCopy(Doubles.round(getValue(), scale, roundingMode));
    }

    /**
     * <ul>
     * <li>[growth rate] = ( [current] - [previous] ) / |[previous]|</li>
     * <ul>
     * 
     * @see <a href="http://www.chemieonline.de/forum/showthread.php?t=101560">Source</a>
     */
    public Percent growthRate(final ADecimal<E> nextValue) {
        final double nextDecimalValue = nextValue.getDefaultValue();
        final double thisDecimalValue = getGenericThis().getDefaultValue();
        final double rate = Doubles.growthRate(thisDecimalValue, nextDecimalValue);
        return new Percent(rate, PercentScale.RATE);
    }

    public E abs() {
        return newValueCopy(Doubles.abs(getValue()));
    }

    /**
     * Returns the natural logarithm.
     */
    public E log() {
        if (isNegativeOrZero()) {
            return zero();
        }
        return newValueCopy(Doubles.log(getValue()));
    }

    /**
     * Returns the logarithm base 10.
     */
    public E log10() {
        if (isNegativeOrZero()) {
            return zero();
        }
        return newValueCopy(Doubles.log10(getValue()));
    }

    /**
     * Returns Euler's number <i>e</i> raised to the power of this value.
     */
    public E exp() {
        return newValueCopy(Doubles.exp(getValue()));
    }

    public E cos() {
        return newValueCopy(Doubles.cos(getValue()));
    }

    public E sin() {
        return newValueCopy(Doubles.sin(getValue()));
    }

    /**
     * Returns 10 raised to the power of this value.
     */
    public E exp10() {
        return newValueCopy(Doubles.exp10(getValue()));
    }

    public E subtract(final ADecimal<E> subtrahend) {
        if (subtrahend == null) {
            return getGenericThis();
        }
        return subtract(subtrahend.doubleValue());
    }

    public E subtract(final Number subtrahend) {
        if (subtrahend == null) {
            return getGenericThis();
        }
        return subtract(subtrahend.doubleValue());
    }

    public E subtract(final Double subtrahend) {
        if (subtrahend == null) {
            return getGenericThis();
        }
        return subtract(subtrahend.doubleValue());
    }

    public E subtract(final double subtrahend) {
        return newValueCopy(getValue() - subtrahend);
    }

    public E add(final ADecimal<E> augend) {
        if (augend == null) {
            return getGenericThis();
        }
        return add(augend.doubleValue());
    }

    public E add(final Number augend) {
        if (augend == null) {
            return getGenericThis();
        }
        return add(augend.doubleValue());
    }

    public E add(final Double augend) {
        if (augend == null) {
            return getGenericThis();
        }
        return add(augend.doubleValue());
    }

    public E add(final double augend) {
        return newValueCopy(getValue() + augend);
    }

    public E multiply(final ADecimal<E> multiplicant) {
        if (isZero()) {
            return getGenericThis();
        } else if (multiplicant == null || multiplicant.isZero()) {
            return zero();
        } else {
            return newValueCopy(getValue() * multiplicant.doubleValue());
        }
    }

    public E multiply(final double multiplicant) {
        if (isZero()) {
            return getGenericThis();
        } else {
            return newValueCopy(getValue() * multiplicant);
        }
    }

    public E multiply(final Double multiplicant) {
        if (isZero()) {
            return getGenericThis();
        } else if (multiplicant == null || multiplicant.doubleValue() == 0D) {
            return zero();
        } else {
            return newValueCopy(getValue() * multiplicant.doubleValue());
        }
    }

    public E multiply(final Number multiplicant) {
        if (isZero()) {
            return getGenericThis();
        } else if (multiplicant == null || multiplicant.doubleValue() == 0D) {
            return zero();
        } else {
            if (multiplicant instanceof IScaledNumber) {
                throw new IllegalArgumentException(new TextDescription(
                        "Multiplication between different types of %ss [%s=%s * %s=%s] does not make any sense. Please be more specific.",
                        IScaledNumber.class.getSimpleName(), this.getClass().getSimpleName(), this,
                        multiplicant.getClass().getSimpleName(), multiplicant).toString());
            }
            return newValueCopy(getValue() * multiplicant.doubleValue());
        }
    }

    /**
     * returns the remainder of the division.
     */
    public E remainder(final ADecimal<E> divisor) {
        if (isZero()) {
            return getGenericThis();
        } else if (divisor == null || divisor.isZero()) {
            return zero();
        } else {
            return newValueCopy(Doubles.remainder(getValue(), divisor.doubleValue()));
        }
    }

    public E remainder(final double divisor) {
        if (isZero()) {
            return getGenericThis();
        } else if (divisor == 0D) {
            return zero();
        } else {
            return newValueCopy(Doubles.remainder(getValue(), divisor));
        }
    }

    public E remainder(final Double divisor) {
        if (isZero()) {
            return getGenericThis();
        } else if (divisor == null || divisor.doubleValue() == 0D) {
            return zero();
        } else {
            return newValueCopy(Doubles.remainder(getValue(), divisor));
        }
    }

    public E remainder(final Number divisor) {
        if (isZero()) {
            return getGenericThis();
        } else if (divisor == null || divisor.doubleValue() == 0D) {
            //results in 0, thus multiply by 0
            return zero();
        } else {
            if (divisor instanceof IScaledNumber) {
                throw new IllegalArgumentException(new TextDescription(
                        "Division between different types of %ss [%s=%s / %s=%s] does not make any sense. Please be more specific.",
                        IScaledNumber.class.getSimpleName(), this.getClass().getSimpleName(), this,
                        divisor.getClass().getSimpleName(), divisor).toString());
            }
            return newValueCopy(Doubles.remainder(getValue(), divisor.doubleValue()));
        }
    }

    /**
     * If the divisor is 0, 0 is returned. This goes against the mathematical rules, but makes a developers life easier.
     */
    public E divide(final ADecimal<E> divisor) {
        if (isZero()) {
            //prevent NaN
            return getGenericThis();
        } else if (divisor == null || divisor.isZero()) {
            return zero();
        } else {
            return newValueCopy(getValue() / divisor.doubleValue());
        }
    }

    public E divide(final double divisor) {
        if (isZero()) {
            //prevent NaN
            return getGenericThis();
        } else if (divisor == 0D) {
            //results in 0, thus multiply by 0
            return zero();
        } else {
            return newValueCopy(getValue() / divisor);
        }
    }

    public E divide(final Double divisor) {
        if (isZero()) {
            //prevent NaN
            return getGenericThis();
        } else if (divisor == null || divisor.doubleValue() == 0D) {
            //results in 0, thus multiply by 0
            return zero();
        } else {
            return newValueCopy(getValue() / divisor.doubleValue());
        }
    }

    /**
     * If the divisor is 0, 0 is returned. This goes against the mathematical rules, but makes a developers life easier.
     */
    public E divide(final Number divisor) {
        if (isZero()) {
            //prevent NaN
            return getGenericThis();
        } else if (divisor == null || divisor.doubleValue() == 0D) {
            //results in 0, thus multiply by 0
            return zero();
        } else {
            if (divisor instanceof IScaledNumber) {
                throw new IllegalArgumentException(new TextDescription(
                        "Division between different types of %ss [%s=%s / %s=%s] does not make any sense. Please be more specific.",
                        IScaledNumber.class.getSimpleName(), this.getClass().getSimpleName(), this,
                        divisor.getClass().getSimpleName(), divisor).toString());
            }
            return newValueCopy(getValue() / divisor.doubleValue());
        }
    }

    /**
     * With a step of 0.5: (Math.ceil(x * 2) / 2)
     */
    public E roundToStep(final ADecimal<E> step) {
        return roundToStep(step, DEFAULT_ROUNDING_MODE);
    }

    public E roundToStep(final ADecimal<E> step, final RoundingMode roundingMode) {
        return fromDefaultValue((Doubles.roundToStep(getDefaultValue(), step.getDefaultValue())));
    }

    public E reciprocal() {
        if (isZero()) {
            return zero();
        } else {
            return newValueCopy(Doubles.reciprocal(getDefaultValue()));
        }
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

    public E square() {
        return newValueCopy(Doubles.square(getValue()));
    }

    public E pow(final ADecimal<E> exponent) {
        return newValueCopy(Doubles.pow(getValue(), exponent.doubleValue()));
    }

    public E pow(final double exponent) {
        return newValueCopy(Doubles.pow(getValue(), exponent));
    }

    public E pow(final Double exponent) {
        return newValueCopy(Doubles.pow(getValue(), exponent.doubleValue()));
    }

    public E pow(final Number exponent) {
        return newValueCopy(Doubles.pow(getValue(), exponent.doubleValue()));
    }

    public E sqrt() {
        return newValueCopy(Doubles.sqrt(getValue()));
    }

    public E root(final double n) {
        return newValueCopy(Doubles.root(getValue(), n));
    }

    public E root(final Double n) {
        return newValueCopy(Doubles.root(getValue(), n.doubleValue()));
    }

    /**
     * Root = Value^1/n
     * 
     * @see <a href="http://www.ee.ucl.ac.uk/~mflanaga/java/Stat.html#geom2">Source with BigDecimal</a>
     */
    public E root(final Number n) {
        return newValueCopy(Doubles.root(getValue(), n.doubleValue()));
    }

    public E distance(final ADecimal<E> to) {
        return subtract(to).abs();
    }

    public E negate() {
        return newValueCopy(Doubles.negate(getValue()));
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
                return POSITIVE_SIGN;
            } else {
                return NEGATIVE_SIGN;
            }
        } else {
            if (!inverted) {
                return NEGATIVE_SIGN;
            } else {
                return POSITIVE_SIGN;
            }
        }
    }

    public abstract String toFormattedString();

    public abstract String toFormattedString(String format);

    public static String toString(final ADecimal<?> value) {
        if (value == null) {
            return null;
        } else {
            return value.toString();
        }
    }

    public abstract E zero();

    public static <T extends ADecimal<T>> T zeroToNull(final T value) {
        if (value == null || value.isZero()) {
            return null;
        } else {
            return value;
        }
    }

    public static <T extends ADecimal<T>> T sum(final T value1, final T value2) {
        if (value1 == null) {
            return value2;
        } else {
            return value1.add(value2);
        }
    }

    public static <T extends ADecimal<T>> T max(final T value1, final T value2) {
        if (value1 == null) {
            return value2;
        } else {
            return value1.orHigher(value2);
        }
    }

    public static <T extends ADecimal<T>> T min(final T value1, final T value2) {
        if (value1 == null) {
            return value2;
        } else {
            return value1.orLower(value2);
        }
    }

    public static <T extends ADecimal<T>> T negate(final T value) {
        if (value == null) {
            return null;
        } else {
            return value.negate();
        }
    }

    public static <T extends ADecimal<T>> T abs(final T value) {
        if (value == null) {
            return null;
        } else {
            return value.abs();
        }
    }

    public static <T extends ADecimal<T>> List<T> asListVector(final T[] vector) {
        if (vector == null) {
            return null;
        }
        return Arrays.asList(vector);
    }

    public static <T extends ADecimal<T>> List<List<T>> asListMatrix(final T[][] matrix) {
        if (matrix == null) {
            return null;
        }
        final List<List<T>> matrixAsList = new ArrayList<List<T>>(matrix.length);
        for (final T[] vector : matrix) {
            matrixAsList.add(asListVector(vector));
        }
        return matrixAsList;
    }

    public static <T extends ADecimal<T>> T[][] fixInconsistentMatrixDimensions(final T[][] matrix) {
        T missingValue = null;
        OUTER: for (int i = 0; i < matrix.length; i++) {
            final T[] vector = matrix[i];
            for (int j = 0; j < vector.length; j++) {
                final T value = vector[j];
                if (value != null) {
                    missingValue = value.zero();
                    break OUTER;
                }
            }
        }
        return fixInconsistentMatrixDimensions(matrix, missingValue);
    }

    public static <T extends ADecimal<T>> T[][] fixInconsistentMatrixDimensions(final T[][] matrix,
            final T missingValue) {
        return fixInconsistentMatrixDimensions(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static <T extends ADecimal<T>> T[][] fixInconsistentMatrixDimensions(final T[][] matrix,
            final T missingValue, final boolean appendMissingValues) {
        return Objects.fixInconsistentMatrixDimensions(matrix, missingValue, appendMissingValues);
    }

    public static <T extends ADecimal<T>> List<? extends List<T>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends T>> matrix) {
        T missingValue = null;
        OUTER: for (int i = 0; i < matrix.size(); i++) {
            final List<? extends T> vector = matrix.get(i);
            for (int j = 0; j < vector.size(); j++) {
                final T value = vector.get(j);
                if (value != null) {
                    missingValue = value.zero();
                    break OUTER;
                }
            }
        }
        return fixInconsistentMatrixDimensionsAsList(matrix, missingValue);
    }

    public static <T extends ADecimal<T>> List<List<T>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends T>> matrix, final T missingValue) {
        return fixInconsistentMatrixDimensionsAsList(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static <T extends ADecimal<T>> List<List<T>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends T>> matrix, final T missingValue, final boolean appendMissingValues) {
        return Objects.fixInconsistentMatrixDimensionsAsList(matrix, missingValue, appendMissingValues);
    }

}