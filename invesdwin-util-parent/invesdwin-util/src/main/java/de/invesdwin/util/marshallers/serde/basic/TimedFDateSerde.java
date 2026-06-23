package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.marshallers.serde.FixedLengthBufferingIteratorDelegateSerde;
import de.invesdwin.util.marshallers.serde.ISerde;
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
    public TimedFDate fromBuffer(final IByteBuffer buffer) {
        if (buffer.capacity() == 0) {
            return null;
        }
        final FDate time = FDateSerde.getFDateNotNullSafe(buffer, TIME_INDEX);
        final FDate value = FDateSerde.getFDateNotNullSafe(buffer, VALUE_INDEX);

        final TimedFDate timedMoney = new TimedFDate(time, value);
        return timedMoney;
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final TimedFDate obj) {
        if (obj == null) {
            return 0;
        }
        FDateSerde.putFDateNotNullSafe(buffer, TIME_INDEX, obj.getTime());
        FDateSerde.putFDateNotNullSafe(buffer, VALUE_INDEX, obj.getValue());
        return FIXED_LENGTH;
    }

}
