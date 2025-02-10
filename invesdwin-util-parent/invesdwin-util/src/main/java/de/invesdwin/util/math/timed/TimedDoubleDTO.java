package de.invesdwin.util.math.timed;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.time.date.FDate;

@NotThreadSafe
public class TimedDoubleDTO implements ITimedDouble {

    private FDate time;
    private double value;

    public TimedDoubleDTO(final ITimedDouble copyOf) {
        this.time = copyOf.getTime();
        this.value = copyOf.getValue();
    }

    public TimedDoubleDTO() {
        value = Double.NaN;
    }

    public void setTime(final FDate time) {
        this.time = time;
    }

    @Override
    public FDate getTime() {
        return time;
    }

    public void setValue(final double value) {
        this.value = value;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return TimedDoubleBaseMethods.hashCode(this);
    }

    @Override
    public boolean equals(final Object obj) {
        return TimedDoubleBaseMethods.equals(this, obj);
    }

    @Override
    public String toString() {
        return TimedDoubleBaseMethods.toString(this);
    }

    public static TimedDoubleDTO valueOf(final ITimedDouble value) {
        if (value == null) {
            return null;
        }
        if (value instanceof TimedDoubleDTO) {
            return (TimedDoubleDTO) value;
        }
        return new TimedDoubleDTO(value);
    }

}
