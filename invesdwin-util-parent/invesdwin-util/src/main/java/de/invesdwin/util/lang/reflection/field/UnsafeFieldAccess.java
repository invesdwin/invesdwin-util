package de.invesdwin.util.lang.reflection.field;

import java.lang.reflect.Field;

import javax.annotation.concurrent.Immutable;

import org.agrona.UnsafeAccess;

import de.invesdwin.util.error.UnknownArgumentException;

@Immutable
@SuppressWarnings({ "unchecked", "restriction" })
public enum UnsafeFieldAccess {
    BOOLEAN(boolean.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Boolean value = UnsafeAccess.UNSAFE.getBoolean(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            UnsafeAccess.UNSAFE.putBoolean(obj, offset, (boolean) value);
        }
    },
    BYTE(byte.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Byte value = UnsafeAccess.UNSAFE.getByte(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            UnsafeAccess.UNSAFE.putByte(obj, offset, (byte) value);
        }
    },
    CHAR(char.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Character value = UnsafeAccess.UNSAFE.getChar(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            UnsafeAccess.UNSAFE.putChar(obj, offset, (char) value);
        }
    },
    SHORT(short.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Short value = UnsafeAccess.UNSAFE.getShort(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            UnsafeAccess.UNSAFE.putShort(obj, offset, (short) value);
        }
    },
    INT(int.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Integer value = UnsafeAccess.UNSAFE.getInt(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            UnsafeAccess.UNSAFE.putInt(obj, offset, (int) value);
        }
    },
    LONG(long.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Long value = UnsafeAccess.UNSAFE.getLong(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            UnsafeAccess.UNSAFE.putLong(obj, offset, (long) value);
        }
    },
    FLOAT(float.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Float value = UnsafeAccess.UNSAFE.getFloat(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            UnsafeAccess.UNSAFE.putFloat(obj, offset, (float) value);
        }
    },
    DOUBLE(double.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Double value = UnsafeAccess.UNSAFE.getDouble(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            UnsafeAccess.UNSAFE.putDouble(obj, offset, (double) value);
        }
    },
    OBJECT(Object.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Object value = UnsafeAccess.UNSAFE.getObject(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            UnsafeAccess.UNSAFE.putObject(obj, offset, value);
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
