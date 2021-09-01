package de.invesdwin.util.marshallers.serde.basic;

import java.util.Currency;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Charsets;
import de.invesdwin.util.lang.Currencies;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.streams.buffer.ByteBuffers;
import de.invesdwin.util.streams.buffer.IByteBuffer;

@Immutable
public final class CurrencySerde implements ISerde<Currency> {

    public static final String MISSING_CURRENCY_CODE = "___";
    public static final byte[] MISSING_CURRENCY_CODE_BYTES = ByteBuffers.newStringAsciiBytes(MISSING_CURRENCY_CODE);

    public static final CurrencySerde GET = new CurrencySerde();
    public static final int FIXED_LENGTH = Currencies.BYTES;

    private CurrencySerde() {
    }

    @Override
    public Currency fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final Currency obj) {
        return SerdeBaseMethods.toBytes(this, obj);
    }

    @Override
    public Currency fromBuffer(final IByteBuffer buffer, final int length) {
        if (length == 0) {
            return null;
        }
        final String currencyCode = buffer.getStringAsciii(0, length);
        return Currencies.getInstance(currencyCode);
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final Currency obj) {
        if (obj == null) {
            return 0;
        }
        final String currencyCode = obj.getCurrencyCode();
        buffer.putStringAsciii(0, currencyCode);
        return ByteBuffers.newStringAsciiLength(currencyCode);
    }

    public static int putCurrency(final IByteBuffer buffer, final int index, final Currency currency) {
        if (currency == null) {
            buffer.putBytes(index, MISSING_CURRENCY_CODE_BYTES);
        } else {
            buffer.putStringAsciii(index, currency.getCurrencyCode());
        }
        return FIXED_LENGTH;
    }

    public static int putCurrencyCode(final IByteBuffer buffer, final int index, final String currencyCode) {
        if (currencyCode == null) {
            buffer.putBytes(index, MISSING_CURRENCY_CODE_BYTES);
        } else {
            final byte[] bytes = currencyCode.getBytes(Charsets.UTF_8);
            Assertions.checkEquals(FIXED_LENGTH, bytes.length);
            buffer.putBytes(index, bytes);
        }
        return FIXED_LENGTH;
    }

    public static Currency getCurrency(final IByteBuffer buffer, final int index) {
        final String currencyCode = buffer.getStringAsciii(index, FIXED_LENGTH);
        if (MISSING_CURRENCY_CODE.equals(currencyCode)) {
            return null;
        } else {
            return Currencies.getInstance(currencyCode);
        }
    }

    public static String getCurrencyCode(final IByteBuffer buffer, final int index) {
        final String currencyCode = buffer.getStringAsciii(index, FIXED_LENGTH);
        if (MISSING_CURRENCY_CODE.equals(currencyCode)) {
            return null;
        } else {
            return currencyCode;
        }
    }

}
