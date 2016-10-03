package de.invesdwin.util.collections;

import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ADelegateSet<E> extends ADelegateCollection<E> implements Set<E> {

    private final Set<E> delegateSet = (Set<E>) super.getDelegate();

    @Override
    protected Set<E> getDelegate() {
        return delegateSet;
    }

    @Override
    protected abstract Set<E> newDelegate();

}
