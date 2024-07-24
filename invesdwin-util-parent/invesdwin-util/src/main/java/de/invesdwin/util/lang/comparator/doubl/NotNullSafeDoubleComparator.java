package de.invesdwin.util.lang.comparator.doubl;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.comparator.NotNullSafeComparator;

@Immutable
public class NotNullSafeDoubleComparator extends NotNullSafeComparator<Double> implements IDoubleComparator {

    private final IDoubleComparator nullSafe;

    protected NotNullSafeDoubleComparator(final IDoubleComparator nullSafe) {
        super(nullSafe);
        this.nullSafe = nullSafe;
    }

    @Override
    public IDoubleComparator asAscending() {
        return (IDoubleComparator) super.asAscending();
    }

    @Override
    public IDoubleComparator asDescending() {
        return (IDoubleComparator) super.asDescending();
    }

    @Override
    public IDoubleComparator asNullSafe() {
        return (IDoubleComparator) super.asNullSafe();
    }

    @Override
    public IDoubleComparator asNotNullSafe() {
        return (IDoubleComparator) super.asNotNullSafe();
    }

    @Override
    public int comparePrimitive(final double e1, final double e2) {
        return nullSafe.comparePrimitive(e1, e2);
    }

}
