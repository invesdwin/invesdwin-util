package de.invesdwin.util.assertions.type.internal;

import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import org.assertj.core.data.MapEntry;
import org.assertj.core.presentation.StandardRepresentation;

/**
 * Fixes stack overflow exception when printing objects that implement Map.Entry and return themselves as key of value.
 */
@Immutable
public class CustomStandardRepresentation extends StandardRepresentation {

    @Override
    protected String toStringOf(final MapEntry<?, ?> mapEntry) {
        if (mapEntry.getKey() == mapEntry || mapEntry.getValue() == mapEntry) {
            return super.fallbackToStringOf(mapEntry);
        } else {
            return super.toStringOf(mapEntry);
        }
    }

    @Override
    protected String toStringOf(final Entry<?, ?> javaMapEntry) {
        if (javaMapEntry.getKey() == javaMapEntry || javaMapEntry.getValue() == javaMapEntry) {
            return super.fallbackToStringOf(javaMapEntry);
        } else {
            return super.toStringOf(javaMapEntry);
        }
    }

}
