package de.invesdwin.util.marshallers.serde;

import java.util.Calendar;
import java.util.Currency;
import java.util.Date;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.marshallers.serde.basic.BooleanSerde;
import de.invesdwin.util.marshallers.serde.basic.ByteSerde;
import de.invesdwin.util.marshallers.serde.basic.CalendarSerde;
import de.invesdwin.util.marshallers.serde.basic.CurrencySerde;
import de.invesdwin.util.marshallers.serde.basic.DateSerde;
import de.invesdwin.util.marshallers.serde.basic.DecimalSerde;
import de.invesdwin.util.marshallers.serde.basic.DoubleSerde;
import de.invesdwin.util.marshallers.serde.basic.FDateSerde;
import de.invesdwin.util.marshallers.serde.basic.IntegerSerde;
import de.invesdwin.util.marshallers.serde.basic.LongSerde;
import de.invesdwin.util.marshallers.serde.basic.StringUtf8Serde;
import de.invesdwin.util.marshallers.serde.basic.TimedBooleanSerde;
import de.invesdwin.util.marshallers.serde.basic.TimedDecimalSerde;
import de.invesdwin.util.marshallers.serde.basic.TimedDoubleSerde;
import de.invesdwin.util.marshallers.serde.basic.TimedFDateSerde;
import de.invesdwin.util.marshallers.serde.basic.VoidSerde;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.TimedDecimal;
import de.invesdwin.util.math.timed.TimedBoolean;
import de.invesdwin.util.math.timed.TimedDouble;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.TimedFDate;

@Immutable
public class TypeDelegateSerde<O> implements ISerde<O> {

    private final ISerde<O> delegate;
    private final Integer fixedLength;

    @SuppressWarnings("unchecked")
    public TypeDelegateSerde(final Class<O> type) {
        delegate = (ISerde<O>) newDelegate(type);
        fixedLength = newFixedLength(type);
    }

    public Integer getFixedLength() {
        return fixedLength;
    }

    protected ISerde<?> newDelegate(final Class<O> type) {
        if (Reflections.isVoid(type)) {
            return VoidSerde.GET;
        } else if (Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type)) {
            return DoubleSerde.GET;
        } else if (Decimal.class.isAssignableFrom(type)) {
            return DecimalSerde.GET;
        } else if (FDate.class.isAssignableFrom(type)) {
            return FDateSerde.GET;
        } else if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type)) {
            return BooleanSerde.GET;
        } else if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
            return IntegerSerde.GET;
        } else if (TimedDecimal.class.isAssignableFrom(type)) {
            return TimedDecimalSerde.GET;
        } else if (TimedDouble.class.isAssignableFrom(type)) {
            return TimedDoubleSerde.GET;
        } else if (TimedBoolean.class.isAssignableFrom(type)) {
            return TimedBooleanSerde.GET;
        } else if (TimedFDate.class.isAssignableFrom(type)) {
            return TimedFDateSerde.GET;
        } else if (Byte.class.isAssignableFrom(type) || byte.class.isAssignableFrom(type)) {
            return ByteSerde.GET;
        } else if (Date.class.isAssignableFrom(type)) {
            return DateSerde.GET;
        } else if (Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)) {
            return LongSerde.GET;
        } else if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
            return IntegerSerde.GET;
        } else if (Calendar.class.isAssignableFrom(type)) {
            return CalendarSerde.GET;
        } else if (String.class.isAssignableFrom(type)) {
            return StringUtf8Serde.GET;
        } else if (Currency.class.isAssignableFrom(type)) {
            return CurrencySerde.GET;
        } else {
            // fallback to slower serialization
            // do not check type to gracefully fall back on interface types like List
            return new RemoteFastSerializingSerde<O>(true, type);
        }
    }

    protected Integer newFixedLength(final Class<O> type) {
        if (Reflections.isVoid(type)) {
            return VoidSerde.FIXED_LENGTH;
        } else if (Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type)) {
            return DoubleSerde.FIXED_LENGTH;
        } else if (Decimal.class.isAssignableFrom(type)) {
            return DecimalSerde.FIXED_LENGTH;
        } else if (FDate.class.isAssignableFrom(type)) {
            return FDateSerde.FIXED_LENGTH;
        } else if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type)) {
            return BooleanSerde.FIXED_LENGTH;
        } else if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
            return IntegerSerde.FIXED_LENGTH;
        } else if (TimedDecimal.class.isAssignableFrom(type)) {
            return TimedDecimalSerde.FIXED_LENGTH;
        } else if (Byte.class.isAssignableFrom(type) || byte.class.isAssignableFrom(type)) {
            return null;
        } else if (Date.class.isAssignableFrom(type)) {
            return DateSerde.FIXED_LENGTH;
        } else if (Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)) {
            return LongSerde.FIXED_LENGTH;
        } else if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
            return IntegerSerde.FIXED_LENGTH;
        } else if (Calendar.class.isAssignableFrom(type)) {
            return CalendarSerde.FIXED_LENGTH;
        } else if (String.class.isAssignableFrom(type)) {
            return null;
        } else if (Currency.class.isAssignableFrom(type)) {
            return CurrencySerde.FIXED_LENGTH;
        } else {
            // fallback to slower serialization
            // do not check type to gracefully fall back on interface types like List
            return null;
        }
    }

    @Override
    public O fromBytes(final byte[] bytes) {
        return delegate.fromBytes(bytes);
    }

    @Override
    public byte[] toBytes(final O obj) {
        return delegate.toBytes(obj);
    }

    @Override
    public O fromBuffer(final IByteBuffer buffer) {
        return delegate.fromBuffer(buffer);
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final O obj) {
        return delegate.toBuffer(buffer, obj);
    }

}
