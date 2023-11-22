package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.marshallers.serde.FixedLengthBufferingIteratorDelegateSerde;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.TimedFDate;

@ThreadSafe
public class TimedFDateSerde implements ISerde<TimedFDate> {

    public static final TimedFDateSerde GET = new TimedFDateSerde();

    public static final int TIME_INDEX = 0;
    public static final int TIME_SIZE = FDate.BYTES;

    public static final int VALUE_INDEX = TIME_INDEX + TIME_SIZE;
    public static final int VALUE_SIZE = FDate.BYTES;

    public static final int FIXED_LENGTH = VALUE_INDEX + VALUE_SIZE;

    public static final FixedLengthBufferingIteratorDelegateSerde<TimedFDate> GET_LIST = new FixedLengthBufferingIteratorDelegateSerde<TimedFDate>(
            GET, FIXED_LENGTH);

    static {
        Assertions.assertThat(GET.fromBytes(GET.toBytes(TimedFDate.DUMMY))).isEqualTo(TimedFDate.DUMMY);
    }

    @Override
    public TimedFDate fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final TimedFDate obj) {
        return SerdeBaseMethods.toBytes(this, obj);
    }

    @Override
    public TimedFDate fromBuffer(final IByteBuffer buffer) {
        if (buffer.capacity() == 0) {
            return null;
        }
        final long time = buffer.getLong(TIME_INDEX);
        final long value = buffer.getLong(VALUE_INDEX);

        final TimedFDate timedMoney = new TimedFDate(FDate.valueOf(time), FDate.valueOf(value));
        return timedMoney;
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final TimedFDate obj) {
        if (obj == null) {
            return 0;
        }
        buffer.putLong(TIME_INDEX, obj.getTime().millisValue());
        buffer.putLong(VALUE_INDEX, obj.getValue().millisValue());
        return FIXED_LENGTH;
    }

}
