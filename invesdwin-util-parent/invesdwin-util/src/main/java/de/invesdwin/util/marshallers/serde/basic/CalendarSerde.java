package de.invesdwin.util.marshallers.serde.basic;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.buffer.IByteBuffer;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;

@Immutable
public class CalendarSerde implements ISerde<Calendar> {

    public static final CalendarSerde GET = new CalendarSerde();
    public static final int FIXED_LENGTH = DateSerde.FIXED_LENGTH;

    @Override
    public Calendar fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final Calendar obj) {
        return SerdeBaseMethods.toBytes(this, obj, FIXED_LENGTH);
    }

    @Override
    public Calendar fromBuffer(final IByteBuffer buffer) {
        final Date date = DateSerde.GET.fromBuffer(buffer);
        if (date == null) {
            return null;
        } else {
            //CHECKSTYLE:OFF
            final Calendar cal = Calendar.getInstance();
            //CHECKSTYLE:ON
            cal.setTime(date);
            return cal;
        }
    }

    @Override
    public int toBuffer(final Calendar obj, final IByteBuffer buffer) {
        final Date date;
        if (obj == null) {
            date = null;
        } else {
            date = obj.getTime();
        }
        return DateSerde.GET.toBuffer(date, buffer);
    }

}
