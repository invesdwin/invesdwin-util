package de.invesdwin.util.collections;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.bean.AValueObject;

@ThreadSafe
public class MutableReference<T> extends AValueObject {

    private volatile T value;

    public T get() {
        return value;
    }

    public void set(final T value) {
        this.value = value;
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
        } else if (obj instanceof MutableReference) {
            final MutableReference<?> cObj = (MutableReference<?>) obj;
            return value.equals(cObj.value);
        } else {
            return value.equals(obj);
        }
    }

}
