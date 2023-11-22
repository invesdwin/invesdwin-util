package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.marshallers.serde.FixedLengthBufferingIteratorDelegateSerde;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.math.TimedDouble;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.time.date.FDate;

@ThreadSafe
public class TimedDoubleSerde implements ISerde<TimedDouble> {

    public static final TimedDoubleSerde GET = new TimedDoubleSerde();

    public static final int TIME_INDEX = 0;
    public static final int TIME_SIZE = FDate.BYTES;

    public static final int VALUE_INDEX = TIME_INDEX + TIME_SIZE;
    public static final int VALUE_SIZE = Double.BYTES;

    public static final int FIXED_LENGTH = VALUE_INDEX + VALUE_SIZE;

    public static final FixedLengthBufferingIteratorDelegateSerde<TimedDouble> GET_LIST = new FixedLengthBufferingIteratorDelegateSerde<TimedDouble>(
            GET, FIXED_LENGTH);

    static {
        Assertions.assertThat(GET.fromBytes(GET.toBytes(TimedDouble.DUMMY))).isEqualTo(TimedDouble.DUMMY);
    }

    @Override
    public TimedDouble fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final TimedDouble obj) {
        return SerdeBaseMethods.toBytes(this, obj);
    }

    @Override
    public TimedDouble fromBuffer(final IByteBuffer buffer) {
        if (buffer.capacity() == 0) {
            return null;
        }
        final long time = buffer.getLong(TIME_INDEX);
        final double value = buffer.getDouble(VALUE_INDEX);

        final TimedDouble timedMoney = new TimedDouble(FDate.valueOf(time), value);
        return timedMoney;
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final TimedDouble obj) {
        if (obj == null) {
            return 0;
        }
        buffer.putLong(TIME_INDEX, obj.getTime().millisValue());
        buffer.putDouble(VALUE_INDEX, obj.doubleValue());
        return FIXED_LENGTH;
    }

}
