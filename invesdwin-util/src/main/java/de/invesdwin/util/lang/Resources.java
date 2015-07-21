package de.invesdwin.util.lang;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.springframework.core.io.Resource;

@Immutable
public final class Resources {

    private Resources() {}

    public static List<String> extractMetaInfResourceLocations(final Iterable<? extends Resource> resources) {
        final List<String> locationStrings = new ArrayList<String>();
        for (final Resource resource : resources) {
            final String resourceString = resource.toString();
            if (resourceString != null && Strings.contains(resourceString, "META-INF")) {
                locationStrings.add("/META-INF"
                        + Strings.removeEnd(Strings.substringAfter(resourceString, "META-INF"), "]"));
            }
        }
        return locationStrings;
    }

}
