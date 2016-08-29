package de.invesdwin.util.math.decimal.internal;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

import com.graphbuilder.curve.BSpline;
import com.graphbuilder.curve.BezierCurve;
import com.graphbuilder.curve.ControlPath;
import com.graphbuilder.curve.CubicBSpline;
import com.graphbuilder.curve.Curve;
import com.graphbuilder.curve.GroupIterator;
import com.graphbuilder.curve.MultiPath;
import com.graphbuilder.geom.PointFactory;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.decimal.config.BSplineInterpolationConfig;
import de.invesdwin.util.math.decimal.config.InterpolationConfig;
import de.invesdwin.util.math.decimal.config.LoessInterpolationConfig;
import de.invesdwin.util.math.decimal.scaled.PercentScale;

@NotThreadSafe
public class DecimalAggregateInterpolations<E extends ADecimal<E>> {

    private static final double PUNISH_EDGE_FACTOR = 2;

    private final DecimalAggregate<E> parent;
    private final List<? extends E> values;
    private final E converter;

    public DecimalAggregateInterpolations(final DecimalAggregate<E> parent) {
        this.parent = parent;
        this.values = parent.values();
        this.converter = parent.getConverter();
    }

    public IDecimalAggregate<E> cubicBSplineInterpolation(final InterpolationConfig config) {
        if (values.size() < 4) {
            return parent;
        }

        final List<Double> xval = new ArrayList<Double>();
        final List<Double> yval = new ArrayList<Double>();
        fillInterpolationPoints(xval, yval, config);

        final ControlPath cp = new ControlPath();
        for (int i = 0; i < xval.size(); i++) {
            cp.addPoint(PointFactory.create(xval.get(i), yval.get(i)));
        }
        final GroupIterator gi = new GroupIterator("0:n-1", cp.numPoints());
        final CubicBSpline curve = new CubicBSpline(cp, gi);
        curve.setInterpolateEndpoints(true);
        calculateCurve(xval, yval, curve);

        final UnivariateInterpolator interpolator = new SplineInterpolator();
        return interpolate(xval, yval, interpolator);
    }

    public IDecimalAggregate<E> bezierCurveInterpolation(final InterpolationConfig config) {
        final List<Double> xval = new ArrayList<Double>();
        final List<Double> yval = new ArrayList<Double>();
        fillInterpolationPoints(xval, yval, config);

        final ControlPath cp = new ControlPath();
        for (int i = 0; i < xval.size(); i++) {
            cp.addPoint(PointFactory.create(xval.get(i), yval.get(i)));
        }
        final GroupIterator gi = new GroupIterator("0:n-1", cp.numPoints());
        final BezierCurve curve = new BezierCurve(cp, gi);
        calculateCurve(xval, yval, curve);

        final UnivariateInterpolator interpolator = new SplineInterpolator();
        return interpolate(xval, yval, interpolator);
    }

    public IDecimalAggregate<E> bSplineInterpolation(final BSplineInterpolationConfig config) {
        final List<Double> xval = new ArrayList<Double>();
        final List<Double> yval = new ArrayList<Double>();
        fillInterpolationPoints(xval, yval, config);

        final ControlPath cp = new ControlPath();
        for (int i = 0; i < xval.size(); i++) {
            cp.addPoint(PointFactory.create(xval.get(i), yval.get(i)));
        }
        final GroupIterator gi = new GroupIterator("0:n-1", cp.numPoints());
        final BSpline curve = new BSpline(cp, gi);
        curve.setDegree(config.getDegree());
        final int maxDegree = cp.numPoints() - 1;
        if (curve.getDegree() > maxDegree) {
            curve.setDegree(maxDegree);
        }
        calculateCurve(xval, yval, curve);

        final UnivariateInterpolator interpolator = new SplineInterpolator();
        return interpolate(xval, yval, interpolator);
    }

    private void calculateCurve(final List<Double> xval, final List<Double> yval, final Curve curve) {
        final MultiPath mp = new MultiPath(2);
        curve.appendTo(mp);
        xval.clear();
        yval.clear();
        for (int p = 0; p < mp.getNumPoints(); p++) {
            final double[] point = mp.get(p);
            Assertions.checkEquals(point.length, 3);
            final double x = point[0];
            if (xval.isEmpty() || x > xval.get(xval.size() - 1)) {
                final double y = point[1];
                xval.add(x);
                yval.add(y);
            }
        }
    }

    private IDecimalAggregate<E> interpolate(final List<Double> xval, final List<Double> yval,
            final UnivariateInterpolator interpolator) {
        final UnivariateFunction interpolated = interpolator.interpolate(Doubles.toArray(xval), Doubles.toArray(yval));
        final List<E> interpolatedValues = new ArrayList<E>();
        for (int i = 0; i < values.size(); i++) {
            final E value = converter.fromDefaultValue(Decimal.valueOf(interpolated.value(i)));
            interpolatedValues.add(value);
        }
        Assertions.assertThat(interpolatedValues).hasSameSizeAs(values);
        return new DecimalAggregate<E>(interpolatedValues, converter);
    }

    public IDecimalAggregate<E> loessInterpolation(final LoessInterpolationConfig config) {
        if (values.size() < 3) {
            return parent;
        }

        final List<Double> xval = new ArrayList<Double>();
        final List<Double> yval = new ArrayList<Double>();
        fillInterpolationPoints(xval, yval, config);
        double bandwidth = config.getSmoothness().getValue(PercentScale.RATE).doubleValue();
        if (bandwidth * values.size() < 2) {
            bandwidth = Decimal.TWO.divide(values.size()).doubleValue();
        }
        final UnivariateInterpolator interpolator = new LoessInterpolator(bandwidth,
                LoessInterpolator.DEFAULT_ROBUSTNESS_ITERS);
        return interpolate(xval, yval, interpolator);
    }

    private void fillInterpolationPoints(final List<Double> xval, final List<Double> yval,
            final InterpolationConfig config) {
        for (int i = 0; i < values.size(); i++) {
            xval.add((double) i);
            final double y = values.get(i).doubleValue();
            if (Double.isFinite(y)) {
                yval.add(y);
            } else {
                yval.add(0D);
            }
        }
        if (config.isPunishEdges() && values.size() >= 5) {
            Double minValue = null;
            Double maxValue = null;
            for (final double y : yval) {
                minValue = Doubles.min(minValue, y);
                maxValue = Doubles.max(maxValue, y);
            }
            xval.add(0, -1D);
            yval.add(0, punishEdgeValue(yval.get(0), config, minValue, maxValue));
            xval.add((double) values.size());
            yval.add(punishEdgeValue(yval.get(yval.size() - 1), config, minValue, maxValue));
        }
    }

    private double punishEdgeValue(final double value, final InterpolationConfig config, final Double minValue,
            final Double maxValue) {
        if (config.isHigherIsBetter()) {
            if (value > 0) {
                return Math.max(minValue, value / PUNISH_EDGE_FACTOR);
            } else {
                return Math.max(minValue, value * PUNISH_EDGE_FACTOR);
            }
        } else {
            if (value > 0) {
                return Math.min(maxValue, value * PUNISH_EDGE_FACTOR);
            } else {
                return Math.min(maxValue, value / PUNISH_EDGE_FACTOR);
            }
        }
    }

}
