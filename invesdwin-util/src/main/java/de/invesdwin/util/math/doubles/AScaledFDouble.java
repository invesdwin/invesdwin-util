package de.invesdwin.util.math.doubles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.IScaledNumber;

@Immutable
public abstract class AScaledFDouble<T extends AScaledFDouble<T, S>, S extends IFDoubleScale<T, S>> extends AFDouble<T>
        implements Cloneable, IScaledNumber {

    protected final S scale;
    @GuardedBy("none for performance")
    private double scaledValue;
    @GuardedBy("none for performance")
    private double defaultValue;

    protected AScaledFDouble(final double value, final S scale) {
        this.scale = scale;
        final S defaultScale = getDefaultScale();
        if (defaultScale == null) {
            throw new NullPointerException("defaultScale should not be null");
        }
        validateScale(defaultScale);
        this.scaledValue = Doubles.nanToZero(value);
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
            final AScaledFDouble<T, S> clone = (AScaledFDouble<T, S>) clone();
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
            final AScaledFDouble castedObj = (AScaledFDouble) obj;
            return castedObj.getDefaultScale().equals(this.getDefaultScale())
                    //force default rounding if not explicitly done yet
                    && castedObj.getDefaultValue() == this.getDefaultValue();
        } else {
            return false;
        }
    }

    public final S getScale() {
        return scale;
    }

    public abstract S getDefaultScale();

    @Override
    public final String toString() {
        return toString(scale);
    }

    public final String toString(final boolean withSymbol) {
        return toString(scale, withSymbol);
    }

    public final String toString(final S scale) {
        return toString(scale, true);
    }

    public final String toString(final S scale, final boolean withSymbol) {
        return toStringBuilder().withScale(scale).withSymbol(withSymbol).toString();
    }

    public ScaledFDoubleToStringBuilder<T, S> toStringBuilder() {
        return new ScaledFDoubleToStringBuilder<T, S>(getGenericThis());
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

    @SuppressWarnings("unchecked")
    @Override
    public T subtract(final AFDouble<T> subtrahend) {
        if (subtrahend == null) {
            return getGenericThis();
        }
        final double defaultScaledSubtrahend = maybeGetDefaultScaledNumber(subtrahend);
        final double newDefault = getDefaultValue() - defaultScaledSubtrahend;
        return fromDefaultValue(newDefault);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T add(final AFDouble<T> augend) {
        if (augend == null) {
            return getGenericThis();
        }
        final double defaultScaledAugend = maybeGetDefaultScaledNumber(augend);
        final double newDefault = getDefaultValue() + defaultScaledAugend;
        return fromDefaultValue(newDefault);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T multiply(final AFDouble<T> multiplicant) {
        if (isZero()) {
            return getGenericThis();
        } else if (multiplicant == null || multiplicant.isZero()) {
            return zero();
        } else {
            final double defaultScaledMultiplicant = maybeGetDefaultScaledNumber(multiplicant);
            final double newDefault = getDefaultValue() * defaultScaledMultiplicant;
            return fromDefaultValue(newDefault);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T divide(final AFDouble<T> divisor) {
        if (isZero()) {
            //prevent NaN
            return getGenericThis();
        } else if (divisor == null || divisor.isZero()) {
            return divide(0);
        } else {
            final double defaultScaledDivisor = maybeGetDefaultScaledNumber(divisor);
            final double newDefault = getDefaultValue() / defaultScaledDivisor;
            return fromDefaultValue(newDefault);
        }
    }

    @Override
    public T remainder(final AFDouble<T> divisor) {
        if (isZero()) {
            return getGenericThis();
        } else if (divisor == null || divisor.isZero()) {
            return remainder(0);
        } else {
            final double defaultScaledDivisor = maybeGetDefaultScaledNumber(divisor);
            final double newDefault = Doubles.remainder(getDefaultValue(), defaultScaledDivisor);
            return fromDefaultValue(newDefault);
        }
    }

    private double maybeGetDefaultScaledNumber(final AFDouble<?> number) {
        assertSameDefaultScale(number);
        return number.getDefaultValue();
    }

    public void assertSameDefaultScale(final Number number) {
        if (number instanceof AScaledFDouble) {
            final AScaledFDouble<?, ?> scaledNumber = (AScaledFDouble<?, ?>) number;
            if (!scaledNumber.getDefaultScale().equals(getDefaultScale())) {
                throw new IllegalArgumentException(
                        "Cannot mix two different default scales on division: " + getDefaultScale() + " [" + this
                                + "]  != " + scaledNumber.getDefaultScale() + " [" + scaledNumber + "]");
            }
        }
    }

    public static <T, D extends AFDouble<D>> List<D> extractValues(final Function<T, D> getter, final List<T> objects) {
        final List<D> decimals = new ArrayList<D>();
        for (final T obj : objects) {
            final D decimal = getter.apply(obj);
            decimals.add(decimal);
        }
        return decimals;
    }

    public static <T, D extends AFDouble<D>> List<D> extractValues(final Function<T, D> getter, final T... objects) {
        return extractValues(getter, Arrays.asList(objects));
    }

}
