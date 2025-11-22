package de.invesdwin.util.time.date.holiday.provider.custom;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.time.date.holiday.IHolidayManager;
import de.invesdwin.util.time.date.holiday.provider.IHolidayManagerProvider;
import de.invesdwin.util.time.date.holiday.provider.custom.specific.DisabledHolidayManager;
import de.invesdwin.util.time.date.holiday.provider.custom.specific.EurexFuturesHolidayManager;
import de.invesdwin.util.time.date.holiday.provider.custom.specific.EurexFuturesWithRolloversHolidayManager;
import de.invesdwin.util.time.date.holiday.provider.custom.specific.NewYearsEveHolidayManager;
import de.invesdwin.util.time.date.holiday.provider.custom.specific.WeekendHolidayManager;
import de.invesdwin.util.time.date.holiday.provider.custom.specific.XetraFuturesHolidayManager;
import de.invesdwin.util.time.date.holiday.provider.custom.specific.XetraFuturesWithRolloversHolidayManager;
import de.invesdwin.util.time.date.holiday.provider.custom.specific.expiration.secondfriday.MonthlyExpirationSecondFridayHolidayManager;
import de.invesdwin.util.time.date.holiday.provider.custom.specific.expiration.secondfriday.QuarterlyExpirationSecondFridayHolidayManager;
import de.invesdwin.util.time.date.holiday.provider.custom.specific.expiration.thirdfriday.MonthlyExpirationThirdFridayHolidayManager;
import de.invesdwin.util.time.date.holiday.provider.custom.specific.expiration.thirdfriday.QuarterlyExpirationThirdFridayHolidayManager;

@NotThreadSafe
public final class CustomHolidayManagerProvider implements IHolidayManagerProvider {

    public static final CustomHolidayManagerProvider INSTANCE = new CustomHolidayManagerProvider();

    private static final Map<String, IHolidayManager> ID_MANAGER = new HashMap<>();
    @GuardedBy("none for performance")
    private static String availableCalendarIdsInfo;

    static {
        registerHolidayManager(QuarterlyExpirationThirdFridayHolidayManager.INSTANCE);
        registerHolidayManager(MonthlyExpirationThirdFridayHolidayManager.INSTANCE);
        registerHolidayManager(QuarterlyExpirationSecondFridayHolidayManager.INSTANCE);
        registerHolidayManager(MonthlyExpirationSecondFridayHolidayManager.INSTANCE);
        registerHolidayManager(NewYearsEveHolidayManager.INSTANCE);
        registerHolidayManager(WeekendHolidayManager.INSTANCE);
        registerHolidayManager(XetraFuturesHolidayManager.INSTANCE);
        registerHolidayManager(XetraFuturesWithRolloversHolidayManager.INSTANCE);
        registerHolidayManager(EurexFuturesHolidayManager.INSTANCE);
        registerHolidayManager(EurexFuturesWithRolloversHolidayManager.INSTANCE);
        registerHolidayManager(DisabledHolidayManager.INSTANCE);
    }

    public static String prepareHolidayCalendarId(final String holidayCalendarId) {
        if (Strings.isBlank(holidayCalendarId)) {
            return null;
        }
        return holidayCalendarId.trim().toUpperCase();
    }

    public static boolean registerHolidayManager(final IHolidayManager holidayManager) {
        //        if (holidayManager == null) {
        //            return false;
        //        }
        final boolean changed = ID_MANAGER.putIfAbsent(prepareHolidayCalendarId(holidayManager.getHolidayCalendarId()),
                holidayManager) == null;
        if (changed) {
            availableCalendarIdsInfo = null;
        }
        return changed;
    }

    public static boolean unregisterHolidayManager(final IHolidayManager holidayManager) {
        final boolean changed = ID_MANAGER
                .remove(prepareHolidayCalendarId(holidayManager.getHolidayCalendarId())) != null;
        if (changed) {
            availableCalendarIdsInfo = null;
        }
        return changed;
    }

    public static Collection<IHolidayManager> getHolidayManagers() {
        return ID_MANAGER.values();
    }

    @Override
    public IHolidayManager getInstance(final String holidayCalendarId) {
        return ID_MANAGER.get(prepareHolidayCalendarId(holidayCalendarId));
    }

    @Override
    public String lookupHolidayCalendarId(final String holidayCalendarId) {
        final String prepared = prepareHolidayCalendarId(holidayCalendarId);
        if (getInstance(prepared) != null) {
            return prepared;
        } else {
            return null;
        }
    }

    @Override
    public String getAvailableCalendarIdsInfo() {
        if (availableCalendarIdsInfo == null) {
            availableCalendarIdsInfo = newAvailableCalendarIdsInfo();
        }
        return availableCalendarIdsInfo;
    }

    private String newAvailableCalendarIdsInfo() {
        final StringBuilder sb = new StringBuilder();
        for (final String calId : ID_MANAGER.keySet()) {
            if (sb.length() > 0) {
                sb.append(AVAILABLE_CALENDAR_ID_SEPARATOR);
            }
            sb.append(calId);
        }
        return sb.toString();
    }

}
