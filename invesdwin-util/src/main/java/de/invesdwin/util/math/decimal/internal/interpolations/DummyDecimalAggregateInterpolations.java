package de.invesdwin.util.math.decimal.internal.interpolations;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.decimal.internal.DummyDecimalAggregate;
import de.invesdwin.util.math.decimal.interpolations.IDecimalAggregateInterpolations;
import de.invesdwin.util.math.decimal.interpolations.config.BSplineInterpolationConfig;
import de.invesdwin.util.math.decimal.interpolations.config.InterpolationConfig;
import de.invesdwin.util.math.decimal.interpolations.config.LoessInterpolationConfig;

@Immutable
public final class DummyDecimalAggregateInterpolations<E extends ADecimal<E>>
        implements IDecimalAggregateInterpolations<E> {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static final IDecimalAggregateInterpolations INSTANCE = new DummyDecimalAggregateInterpolations(
            DummyDecimalAggregate.getInstance());
    private final IDecimalAggregate<E> parent;

    private DummyDecimalAggregateInterpolations(final IDecimalAggregate<E> parent) {
        this.parent = parent;
    }

    @Override
    public IDecimalAggregate<E> loess(final LoessInterpolationConfig config) {
        return parent;
    }

    @Override
    public IDecimalAggregate<E> bSpline(final BSplineInterpolationConfig config) {
        return parent;
    }

    @Override
    public IDecimalAggregate<E> cubicBSpline(final InterpolationConfig config) {
        return parent;
    }

    @Override
    public IDecimalAggregate<E> bezierCurve(final InterpolationConfig config) {
        return parent;
    }

}
