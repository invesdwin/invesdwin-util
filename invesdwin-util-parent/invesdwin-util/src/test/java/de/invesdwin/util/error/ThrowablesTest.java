package de.invesdwin.util.error;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class ThrowablesTest {

    @Test
    public void testMessagesAndStackTraceIfDebugEnabled() {
        final RuntimeException t = new RuntimeException("outer refusal", new IOException("inner cause"));
        final boolean enabledBefore = Throwables.isDebugStackTraceEnabled();
        try {
            Throwables.setDebugStackTraceEnabled(false);
            final String messages = Throwables.getMessagesAndStackTraceIfDebugEnabled(t);
            Assertions.assertThat(messages).contains("outer refusal");
            Assertions.assertThat(messages).contains("Caused by java.io.IOException: inner cause");
            Assertions.assertThat(messages).doesNotContain("at de.invesdwin.util.error.ThrowablesTest");

            Throwables.setDebugStackTraceEnabled(true);
            final String withStack = Throwables.getMessagesAndStackTraceIfDebugEnabled(t);
            Assertions.assertThat(withStack).startsWith(messages);
            //the switch is what the verdict reports check, so the caller's own frame must be in there
            Assertions.assertThat(withStack).contains("\tat de.invesdwin.util.error.ThrowablesTest.");
        } finally {
            Throwables.setDebugStackTraceEnabled(enabledBefore);
        }
    }

}
