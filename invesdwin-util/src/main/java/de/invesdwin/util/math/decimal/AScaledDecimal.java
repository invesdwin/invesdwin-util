package de.invesdwin.util.math.decimal;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.decimal.internal.impl.ADecimalImpl;
import de.invesdwin.util.math.decimal.scaled.IDecimalScale;

@SuppressWarnings({ "rawtypes", "serial" })
@Immutable
public abstract class AScaledDecimal<T extends AScaledDecimal<T, S>, S extends IDecimalScale<T, S>>
        extends ADecimal<T> {

    private static final DecimalFormatSymbols ENGLISH_DECIMAL_FORMAT_SYMBOLS = DecimalFormatSymbols
            .getInstance(Locale.ENGLISH);
    protected final S scale;
    private final ScaledDecimalDelegateImpl impl;

    protected AScaledDecimal(final Decimal value, final S scale) {
        this.impl = new ScaledDecimalDelegateImpl(this, Decimal.nullToZero(value).getImpl());
        validateScale(scale);
        this.scale = scale;
    }

    protected void validateScale(final S scale) {}

    protected abstract T newValueCopy(Decimal value, S scale);

    @Override
    protected ScaledDecimalDelegateImpl getImpl() {
        return impl;
    }

    @Override
    protected final T newValueCopy(final ADecimalImpl value) {
        return newValueCopy(new Decimal(value), scale);
    }

    @Override
    public final T fromDefaultValue(final Decimal value) {
        final Decimal scaledValue = scale.convertValue(getGenericThis(), value, getDefaultScale());
        return newValueCopy(scaledValue, scale);
    }

    @Override
    public final Decimal getDefaultValue() {
        return getValue(getDefaultScale());
    }

    public final Decimal getValue(final S scale) {
        validateScale(scale);
        if (scale == this.scale) {
            return new Decimal(getImpl().getDelegate());
        } else {
            return scale.convertValue(getGenericThis(), new Decimal(getImpl().getDelegate()), this.scale);
        }
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() + getScale().hashCode() + getDefaultValue().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj != null && getGenericThis().getClass().isAssignableFrom(obj.getClass())) {
            final AScaledDecimal castedObj = (AScaledDecimal) obj;
            return castedObj.getDefaultValue().equals(this.getDefaultValue());
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

    public String toString(final boolean withSymbol) {
        return toString(scale, withSymbol);
    }

    public String toString(final S scale) {
        return toString(scale, true);
    }

    public String toString(final S scale, final boolean withSymbol) {
        final String formatStr = scale.getFormat(getGenericThis(), withSymbol);
        final DecimalFormat format = new DecimalFormat(formatStr, ENGLISH_DECIMAL_FORMAT_SYMBOLS);
        return format.format(getValue(scale).getImpl().numberValue());
    }

    public static <T extends AScaledDecimal<T, ?>> IDecimalAggregate<T> valueOf(final T... values) {
        return valueOf(Arrays.asList(values));
    }

    public static <T extends AScaledDecimal<T, ?>> IDecimalAggregate<T> valueOf(final List<? extends T> values) {
        if (values == null || values.size() == 0) {
            return DummyDecimalAggregate.getInstance();
        } else {
            return new DecimalAggregate<T>(values);
        }
    }

    public T asScale(final S scale) {
        validateScale(scale);
        return newValueCopy(getValue(scale), scale);
    }

}