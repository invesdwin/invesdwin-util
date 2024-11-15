package de.invesdwin.util.math;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.bean.tuple.IPair;
import de.invesdwin.util.lang.Objects;

@Immutable
public class DoublePair extends AValueObject implements IPair<Double, Double> {

    private static final DoublePair EMPTY = DoublePair.of(Double.NaN, Double.NaN);

    /** The first value in this tuple. */
    private final double first;

    /** The second value in this tuple. */
    private final double second;

    /**
     * Creates a new </code>{@link DoublePair}</code>.
     * 
     * @param first
     *            the 1st. value in this tuple.
     * @param second
     *            the 2nd. value in this tuple.
     */
    protected DoublePair(final double first, final double second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public Double getFirst() {
        return first;
    }

    public double getFirstValue() {
        return first;
    }

    @Override
    public Double getSecond() {
        return second;
    }

    public double getSecondValue() {
        return second;
    }

    public static DoublePair of(final double first, final double second) {
        return new DoublePair(first, second);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(IPair.class, getFirst(), getSecond());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof DoublePair) {
            final DoublePair castObj = (DoublePair) obj;
            return getFirstValue() == castObj.getFirstValue() && getSecondValue() == castObj.getSecondValue();
        } else {
            return false;
        }
    }

    public static DoublePair empty() {
        return EMPTY;
    }

}
