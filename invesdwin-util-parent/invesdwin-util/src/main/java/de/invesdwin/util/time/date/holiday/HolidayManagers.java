package de.invesdwin.util.time.date.holiday;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.time.date.holiday.provider.IHolidayManagerProvider;
import de.invesdwin.util.time.date.holiday.provider.custom.CustomHolidayManagerProvider;
import de.invesdwin.util.time.date.holiday.provider.jollyday.JollydayHolidayManagerProvider;
import de.invesdwin.util.time.date.holiday.provider.strata.StrataHolidayManagerProvider;

@ThreadSafe
public final class HolidayManagers {

    public static final IHolidayManager ZORRO = JollydayHolidayManagerProvider.ZORRO;
    public static final IHolidayManager XETRA = JollydayHolidayManagerProvider.XETRA;
    public static final IHolidayManager GERMANY = JollydayHolidayManagerProvider.GERMANY;

    private static final List<IHolidayManagerProvider> PROVIDERS;

    static {
        PROVIDERS = new ArrayList<>();
        //custom has highest priority, so that overrides are possible
        PROVIDERS.add(CustomHolidayManagerProvider.INSTANCE);
        PROVIDERS.add(StrataHolidayManagerProvider.INSTANCE);
        PROVIDERS.add(JollydayHolidayManagerProvider.INSTANCE);
    }

    private HolidayManagers() {
    }

    public static List<IHolidayManagerProvider> getProviders() {
        return PROVIDERS;
    }

    public static String assertLookupHolidayCalendarId(final String holidayCalendarId) {
        final String lookup = lookupHolidayCalendarId(holidayCalendarId);
        if (lookup == null) {
            throw new RuntimeException("Invalid " + IHolidayManager.class.getSimpleName() + " id [" + holidayCalendarId
                    + "]. Available ids are: " + getAvailableCalendarIdsInfo());
        }
        return lookup;
    }

    public static String lookupHolidayCalendarId(final String holidayCalendarId) {
        for (int i = 0; i < PROVIDERS.size(); i++) {
            final IHolidayManagerProvider provider = PROVIDERS.get(i);
            final String lookup = provider.lookupHolidayCalendarId(holidayCalendarId);
            if (lookup != null) {
                return lookup;
            }
        }
        return null;
    }

    public static IHolidayManager getInstance(final String holidayCalendarId) {
        for (int i = 0; i < PROVIDERS.size(); i++) {
            final IHolidayManagerProvider provider = PROVIDERS.get(i);
            final IHolidayManager instance = provider.getInstance(holidayCalendarId);
            if (instance != null) {
                return instance;
            }
        }
        throw new RuntimeException("Invalid " + IHolidayManager.class.getSimpleName() + " id [" + holidayCalendarId
                + "]. Available ids are: " + getAvailableCalendarIdsInfo());
    }

    private static String getAvailableCalendarIdsInfo() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < PROVIDERS.size(); i++) {
            if (i > 0) {
                sb.append(IHolidayManagerProvider.AVAILABLE_CALENDAR_ID_SEPARATOR);
            }
            final IHolidayManagerProvider provider = PROVIDERS.get(i);
            sb.append(provider.getAvailableCalendarIdsInfo());
        }
        return sb.toString();
    }

}
