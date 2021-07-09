package de.invesdwin.util.time.date.holiday.provider.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.holiday.IHolidayManager;

@NotThreadSafe
public class OrCombinedHolidayManager implements IHolidayManager {

    private final String holidayCalendarId;
    private final List<IHolidayManager> holidayManagers;

    public OrCombinedHolidayManager(final String holidayCalendarId, final IHolidayManager... holidayManagers) {
        this.holidayCalendarId = holidayCalendarId;
        this.holidayManagers = new ArrayList<>(Arrays.asList(holidayManagers));
    }

    public OrCombinedHolidayManager(final String holidayCalendarId, final Collection<IHolidayManager> holidayManagers) {
        this.holidayCalendarId = holidayCalendarId;
        this.holidayManagers = new ArrayList<>(holidayManagers);
    }

    public OrCombinedHolidayManager(final String holidayCalendarId, final List<IHolidayManager> holidayManagers) {
        this.holidayCalendarId = holidayCalendarId;
        this.holidayManagers = holidayManagers;
    }

    public OrCombinedHolidayManager(final String holidayCalendarId) {
        this.holidayCalendarId = holidayCalendarId;
        this.holidayManagers = new ArrayList<>();
    }

    public OrCombinedHolidayManager and(final IHolidayManager holidayManager) {
        if (holidayManager != null) {
            holidayManagers.add(holidayManager);
        }
        return this;
    }

    @Override
    public boolean isHoliday(final FDate date) {
        for (int i = 0; i < holidayManagers.size(); i++) {
            if (holidayManagers.get(i).isHoliday(date)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getHolidayCalendarId() {
        return holidayCalendarId;
    }

    @Override
    public String toString() {
        return getHolidayCalendarId();
    }

    public boolean isEmpty() {
        return holidayManagers.isEmpty();
    }

    public int size() {
        return holidayManagers.size();
    }

}
