package de.invesdwin.util.lang.reflection.field;

import java.lang.reflect.Field;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.reflection.Reflections;

/**
 * Useful to access private field of private inner classes where methodhandles do not work. This also circumvents the
 * illegal reflection access warning that occurs for JVM internals.
 * 
 * This is also an alternative to VarHandle with MethodHandles.privateLookup in Java 8, since that feature was only
 * added in Java 9.
 */
@NotThreadSafe
public class UnsafeStaticField<T> implements IUnsafeField<T> {

    private final Field field;
    private final long offset;
    private final Object staticFieldBase;
    private final UnsafeFieldAccess access;

    @SuppressWarnings("restriction")
    public UnsafeStaticField(final Field field) {
        this.field = field;
        this.offset = Reflections.getUnsafe().staticFieldOffset(field);
        this.staticFieldBase = Reflections.getUnsafe().staticFieldBase(field);
        this.access = UnsafeFieldAccess.valueOf(field);
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public T get(final Object obj) {
        return access.get(staticFieldBase, offset);
    }

    @Override
    public void put(final Object obj, final T value) {
        access.put(staticFieldBase, offset, value);
    }

}
