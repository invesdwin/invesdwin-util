package de.invesdwin.util.math.decimal;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.math.decimal.internal.DecimalAggregate;
import de.invesdwin.util.math.decimal.internal.DummyDecimalAggregate;
import de.invesdwin.util.math.decimal.internal.impl.ADecimalImpl;
import de.invesdwin.util.math.decimal.internal.impl.DoubleDecimalImplFactory;
import de.invesdwin.util.math.decimal.internal.impl.IDecimalImplFactory;

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

    /**
     * Due to implementation simplifications of equals, hashCode and compareTo only one implementations cannot be mixed
     * with each other.
     */
    private static final IDecimalImplFactory<?> DECIMAL_IMPL_FACTORY;

    static {
        /*
         * double is the fastest implementation, thus defaulting to that. The other ones are still there for comparison
         * purposes.
         */
        DECIMAL_IMPL_FACTORY = new DoubleDecimalImplFactory();
        MINUS_THREE = new Decimal("-3");
        MINUS_TWO = new Decimal("-2");
        MINUS_ONE = new Decimal("-1");
        ZERO = new Decimal("0");
        ONE = new Decimal("1");
        TWO = new Decimal("2");
        THREE = new Decimal("3");
        FOUR = new Decimal("4");
        FIVE = new Decimal("5");
        SIX = new Decimal("6");
        TEN = new Decimal("10");
        FIFTY = new Decimal("50");
        SEVENTYFIVE = new Decimal("75");
        ONE_HUNDRED = new Decimal("100");

        PI = new Decimal(Math.PI);
    }

    private final ADecimalImpl<?, ?> impl;

    public Decimal(final Number value) {
        this(DECIMAL_IMPL_FACTORY.valueOf(value));
    }

    public Decimal(final Double value) {
        this(DECIMAL_IMPL_FACTORY.valueOf(value));
    }

    public Decimal(final Float value) {
        this(DECIMAL_IMPL_FACTORY.valueOf(value));
    }

    public Decimal(final Long value) {
        this(DECIMAL_IMPL_FACTORY.valueOf(value));
    }

    public Decimal(final Integer value) {
        this(DECIMAL_IMPL_FACTORY.valueOf(value));
    }

    public Decimal(final Short value) {
        this(DECIMAL_IMPL_FACTORY.valueOf(value));
    }

    public Decimal(final Byte value) {
        this(DECIMAL_IMPL_FACTORY.valueOf(value));
    }

    public Decimal(final String value) {
        this(DECIMAL_IMPL_FACTORY.valueOf(value));
    }

    public Decimal(final ADecimalImpl<?, ?> impl) {
        if (impl instanceof ScaledDecimalDelegateImpl) {
            this.impl = ((ScaledDecimalDelegateImpl) impl).getDelegate();
        } else {
            this.impl = impl;
        }
    }

    @Override
    public ADecimalImpl getImpl() {
        return impl;
    }

    @Override
    protected Decimal newValueCopy(final ADecimalImpl value) {
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
    public Decimal fromDefaultValue(final Decimal value) {
        return value;
    }

    @Override
    public Decimal getDefaultValue() {
        return this;
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
     * Use default values of the scaled decimal instead!
     */
    @Deprecated
    public static Decimal valueOf(final AScaledDecimal<?, ?> value) {
        throw new UnsupportedOperationException();
    }

    public static Decimal valueOf(final String value) {
        if (value == null) {
            return null;
        } else {
            return new Decimal(value);
        }
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
            if (value instanceof AScaledDecimal) {
                throw new IllegalArgumentException("value [" + value + "] should not be an instance of "
                        + AScaledDecimal.class.getSimpleName() + ": " + value.getClass().getSimpleName());
            }
            return new Decimal(value);
        }
    }

    public static Decimal fromDefaultValue(final AScaledDecimal<?, ?> scaledDecimal) {
        if (scaledDecimal != null) {
            return scaledDecimal.getDefaultValue();
        } else {
            return null;
        }
    }

    @Override
    public String toFormattedString() {
        return toFormattedString(DEFAULT_DECIMAL_FORMAT);
    }

    @Override
    public String toFormattedString(final String format) {
        final DecimalFormat dc = newDecimalFormatInstance(format);
        final String str = dc.format(this);
        if (str.startsWith("-0") && str.matches("-0([\\.,](0)*)?")) {
            return Strings.removeStart(str, "-");
        } else {
            return str;
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

    public static <T extends ADecimal<?>> double[] toPrimitive(final T[] array) {
        final double[] doubleArray = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            doubleArray[i] = array[i].getDefaultValue().doubleValueRaw();
        }
        return doubleArray;
    }

    public static Decimal[] toObject(final double[] array) {
        final Decimal[] decimalArray = new Decimal[array.length];
        for (int i = 0; i < array.length; i++) {
            decimalArray[i] = new Decimal(array[i]);
        }
        return decimalArray;
    }

}
