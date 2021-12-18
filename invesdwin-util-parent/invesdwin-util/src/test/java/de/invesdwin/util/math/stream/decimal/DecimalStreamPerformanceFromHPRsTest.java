package de.invesdwin.util.math.stream.decimal;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;

@NotThreadSafe
public class DecimalStreamPerformanceFromHPRsTest {

    @Test
    public void testProcessAll() {
        final DecimalStreamPerformanceFromHPRs perrormance = new DecimalStreamPerformanceFromHPRs();
        perrormance.process(Percent.TEN_PERCENT);
        perrormance.process(Percent.MINUS_TWO_PERCENT);
        perrormance.process(Percent.TEN_PERCENT);
        perrormance.process(Percent.ONE_PERCENT);
        perrormance.process(Percent.MINUS_FIVE_PERCENT);

        final DecimalStreamPerformanceFromHPRs laterGeomAvg = new DecimalStreamPerformanceFromHPRs();
        final Percent prevPerformance = perrormance.getPerformance();
        laterGeomAvg.process(
                new Percent(prevPerformance.subtract(Percent.ONE_HUNDRED_PERCENT).doubleValue(), PercentScale.RATE));
        laterGeomAvg.process(Percent.TEN_PERCENT);

        Assertions.assertThat(perrormance.getPerformance()).isSameAs(prevPerformance);

        perrormance.process(Percent.TEN_PERCENT);

        Assertions.assertThat(perrormance.getPerformance()).isNotEqualTo(prevPerformance);
        Assertions.assertThat(prevPerformance.multiply(Percent.ONE_HUNDRED_PERCENT.add(Percent.TEN_PERCENT).getRate()))
                .isEqualTo(perrormance.getPerformance());
        Assertions.assertThat(laterGeomAvg.getPerformance()).isEqualTo(perrormance.getPerformance());
    }

}
