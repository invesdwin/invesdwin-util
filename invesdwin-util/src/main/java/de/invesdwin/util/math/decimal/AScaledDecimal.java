package de.invesdwin.util.math.decimal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.Lists;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.math.decimal.internal.DecimalAggregate;
import de.invesdwin.util.math.decimal.internal.DummyDecimalAggregate;
import de.invesdwin.util.math.decimal.internal.impl.ADecimalImpl;
import de.invesdwin.util.math.decimal.scaled.IDecimalScale;

@SuppressWarnings({ "rawtypes", "serial" })
@ThreadSafe
public abstract class AScaledDecimal<T extends AScaledDecimal<T, S>, S extends IDecimalScale<T, S>> extends ADecimal<T>
        implements Cloneable {

    protected final S scale;
    @GuardedBy("none for performance")
    private Decimal scaledValue;
    @GuardedBy("none for performance")
    private ScaledDecimalDelegateImpl impl;
    @GuardedBy("none for performance")
    private Decimal defaultValue;
    private final S defaultScale;

    protected AScaledDecimal(final Decimal value, final S scale, final S defaultScale) {
        this.defaultScale = defaultScale;
        if (defaultScale == null) {
            throw new NullPointerException("defaultScale should not be null");
        }
        validateScale(defaultScale);
        this.scaledValue = Decimal.nullToZero(value);
        validateScale(scale);
        this.scale = scale;
    }

    protected void validateScale(final S scale) {}

    protected abstract T newValueCopy(Decimal value, S scale);

    @Override
    public ScaledDecimalDelegateImpl getImpl() {
        if (impl == null) {
            impl = new ScaledDecimalDelegateImpl(this, getScaledValue().getImpl());
        }
        return impl;
    }

    @Override
    protected final T newValueCopy(final ADecimalImpl value) {
        return newValueCopy(new Decimal(value), scale);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final T fromDefaultValue(final Decimal value) {
        try {
            final AScaledDecimal<T, S> clone = (AScaledDecimal<T, S>) clone();
            clone.scaledValue = null;
            clone.impl = null;
            clone.defaultValue = value;
            clone.isPositive = null;
            clone.isZero = null;
            return (T) clone;
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final Decimal getDefaultValue() {
        if (defaultValue == null) {
            defaultValue = innerGetValue(defaultScale);
        }
        return defaultValue;
    }

    public final Decimal getValue(final S scale) {
        if (defaultScale.equals(scale)) {
            return getDefaultValue();
        }
        return innerGetValue(scale);
    }

    private Decimal innerGetValue(final S scale) {
        if (scale == this.scale) {
            return getScaledValue();
        } else {
            validateScale(scale);
            if (scaledValue != null) {
                return scale.convertValue(getGenericThis(), new Decimal(getImpl().getDelegate()), this.scale);
            } else {
                return scale.convertValue(getGenericThis(), defaultValue, defaultScale);
            }
        }
    }

    private Decimal getScaledValue() {
        if (scaledValue == null) {
            scaledValue = scale.convertValue(getGenericThis(), defaultValue, this.defaultScale);
        }
        return scaledValue;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() + getDefaultScale().hashCode() + getDefaultValue().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj != null && getGenericThis().getClass().isAssignableFrom(obj.getClass())) {
            final AScaledDecimal castedObj = (AScaledDecimal) obj;
            return castedObj.getDefaultScale().equals(this.getDefaultScale())
                    && castedObj.getDefaultValue().equals(this.getDefaultValue());
        } else {
            return false;
        }
    }

    public final S getScale() {
        return scale;
    }

    public final S getDefaultScale() {
        return defaultScale;
    }

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

    @SuppressWarnings("unchecked")
    @Override
    public T subtract(final ADecimal<T> subtrahend) {
        if (subtrahend == null) {
            return getGenericThis();
        }
        final ADecimal<?> defaultScaledSubtrahend = maybeGetDefaultScaledNumber(subtrahend);
        final ADecimalImpl newDefault = getDefaultValue().getImpl().subtract(defaultScaledSubtrahend);
        return fromDefaultValue(new Decimal(newDefault));
    }

    @SuppressWarnings("unchecked")
    @Override
    public T add(final ADecimal<T> augend) {
        if (augend == null) {
            return getGenericThis();
        }
        final ADecimal<?> defaultScaledAugend = maybeGetDefaultScaledNumber(augend);
        final ADecimalImpl newDefault = getDefaultValue().getImpl().add(defaultScaledAugend);
        return fromDefaultValue(new Decimal(newDefault));
    }

    @SuppressWarnings("unchecked")
    @Override
    public T multiply(final ADecimal<T> multiplicant) {
        if (isZero()) {
            return getGenericThis();
        } else if (multiplicant == null) {
            return multiply(0);
        } else {
            final ADecimal<?> defaultScaledMultiplicant = maybeGetDefaultScaledNumber(multiplicant);
            final ADecimalImpl newDefault = getDefaultValue().getImpl().multiply(defaultScaledMultiplicant);
            return fromDefaultValue(new Decimal(newDefault));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T divide(final ADecimal<T> divisor) {
        if (isZero()) {
            //prevent NaN
            return getGenericThis();
        } else if (divisor == null || divisor.isZero()) {
            return divide(0);
        } else {
            final ADecimal<?> defaultScaledDivisor = maybeGetDefaultScaledNumber(divisor);
            final ADecimalImpl newDefault = getDefaultValue().getImpl().divide(defaultScaledDivisor);
            return fromDefaultValue(new Decimal(newDefault));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T remainder(final ADecimal<T> divisor) {
        if (isZero()) {
            return getGenericThis();
        } else if (divisor == null || divisor.isZero()) {
            return remainder(0);
        } else {
            final ADecimal<?> defaultScaledDivisor = maybeGetDefaultScaledNumber(divisor);
            final ADecimalImpl newDefault = getDefaultValue().getImpl().remainder(defaultScaledDivisor);
            return fromDefaultValue(new Decimal(newDefault));
        }
    }

    private ADecimal<?> maybeGetDefaultScaledNumber(final ADecimal<?> number) {
        assertSameDefaultScale(number);
        return number.getDefaultValue();
    }

    public void assertSameDefaultScale(final Number number) {
        if (number instanceof AScaledDecimal) {
            final AScaledDecimal<?, ?> scaledNumber = (AScaledDecimal<?, ?>) number;
            if (!scaledNumber.getDefaultScale().equals(getDefaultScale())) {
                throw new IllegalArgumentException(
                        "Cannot mix two different default scales on division: " + getDefaultScale() + " [" + this
                                + "]  != " + scaledNumber.getDefaultScale() + " [" + scaledNumber + "]");
            }
        }
    }

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

    public static <T, D extends ADecimal<D>> List<D> extractValues(final Function<T, D> getter, final List<T> objects) {
        final List<D> decimals = new ArrayList<D>();
        for (final T obj : objects) {
            final D decimal = getter.apply(obj);
            decimals.add(decimal);
        }
        return decimals;
    }

    public static <T, D extends ADecimal<D>> List<D> extractValues(final Function<T, D> getter, final T... objects) {
        return extractValues(getter, Arrays.asList(objects));
    }

}