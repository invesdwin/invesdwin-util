package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.buffer.IByteBuffer;
import de.invesdwin.util.marshallers.serde.FixedLengthBufferingIteratorDelegateSerde;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.range.TimeRange;

@Immutable
public class TimeRangeSerde implements ISerde<TimeRange> {

    public static final TimeRangeSerde GET = new TimeRangeSerde();

    public static final int FIXED_LENGTH = FDate.BYTES + FDate.BYTES;

    public static final FixedLengthBufferingIteratorDelegateSerde<TimeRange> GET_LIST = new FixedLengthBufferingIteratorDelegateSerde<TimeRange>(
            GET, FIXED_LENGTH);

    static {
        Assertions.assertThat(GET.fromBytes(GET.toBytes(TimeRange.UNLIMITED))).isEqualTo(TimeRange.UNLIMITED);
    }

    public TimeRangeSerde() {
    }

    @Override
    public TimeRange fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final TimeRange obj) {
        return SerdeBaseMethods.toBytes(this, obj, FIXED_LENGTH);
    }

    @Override
    public TimeRange fromBuffer(final IByteBuffer buffer) {
        return extractTimeRange(buffer, 0);
    }

    @Override
    public int toBuffer(final TimeRange obj, final IByteBuffer buffer) {
        return putTimeRange(buffer, 0, obj);
    }

    public static int putTimeRange(final IByteBuffer buffer, final int index, final TimeRange timeRange) {
        if (timeRange == null) {
            buffer.putLong(index, Long.MIN_VALUE);
            buffer.putLong(index + FDate.BYTES, Long.MIN_VALUE);
        } else {
            FDateSerde.putFDate(buffer, index, timeRange.getFrom());
            FDateSerde.putFDate(buffer, index + FDate.BYTES, timeRange.getTo());
        }
        return FIXED_LENGTH;
    }

    public static TimeRange extractTimeRange(final IByteBuffer buffer, final int index) {
        final long from = buffer.getLong(index);
        final long to = buffer.getLong(index + FDate.BYTES);
        if (from == Long.MIN_VALUE && to == Long.MIN_VALUE) {
            return null;
        } else {
            return new TimeRange(FDateSerde.extractFDate(buffer, index), FDateSerde.extractFDate(buffer, index + 8));
        }
    }

}
