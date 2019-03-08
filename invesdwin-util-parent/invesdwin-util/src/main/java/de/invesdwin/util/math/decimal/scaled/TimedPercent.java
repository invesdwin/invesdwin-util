package de.invesdwin.util.math.decimal.scaled;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class TimedPercent extends Percent {

    private final FDate time;

    public TimedPercent(final FDate time, final Percent percent) {
        super(percent);
        Assertions.checkNotNull(time);
        this.time = time;
    }

    public FDate getTime() {
        return time;
    }

}
