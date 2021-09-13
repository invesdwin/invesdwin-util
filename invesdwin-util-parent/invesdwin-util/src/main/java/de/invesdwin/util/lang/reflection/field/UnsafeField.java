package de.invesdwin.util.lang.reflection.field;

import java.lang.reflect.Field;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.UnsafeAccess;

/**
 * Useful to access private field of private inner classes where methodhandles do not work. This also circumvents the
 * illegal reflection access warning that occurs for JVM internals.
 * 
 * This is also an alternative to VarHandle with MethodHandles.privateLookup in Java 8, since that feature was only
 * added in Java 9.
 */
@NotThreadSafe
public class UnsafeField<T> implements IUnsafeField<T> {

    private final Field field;
    private final long offset;
    private final UnsafeFieldAccess access;

    @SuppressWarnings("restriction")
    public UnsafeField(final Field field) {
        this.field = field;
        this.offset = UnsafeAccess.UNSAFE.objectFieldOffset(field);
        this.access = UnsafeFieldAccess.valueOf(field);
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public T get(final Object obj) {
        return access.get(obj, offset);
    }

    @Override
    public void put(final Object obj, final T value) {
        access.put(obj, offset, value);
    }

}
