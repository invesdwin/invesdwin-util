package de.invesdwin.util.math.stream.decimal;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.decimal.scaled.Percent;

@NotThreadSafe
public class DecimalStreamGeomAvgTest {

    @Test
    public void testProcessAll() {
        final DecimalStreamGeomAvg<Percent> geomAvg = new DecimalStreamGeomAvg<Percent>(Percent.ZERO_PERCENT) {
            @Override
            protected Percent getValueAdjustmentAddition() {
                return Percent.ONE_HUNDRED_PERCENT;
            }
        };
        geomAvg.process(Percent.TEN_PERCENT);
        geomAvg.process(Percent.MINUS_TWO_PERCENT);
        geomAvg.process(Percent.TEN_PERCENT);
        geomAvg.process(Percent.ONE_PERCENT);
        geomAvg.process(Percent.MINUS_FIVE_PERCENT);

        final DecimalStreamGeomAvg<Percent> laterGeomAvg = new DecimalStreamGeomAvg<Percent>(Percent.ZERO_PERCENT) {
            @Override
            protected Percent getValueAdjustmentAddition() {
                return Percent.ONE_HUNDRED_PERCENT;
            }
        };
        final Percent prevGeomAvg = geomAvg.getGeomAvg();
        laterGeomAvg.process(prevGeomAvg);
        laterGeomAvg.process(Percent.TEN_PERCENT);

        Assertions.assertThat(geomAvg.getGeomAvg()).isSameAs(prevGeomAvg);

        geomAvg.process(Percent.TEN_PERCENT);

        Assertions.assertThat(geomAvg.getGeomAvg()).isNotEqualTo(prevGeomAvg);
        Assertions.assertThat(prevGeomAvg.multiply(Percent.ONE_HUNDRED_PERCENT.add(Percent.TEN_PERCENT)))
                .isNotEqualTo(geomAvg.getGeomAvg());
        Assertions.assertThat(laterGeomAvg.getGeomAvg()).isNotEqualTo(geomAvg.getGeomAvg());
    }

}
