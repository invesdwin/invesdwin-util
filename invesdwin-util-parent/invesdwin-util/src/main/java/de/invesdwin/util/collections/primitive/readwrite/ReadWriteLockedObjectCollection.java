package de.invesdwin.util.collections.primitive.readwrite;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.primitive.objkey.striped.IObjectIterator;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.error.FastNoSuchElementException;
import it.unimi.dsi.fastutil.objects.ObjectCollection;

/**
 * @see java.util.Collections#synchronizedCollection(Collection)
 */
@ThreadSafe
public class ReadWriteLockedObjectCollection<E> implements ObjectCollection<E>, Serializable {
    @Serial
    private static final long serialVersionUID = -2261498858663173273L;
    private final Collection<E> c; // Backing Collection
    private final IReadWriteLock lock;

    public ReadWriteLockedObjectCollection(final Collection<E> c) {
        this.c = Assertions.checkNotNull(c);
        //CHECKSTYLE:OFF
        lock = Locks.newReentrantReadWriteLock(getClass().getSimpleName());
        //CHECKSTYLE:ON
    }//new

    public ReadWriteLockedObjectCollection(final Collection<E> c, final IReadWriteLock mutex) {
        this.c = Assertions.checkNotNull(c);
        this.lock = Assertions.checkNotNull(mutex);
    }//new

    private void unlockReadLock() {
        lock.readLock().unlock();
    }

    protected ICloseableLock read() {
        lock.readLock().lock();
        return this::unlockReadLock;
    }

    private void unlockWriteLock() {
        lock.writeLock().unlock();
    }

    protected ICloseableLock write() {
        lock.writeLock().lock();
        return this::unlockWriteLock;
    }

    @Override
    public int size() {
        try (ICloseableLock lock = read()) {
            return c.size();
        }
    }

    @Override
    public boolean isEmpty() {
        try (ICloseableLock lock = read()) {
            return c.isEmpty();
        }
    }

    @Override
    public boolean contains(final Object o) {
        try (ICloseableLock lock = read()) {
            return c.contains(o);
        }
    }

    @Override
    public IObjectIterator<E> iterator() {
        return new IObjectIterator<E>() {
            private Iterator<E> src;

            private Iterator<E> it() {
                if (src == null) {
                    src = c.iterator();
                }
                return src;
            }

            @Override
            public boolean hasNext() {
                try (ICloseableLock lock = read()) {
                    return it().hasNext();
                }
            }

            @Override
            public E next() {
                try (ICloseableLock lock = read()) {
                    if (!hasNext()) {
                        throw FastNoSuchElementException.getInstance("hasNext returned false");
                    }
                    return it().next();
                }
            }

            @Override
            public void remove() {
                try (ICloseableLock lock = write()) {
                    it().remove();
                }
            }

            @Override
            public boolean tryAdvance(final Consumer<? super E> action) {
                final E v;
                try (ICloseableLock lock = read()) {
                    final Iterator<E> i = it();
                    if (i.hasNext()) {
                        v = i.next();
                    } else {
                        return false;
                    }
                }
                action.accept(v);
                return true;
            }

            @Override
            public long estimateSize() {
                return ReadWriteLockedObjectCollection.this.size();
            }

            @Override
            public long getExactSizeIfKnown() {
                return ReadWriteLockedObjectCollection.this.size();
            }
        };
    }

    @Override
    public Object[] toArray() {
        try (ICloseableLock lock = read()) {
            return c.toArray();
        }
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        try (ICloseableLock lock = read()) {
            return c.toArray(a);
        }
    }

    @Override
    public <T> T[] toArray(final IntFunction<T[]> f) {
        try (ICloseableLock lock = read()) {
            return c.toArray(f);
        }
    }

    @Override
    public boolean containsAll(final Collection<?> coll) {
        try (ICloseableLock lock = read()) {
            return c.containsAll(coll);
        }
    }

    @Override
    public String toString() {
        try (ICloseableLock lock = read()) {
            return c.toString();
        }
    }

    @Override
    public void forEach(final Consumer<? super E> consumer) {
        try (ICloseableLock lock = read()) {
            c.forEach(consumer);
        }
    }

    @Serial
    private void writeObject(final ObjectOutputStream s) throws IOException {
        try (ICloseableLock lock = read()) {
            s.defaultWriteObject();
        }
    }

    @Override
    public int hashCode() {
        try (ICloseableLock lock = read()) {
            return c.hashCode();
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        try (ICloseableLock lock = read()) {
            if (obj instanceof ReadWriteLockedObjectCollection<?>) {
                final ReadWriteLockedObjectCollection<?> cObj = (ReadWriteLockedObjectCollection<?>) obj;
                return c.equals(cObj.c);
            } else {
                return c.equals(obj);
            }
        }
    }

    @Override
    public boolean add(final E e) {
        try (ICloseableLock lock = write()) {
            return c.add(e);
        }
    }

    @Override
    public boolean remove(final Object o) {
        try (ICloseableLock lock = write()) {
            return c.remove(o);
        }
    }

    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        try (ICloseableLock lock = write()) {
            return c.addAll(coll);
        }
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public final boolean addAll(final E... elements) {
        try (ICloseableLock lock = write()) {
            return Collections.addAll(c, elements);
        }
    }

    @Override
    public boolean removeAll(final Collection<?> coll) {
        try (ICloseableLock lock = write()) {
            return c.removeAll(coll);
        }
    }

    @Override
    public boolean removeIf(final Predicate<? super E> filter) {
        try (ICloseableLock lock = write()) {
            return c.removeIf(filter);
        }
    }

    @Override
    public boolean retainAll(final Collection<?> coll) {
        try (ICloseableLock lock = write()) {
            return c.retainAll(coll);
        }
    }

    @Override
    public void clear() {
        try (ICloseableLock lock = write()) {
            c.clear();
        }
    }

    @Override
    public IObjectIterator<E> spliterator() {
        return iterator();
    }

    @Override
    public Stream<E> stream() {
        return iterator().stream();
    }

    @Override
    public Stream<E> parallelStream() {
        return iterator().stream().parallel();
    }
}