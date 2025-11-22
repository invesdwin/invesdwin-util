package de.invesdwin.util.time.date.millis;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum WeekAdjustment {
    PREVIOUS {
        @Override
        public long adjust(final long millis, final long newMillis) {
            if (!FDatesMillis.isSameJulianDay(newMillis, millis) && FDateMillis.isAfter(newMillis, millis)) {
                return FDateMillis.addWeeks(newMillis, -1);
            } else {
                return newMillis;
            }
        }
    },
    UNADJUSTED {
        @Override
        public long adjust(final long millis, final long newMillis) {
            return newMillis;
        }
    },
    NEXT {
        @Override
        public long adjust(final long millis, final long newMillis) {
            if (!FDatesMillis.isSameJulianDay(newMillis, millis) && FDateMillis.isBefore(newMillis, millis)) {
                return FDateMillis.addWeeks(newMillis, 1);
            } else {
                return newMillis;
            }
        }
    };

    public abstract long adjust(long millis, long newMillis);
}
