package de.invesdwin.util.lang.reflection;

import java.lang.reflect.Field;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Useful to access private field of private inner classes where methodhandles do not work. This also circumvents the
 * illegal reflection access warning that occurs for JVM internals.
 * 
 * This is also an alternative to VarHandle with MethodHandles.privateLookup in Java 8, since that feature was only
 * added in Java 9.
 */
@NotThreadSafe
@SuppressWarnings("restriction")
public class UnsafeField<T> {

    private final sun.misc.Unsafe unsafe = Reflections.getUnsafe();
    private final long offset;

    public UnsafeField(final Field field) {
        this.offset = unsafe.objectFieldOffset(field);
    }

    @SuppressWarnings("unchecked")
    public T get(final Object obj) {
        return (T) unsafe.getObject(obj, offset);
    }

    public void set(final Object obj, final T value) {
        unsafe.putObject(obj, offset, value);
    }

}
