package de.invesdwin.util.lang.comparator.doubl;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.lang.comparator.AComparator;
import de.invesdwin.util.lang.comparator.IComparator;

@ThreadSafe
public abstract class ADoubleComparator extends AComparator<Double> implements IDoubleComparator {

    @Override
    public int compareTypedNotNullSafe(final Double e1, final Double e2) {
        return comparePrimitive(e1.doubleValue(), e2.doubleValue());
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
    public IDoubleComparator asAscending() {
        return (IDoubleComparator) super.asAscending();
    }

    @Override
    public IDoubleComparator asDescending() {
        return (IDoubleComparator) super.asDescending();
    }

    @Override
    protected IComparator<Double> newNotNullSafeComparator() {
        return new NotNullSafeDoubleComparator(this);
    }

    @Override
    protected IComparator<Double> newDescendingComparator() {
        return new DescendingDoubleComparator(this);
    }

}
