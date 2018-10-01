package de.invesdwin.util.math.decimal;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class TimedDecimal extends Decimal {

    private final FDate time;

    public TimedDecimal(final FDate time, final Decimal decimal) {
        super(decimal);
        Assertions.checkNotNull(time);
        this.time = time;
    }

    public FDate getTime() {
        return time;
    }

}
