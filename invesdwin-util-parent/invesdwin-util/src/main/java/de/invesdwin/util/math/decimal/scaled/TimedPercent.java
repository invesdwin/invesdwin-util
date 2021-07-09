package de.invesdwin.util.math.decimal.scaled;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.bean.tuple.IPair;
import de.invesdwin.util.time.date.FDate;

@Immutable
public class TimedPercent extends Percent implements IPair<FDate, Percent> {

    private final FDate time;

    public TimedPercent(final FDate time, final Percent percent) {
        super(percent);
        Assertions.checkNotNull(time);
        this.time = time;
    }

    public TimedPercent(final FDate time, final double value, final PercentScale scale) {
        super(value, scale);
        Assertions.checkNotNull(time);
        this.time = time;
    }

    public FDate getTime() {
        return time;
    }

    @Override
    public String toString() {
        return time + ": " + super.toString();
    }

    @Override
    public FDate getFirst() {
        return getTime();
    }

    @Override
    public Percent getSecond() {
        return this;
    }

}
