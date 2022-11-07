package de.invesdwin.util.assertions.type;

import javax.annotation.concurrent.NotThreadSafe;

import org.assertj.core.api.DoubleAssert;

import de.invesdwin.util.math.Doubles;

@NotThreadSafe
public class RoundingDoubleAssert extends DoubleAssert {

    public RoundingDoubleAssert(final Double actual) {
        super(Doubles.round(actual));
    }

    public RoundingDoubleAssert(final double actual) {
        super(Doubles.round(actual));
    }

    @Override
    public DoubleAssert isEqualByComparingTo(final Double other) {
        return super.isEqualByComparingTo(Doubles.round(other));
    }

    @Override
    public DoubleAssert isEqualTo(final Double expected) {
        return super.isEqualTo(Doubles.round(expected));
    }

    @Override
    public DoubleAssert isEqualTo(final double expected) {
        return super.isEqualTo(Doubles.round(expected));
    }

}
