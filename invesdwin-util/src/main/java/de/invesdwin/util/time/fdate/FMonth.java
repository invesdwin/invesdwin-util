package de.invesdwin.util.time.fdate;

import java.time.Month;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import org.joda.time.DateTimeConstants;

import de.invesdwin.util.error.UnknownArgumentException;

@Immutable
public enum FMonth {
    January {
        @Override
        public int calendarValue() {
            return Calendar.JANUARY;
        }

        @Override
        public Month javaTimeValue() {
            return Month.JANUARY;
        }

        @Override
        public int jodaTimeValue() {
            return DateTimeConstants.JANUARY;
        }
    },
    February {
        @Override
        public int calendarValue() {
            return Calendar.FEBRUARY;
        }

        @Override
        public Month javaTimeValue() {
            return Month.FEBRUARY;
        }

        @Override
        public int jodaTimeValue() {
            return DateTimeConstants.FEBRUARY;
        }
    },
    March {
        @Override
        public int calendarValue() {
            return Calendar.MARCH;
        }

        @Override
        public Month javaTimeValue() {
            return Month.MARCH;
        }

        @Override
        public int jodaTimeValue() {
            return DateTimeConstants.MARCH;
        }
    },
    April {
        @Override
        public int calendarValue() {
            return Calendar.APRIL;
        }

        @Override
        public Month javaTimeValue() {
            return Month.APRIL;
        }

        @Override
        public int jodaTimeValue() {
            return DateTimeConstants.APRIL;
        }
    },
    May {
        @Override
        public int calendarValue() {
            return Calendar.MAY;
        }

        @Override
        public Month javaTimeValue() {
            return Month.MAY;
        }

        @Override
        public int jodaTimeValue() {
            return DateTimeConstants.MAY;
        }
    },
    June {
        @Override
        public int calendarValue() {
            return Calendar.JUNE;
        }

        @Override
        public Month javaTimeValue() {
            return Month.JUNE;
        }

        @Override
        public int jodaTimeValue() {
            return DateTimeConstants.JUNE;
        }
    },
    July {
        @Override
        public int calendarValue() {
            return Calendar.JULY;
        }

        @Override
        public Month javaTimeValue() {
            return Month.JULY;
        }

        @Override
        public int jodaTimeValue() {
            return DateTimeConstants.JULY;
        }
    },
    August {
        @Override
        public int calendarValue() {
            return Calendar.AUGUST;
        }

        @Override
        public Month javaTimeValue() {
            return Month.AUGUST;
        }

        @Override
        public int jodaTimeValue() {
            return DateTimeConstants.AUGUST;
        }
    },
    September {

        @Override
        public int calendarValue() {
            return Calendar.SEPTEMBER;
        }

        @Override
        public Month javaTimeValue() {
            return Month.SEPTEMBER;
        }

        @Override
        public int jodaTimeValue() {
            return DateTimeConstants.SEPTEMBER;
        }

    },
    October {
        @Override
        public int calendarValue() {
            return Calendar.OCTOBER;
        }

        @Override
        public Month javaTimeValue() {
            return Month.OCTOBER;
        }

        @Override
        public int jodaTimeValue() {
            return DateTimeConstants.OCTOBER;
        }

    },
    November {
        @Override
        public int calendarValue() {
            return Calendar.NOVEMBER;
        }

        @Override
        public Month javaTimeValue() {
            return Month.NOVEMBER;
        }

        @Override
        public int jodaTimeValue() {
            return DateTimeConstants.NOVEMBER;
        }
    },
    December {
        @Override
        public int calendarValue() {
            return Calendar.DECEMBER;
        }

        @Override
        public Month javaTimeValue() {
            return Month.DECEMBER;
        }

        @Override
        public int jodaTimeValue() {
            return DateTimeConstants.DECEMBER;
        }
    };

    private static final Map<Integer, FMonth> CALENDAR_LOOKUP = new HashMap<Integer, FMonth>();
    private static final Map<Month, FMonth> JAVA_TIME_LOOKUP = new HashMap<Month, FMonth>();
    private static final Map<Integer, FMonth> JODA_TIME_LOOKUP = new HashMap<Integer, FMonth>();

    static {
        for (final FMonth f : FMonth.values()) {
            CALENDAR_LOOKUP.put(f.calendarValue(), f);
            JAVA_TIME_LOOKUP.put(f.javaTimeValue(), f);
            JODA_TIME_LOOKUP.put(f.jodaTimeValue(), f);
        }
    }

    public static FMonth valueOfCalendar(final int month) {
        return lookup(CALENDAR_LOOKUP, month);
    }

    public static FMonth valueOfJavaTime(final Month month) {
        return lookup(JAVA_TIME_LOOKUP, month);
    }

    public static FMonth valueOfJodaTime(final int month) {
        return lookup(JODA_TIME_LOOKUP, month);
    }

    public static FMonth valueOfIndex(final int month) {
        return valueOfJodaTime(month);
    }

    @SuppressWarnings("unchecked")
    private static <T> FMonth lookup(final Map<T, FMonth> map, final T month) {
        if (month == null) {
            throw new NullPointerException("parameter field should not be null");
        }
        final FMonth value = map.get(month);
        if (value == null) {
            throw UnknownArgumentException.newInstance((Class<T>) month.getClass(), month);
        } else {
            return value;
        }
    }

    public abstract int calendarValue();

    public abstract Month javaTimeValue();

    public abstract int jodaTimeValue();

    public int indexValue() {
        return jodaTimeValue();
    }

    public String toString3Letters() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this);
        sb.setLength(3);
        return sb.toString();
    }

}
