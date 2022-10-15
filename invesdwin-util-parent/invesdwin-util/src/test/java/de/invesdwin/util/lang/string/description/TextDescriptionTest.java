package de.invesdwin.util.lang.string.description;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;
import org.slf4j.helpers.MessageFormatter;

import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class TextDescriptionTest {

    @Test
    public void tooManyArgs() {
        Assertions.assertThat(format("%s", "1", "2", "3")).isEqualTo("1 [2, 3]");
        Assertions.assertThat(format("", "1", "2", "3")).isEqualTo("[1, 2, 3]");
        Assertions.assertThat(format(null, "1", "2", "3")).isEqualTo("[1, 2, 3]");
        Assertions.assertThat(format("%s %s %s %s", "1", "2", "3")).isEqualTo("1 2 3 %s");
        Assertions.assertThat(format("abc x:\\%s def", "1", "2", "3")).isEqualTo("abc x:%s def [1, 2, 3]");
    }

    private String format(final String string, final Object... args) {
        return TextDescription.format(string, args);
    }

    @Test
    public void tooManyArgsAfterSlf4j() {
        Assertions.assertThat(formatSlf4j("%s", "1", "2", "3")).isEqualTo("1 [2, 3]");
        Assertions.assertThat(formatSlf4j("", "1", "2", "3")).isEqualTo("[1, 2, 3]");
        Assertions.assertThat(formatSlf4j(null, "1", "2", "3")).isEqualTo("[1, 2, 3]");
        Assertions.assertThat(formatSlf4j("%s %s %s %s", "1", "2", "3")).isEqualTo("1 2 3 %s");
        Assertions.assertThat(formatSlf4j("abc x:\\%s def", "1", "2", "3")).isEqualTo("abc x:%s def [1, 2, 3]");
    }

    private String formatSlf4j(final String string, final Object... args) {
        return MessageFormatter.arrayFormat(format(string, args), args).getMessage();
    }

}
