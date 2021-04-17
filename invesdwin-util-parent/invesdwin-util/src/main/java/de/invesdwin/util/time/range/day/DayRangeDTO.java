package de.invesdwin.util.time.range.day;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;

@NotThreadSafe
public class DayRangeDTO implements IDayRangeData, ISerializableValueObject {

    private IDayTimeData from;
    private IDayTimeData to;

    public DayRangeDTO(final IDayRangeData dayRangeData) {
        from = new DayTimeDTO(dayRangeData.getFrom());
        to = new DayTimeDTO(dayRangeData.getTo());
    }

    public DayRangeDTO() {
    }

    @Override
    public IDayTimeData getFrom() {
        return from;
    }

    public void setFrom(final IDayTimeData from) {
        this.from = from;
    }

    @Override
    public IDayTimeData getTo() {
        return to;
    }

    public void setTo(final IDayTimeData to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return from + FROM_TO_SEPARATOR + to;
    }

}
