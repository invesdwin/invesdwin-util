package de.invesdwin.util.lang.reflection.field;

import java.lang.reflect.Field;

import de.invesdwin.util.lang.reflection.Reflections;

public interface IUnsafeField<T> {

    Field getField();

    T get(Object obj);

    void put(Object obj, T value);

    static <T> IUnsafeField<T> valueOf(final Field field) {
        if (Reflections.isStatic(field)) {
            return new UnsafeStaticField<>(field);
        } else {
            return new UnsafeField<>(field);
        }
    }

}
