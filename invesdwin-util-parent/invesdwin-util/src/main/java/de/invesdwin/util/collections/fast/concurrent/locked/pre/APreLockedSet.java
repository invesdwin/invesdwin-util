package de.invesdwin.util.collections.fast.concurrent.locked.pre;

import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.lang.description.TextDescription;

@ThreadSafe
public abstract class APreLockedSet<E> extends APreLockedCollection<E> implements Set<E> {

    public APreLockedSet(final TextDescription iteratorName) {
        super(iteratorName);
    }

    public APreLockedSet(final TextDescription iteratorName, final ILock lock) {
        super(iteratorName, lock);
    }

    @Override
    protected abstract Set<E> getPreLockedDelegate();

}