package de.invesdwin.util.time.date;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class LoopIsSameJulianDayCheck {

    private long prevJulianDay;
    private FDate lastTime;

    public LoopIsSameJulianDayCheck() {
        this.prevJulianDay = 0L;
        this.lastTime = FDate.MIN_DATE;
    }

    public LoopIsSameJulianDayCheck(final FDate initialTime) {
        this.prevJulianDay = initialTime.millisValue() / FDates.MILLISECONDS_IN_DAY;
        this.lastTime = initialTime;
    }

    public FDate getLastTime() {
        return lastTime;
    }

    public boolean isSameJulianDay(final FDate time) {
        final long newJulianDay = time.millisValue() / FDates.MILLISECONDS_IN_DAY;
        if (prevJulianDay == newJulianDay) {
            return true;
        } else {
            lastTime = time;
            prevJulianDay = newJulianDay;
            return false;
        }
    }

    public void reset() {
        prevJulianDay = 0L;
        lastTime = FDate.MIN_DATE;
    }

    public void reset(final FDate initialTime) {
        prevJulianDay = initialTime.millisValue() / FDates.MILLISECONDS_IN_DAY;
        lastTime = initialTime;
    }

}
