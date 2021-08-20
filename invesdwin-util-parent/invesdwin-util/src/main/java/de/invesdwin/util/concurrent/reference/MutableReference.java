package de.invesdwin.util.concurrent.reference;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.bean.AValueObject;

@NotThreadSafe
public class MutableReference<T> extends AValueObject implements IMutableReference<T> {

    private T value;

    public MutableReference() {
    }

    public MutableReference(final T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(final T value) {
        this.value = value;
    }

    @Override
    public T getAndSet(final T value) {
        final T get = this.value;
        this.value = value;
        return get;
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

}
