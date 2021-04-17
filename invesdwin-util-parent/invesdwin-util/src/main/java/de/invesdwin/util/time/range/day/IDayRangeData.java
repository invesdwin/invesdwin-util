package de.invesdwin.util.time.range.day;

public interface IDayRangeData {

    String FROM_TO_SEPARATOR = "-";

    IDayTimeData getFrom();

    IDayTimeData getTo();

}
