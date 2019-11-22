package de.invesdwin.util.time.fdate;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.lang.Strings;
import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import de.jollyday.HolidayType;
import de.jollyday.ManagerParameters;
import de.jollyday.util.ResourceUtil;

@ThreadSafe
public final class FHolidayManager {

    /**
     * http://zorro-project.com/manual/en/weekend.htm
     * 
     * Holidays on 1.January and 25.December only
     */
    public static final FHolidayManager ZORRO;
    public static final FHolidayManager GERMANY;
    public static final Set<String> AVAILABLE_CALENDAR_IDS;
    @GuardedBy("none for performance")
    private static String availableCalendarIdsInfo;

    private static final Map<String, String> CALENDAR_ID;
    private static final Map<String, HolidayCalendar> ID_CALENDAR;

    private static final ALoadingCache<String, FHolidayManager> ID_MANAGER;

    static {
        ID_MANAGER = new ALoadingCache<String, FHolidayManager>() {

            @Override
            protected FHolidayManager loadValue(final String key) {
                try {
                    return new FHolidayManager(key);
                } catch (final Throwable t) {
                    throw new RuntimeException("Invalid " + FHolidayManager.class.getSimpleName() + " id [" + key
                            + "]. Available ids are: " + getAvailableCalendarIdsInfo());
                }
            }
        };
        CALENDAR_ID = new HashMap<>();
        ID_CALENDAR = new HashMap<>();
        for (final HolidayCalendar cal : HolidayCalendar.values()) {
            CALENDAR_ID.put(prepareHolidayCalendarId(cal.name()), cal.getId());
            ID_CALENDAR.put(prepareHolidayCalendarId(cal.getId()), cal);
        }
        ZORRO = getInstance("zorro");
        GERMANY = getInstance(HolidayCalendar.GERMANY);

        AVAILABLE_CALENDAR_IDS = newAvailableCalendarIds();
    }

    private final HolidayManager delegate;

    private final ALoadingCache<FDate, Boolean> day_holiday = new ALoadingCache<FDate, Boolean>() {
        @Override
        protected Boolean loadValue(final FDate key) {
            return delegate.isHoliday(key.calendarValue(), HolidayType.OFFICIAL_HOLIDAY);
        }

        @Override
        protected Integer getInitialMaximumSize() {
            return 1000;
        }
    };

    private final String calendarId;

    private FHolidayManager(final String calendarId) {
        this.calendarId = calendarId;
        this.delegate = HolidayManager.getInstance(ManagerParameters.create(calendarId));
    }

    private static Set<String> newAvailableCalendarIds() {
        final Set<String> availableCalendarIds = new TreeSet<>();
        final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            final Resource[] resources = resolver.getResources("classpath*:/holidays/Holidays_*.xml");
            for (int i = 0; i < resources.length; i++) {
                final Resource resource = resources[i];
                final String filename = resource.getFilename();
                final String calendarId = Strings.substringBetween(filename, "Holidays_", ".xml");
                availableCalendarIds.add(calendarId);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return Collections.unmodifiableSet(availableCalendarIds);
    }

    public static String getAvailableCalendarIdsInfo() {
        if (availableCalendarIdsInfo == null) {
            availableCalendarIdsInfo = newAvailableCalndarIdsInfo();
        }
        return availableCalendarIdsInfo;
    }

    private static String newAvailableCalndarIdsInfo() {
        final StringBuilder sb = new StringBuilder();
        for (final String calId : AVAILABLE_CALENDAR_IDS) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            final HolidayCalendar alias = ID_CALENDAR.get(calId);
            if (alias != null) {
                final String aliasId = prepareHolidayCalendarId(alias.name());
                if (!aliasId.equals(calId)) {
                    sb.append(aliasId);
                    sb.append(" (");
                    sb.append(calId);
                    sb.append(")");
                } else {
                    sb.append(calId);
                }
            } else {
                sb.append(calId);
            }
            final HolidayManager manager = HolidayManager.getInstance(ManagerParameters.create(calId));
            final String description = extractDescription(manager);
            if (description != null) {
                sb.append(": ");
                sb.append(description);
            }
        }
        return sb.toString();
    }

    private static String extractDescription(final HolidayManager manager) {
        final String description = manager.getCalendarHierarchy().getDescription(Locale.US);
        if (Strings.equals(ResourceUtil.UNDEFINED, description)) {
            return null;
        } else {
            return description;
        }
    }

    public String getCalendarId() {
        return calendarId;
    }

    public String getDescription() {
        return extractDescription(delegate);
    }

    public boolean isHoliday(final FDate date) {
        return day_holiday.get(date.withoutTime());
    }

    public static FHolidayManager getInstance(final String holidayCalendarId) {
        final String prepared = prepareHolidayCalendarId(holidayCalendarId);
        if (prepared == null) {
            return null;
        }
        final String replaced = CALENDAR_ID.get(prepared);
        if (replaced != null) {
            return ID_MANAGER.get(replaced);
        } else {
            return ID_MANAGER.get(prepared);
        }
    }

    public static String prepareHolidayCalendarId(final String holidayCalendarId) {
        if (Strings.isBlank(holidayCalendarId)) {
            return null;
        }
        return holidayCalendarId.trim().toLowerCase();
    }

    public static FHolidayManager getInstance(final HolidayCalendar holidayCalendar) {
        if (holidayCalendar == null) {
            return null;
        }
        final String prepared = prepareHolidayCalendarId(holidayCalendar.getId());
        return ID_MANAGER.get(prepared);
    }

    @Override
    public String toString() {
        return calendarId;
    }

}
