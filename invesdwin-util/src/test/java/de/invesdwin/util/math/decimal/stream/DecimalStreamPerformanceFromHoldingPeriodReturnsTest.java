package de.invesdwin.util.math.decimal.stream;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;

@NotThreadSafe
public class DecimalStreamPerformanceFromHoldingPeriodReturnsTest {

    @Test
    public void testProcessAll() {
        final DecimalStreamPerformanceFromHoldingPeriodReturns perrormance = new DecimalStreamPerformanceFromHoldingPeriodReturns();
        perrormance.process(Percent.TEN_PERCENT);
        perrormance.process(Percent.MINUS_TWO_PERCENT);
        perrormance.process(Percent.TEN_PERCENT);
        perrormance.process(Percent.ONE_PERCENT);
        perrormance.process(Percent.MINUS_FIVE_PERCENT);

        final DecimalStreamPerformanceFromHoldingPeriodReturns laterGeomAvg = new DecimalStreamPerformanceFromHoldingPeriodReturns();
        final Decimal prevPerformance = perrormance.getPerformance();
        laterGeomAvg.process(new Percent(prevPerformance.subtract(Decimal.ONE), PercentScale.RATE));
        laterGeomAvg.process(Percent.TEN_PERCENT);

        Assertions.assertThat(perrormance.getPerformance()).isSameAs(prevPerformance);

        perrormance.process(Percent.TEN_PERCENT);

        Assertions.assertThat(perrormance.getPerformance()).isNotEqualTo(prevPerformance);
        Assertions.assertThat(prevPerformance.multiply(Percent.ONE_HUNDRED_PERCENT.add(Percent.TEN_PERCENT).getRate()))
                .isEqualTo(perrormance.getPerformance());
        Assertions.assertThat(laterGeomAvg.getPerformance()).isEqualTo(perrormance.getPerformance());
    }

}
