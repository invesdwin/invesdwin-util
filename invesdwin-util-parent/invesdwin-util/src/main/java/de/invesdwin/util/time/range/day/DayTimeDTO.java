package de.invesdwin.util.time.range.day;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DayTimeDTO implements IDayTimeData {

    private long longValue;

    public DayTimeDTO() {}

    public DayTimeDTO(final long longValue) {
        this.longValue = longValue;
    }

    public DayTimeDTO(final IDayTimeData dayTimeData) {
        this.longValue = dayTimeData.longValue();
    }

    @Override
    public long longValue() {
        return longValue;
    }

    public void setLongValue(final int longValue) {
        this.longValue = longValue;
    }

    @Override
    public String toString() {
        return FDayTime.valueOf(longValue, false).toString();
    }

}
