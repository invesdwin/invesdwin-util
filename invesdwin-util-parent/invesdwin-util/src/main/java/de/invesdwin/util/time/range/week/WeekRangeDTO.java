package de.invesdwin.util.time.range.week;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;

@NotThreadSafe
public class WeekRangeDTO implements IWeekRangeData, ISerializableValueObject {

    private IWeekTimeData from;
    private IWeekTimeData to;

    public WeekRangeDTO(final IWeekRangeData weekRangeData) {
        from = new WeekTimeDTO(weekRangeData.getFrom());
        to = new WeekTimeDTO(weekRangeData.getTo());
    }

    public WeekRangeDTO() {}

    @Override
    public IWeekTimeData getFrom() {
        return from;
    }

    public void setFrom(final IWeekTimeData from) {
        this.from = from;
    }

    @Override
    public IWeekTimeData getTo() {
        return to;
    }

    public void setTo(final IWeekTimeData to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return from + FROM_TO_SEPARATOR + to;
    }

}
