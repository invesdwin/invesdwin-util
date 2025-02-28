package de.invesdwin.util.collections.delegate;

import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ADelegateSet<E> extends ADelegateCollection<E> implements Set<E> {

    public ADelegateSet() {
        super();
    }

    protected ADelegateSet(final Set<E> delegate) {
        super(delegate);
    }

    @Override
    public Set<E> getDelegate() {
        return (Set<E>) super.getDelegate();
    }

    @Override
    protected abstract Set<E> newDelegate();

    public static <T> Set<T> maybeUnwrapToRoot(final Set<T> set) {
        Set<T> cur = set;
        while (cur instanceof ADelegateSet) {
            final ADelegateSet<T> c = (ADelegateSet<T>) cur;
            cur = c.getDelegate();
        }
        return cur;
    }

}
