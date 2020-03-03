package de.invesdwin.util.collections.iterable.collection.fast;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ACloseableIterator;
import de.invesdwin.util.lang.description.TextDescription;

@NotThreadSafe
public abstract class AFastToListCloseableIterator<E> extends ACloseableIterator<E>
        implements IFastToListCloseableIterator<E> {

    public AFastToListCloseableIterator(final TextDescription name) {
        super(name);
    }

}
