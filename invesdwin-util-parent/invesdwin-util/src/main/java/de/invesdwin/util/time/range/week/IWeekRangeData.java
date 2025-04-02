package de.invesdwin.util.time.range.week;

public interface IWeekRangeData {

    String FROM_TO_SEPARATOR = "-";

    IWeekTimeData getFrom();

    IWeekTimeData getTo();

}
