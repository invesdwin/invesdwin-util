package de.invesdwin.util.assertions.type.internal;

import java.util.Date;

import javax.annotation.concurrent.NotThreadSafe;

import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;

@NotThreadSafe
public final class ShouldBeWithin extends BasicErrorMessageFactory {

    private ShouldBeWithin(final Date actual, final String fieldDescription, final int fieldValue) {
        super("%nExpecting:%n <%s>%nto be on %s <%s>", actual, fieldDescription, fieldValue);
    }

    public static ErrorMessageFactory shouldBeWithin(final Date actual, final String fieldDescription,
            final int fieldValue) {
        return new ShouldBeWithin(actual, fieldDescription, fieldValue);
    }
}