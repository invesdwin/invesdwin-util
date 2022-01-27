package de.invesdwin.util.streams.resource;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import de.invesdwin.util.lang.Strings;

@Immutable
public final class Resources {

    public static final Resource[] EMPTY_ARRAY = new Resource[0];

    private Resources() {
    }

    public static List<String> extractMetaInfResourceLocations(final Iterable<? extends Resource> resources) {
        final List<String> locationStrings = new ArrayList<String>();
        for (final Resource resource : resources) {
            final String resourceString = resource.toString();
            if (resourceString != null && Strings.contains(resourceString, "META-INF")) {
                locationStrings
                        .add("/META-INF" + Strings.removeEnd(Strings.substringAfter(resourceString, "META-INF"), "]"));
            }
        }
        return locationStrings;
    }

    public static String resourceToPatternString(final Resource resource) {
        if (resource instanceof ClassPathResource) {
            final ClassPathResource cResource = (ClassPathResource) resource;
            return "classpath:" + cResource.getPath();
        } else if (resource instanceof FileSystemResource) {
            final FileSystemResource cResource = (FileSystemResource) resource;
            return "file:" + cResource.getPath();
        } else {
            try {
                final URI uri = resource.getURI();
                if (uri != null) {
                    return uri.toString();
                } else {
                    //fallback garbage
                    return resource.toString();
                }
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
