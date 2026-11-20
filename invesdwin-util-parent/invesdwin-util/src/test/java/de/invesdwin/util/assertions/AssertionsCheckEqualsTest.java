package de.invesdwin.util.assertions;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

@NotThreadSafe
public class AssertionsCheckEqualsTest {

    @Test
    public void testMessageLabelsTheComparisonOfTwoStrings() {
        try {
            Assertions.checkEquals("one two", "one three", "[%s] the reference chunk differs", "idx_3");
            Assertions.fail("a differing string must fail the check");
        } catch (final AssertionError e) {
            //the test runner's comparison editor needs the failure to carry the two values, not the label
            Assertions.assertThat(e).isInstanceOf(AssertionFailedError.class);
            final AssertionFailedError comparisonFailure = (AssertionFailedError) e;
            Assertions.assertThat(comparisonFailure.getExpected().getValue()).isEqualTo("one two");
            Assertions.assertThat(comparisonFailure.getActual().getValue()).isEqualTo("one three");
            Assertions.assertThat(e.getMessage()).startsWith("[idx_3] the reference chunk differs");
        }
    }
}
