package de.invesdwin.util.marshallers.serde.basic;

import java.util.Date;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.streams.buffer.IByteBuffer;

@Immutable
public class DateSerde implements ISerde<Date> {

    public static final DateSerde GET = new DateSerde();
    public static final int FIXED_LENGTH = Long.BYTES;

    @Override
    public Date fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final Date obj) {
        return SerdeBaseMethods.toBytes(this, obj);
    }

    @Override
    public Date fromBuffer(final IByteBuffer buffer, final int length) {
        final Long time = LongSerde.GET.fromBuffer(buffer, length);
        if (time == null) {
            return null;
        } else {
            //CHECKSTYLE:OFF
            return new Date(time);
            //CHECKSTYLE:ON
        }
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final Date obj) {
        final Long time;
        if (obj == null) {
            time = null;
        } else {
            time = obj.getTime();
        }
        return LongSerde.GET.toBuffer(buffer, time);
    }

}