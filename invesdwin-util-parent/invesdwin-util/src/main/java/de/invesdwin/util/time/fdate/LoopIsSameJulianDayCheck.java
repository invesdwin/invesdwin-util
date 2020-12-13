package de.invesdwin.util.time.fdate;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class LoopIsSameJulianDayCheck {

    private long prevJulianDay;

    public LoopIsSameJulianDayCheck() {
        this.prevJulianDay = 0L;
    }

    public LoopIsSameJulianDayCheck(final FDate initialTime) {
        this.prevJulianDay = initialTime.millisValue() / FDates.MILLISECONDS_IN_DAY;
    }

    public boolean isSameJulianDay(final FDate time) {
        final long newJulianDay = time.millisValue() / FDates.MILLISECONDS_IN_DAY;
        if (prevJulianDay != newJulianDay) {
            prevJulianDay = newJulianDay;
            return true;
        } else {
            return false;
        }
    }

}
