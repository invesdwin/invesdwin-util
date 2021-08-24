package de.invesdwin.util.lang;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.math.decimal.Decimal;
import io.netty.util.concurrent.FastThreadLocal;

@Immutable
public final class Currencies {

    public static final int BYTES = 3;

    public static final String CNY_SYMBOL = "元";
    public static final String JPY_SYMBOL = "¥";
    public static final String GBP_SYMBOL = "£";
    public static final String EUR_SYMBOL = "€";
    public static final String USD_SYMBOL = "$";

    public static final String PCT_SYMBOL = "%";
    public static final String PIP_SYMBOL = "pip";
    public static final String ABS_SYMBOL = "";

    public static final Currency ARS = Currency.getInstance("ARS");
    public static final Currency AUD = Currency.getInstance("AUD");
    public static final Currency BGN = Currency.getInstance("BGN");
    public static final Currency BRL = Currency.getInstance("BRL");
    public static final Currency CAD = Currency.getInstance("CAD");
    public static final Currency CHF = Currency.getInstance("CHF");
    public static final Currency CNY = Currency.getInstance("CNY");
    public static final Currency CZK = Currency.getInstance("CZK");
    public static final Currency DKK = Currency.getInstance("DKK");
    public static final Currency EUR = Currency.getInstance("EUR");
    public static final Currency GBP = Currency.getInstance("GBP");
    public static final Currency HKD = Currency.getInstance("HKD");
    public static final Currency HUF = Currency.getInstance("HUF");
    public static final Currency INR = Currency.getInstance("INR");
    public static final Currency JPY = Currency.getInstance("JPY");
    public static final Currency MXN = Currency.getInstance("MXN");
    public static final Currency NOK = Currency.getInstance("NOK");
    public static final Currency NZD = Currency.getInstance("NZD");
    public static final Currency PLN = Currency.getInstance("PLN");
    public static final Currency RON = Currency.getInstance("RON");
    public static final Currency RUB = Currency.getInstance("RUB");
    public static final Currency SAR = Currency.getInstance("SAR");
    public static final Currency SEK = Currency.getInstance("SEK");
    public static final Currency SGD = Currency.getInstance("SGD");
    public static final Currency TRY = Currency.getInstance("TRY");
    public static final Currency TWD = Currency.getInstance("TWD");
    public static final Currency USD = Currency.getInstance("USD");
    public static final Currency ZAR = Currency.getInstance("ZAR");

    /**
     * Fake currency to denote percent values that are stored in money objects.
     */
    public static final String PCT = "PCT";
    /**
     * Fake currency to denote pip values that are stored in money objects.
     */
    public static final String PIP = "PIP";
    /**
     * Fake currency to denote absolute values that are stored in money objects.
     */
    public static final String ABS = "ABS";

    private static final Map<String, String> CURRENCY_CODE_2_CURRENCY_SYMBOL = ILockCollectionFactory.getInstance(true)
            .newConcurrentMap();
    private static final ALoadingCache<Locale, FastThreadLocal<NumberFormat>> NUMBER_FORMAT = new ALoadingCache<Locale, FastThreadLocal<NumberFormat>>() {
        @Override
        protected FastThreadLocal<NumberFormat> loadValue(final Locale key) {
            return new FastThreadLocal<NumberFormat>() {
                @Override
                protected NumberFormat initialValue() throws Exception {
                    final NumberFormat nf = NumberFormat.getNumberInstance(key);
                    nf.setMinimumFractionDigits(2);
                    nf.setMaximumFractionDigits(2);
                    return nf;
                }
            };
        }

        @Override
        protected boolean isHighConcurrency() {
            return true;
        }
    };

    static {
        //only put commonly (internationally) known symbols here
        CURRENCY_CODE_2_CURRENCY_SYMBOL.put("USD", USD_SYMBOL);
        CURRENCY_CODE_2_CURRENCY_SYMBOL.put("EUR", EUR_SYMBOL);
        CURRENCY_CODE_2_CURRENCY_SYMBOL.put("GBP", GBP_SYMBOL);
        CURRENCY_CODE_2_CURRENCY_SYMBOL.put("JPY", JPY_SYMBOL);
        CURRENCY_CODE_2_CURRENCY_SYMBOL.put("CNY", CNY_SYMBOL);
        CURRENCY_CODE_2_CURRENCY_SYMBOL.put(PCT, PCT_SYMBOL);
        CURRENCY_CODE_2_CURRENCY_SYMBOL.put(PIP, PIP_SYMBOL);
        CURRENCY_CODE_2_CURRENCY_SYMBOL.put(ABS, ABS_SYMBOL);
    }

    private Currencies() {
    }

    /**
     * This method uses symbols for the most common currencies.
     * 
     * This results in: 1,212.12 € or 1,212.12 CNY
     * 
     * The symbol is put behind the number with a space, to increase readability in tables.
     */
    public static String formatMoney(final Locale locale, final String currencyCode, final Decimal price) {
        String str = NUMBER_FORMAT.get(locale).get().format(price);
        str += " " + getSymbol(currencyCode);
        return str;
    }

    public static String getSymbol(final String currencyCode) {
        final String currencySymbolFromCode = CURRENCY_CODE_2_CURRENCY_SYMBOL.get(currencyCode);
        if (currencySymbolFromCode != null) {
            return currencySymbolFromCode;
        }

        final Currency currency = Currencies.getInstanceOrNull(currencyCode);
        final String symbol;
        if (currency != null) {
            symbol = currency.getSymbol();
        } else {
            symbol = currencyCode;
        }
        final String currencySymbol = CURRENCY_CODE_2_CURRENCY_SYMBOL.get(symbol);
        if (currencySymbol != null) {
            CURRENCY_CODE_2_CURRENCY_SYMBOL.put(currencyCode, currencySymbol);
            if (!symbol.equals(currencySymbol)) {
                CURRENCY_CODE_2_CURRENCY_SYMBOL.put(symbol, currencySymbol);
            }
            return currencySymbol;
        } else {
            CURRENCY_CODE_2_CURRENCY_SYMBOL.put(currencyCode, symbol);
            return symbol;
        }
    }

    public static String replaceCurrencySymbolsWithCurrencyCode(final String str) {
        String replaced = str;
        for (final Entry<String, String> entry : CURRENCY_CODE_2_CURRENCY_SYMBOL.entrySet()) {
            final String code = entry.getKey();
            if (!isFakeCurrency(code)) { //except percent which might not be used as currency
                final String symbol = entry.getValue();
                if (Strings.isNotBlank(symbol)) {
                    replaced = replaced.replace(symbol, code);
                }
            }
        }
        return replaced;
    }

    public static Currency getInstance(final String currencyCode) {
        try {
            return Currency.getInstance(currencyCode);
        } catch (final Throwable t) {
            throw new RuntimeException("On: " + currencyCode, t);
        }
    }

    public static Currency getInstanceOrNull(final String currencyCode) {
        try {
            return Currency.getInstance(currencyCode);
        } catch (final Throwable t) {
            return null;
        }
    }

    public static boolean isFakeCurrency(final String currencyCode) {
        return Strings.equalsAny(currencyCode, PCT, PIP, ABS);
    }

}
