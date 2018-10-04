package de.invesdwin.util.math.doubles;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.AScaledDecimal;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.IScaledNumber;
import de.invesdwin.util.math.doubles.internal.DummyFDoubleAggregate;
import de.invesdwin.util.math.doubles.internal.FDoubleAggregate;

@Immutable
public class FDouble extends AFDouble<FDouble> {

    public static final FDouble MINUS_THREE;
    public static final FDouble MINUS_TWO;
    public static final FDouble MINUS_ONE;
    public static final FDouble ZERO;
    public static final FDouble ONE;
    public static final FDouble TWO;
    public static final FDouble THREE;
    public static final FDouble FOUR;
    public static final FDouble FIVE;
    public static final FDouble SIX;
    public static final FDouble TEN;
    public static final FDouble FIFTY;
    public static final FDouble SEVENTYFIVE;
    public static final FDouble ONE_HUNDRED;
    public static final FDouble PI;

    static {
        MINUS_THREE = new FDouble(-3D);
        MINUS_TWO = new FDouble(-2D);
        MINUS_ONE = new FDouble(-1D);
        ZERO = new FDouble(0D);
        ONE = new FDouble(1D);
        TWO = new FDouble(2D);
        THREE = new FDouble(3D);
        FOUR = new FDouble(4D);
        FIVE = new FDouble(5D);
        SIX = new FDouble(6D);
        TEN = new FDouble(10D);
        FIFTY = new FDouble(50D);
        SEVENTYFIVE = new FDouble(75D);
        ONE_HUNDRED = new FDouble(100D);

        PI = new FDouble(Math.PI);
    }

    private final double value;

    public FDouble(final double value) {
        this.value = Doubles.nanToZero(value);
    }

    public FDouble(final Decimal value) {
        this(value.doubleValueRaw());
    }

    public FDouble(final Number value) {
        this(value.doubleValue());
    }

    public FDouble(final String value) {
        this(Double.parseDouble(value));
    }

    @Override
    protected double getValue() {
        return value;
    }

    @Override
    protected FDouble newValueCopy(final double value) {
        return new FDouble(value);
    }

    @Override
    public FDouble zero() {
        return ZERO;
    }

    public static FDouble nullToZero(final FDouble value) {
        if (value == null) {
            return ZERO;
        } else {
            return value;
        }
    }

    @Override
    protected FDouble getGenericThis() {
        return this;
    }

    @Override
    public FDouble fromDefaultValue(final double value) {
        return new FDouble(value);
    }

    @Override
    public double getDefaultValue() {
        return getValue();
    }

    public static <T> List<FDouble> extractValues(final Function<T, FDouble> getter, final List<T> objects) {
        final List<FDouble> fdoubles = new ArrayList<FDouble>();
        for (final T obj : objects) {
            final FDouble fdouble = getter.apply(obj);
            fdoubles.add(fdouble);
        }
        return fdoubles;
    }

    public static <T> List<FDouble> extractValues(final Function<T, FDouble> getter, final T... objects) {
        return extractValues(getter, Arrays.asList(objects));
    }

    /**
     * Use default values of the scaled FDouble instead!
     */
    @Deprecated
    public static FDouble valueOf(final IScaledNumber value) {
        throw new UnsupportedOperationException();
    }

    public static FDouble valueOf(final String value) {
        if (value == null) {
            return null;
        } else {
            return new FDouble(value);
        }
    }

    public static FDouble valueOf(final double value) {
        return new FDouble(value);
    }

    public static FDouble valueOf(final Double value) {
        if (value == null) {
            return null;
        } else {
            return new FDouble(value);
        }
    }

    public static FDouble valueOf(final Number value) {
        if (value == null) {
            return null;
        } else if (value instanceof FDouble) {
            return (FDouble) value;
        } else {
            if (value instanceof IScaledNumber) {
                throw new IllegalArgumentException("value [" + value + "] should not be an instance of "
                        + IScaledNumber.class.getSimpleName() + ": " + value.getClass().getSimpleName());
            }
            return new FDouble(value);
        }
    }

    public static FDouble fromDefaultValue(final AScaledDecimal<?, ?> scaled) {
        if (scaled != null) {
            return new FDouble(scaled.getDefaultValue());
        } else {
            return null;
        }
    }

    public static FDouble fromDefaultValue(final AScaledFDouble<?, ?> scaled) {
        if (scaled != null) {
            return new FDouble(scaled.getDefaultValue());
        } else {
            return null;
        }
    }

    @Override
    public String toFormattedString() {
        return toFormattedString(Decimal.DEFAULT_DECIMAL_FORMAT);
    }

    @Override
    public String toFormattedString(final String format) {
        final DecimalFormat dc = Decimal.newDecimalFormatInstance(format);
        final String str = dc.format(this);
        if (str.startsWith("-0") && str.matches("-0([\\.,](0)*)?")) {
            return Strings.removeStart(str, "-");
        } else {
            return str;
        }
    }

    public static void putFDouble(final ByteBuffer buffer, final FDouble value) {
        if (value == null) {
            buffer.putDouble(Double.MIN_VALUE);
        } else {
            buffer.putDouble(value.doubleValue());
        }
    }

    public static FDouble extractFDouble(final ByteBuffer buffer, final int index) {
        final double value = buffer.getDouble(index);
        return extractFDouble(value);
    }

    public static FDouble extractFDouble(final ByteBuffer buffer) {
        final double value = buffer.getDouble();
        return extractFDouble(value);
    }

    public static FDouble extractFDouble(final double value) {
        if (value == Double.MIN_VALUE) {
            return null;
        } else {
            return new FDouble(value);
        }
    }

    public static IFDoubleAggregate<FDouble> valueOf(final FDouble... values) {
        return valueOf(Arrays.asList(values));
    }

    public static IFDoubleAggregate<FDouble> valueOf(final List<? extends FDouble> values) {
        if (values == null || values.size() == 0) {
            return DummyFDoubleAggregate.getInstance();
        } else {
            return new FDoubleAggregate<FDouble>(values, FDouble.ZERO);
        }
    }

}
