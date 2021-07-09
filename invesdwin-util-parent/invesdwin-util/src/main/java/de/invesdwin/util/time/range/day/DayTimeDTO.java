package de.invesdwin.util.time.range.day;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.time.date.FDayTime;

@NotThreadSafe
public class DayTimeDTO implements IDayTimeData {

    private int intValue;

    public DayTimeDTO() {
    }

    public DayTimeDTO(final int intValue) {
        this.intValue = intValue;
    }

    public DayTimeDTO(final IDayTimeData dayTimeData) {
        this.intValue = dayTimeData.intValue();
    }

    @Override
    public int intValue() {
        return intValue;
    }

    public void setIntValue(final int intValue) {
        this.intValue = intValue;
    }

    @Override
    public String toString() {
        return FDayTime.valueOf(intValue, false).toString();
    }

}
