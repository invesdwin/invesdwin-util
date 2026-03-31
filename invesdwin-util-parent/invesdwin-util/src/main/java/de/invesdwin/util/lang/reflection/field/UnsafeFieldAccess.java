package de.invesdwin.util.lang.reflection.field;

import java.lang.reflect.Field;

import javax.annotation.concurrent.Immutable;

import org.agrona.UnsafeApi;

import de.invesdwin.util.error.UnknownArgumentException;

@Immutable
@SuppressWarnings("unchecked")
public enum UnsafeFieldAccess {
    BOOLEAN(boolean.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Boolean value = UnsafeApi.getBoolean(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            UnsafeApi.putBoolean(obj, offset, (Boolean) value);
        }
    },
    BYTE(byte.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Byte value = UnsafeApi.getByte(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            UnsafeApi.putByte(obj, offset, (Byte) value);
        }
    },
    CHAR(char.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Character value = UnsafeApi.getChar(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            UnsafeApi.putChar(obj, offset, (Character) value);
        }
    },
    SHORT(short.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Short value = UnsafeApi.getShort(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            UnsafeApi.putShort(obj, offset, (Short) value);
        }
    },
    INT(int.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Integer value = UnsafeApi.getInt(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            UnsafeApi.putInt(obj, offset, (Integer) value);
        }
    },
    LONG(long.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Long value = UnsafeApi.getLong(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            UnsafeApi.putLong(obj, offset, (Long) value);
        }
    },
    FLOAT(float.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Float value = UnsafeApi.getFloat(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            UnsafeApi.putFloat(obj, offset, (Float) value);
        }
    },
    DOUBLE(double.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Double value = UnsafeApi.getDouble(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            UnsafeApi.putDouble(obj, offset, (Double) value);
        }
    },
    OBJECT(Object.class) {
        @Override
        public <T> T get(final Object obj, final long offset) {
            final Object value = UnsafeApi.getReference(obj, offset);
            return (T) value;
        }

        @Override
        public <T> void put(final Object obj, final long offset, final T value) {
            UnsafeApi.putReference(obj, offset, value);
        }
    };

    private final Class<?> type;

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
