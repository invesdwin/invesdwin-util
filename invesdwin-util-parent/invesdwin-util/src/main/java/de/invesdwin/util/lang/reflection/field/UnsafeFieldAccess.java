package de.invesdwin.util.lang.reflection.field;

import java.lang.reflect.Field;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.reflection.Reflections;

@Immutable
@SuppressWarnings({ "unchecked", "restriction" })
public enum UnsafeFieldAccess {
    BOOLEAN(boolean.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Boolean value = Reflections.getUnsafe().getBoolean(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            Reflections.getUnsafe().putBoolean(obj, offset, (Boolean) value);
        }
    },
    BYTE(byte.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Byte value = Reflections.getUnsafe().getByte(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            Reflections.getUnsafe().putByte(obj, offset, (Byte) value);
        }
    },
    CHAR(char.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Character value = Reflections.getUnsafe().getChar(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            Reflections.getUnsafe().putChar(obj, offset, (Character) value);
        }
    },
    SHORT(short.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Short value = Reflections.getUnsafe().getShort(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            Reflections.getUnsafe().putShort(obj, offset, (Short) value);
        }
    },
    INT(int.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Integer value = Reflections.getUnsafe().getInt(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            Reflections.getUnsafe().putInt(obj, offset, (Integer) value);
        }
    },
    LONG(long.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Long value = Reflections.getUnsafe().getLong(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            Reflections.getUnsafe().putLong(obj, offset, (Long) value);
        }
    },
    FLOAT(float.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Float value = Reflections.getUnsafe().getFloat(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            Reflections.getUnsafe().putFloat(obj, offset, (Float) value);
        }
    },
    DOUBLE(double.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Double value = Reflections.getUnsafe().getDouble(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            Reflections.getUnsafe().putDouble(obj, offset, (Double) value);
        }
    },
    OBJECT(Object.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Object value = Reflections.getUnsafe().getObject(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            Reflections.getUnsafe().putObject(obj, offset, value);
        }
    };

    private Class<?> type;

    UnsafeFieldAccess(final Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }

    public abstract <T> T get(Object obj, long offset);

    public abstract <T> void put(Object obj, long offset, T value);

    public static UnsafeFieldAccess valueOf(final Field field) {
        return valueOf(field.getType());
    }

    public static UnsafeFieldAccess valueOf(final Class<?> type) {
        if (type.isPrimitive()) {
            if (type == boolean.class) {
                return BOOLEAN;
            } else if (type == byte.class) {
                return BYTE;
            } else if (type == char.class) {
                return CHAR;
            } else if (type == short.class) {
                return SHORT;
            } else if (type == int.class) {
                return INT;
            } else if (type == long.class) {
                return LONG;
            } else if (type == float.class) {
                return FLOAT;
            } else if (type == double.class) {
                return DOUBLE;
            } else {
                throw UnknownArgumentException.newInstance(Class.class, type);
            }
        } else {
            return OBJECT;
        }
    }

}
