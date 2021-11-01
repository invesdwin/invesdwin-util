package de.invesdwin.util.math.decimal.interpolations;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.interpolations.config.BSplineInterpolationConfig;
import de.invesdwin.util.math.decimal.interpolations.config.SplineInterpolationConfig;

@NotThreadSafe
public class DecimalAggregateInterpolationsTest {

    @Test
    public void testBezierCurveLimits() {
        final List<Decimal> values = new ArrayList<Decimal>();
        for (int i = 0; i < 100000; i++) {
            values.add(new Decimal(i));
        }
        final List<? extends Decimal> interpolatedValues = Decimal.valueOf(values)
                .interpolate()
                .bezierCurve(new SplineInterpolationConfig())
                .values();
        Assertions.assertThat(values).hasSameSizeAs(interpolatedValues);
    }

    @Test
    public void testBSplineLimits() {
        final List<Decimal> values = new ArrayList<Decimal>();
        for (int i = 0; i < 21; i++) {
            values.add(new Decimal(i));
        }
        final List<? extends Decimal> interpolatedValues = Decimal.valueOf(values)
                .interpolate()
                .bSpline(new BSplineInterpolationConfig().setDegree(values.size() - 1))
                .values();
        Assertions.assertThat(values).hasSameSizeAs(interpolatedValues);
    }

}
