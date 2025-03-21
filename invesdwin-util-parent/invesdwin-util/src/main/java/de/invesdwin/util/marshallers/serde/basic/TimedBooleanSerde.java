package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.marshallers.serde.FixedLengthBufferingIteratorDelegateSerde;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.timed.TimedBoolean;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.time.date.FDate;

@ThreadSafe
public class TimedBooleanSerde implements ISerde<TimedBoolean> {

    public static final TimedBooleanSerde GET = new TimedBooleanSerde();

    public static final int TIME_INDEX = 0;
    public static final int TIME_SIZE = FDate.BYTES;

    public static final int VALUE_INDEX = TIME_INDEX + TIME_SIZE;
    public static final int VALUE_SIZE = Booleans.BYTES;

    public static final int FIXED_LENGTH = VALUE_INDEX + VALUE_SIZE;

    public static final FixedLengthBufferingIteratorDelegateSerde<TimedBoolean> GET_LIST = new FixedLengthBufferingIteratorDelegateSerde<TimedBoolean>(
            GET, FIXED_LENGTH);

    static {
        Assertions.assertThat(GET.fromBytes(GET.toBytes(TimedBoolean.DUMMY))).isEqualTo(TimedBoolean.DUMMY);
    }

    @Override
    public TimedBoolean fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final TimedBoolean obj) {
        return SerdeBaseMethods.toBytes(this, obj);
    }

    @Override
    public TimedBoolean fromBuffer(final IByteBuffer buffer) {
        if (buffer.capacity() == 0) {
            return null;
        }
        final long time = buffer.getLong(TIME_INDEX);
        final boolean value = buffer.getBoolean(VALUE_INDEX);

        final TimedBoolean timedMoney = new TimedBoolean(FDate.valueOf(time), value);
        return timedMoney;
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final TimedBoolean obj) {
        if (obj == null) {
            return 0;
        }
        buffer.putLong(TIME_INDEX, obj.getTime().millisValue());
        buffer.putBoolean(VALUE_INDEX, obj.booleanValue());
        return FIXED_LENGTH;
    }

}
