package de.invesdwin.util.time.date.holiday.provider.custom;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.holiday.IHolidayManager;

@ThreadSafe
public class CachingHolidayManager implements IHolidayManager {

    private final IHolidayManager delegate;

    private final ALoadingCache<FDate, Boolean> day_holiday = new ALoadingCache<FDate, Boolean>() {
        @Override
        protected Boolean loadValue(final FDate key) {
            return delegate.isHoliday(key);
        }

        @Override
        protected Integer getInitialMaximumSize() {
            return CachingHolidayManager.this.getInitialMaximumSize();
        }

    };

    public CachingHolidayManager(final IHolidayManager delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isHoliday(final FDate date) {
        return day_holiday.get(date);
    }

    @Override
    public String getHolidayCalendarId() {
        return delegate.getHolidayCalendarId();
    }

    @Override
    public String toString() {
        return getHolidayCalendarId();
    }

    protected int getInitialMaximumSize() {
        return 1000;
    }

}
