package de.invesdwin.util.time.date.holiday.provider.jollyday;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.time.date.holiday.IHolidayManager;
import de.invesdwin.util.time.date.holiday.provider.IHolidayManagerProvider;
import de.invesdwin.util.time.date.holiday.provider.custom.CachingHolidayManager;
import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import de.jollyday.ManagerParameters;

@Immutable
public final class JollydayHolidayManagerProvider implements IHolidayManagerProvider {

    public static final JollydayHolidayManagerProvider INSTANCE = new JollydayHolidayManagerProvider();

    /**
     * http://zorro-project.com/manual/en/weekend.htm
     * 
     * Holidays on 1.January and 25.December only
     */
    public static final IHolidayManager ZORRO;
    /**
     * https://www.xetra.com/xetra-de/newsroom/handelskalender
     * 
     * 6 holidays
     */
    public static final IHolidayManager XETRA;
    public static final IHolidayManager GERMANY;

    private static final Set<String> AVAILABLE_CALENDAR_IDS;
    private static final Map<String, String> CALENDAR_ID;
    private static final Map<String, HolidayCalendar> ID_CALENDAR;

    private static final ALoadingCache<String, Optional<IHolidayManager>> ID_MANAGER;
    @GuardedBy("none for performance")
    private static String availableCalendarIdsInfo;

    static {
        ID_MANAGER = new ALoadingCache<String, Optional<IHolidayManager>>() {
            @Override
            protected Optional<IHolidayManager> loadValue(final String key) {
                try {
                    return Optional.of(new CachingHolidayManager(new JollydayHolidayManager(key)));
                } catch (final Throwable t) {
                    return Optional.empty();
                }
            }
        };
        CALENDAR_ID = new HashMap<>();
        ID_CALENDAR = new HashMap<>();
        for (final HolidayCalendar cal : HolidayCalendar.values()) {
            CALENDAR_ID.put(prepareHolidayCalendarId(cal.name()), cal.getId());
            ID_CALENDAR.put(prepareHolidayCalendarId(cal.getId()), cal);
        }
        ZORRO = INSTANCE.getInstance("zorro");
        XETRA = INSTANCE.getInstance("xetra");
        GERMANY = getInstance(HolidayCalendar.GERMANY);

        AVAILABLE_CALENDAR_IDS = newAvailableCalendarIds();
    }

    private JollydayHolidayManagerProvider() {
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

    @Override
    public String getAvailableCalendarIdsInfo() {
        if (availableCalendarIdsInfo == null) {
            availableCalendarIdsInfo = newAvailableCalendarIdsInfo();
        }
        return availableCalendarIdsInfo;
    }

    private String newAvailableCalendarIdsInfo() {
        final StringBuilder sb = new StringBuilder();
        for (final String calId : AVAILABLE_CALENDAR_IDS) {
            if (sb.length() > 0) {
                sb.append(IHolidayManagerProvider.AVAILABLE_CALENDAR_ID_SEPARATOR);
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
            final String description = JollydayHolidayManager.extractDescription(manager);
            if (description != null && (alias == null || !alias.name().equalsIgnoreCase(description))) {
                sb.append(": ");
                sb.append(description);
            }
        }
        return sb.toString();
    }

    public static String prepareHolidayCalendarId(final String holidayCalendarId) {
        if (Strings.isBlank(holidayCalendarId)) {
            return null;
        }
        return holidayCalendarId.trim().toLowerCase();
    }

    public static IHolidayManager getInstance(final HolidayCalendar holidayCalendar) {
        if (holidayCalendar == null) {
            return null;
        }
        final String prepared = prepareHolidayCalendarId(holidayCalendar.getId());
        return ID_MANAGER.get(prepared).orElse(null);
    }

    @Override
    public IHolidayManager getInstance(final String holidayCalendarId) {
        final String prepared = prepareHolidayCalendarId(holidayCalendarId);
        if (prepared == null) {
            return null;
        }
        final String replaced = CALENDAR_ID.get(prepared);
        if (replaced != null) {
            return ID_MANAGER.get(replaced).orElse(null);
        } else {
            return ID_MANAGER.get(prepared).orElse(null);
        }
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

}
