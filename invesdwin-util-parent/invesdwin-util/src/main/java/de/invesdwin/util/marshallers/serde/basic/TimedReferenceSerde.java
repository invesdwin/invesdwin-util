package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.reference.TimedReference;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.time.date.FDate;

@Immutable
public class TimedReferenceSerde<V> implements ISerde<TimedReference<V>> {

    public static final int TIME_INDEX = 0;
    public static final int TIME_SIZE = FDate.BYTES;

    public static final int VALUE_INDEX = TIME_INDEX + TIME_SIZE;
    private final ISerde<V> valueSerde;
    private final Integer valueFixedLength;
    private final Integer fixedLength;

    public TimedReferenceSerde(final ISerde<V> valueSerde, final Integer valueFixedLength) {
        this.valueSerde = valueSerde;
        this.valueFixedLength = valueFixedLength;
        if (valueFixedLength == null) {
            fixedLength = null;
        } else {
            fixedLength = VALUE_INDEX + valueFixedLength;
        }
    }

    public ISerde<V> getValueSerde() {
        return valueSerde;
    }

    public Integer getValueFixedLength() {
        return valueFixedLength;
    }

    public Integer getFixedLength() {
        return fixedLength;
    }

    @Override
    public TimedReference<V> fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final TimedReference<V> obj) {
        return SerdeBaseMethods.toBytes(this, obj);
    }

    @Override
    public TimedReference<V> fromBuffer(final IByteBuffer buffer) {
        if (buffer.capacity() == 0) {
            return null;
        }
        final long time = buffer.getLong(TIME_INDEX);
        final V value = valueSerde.fromBuffer(buffer.sliceFrom(VALUE_INDEX));

        final TimedReference<V> timedReference = new TimedReference<V>(FDate.valueOf(time), value);
        return timedReference;
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final TimedReference<V> obj) {
        if (obj == null) {
            return 0;
        }
        buffer.putLong(TIME_INDEX, obj.getTime().millisValue());
        final int valueSize = valueSerde.toBuffer(buffer.sliceFrom(VALUE_INDEX), obj.get());
        return VALUE_INDEX + valueSize;
    }

}
