package de.invesdwin.util.concurrent.reference;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class UnmodifiableReference<T> implements IMutableReference<T> {

    private final T value;

    private UnmodifiableReference(final T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(final T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T getAndSet(final T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        if (value == null) {
            return super.hashCode();
        } else {
            return value.hashCode();
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (value == null) {
            return super.equals(obj);
        } else if (obj instanceof IReference) {
            final IReference<?> cObj = (IReference<?>) obj;
            return value.equals(cObj.get());
        } else {
            return value.equals(obj);
        }
    }

    public static <T> UnmodifiableReference<T> of(final T value) {
        return new UnmodifiableReference<>(value);
    }

}
