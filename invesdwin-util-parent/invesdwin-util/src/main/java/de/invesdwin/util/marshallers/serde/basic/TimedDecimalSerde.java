package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.marshallers.serde.FixedLengthBufferingIteratorDelegateSerde;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.TimedDecimal;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.time.date.FDate;

@Immutable
public class TimedDecimalSerde implements ISerde<TimedDecimal> {

    public static final TimedDecimalSerde GET = new TimedDecimalSerde();

    public static final int TIME_INDEX = 0;
    public static final int TIME_SIZE = FDate.BYTES;

    public static final int VALUE_INDEX = TIME_INDEX + TIME_SIZE;
    public static final int VALUE_SIZE = Decimal.BYTES;

    public static final int FIXED_LENGTH = VALUE_INDEX + VALUE_SIZE;

    public static final FixedLengthBufferingIteratorDelegateSerde<TimedDecimal> GET_LIST = new FixedLengthBufferingIteratorDelegateSerde<TimedDecimal>(
            GET, FIXED_LENGTH);

    static {
        Assertions.assertThat(GET.fromBytes(GET.toBytes(TimedDecimal.DUMMY))).isEqualTo(TimedDecimal.DUMMY);
    }

    public TimedDecimalSerde() {}

    @Override
    public TimedDecimal fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final TimedDecimal obj) {
        return SerdeBaseMethods.toBytes(this, obj);
    }

    @Override
    public TimedDecimal fromBuffer(final IByteBuffer buffer) {
        if (buffer.capacity() == 0) {
            return null;
        }
        final long time = buffer.getLong(TIME_INDEX);
        final double value = buffer.getDouble(VALUE_INDEX);

        final TimedDecimal timedMoney = new TimedDecimal(FDate.valueOf(time), value);
        return timedMoney;
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final TimedDecimal obj) {
        if (obj == null) {
            return 0;
        }
        buffer.putLong(TIME_INDEX, obj.getTime().millisValue());
        buffer.putDouble(VALUE_INDEX, obj.doubleValue());
        return FIXED_LENGTH;
    }

}
