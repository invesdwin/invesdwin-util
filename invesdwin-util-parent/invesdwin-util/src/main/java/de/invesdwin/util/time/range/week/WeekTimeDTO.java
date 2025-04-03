package de.invesdwin.util.time.range.week;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.time.date.FDayTime;

@NotThreadSafe
public class WeekTimeDTO implements IWeekTimeData {

    private long longValue;

    public WeekTimeDTO() {}

    public WeekTimeDTO(final int longValue) {
        this.longValue = longValue;
    }

    public WeekTimeDTO(final IWeekTimeData dayTimeData) {
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
