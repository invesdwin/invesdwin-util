package de.invesdwin.util.lang.internal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.builder.ToStringSummary;

import de.invesdwin.util.lang.reflection.field.IUnsafeField;

@NotThreadSafe
public class ExtendedReflectionToStringBuilder extends ReflectionToStringBuilder {

    private static final Map<Class<?>, IUnsafeField<Object>[]> FIELDS_CACHE = new ConcurrentHashMap<Class<?>, IUnsafeField<Object>[]>();

    public ExtendedReflectionToStringBuilder(final Object object) {
        super(object);
    }

    public ExtendedReflectionToStringBuilder(final Object object, final ToStringStyle style) {
        super(object, style);
    }

    public ExtendedReflectionToStringBuilder(final Object object, final ToStringStyle style,
            final StringBuffer buffer) {
        super(object, style, buffer);
    }

    public <T> ExtendedReflectionToStringBuilder(final T object, final ToStringStyle style, final StringBuffer buffer,
            final Class<? super T> reflectUpToClass, final boolean outputTransients, final boolean outputStatics) {
        super(object, style, buffer, reflectUpToClass, outputTransients, outputStatics);
    }

    public <T> ExtendedReflectionToStringBuilder(final T object, final ToStringStyle style, final StringBuffer buffer,
            final Class<? super T> reflectUpToClass, final boolean outputTransients, final boolean outputStatics,
            final boolean excludeNullValues) {
        super(object, style, buffer, reflectUpToClass, outputTransients, outputStatics, excludeNullValues);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void appendFieldsIn(final Class<?> clazz) {
        if (clazz.isArray()) {
            this.reflectionAppendArray(this.getObject());
            return;
        }
        // The elements in the returned array are not sorted and are not in any particular order.
        final IUnsafeField<Object>[] fields = FIELDS_CACHE.computeIfAbsent(clazz, key -> {
            //keep order of declaration
            final Field[] declaredFields = clazz.getDeclaredFields();
            final List<IUnsafeField<Object>> unsafeFields = new ArrayList<>(declaredFields.length);
            for (int i = 0; i < declaredFields.length; i++) {
                final Field field = declaredFields[i];
                unsafeFields.add(IUnsafeField.valueOf(field));
            }
            return unsafeFields.toArray(new IUnsafeField[unsafeFields.size()]);
        });
        for (int i = 0; i < fields.length; i++) {
            final IUnsafeField<Object> unsafeField = fields[i];
            final Field field = unsafeField.getField();
            final String fieldName = field.getName();
            if (this.accept(field)) {
                // Warning: Field.get(Object) creates wrappers objects
                // for primitive types.
                final Object fieldValue = this.getValue(unsafeField);
                if (!isExcludeNullValues() || fieldValue != null) {
                    this.append(fieldName, fieldValue, !field.isAnnotationPresent(ToStringSummary.class));
                }
            }
        }
    }

    private Object getValue(final IUnsafeField<Object> unsafeField) {
        return unsafeField.get(getObject());
    }

    @Override
    protected Object getValue(final Field field) throws IllegalAccessException {
        throw new UnsupportedOperationException();
    }

    public static String toString(final Object object) {
        return toString(object, null, false, false, null);
    }

    public static String toString(final Object object, final ToStringStyle style) {
        return toString(object, style, false, false, null);
    }

    public static String toString(final Object object, final ToStringStyle style, final boolean outputTransients) {
        return toString(object, style, outputTransients, false, null);
    }

    public static String toString(final Object object, final ToStringStyle style, final boolean outputTransients,
            final boolean outputStatics) {
        return toString(object, style, outputTransients, outputStatics, null);
    }

    public static <T> String toString(final T object, final ToStringStyle style, final boolean outputTransients,
            final boolean outputStatics, final Class<? super T> reflectUpToClass) {
        return new ExtendedReflectionToStringBuilder(object, style, null, reflectUpToClass, outputTransients,
                outputStatics).toString();
    }

    public static <T> String toString(final T object, final ToStringStyle style, final boolean outputTransients,
            final boolean outputStatics, final boolean excludeNullValues, final Class<? super T> reflectUpToClass) {
        return new ExtendedReflectionToStringBuilder(object, style, null, reflectUpToClass, outputTransients,
                outputStatics, excludeNullValues).toString();
    }

    public static String toStringExclude(final Object object, final Collection<String> excludeFieldNames) {
        return toStringExclude(object, toNoNullStringArray(excludeFieldNames));
    }

    static String[] toNoNullStringArray(final Collection<String> collection) {
        if (collection == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return toNoNullStringArray(collection.toArray());
    }

    static String[] toNoNullStringArray(final Object[] array) {
        final List<String> list = new ArrayList<>(array.length);
        for (final Object e : array) {
            if (e != null) {
                list.add(e.toString());
            }
        }
        return list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public static String toStringExclude(final Object object, final String... excludeFieldNames) {
        return new ExtendedReflectionToStringBuilder(object).setExcludeFieldNames(excludeFieldNames).toString();
    }

}
