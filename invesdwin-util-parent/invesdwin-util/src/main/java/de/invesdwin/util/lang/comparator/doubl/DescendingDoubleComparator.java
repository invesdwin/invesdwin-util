package de.invesdwin.util.lang.comparator.doubl;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.lang.comparator.DescendingComparator;
import de.invesdwin.util.lang.comparator.IComparator;

@ThreadSafe
public class DescendingDoubleComparator extends DescendingComparator<Double> implements IDoubleComparator {

    private final IDoubleComparator ascending;

    protected DescendingDoubleComparator(final IDoubleComparator ascending) {
        super(ascending);
        this.ascending = ascending;
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
    protected IComparator<Double> newNotNullSafeComparator() {
        return new NotNullSafeDoubleComparator(this);
    }

    @Override
    public int comparePrimitive(final double e1, final double e2) {
        return ascending.comparePrimitive(e1, e2) * -1;
    }

}
