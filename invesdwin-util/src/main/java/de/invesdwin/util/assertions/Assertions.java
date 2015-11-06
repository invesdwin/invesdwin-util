package de.invesdwin.util.assertions;

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
            fail("Exception expected");
        }
    }

}
