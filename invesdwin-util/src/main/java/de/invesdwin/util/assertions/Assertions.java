package de.invesdwin.util.assertions;

import java.util.Collection;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.assertions.internal.AAssertionsStaticFacade;
import de.invesdwin.util.assertions.internal.DecimalAssert;
import de.invesdwin.util.assertions.internal.FDateAssert;
import de.invesdwin.util.assertions.internal.StringAssert;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.time.fdate.FDate;

@StaticFacadeDefinition(name = "de.invesdwin.util.assertions.internal.AAssertionsStaticFacade", targets = {
        org.assertj.core.api.Assertions.class, org.assertj.guava.api.Assertions.class,
        com.google.common.base.Preconditions.class }, filterMethodSignatureExpressions = ".* org\\.assertj\\.core\\.api\\.StringAssert assertThat\\(java\\.lang\\.String .*")
@Immutable
public final class Assertions extends AAssertionsStaticFacade {

    private Assertions() {}

    public static <T extends ADecimal<T>> DecimalAssert<T> assertThat(final T actual) {
        return new DecimalAssert<T>(actual);
    }

    public static StringAssert assertThat(final String actual) {
        return new StringAssert(actual);
    }

    public static FDateAssert assertThat(final FDate actual) {
        return new FDateAssert(actual);
    }

    public static void checkEquals(final Object o1, final Object o2) {
        if (!Objects.equals(o1, o2)) {
            assertThat(o1).isEqualTo(o2);
            failExceptionExpected();
        }
    }

    public static void checkEquals(final Object o1, final Object o2, final String message, final Object... args) {
        if (!Objects.equals(o1, o2)) {
            assertThat(o1).as(message, args).isEqualTo(o2);
            failExceptionExpected();
        }
    }

    public static void checkNotEquals(final Object o1, final Object o2) {
        if (Objects.equals(o1, o2)) {
            assertThat(o1).isNotEqualTo(o2);
            failExceptionExpected();
        }
    }

    public static void checkNotEquals(final Object o1, final Object o2, final String message, final Object... args) {
        if (Objects.equals(o1, o2)) {
            assertThat(o1).as(message, args).isNotEqualTo(o2);
            failExceptionExpected();
        }
    }

    public static void failExceptionExpected() {
        fail("Exception expected");
    }

    public static void checkSame(final Object o1, final Object o2) {
        if (o1 != o2) {
            assertThat(o1).isSameAs(o2);
            failExceptionExpected();
        }
    }

    public static void checkNull(final Object obj) {
        if (obj != null) {
            assertThat(obj).isNull();
            failExceptionExpected();
        }
    }

    public static void checkNull(final Object obj, final String message, final Object... args) {
        if (obj != null) {
            assertThat(obj).as(message, args).isNull();
            failExceptionExpected();
        }
    }

    public static void checkTrue(final boolean expression) {
        if (!expression) {
            assertThat(expression).isTrue();
            failExceptionExpected();
        }
    }

    public static void checkTrue(final boolean expression, final String message, final Object... args) {
        if (!expression) {
            assertThat(expression).as(message, args).isTrue();
            failExceptionExpected();
        }
    }

    public static void checkFalse(final boolean expression) {
        if (expression) {
            assertThat(expression).isFalse();
            failExceptionExpected();
        }
    }

    public static void checkFalse(final boolean expression, final String message, final Object... args) {
        if (expression) {
            assertThat(expression).as(message, args).isFalse();
            failExceptionExpected();
        }
    }

    public static void checkNotEmpty(final Collection<?> collection) {
        if (collection.isEmpty()) {
            assertThat(collection).isNotEmpty();
            failExceptionExpected();
        }
    }

    public static void checkNotEmpty(final Collection<?> collection, final String message, final Object... args) {
        if (collection.isEmpty()) {
            assertThat(collection).as(message, args).isNotEmpty();
            failExceptionExpected();
        }
    }

    public static void checkNotEmpty(final Object[] collection) {
        if (collection.length == 0) {
            assertThat(collection).isNotEmpty();
            failExceptionExpected();
        }
    }

    public static void checkNotEmpty(final Object[] collection, final String message, final Object... args) {
        if (collection.length == 0) {
            assertThat(collection).as(message, args).isNotEmpty();
            failExceptionExpected();
        }
    }

    public static void checkContains(final Collection<?> collection, final Object element) {
        if (!collection.contains(element)) {
            assertThat(collection).contains(element);
            failExceptionExpected();
        }
    }

    public static void checkContains(final Collection<?> collection, final Object element, final String message,
            final Object... args) {
        if (!collection.contains(element)) {
            assertThat(collection).as(message, args).contains(element);
            failExceptionExpected();
        }
    }

}
