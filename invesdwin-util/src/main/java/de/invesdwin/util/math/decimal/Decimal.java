package de.invesdwin.util.math.decimal;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.internal.DecimalAggregate;
import de.invesdwin.util.math.decimal.internal.DummyDecimalAggregate;

@Immutable
public class Decimal extends ADecimal<Decimal> {

    public static final DecimalFormatSymbols DEFAULT_DECIMAL_FORMAT_SYMBOLS = DecimalFormatSymbols
            .getInstance(Locale.ENGLISH);
    public static final String DEFAULT_DECIMAL_FORMAT = newDefaultDecimalFormat(2);
    public static final String INTEGER_DECIMAL_FORMAT = "#,##0";
    public static final String MONEY_DECIMAL_FORMAT = newMoneyDecimalFormat(2);

    public static final Decimal MINUS_THREE;
    public static final Decimal MINUS_TWO;
    public static final Decimal MINUS_ONE;
    public static final Decimal ZERO;
    public static final Decimal ONE;
    public static final Decimal TWO;
    public static final Decimal THREE;
    public static final Decimal FOUR;
    public static final Decimal FIVE;
    public static final Decimal SIX;
    public static final Decimal TEN;
    public static final Decimal FIFTY;
    public static final Decimal SEVENTYFIVE;
    public static final Decimal ONE_HUNDRED;
    public static final Decimal PI;

    static {
        MINUS_THREE = new Decimal(-3D);
        MINUS_TWO = new Decimal(-2D);
        MINUS_ONE = new Decimal(-1D);
        ZERO = new Decimal(0D);
        ONE = new Decimal(1D);
        TWO = new Decimal(2D);
        THREE = new Decimal(3D);
        FOUR = new Decimal(4D);
        FIVE = new Decimal(5D);
        SIX = new Decimal(6D);
        TEN = new Decimal(10D);
        FIFTY = new Decimal(50D);
        SEVENTYFIVE = new Decimal(75D);
        ONE_HUNDRED = new Decimal(100D);

        PI = new Decimal(Math.PI);
    }

    private final double value;

    public Decimal(final double value) {
        this.value = Doubles.nanToZero(value);
    }

    public Decimal(final Number value) {
        this(value.doubleValue());
    }

    public Decimal(final String value) {
        this(Double.parseDouble(value));
    }

    @Override
    protected double getValue() {
        return value;
    }

    @Override
    protected Decimal newValueCopy(final double value) {
        return new Decimal(value);
    }

    @Override
    public Decimal zero() {
        return ZERO;
    }

    public static Decimal nullToZero(final Decimal value) {
        if (value == null) {
            return ZERO;
        } else {
            return value;
        }
    }

    @Override
    protected Decimal getGenericThis() {
        return this;
    }

    @Override
    public Decimal fromDefaultValue(final double value) {
        return new Decimal(value);
    }

    @Override
    public double getDefaultValue() {
        return getValue();
    }

    public static <T> List<Decimal> extractValues(final Function<T, Decimal> getter, final List<T> objects) {
        final List<Decimal> decimals = new ArrayList<Decimal>();
        for (final T obj : objects) {
            final Decimal decimal = getter.apply(obj);
            decimals.add(decimal);
        }
        return decimals;
    }

    public static <T> List<Decimal> extractValues(final Function<T, Decimal> getter, final T... objects) {
        return extractValues(getter, Arrays.asList(objects));
    }

    /**
     * Use default values of the scaled Decimal instead!
     */
    @Deprecated
    public static Decimal valueOf(final IScaledNumber value) {
        throw new UnsupportedOperationException();
    }

    public static Decimal valueOf(final String value) {
        if (value == null) {
            return null;
        } else {
            return new Decimal(value);
        }
    }

    public static Decimal valueOf(final double value) {
        return new Decimal(value);
    }

    public static Decimal valueOf(final Double value) {
        if (value == null) {
            return null;
        } else {
            return new Decimal(value);
        }
    }

    public static Decimal valueOf(final Number value) {
        if (value == null) {
            return null;
        } else if (value instanceof Decimal) {
            return (Decimal) value;
        } else {
            if (value instanceof IScaledNumber) {
                throw new IllegalArgumentException("value [" + value + "] should not be an instance of "
                        + IScaledNumber.class.getSimpleName() + ": " + value.getClass().getSimpleName());
            }
            return new Decimal(value);
        }
    }

    public static Decimal fromDefaultValue(final AScaledDecimal<?, ?> scaled) {
        if (scaled != null) {
            return new Decimal(scaled.getDefaultValue());
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

    public static void putDecimal(final ByteBuffer buffer, final Decimal value) {
        if (value == null) {
            buffer.putDouble(Double.MIN_VALUE);
        } else {
            buffer.putDouble(value.doubleValue());
        }
    }

    public static Decimal extractDecimal(final ByteBuffer buffer, final int index) {
        final double value = buffer.getDouble(index);
        return extractDecimal(value);
    }

    public static Decimal extractDecimal(final ByteBuffer buffer) {
        final double value = buffer.getDouble();
        return extractDecimal(value);
    }

    public static Decimal extractDecimal(final double value) {
        if (value == Double.MIN_VALUE) {
            return null;
        } else {
            return new Decimal(value);
        }
    }

    public static IDecimalAggregate<Decimal> valueOf(final Decimal... values) {
        return valueOf(Arrays.asList(values));
    }

    public static IDecimalAggregate<Decimal> valueOf(final List<? extends Decimal> values) {
        if (values == null || values.size() == 0) {
            return DummyDecimalAggregate.getInstance();
        } else {
            return new DecimalAggregate<Decimal>(values, Decimal.ZERO);
        }
    }

    public static String newDefaultDecimalFormat(final int decimalDigits) {
        String format = "#,##0";
        if (decimalDigits > 0) {
            format += "." + Strings.repeat("#", decimalDigits);
        }
        return format;
    }

    public static String newMoneyDecimalFormat(final int decimalDigits) {
        String format = "#,##0";
        if (decimalDigits > 0) {
            format += "." + Strings.repeat("0", decimalDigits);
        }
        return format;
    }

    public static DecimalFormat newDecimalFormatInstance(final String format) {
        return newDecimalFormatInstance(format, Decimal.DEFAULT_DECIMAL_FORMAT_SYMBOLS);
    }

    public static DecimalFormat newDecimalFormatInstance(final String format, final Locale locale) {
        return newDecimalFormatInstance(format, DecimalFormatSymbols.getInstance(locale));
    }

    public static DecimalFormat newDecimalFormatInstance(final String format, final DecimalFormatSymbols symbols) {
        final DecimalFormat formatter = new DecimalFormat(format, symbols);
        formatter.setRoundingMode(ADecimal.DEFAULT_ROUNDING_MODE);
        return formatter;
    }

    public static Decimal nanToNull(final Double value) {
        if (value == null) {
            return null;
        } else {
            return nanToNull(value.doubleValue());
        }
    }

    public static Decimal nanToNull(final double value) {
        if (Doubles.isInfinite(value)) {
            throw new IllegalArgumentException("Infinite: " + value);
        }
        if (Doubles.isNaN(value)) {
            return null;
        } else {
            return new Decimal(value);
        }
    }

}
