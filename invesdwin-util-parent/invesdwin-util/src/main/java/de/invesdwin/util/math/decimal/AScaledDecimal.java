package de.invesdwin.util.math.decimal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.list.Lists;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.internal.DecimalAggregate;
import de.invesdwin.util.math.decimal.internal.DummyDecimalAggregate;
import de.invesdwin.util.math.decimal.scaled.IDecimalScale;
import de.invesdwin.util.math.decimal.scaled.ScaledDecimalToStringBuilder;

@Immutable
public abstract class AScaledDecimal<T extends AScaledDecimal<T, S>, S extends IDecimalScale<T, S>> extends ADecimal<T>
        implements Cloneable, IScaledNumber {

    protected final S scale;
    @GuardedBy("none for performance")
    private double scaledValue;
    @GuardedBy("none for performance")
    private double defaultValue;

    protected AScaledDecimal(final double value, final S scale) {
        this.scale = scale;
        final S defaultScale = getDefaultScale();
        assert defaultScale != null : "defaultScale should not be null";
        validateScale(defaultScale);
        this.scaledValue = Doubles.nonFiniteToZero(value);
        validateScale(scale);
        if (scale.equals(defaultScale)) {
            defaultValue = value;
        } else {
            defaultValue = Double.NaN;
        }
    }

    protected void validateScale(final S scale) {}

    protected abstract T newValueCopy(double value, S scale);

    @Override
    protected double getValue() {
        return getScaledValue();
    }

    @Override
    protected final T newValueCopy(final double value) {
        return newValueCopy(value, scale);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final T fromDefaultValue(final double value) {
        try {
            final AScaledDecimal<T, S> clone = (AScaledDecimal<T, S>) clone();
            clone.scaledValue = Double.NaN;
            clone.defaultValue = value;
            return (T) clone;
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final double getDefaultValue() {
        if (Doubles.isNaN(defaultValue)) {
            defaultValue = innerGetValue(getDefaultScale());
        }
        return defaultValue;
    }

    public final double getValue(final S scale) {
        if (getDefaultScale().equals(scale)) {
            return getDefaultValue();
        }
        return innerGetValue(scale);
    }

    private double innerGetValue(final S scale) {
        if (scale.equals(this.scale)) {
            return getScaledValue();
        } else {
            validateScale(scale);
            if (!Doubles.isNaN(scaledValue)) {
                return scale.convertValue(getGenericThis(), scaledValue, this.scale);
            } else {
                return scale.convertValue(getGenericThis(), defaultValue, getDefaultScale());
            }
        }
    }

    public double getScaledValue() {
        if (Doubles.isNaN(scaledValue)) {
            scaledValue = scale.convertValue(getGenericThis(), defaultValue, getDefaultScale());
        }
        return scaledValue;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getClass(), getDefaultScale(), getDefaultValue());
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(final Object obj) {
        if (obj != null && getGenericThis().getClass().isAssignableFrom(obj.getClass())) {
            final AScaledDecimal castedObj = (AScaledDecimal) obj;
            return castedObj.getDefaultScale().equals(this.getDefaultScale())
                    //force default rounding if not explicitly done yet
                    && Doubles.equals(castedObj.getDefaultValue(), this.getDefaultValue());
        } else {
            return false;
        }
    }

    public final S getScale() {
        return scale;
    }

    public abstract S getDefaultScale();

    @Override
    public String toString() {
        return toString(scale);
    }

    public final String toString(final boolean withSymbol) {
        return toString(scale, withSymbol);
    }

    public final String toString(final S scale) {
        return toString(scale, true);
    }

    public final String toString(final S scale, final boolean withSymbol) {
        return toStringBuilder().setScale(scale).setSymbol(withSymbol).toString();
    }

    public ScaledDecimalToStringBuilder<T, S> toStringBuilder() {
        return new ScaledDecimalToStringBuilder<T, S>(getGenericThis());
    }

    @Override
    public String toFormattedString() {
        return toFormattedString(Decimal.DEFAULT_DECIMAL_FORMAT);
    }

    @Override
    public String toFormattedString(final String format) {
        return toStringBuilder().toString(format);
    }

    public T asScale(final S scale) {
        validateScale(scale);
        return newValueCopy(getValue(scale), scale);
    }

    @Override
    public T subtract(final ADecimal<T> subtrahend) {
        if (subtrahend == null) {
            return getGenericThis();
        }
        final double defaultScaledSubtrahend = maybeGetDefaultScaledNumberNotNullSafe(subtrahend);
        final double newDefault = getDefaultValue() - defaultScaledSubtrahend;
        return fromDefaultValue(newDefault);
    }

    @Override
    public T add(final ADecimal<T> augend) {
        if (augend == null) {
            return getGenericThis();
        }
        final double defaultScaledAugend = maybeGetDefaultScaledNumberNotNullSafe(augend);
        final double newDefault = getDefaultValue() + defaultScaledAugend;
        return fromDefaultValue(newDefault);
    }

    @Override
    public T multiply(final ADecimal<T> multiplicant) {
        if (isZero()) {
            return getGenericThis();
        } else if (multiplicant == null || multiplicant.isZero()) {
            return zero();
        } else {
            final double defaultScaledMultiplicant = maybeGetDefaultScaledNumberNotNullSafe(multiplicant);
            final double newDefault = getDefaultValue() * defaultScaledMultiplicant;
            return fromDefaultValue(newDefault);
        }
    }

    @Override
    public T divide(final ADecimal<T> divisor) {
        if (isZero()) {
            //prevent NaN
            return getGenericThis();
        } else if (divisor == null || divisor.isZero()) {
            return divide(0D);
        } else {
            final double defaultScaledDivisor = maybeGetDefaultScaledNumberNotNullSafe(divisor);
            final double newDefault = getDefaultValue() / defaultScaledDivisor;
            return fromDefaultValue(newDefault);
        }
    }

    @Override
    public T remainder(final ADecimal<T> divisor) {
        if (isZero()) {
            return getGenericThis();
        } else if (divisor == null || divisor.isZero()) {
            return remainder(0D);
        } else {
            final double defaultScaledDivisor = maybeGetDefaultScaledNumberNotNullSafe(divisor);
            final double newDefault = Doubles.remainder(getDefaultValue(), defaultScaledDivisor);
            return fromDefaultValue(newDefault);
        }
    }

    @Override
    public boolean isGreaterThan(final ADecimal<?> o) {
        return Doubles.isGreaterThan(getDefaultValue(), maybeGetDefaultScaledNumber(o));
    }

    @Override
    public boolean isLessThan(final ADecimal<?> o) {
        return Doubles.isLessThan(getDefaultValue(), maybeGetDefaultScaledNumber(o));
    }

    @Override
    public boolean isBetween(final ADecimal<?> lowerBound, final ADecimal<?> upperBound) {
        return isBetween(maybeGetDefaultScaledNumber(lowerBound), maybeGetDefaultScaledNumber(upperBound));
    }

    @Override
    public boolean isGreaterThanOrEqualTo(final ADecimal<?> o) {
        return Doubles.isGreaterThanOrEqualTo(getDefaultValue(), maybeGetDefaultScaledNumber(o));
    }

    @Override
    public boolean isLessThanOrEqualTo(final ADecimal<?> o) {
        return Doubles.isLessThanOrEqualTo(getDefaultValue(), maybeGetDefaultScaledNumber(o));
    }

    private double maybeGetDefaultScaledNumber(final ADecimal<?> number) {
        if (number == null) {
            return Double.NaN;
        }
        return maybeGetDefaultScaledNumberNotNullSafe(number);
    }

    private double maybeGetDefaultScaledNumberNotNullSafe(final ADecimal<?> number) {
        assertSameDefaultScale(number);
        return number.getDefaultValue();
    }

    public void assertSameDefaultScale(final Number number) {
        assert isSameDefaultScale(number) : newSameDefaultScaleErrorMessage(number);
    }

    private String newSameDefaultScaleErrorMessage(final Number number) {
        final AScaledDecimal<?, ?> scaledNumber = (AScaledDecimal<?, ?>) number;
        return "Cannot mix two different default scales on division: " + getDefaultScale() + " [" + this + "]  != "
                + scaledNumber.getDefaultScale() + " [" + scaledNumber + "]";
    }

    private boolean isSameDefaultScale(final Number number) {
        if (number instanceof AScaledDecimal) {
            final AScaledDecimal<?, ?> scaledNumber = (AScaledDecimal<?, ?>) number;
            if (!scaledNumber.getDefaultScale().equals(getDefaultScale())) {
                return false;
            }
        }
        return true;
    }

    public static <T, D extends ADecimal<D>> List<D> extractValues(final Function<T, D> getter, final List<T> objects) {
        final List<D> decimals = new ArrayList<D>(objects.size());
        for (final T obj : objects) {
            final D decimal = getter.apply(obj);
            decimals.add(decimal);
        }
        return decimals;
    }

    @SuppressWarnings("unchecked")
    public static <T, D extends ADecimal<D>> List<D> extractValues(final Function<T, D> getter, final T... objects) {
        return extractValues(getter, Arrays.asList(objects));
    }

    @SuppressWarnings("unchecked")
    public static <D extends ADecimal<D>> IDecimalAggregate<D> valueOf(final D... values) {
        return valueOf(Arrays.asList(values));
    }

    public static <D extends ADecimal<D>> IDecimalAggregate<D> valueOf(final ICloseableIterable<? extends D> values) {
        return valueOf(Lists.toList(values));
    }

    public static <D extends ADecimal<D>> IDecimalAggregate<D> valueOf(final Iterable<? extends D> values) {
        return valueOf(Lists.toList(values));
    }

    public static <D extends ADecimal<D>> IDecimalAggregate<D> valueOf(final List<? extends D> values) {
        if (values == null || values.size() == 0) {
            return DummyDecimalAggregate.getInstance();
        } else {
            return new DecimalAggregate<D>(values, null);
        }
    }

    public static <D extends ADecimal<D>> IDecimalAggregate<D> valueOf(final List<? extends D> values,
            final D converter) {
        if (values == null || values.size() == 0) {
            return DummyDecimalAggregate.getInstance();
        } else {
            return new DecimalAggregate<D>(values, converter);
        }
    }

    public static <D extends ADecimal<D>> D nullToZero(final D value, final D zero) {
        if (value == null) {
            return zero;
        } else {
            return value;
        }
    }

}
