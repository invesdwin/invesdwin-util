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
import de.invesdwin.util.bean.tuple.Pair;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.decimal.config.BSplineInterpolationConfig;
import de.invesdwin.util.math.decimal.config.InterpolationConfig;
import de.invesdwin.util.math.decimal.config.LoessInterpolationConfig;
import de.invesdwin.util.math.decimal.scaled.PercentScale;

@NotThreadSafe
public class DecimalAggregateInterpolations<E extends ADecimal<E>> {

    //actual limit is 1030, but we want to stay safe
    private static final int BEZIER_CURVE_MAX_SIZE = 1000;
    private static final double PUNISH_NEGATIVE_EDGE_FACTOR = 2;

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

        final Pair<List<Double>, List<Double>> pair = fillInterpolationPoints(config, null);
        final List<Double> xval = pair.getFirst();
        final List<Double> yval = pair.getSecond();

        final ControlPath cp = new ControlPath();
        for (int i = 0; i < xval.size(); i++) {
            cp.addPoint(PointFactory.create(xval.get(i), yval.get(i)));
        }
        final GroupIterator gi = new GroupIterator("0:n-1", cp.numPoints());
        final CubicBSpline curve = new CubicBSpline(cp, gi);
        curve.setInterpolateEndpoints(true);
        calculateCurve(xval, yval, curve);

        final UnivariateInterpolator interpolator = new SplineInterpolator();
        return interpolate(config, xval, yval, interpolator);
    }

    public IDecimalAggregate<E> bezierCurveInterpolation(final InterpolationConfig config) {
        final Pair<List<Double>, List<Double>> pair = fillInterpolationPoints(config, BEZIER_CURVE_MAX_SIZE);
        final List<Double> xval = pair.getFirst();
        final List<Double> yval = pair.getSecond();

        final ControlPath cp = new ControlPath();
        for (int i = 0; i < xval.size(); i++) {
            cp.addPoint(PointFactory.create(xval.get(i), yval.get(i)));
        }
        final GroupIterator gi = new GroupIterator("0:n-1", cp.numPoints());
        final BezierCurve curve = new BezierCurve(cp, gi);
        calculateCurve(xval, yval, curve);

        final UnivariateInterpolator interpolator = new SplineInterpolator();
        return interpolate(config, xval, yval, interpolator);
    }

    public IDecimalAggregate<E> bSplineInterpolation(final BSplineInterpolationConfig config) {
        final Pair<List<Double>, List<Double>> pair = fillInterpolationPoints(config, null);
        final List<Double> xval = pair.getFirst();
        final List<Double> yval = pair.getSecond();

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
        return interpolate(config, xval, yval, interpolator);
    }

    /**
     * Synchronized since BSpline and BezierCurve are not thread-safe since they have static arrays...
     */
    private static synchronized void calculateCurve(final List<Double> xval, final List<Double> yval,
            final Curve curve) {
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

    private IDecimalAggregate<E> interpolate(final InterpolationConfig config, final List<Double> xval,
            final List<Double> yval, final UnivariateInterpolator interpolator) {
        final UnivariateFunction interpolated = interpolator.interpolate(Doubles.toArray(xval), Doubles.toArray(yval));
        final List<E> interpolatedValues = new ArrayList<E>();
        interpolateAndMaybeReverseMultiplier(config, interpolated, interpolatedValues);
        Assertions.assertThat(interpolatedValues).hasSameSizeAs(values);
        return new DecimalAggregate<E>(interpolatedValues, converter);
    }

    private void interpolateAndMaybeReverseMultiplier(final InterpolationConfig config,
            final UnivariateFunction interpolated, final List<E> results) {
        //splitting the loops for performance reasons
        if (config.getValueMultiplicator() != null) {
            final double multiplier = config.getValueMultiplicator().doubleValue();
            for (int i = 0; i < values.size(); i++) {
                final double result = interpolated.value(i) / multiplier;
                final E value = converter.fromDefaultValue(Decimal.valueOf(result));
                results.add(value);
            }
        } else {
            for (int i = 0; i < values.size(); i++) {
                final double result = interpolated.value(i);
                final E value = converter.fromDefaultValue(Decimal.valueOf(result));
                results.add(value);
            }
        }
    }

    public IDecimalAggregate<E> loessInterpolation(final LoessInterpolationConfig config) {
        if (values.size() < 3) {
            return parent;
        }

        final Pair<List<Double>, List<Double>> pair = fillInterpolationPoints(config, null);
        final List<Double> xval = pair.getFirst();
        final List<Double> yval = pair.getSecond();
        double bandwidth = config.getSmoothness().getValue(PercentScale.RATE).doubleValue();
        if (bandwidth * values.size() < 2) {
            bandwidth = Decimal.TWO.divide(values.size()).doubleValue();
        }
        final UnivariateInterpolator interpolator = new LoessInterpolator(bandwidth,
                LoessInterpolator.DEFAULT_ROBUSTNESS_ITERS);
        return interpolate(config, xval, yval, interpolator);
    }

    private <T extends ADecimal<T>> Pair<List<Double>, List<Double>> fillInterpolationPoints(
            final InterpolationConfig config, final Integer absoluteMaxSize) {
        List<Double> xval = new ArrayList<Double>(values.size());
        List<Double> yval = new ArrayList<Double>(values.size());
        fillAndMaybeApplyMultiplier(config, xval, yval);
        final Integer maxSize = Integers.min(absoluteMaxSize, config.getMaxPoints());
        if (maxSize != null) {
            while (xval.size() > maxSize) {
                final Pair<List<Double>, List<Double>> pair = makeHalfSize(xval, yval);
                xval = pair.getFirst();
                yval = pair.getSecond();
            }
        }
        if (config.isPunishEdges() && values.size() >= 5) {
            Double minValue = null;
            Double maxValue = null;
            for (final double y : yval) {
                minValue = Doubles.min(minValue, y);
                maxValue = Doubles.max(maxValue, y);
            }
            final double startPunished = punishEdgeValue(yval.get(0), config, minValue, maxValue);
            xval.add(0, -1D);
            yval.add(0, startPunished);
            xval.add((double) values.size());
            final double endPunished = punishEdgeValue(yval.get(yval.size() - 1), config, minValue, maxValue);
            yval.add(endPunished);
        }
        return Pair.of(xval, yval);
    }

    private void fillAndMaybeApplyMultiplier(final InterpolationConfig config, final List<Double> xval,
            final List<Double> yval) {
        //splitting the loops for performance reasons
        if (config.getValueMultiplicator() != null) {
            final double multipier = config.getValueMultiplicator().doubleValue();
            for (int i = 0; i < values.size(); i++) {
                xval.add((double) i);
                final double y = values.get(i).doubleValue() * multipier;
                if (Double.isFinite(y)) {
                    yval.add(y);
                } else {
                    yval.add(0D);
                }
            }
        } else {
            for (int i = 0; i < values.size(); i++) {
                xval.add((double) i);
                final double y = values.get(i).doubleValue();
                if (Double.isFinite(y)) {
                    yval.add(y);
                } else {
                    yval.add(0D);
                }
            }
        }
    }

    private double punishEdgeValue(final double value, final InterpolationConfig config, final Double minValue,
            final Double maxValue) {
        if (config.isHigherBetter()) {
            if (value > 0) {
                return 0d;
            } else {
                return Math.max(minValue, value * PUNISH_NEGATIVE_EDGE_FACTOR);
            }
        } else {
            if (value > 0) {
                return Math.min(maxValue, value * PUNISH_NEGATIVE_EDGE_FACTOR);
            } else {
                return 0;
            }
        }
    }

    /**
     * Reduce the amount of points by averaging two points together in the middle while keeping the edges as they are so
     * that the interpolation still works
     */
    private static Pair<List<Double>, List<Double>> makeHalfSize(final List<Double> xval, final List<Double> yval) {
        Assertions.checkEquals(xval.size(), yval.size());
        final List<Double> newxval = new ArrayList<Double>(xval.size());
        final List<Double> newyval = new ArrayList<Double>(yval.size());
        //keep first value as it is
        newxval.add(xval.get(0));
        newyval.add(yval.get(yval.size() - 1));
        if (xval.size() % 2 == 0) {
            //we round number of elements, so we can just go from left to right
            for (int i = 1; i < xval.size() - 3; i += 2) {
                newxval.add((xval.get(i) + xval.get(i + 1)) / 2);
                newyval.add((yval.get(i) + yval.get(i + 1)) / 2);
            }
        } else {
            //keep the middle value as it is
            final int middleIndex = xval.size() / 2;
            for (int i = 1; i < middleIndex; i += 2) {
                //make the middle values half the size
                newxval.add((xval.get(i) + xval.get(i + 1)) / 2);
                newyval.add((yval.get(i) + yval.get(i + 1)) / 2);
            }
            newxval.add(xval.get(middleIndex));
            newyval.add(yval.get(middleIndex));
            for (int i = middleIndex + 1; i < xval.size() - 3; i += 2) {
                //make the middle values half the size
                newxval.add((xval.get(i) + xval.get(i + 1)) / 2);
                newyval.add((yval.get(i) + yval.get(i + 1)) / 2);
            }
        }
        //keep last value as it is
        newxval.add(xval.get(xval.size() - 1));
        newyval.add(yval.get(yval.size() - 1));
        return Pair.of(newxval, newyval);
    }

}
