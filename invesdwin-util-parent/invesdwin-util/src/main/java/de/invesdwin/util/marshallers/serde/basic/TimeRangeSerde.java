package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.FixedLengthBufferingIteratorDelegateSerde;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.range.TimeRange;

@Immutable
public class TimeRangeSerde implements ISerde<TimeRange> {

    public static final int FROM_INDEX = 0;
    public static final int FROM_SIZE = FDate.BYTES;

    public static final int TO_INDEX = FROM_INDEX + FROM_SIZE;
    public static final int TO_SIZE = FDate.BYTES;

    public static final int FIXED_LENGTH = TO_INDEX + +TO_SIZE;

    public static final TimeRangeSerde GET = new TimeRangeSerde();
    public static final FixedLengthBufferingIteratorDelegateSerde<TimeRange> GET_LIST = new FixedLengthBufferingIteratorDelegateSerde<TimeRange>(
            GET, FIXED_LENGTH);

    public TimeRangeSerde() {}

    @Override
    public TimeRange fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final TimeRange obj) {
        return SerdeBaseMethods.toBytes(this, obj);
    }

    @Override
    public TimeRange fromBuffer(final IByteBuffer buffer) {
        return getTimeRange(buffer, 0);
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final TimeRange obj) {
        return putTimeRange(buffer, 0, obj);
    }

    public static int putTimeRange(final IByteBuffer buffer, final int index, final TimeRange timeRange) {
        if (timeRange == null) {
            buffer.putLong(index + FROM_INDEX, Long.MIN_VALUE);
            buffer.putLong(index + TO_INDEX, Long.MIN_VALUE);
        } else {
            FDateSerde.putFDate(buffer, index + FROM_INDEX, timeRange.getFrom());
            FDateSerde.putFDate(buffer, index + TO_INDEX, timeRange.getTo());
        }
        return FIXED_LENGTH;
    }

    public static TimeRange getTimeRange(final IByteBuffer buffer, final int index) {
        final long from = buffer.getLong(index + FROM_INDEX);
        final long to = buffer.getLong(index + TO_INDEX);
        if (from == Long.MIN_VALUE && to == Long.MIN_VALUE) {
            return null;
        } else {
            return new TimeRange(FDateSerde.getFDate(from), FDateSerde.getFDate(to));
        }
    }

}
