package de.invesdwin.util.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.nustaq.serialization.FSTConfiguration;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.norva.beanpath.BeanPathObjects;
import de.invesdwin.norva.beanpath.IDeepCloneProvider;
import de.invesdwin.util.lang.internal.AObjectsStaticFacade;
import de.invesdwin.util.math.Integers;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.lang.internal.AObjectsStaticFacade", targets = {
        BeanPathObjects.class })
public final class Objects extends AObjectsStaticFacade {

    public static final FSTConfiguration SERIALIZATION_CONFIG = FSTConfiguration.getDefaultConfiguration();
    public static final Set<String> REFLECTION_EXCLUDED_FIELDS = new HashSet<String>();
    public static final ADelegateComparator<Object> COMPARATOR = new ADelegateComparator<Object>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Object e) {
            return (Comparable<?>) e;
        }
    };

    static {
        //datanucleus enhancer fix
        REFLECTION_EXCLUDED_FIELDS.add("jdoDetachedState");
        REFLECTION_EXCLUDED_FIELDS.add("class");
        //use FST in BeanPathObjects as deepClone fallback instead of java serialization
        BeanPathObjects.setDeepCloneProvider(new IDeepCloneProvider() {
            @Override
            public <T> T deepClone(final T obj) {
                return Objects.deepClone(obj);
            }
        });
    }

    private Objects() {}

    public static <T> T defaultIfNull(final T object, final T defaultValue) {
        return org.apache.commons.lang3.ObjectUtils.defaultIfNull(object, defaultValue);
    }

    @SafeVarargs
    public static <T> T firstNonNull(final T... values) {
        return org.apache.commons.lang3.ObjectUtils.firstNonNull(values);
    }

    //CHECKSTYLE:OFF
    public static boolean equals(@Nullable final Object a, @Nullable final Object b) {
        //CHECKSTYLE:ON
        if (a != null && a.getClass().isArray() && b != null && b.getClass().isArray()) {
            if (a instanceof boolean[] && b instanceof boolean[]) {
                return Arrays.equals((boolean[]) a, (boolean[]) b);
            } else if (a instanceof byte[] && b instanceof byte[]) {
                return Arrays.equals((byte[]) a, (byte[]) b);
            } else if (a instanceof char[] && b instanceof char[]) {
                return Arrays.equals((char[]) a, (char[]) b);
            } else if (a instanceof double[] && b instanceof double[]) {
                return Arrays.equals((double[]) a, (double[]) b);
            } else if (a instanceof float[] && b instanceof float[]) {
                return Arrays.equals((float[]) a, (float[]) b);
            } else if (a instanceof int[] && b instanceof int[]) {
                return Arrays.equals((int[]) a, (int[]) b);
            } else if (a instanceof long[] && b instanceof long[]) {
                return Arrays.equals((long[]) a, (long[]) b);
            } else if (a instanceof short[] && b instanceof short[]) {
                return Arrays.equals((short[]) a, (short[]) b);
            } else if (a instanceof Object[] && b instanceof Object[]) {
                return Arrays.deepEquals((Object[]) a, (Object[]) b);
            }
        }
        return com.google.common.base.Objects.equal(a, b);
    }

    /**
     * Same as equals, just that an empty collection is equal to null.
     */
    public static boolean equalsProperty(final Object oldValue, final Object newValue) {
        if (!equals(oldValue, newValue)) {
            Iterable<?> iterable = null;
            Object other = null;
            if (oldValue instanceof Iterable) {
                iterable = (Iterable<?>) oldValue;
                other = newValue;
            } else if (newValue instanceof Iterable) {
                iterable = (Iterable<?>) newValue;
                other = oldValue;
            }
            if (iterable == null) {
                return false;
            } else {
                return !iterable.iterator().hasNext() && other == null;
            }
        } else {
            return true;
        }
    }

    public static int hashCode(final Object object) {
        return java.util.Objects.hashCode(object);
    }

    public static int hashCode(final Object o1, final Object o2) {
        //        final int prime = 31;
        //        int result = super.hashCode();
        //        result = prime * result + ((first == null) ? 0 : first.hashCode());
        //        result = prime * result + ((second == null) ? 0 : second.hashCode());
        //        return result;
        final int prime = 31;
        int result = 1;
        result = prime * result + hashCode(o1);
        result = prime * result + hashCode(o2);
        return result;
    }

    public static int hashCode(final Object o1, final Object o2, final Object o3) {
        final int prime = 31;
        int result = 1;
        result = prime * result + hashCode(o1);
        result = prime * result + hashCode(o2);
        result = prime * result + hashCode(o3);
        return result;
    }

    public static int hashCode(final Object o1, final Object o2, final Object o3, final Object o4) {
        final int prime = 31;
        int result = 1;
        result = prime * result + hashCode(o1);
        result = prime * result + hashCode(o2);
        result = prime * result + hashCode(o3);
        result = prime * result + hashCode(o4);
        return result;
    }

    public static int hashCode(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5) {
        final int prime = 31;
        int result = 1;
        result = prime * result + hashCode(o1);
        result = prime * result + hashCode(o2);
        result = prime * result + hashCode(o3);
        result = prime * result + hashCode(o4);
        result = prime * result + hashCode(o5);
        return result;
    }

    public static int hashCode(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5,
            final Object o6) {
        final int prime = 31;
        int result = 1;
        result = prime * result + hashCode(o1);
        result = prime * result + hashCode(o2);
        result = prime * result + hashCode(o3);
        result = prime * result + hashCode(o4);
        result = prime * result + hashCode(o5);
        result = prime * result + hashCode(o6);
        return result;
    }

    public static int hashCode(@Nullable final Object... objects) {
        return com.google.common.base.Objects.hashCode(objects);
    }

    @SuppressWarnings({ "unchecked", "null" })
    public static <T> T deepClone(final T obj) {
        if (obj == null) {
            return (T) null;
        }
        final byte[] serialized = serialize((Serializable) obj);
        return (T) deserialize(serialized);
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(final byte[] objectData) {
        try {
            return (T) SERIALIZATION_CONFIG.asObject(objectData);
        } finally {
            //TODO remove with next version of FST when resetForReuseArray also clears the callbacks
            //calls internally resetForReuse which clears the callbacks
            //https://github.com/RuedigerMoeller/fast-serialization/pull/200
            SERIALIZATION_CONFIG.getObjectInput();
        }
    }

    public static <T> T deserialize(final InputStream in) {
        //FST is unreliable regarding input streams
        try {
            return deserialize(IOUtils.toByteArray(in));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] serialize(final Serializable obj) {
        return SERIALIZATION_CONFIG.asByteArray(obj);
    }

    public static String toString(final Object obj) {
        return Strings.asStringReflective(obj);
    }

    public static String toStringMultiline(final Object obj) {
        return Strings.asStringReflectiveMultiline(obj);
    }

    public static String toStringIdentity(final Object obj) {
        return Strings.asStringIdentity(obj);
    }

    public static ToStringHelper toStringHelper(final Object obj) {
        return new ToStringHelper(obj, false);
    }

    public static ToStringHelper toStringHelperMultiline(final Object obj) {
        return new ToStringHelper(obj, true);
    }

    public static int reflectionHashCode(final Object obj) {
        return hashCode(obj.getClass(), HashCodeBuilder.reflectionHashCode(obj, REFLECTION_EXCLUDED_FIELDS));
    }

    public static boolean reflectionEquals(final Object thisObj, final Object obj) {
        return EqualsBuilder.reflectionEquals(thisObj, obj, REFLECTION_EXCLUDED_FIELDS);
    }

    public static int reflectionCompareTo(final Object thisObj, final Object obj) {
        return CompareToBuilder.reflectionCompare(thisObj, obj, REFLECTION_EXCLUDED_FIELDS);
    }

    public static boolean equalsAny(final Object thisObj, final Object... anyObjs) {
        for (final Object anyObj : anyObjs) {
            if (equals(thisObj, anyObj)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[][] fixInconsistentMatrixDimensions(final T[][] matrix, final T missingValue) {
        final int rows = matrix.length;
        int cols = 0;
        boolean colsInconsistent = false;
        for (int i = 0; i < rows; i++) {
            final T[] vector = matrix[i];
            if (cols != 0 && cols != vector.length) {
                colsInconsistent = true;
            }
            cols = Integers.max(cols, vector.length);
        }
        if (!colsInconsistent) {
            return matrix;
        }
        final T[][] fixedMatrix = (T[][]) Array.newInstance(matrix.getClass().getComponentType(), rows, cols);
        for (int i = 0; i < matrix.length; i++) {
            final T[] vector = matrix[i];
            final T[] fixedVector = fixedMatrix[i];
            System.arraycopy(vector, 0, fixedVector, 0, vector.length);
            if (missingValue != null) {
                for (int j = vector.length - 1; j < fixedVector.length; j++) {
                    fixedVector[j] = missingValue;
                }
            }
        }
        return fixedMatrix;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<List<T>> fixInconsistentMatrixDimensionsAsList(final List<? extends List<? extends T>> matrix,
            final T missingValue) {
        final int rows = matrix.size();
        int cols = 0;
        boolean colsInconsistent = false;
        for (int i = 0; i < rows; i++) {
            final List<? extends T> vector = matrix.get(i);
            if (cols != 0 && cols != vector.size()) {
                colsInconsistent = true;
            }
            cols = Integers.max(cols, vector.size());
        }
        if (!colsInconsistent) {
            return (List<List<T>>) matrix;
        }
        final List<List<T>> fixedMatrix = new ArrayList<List<T>>(rows);
        for (int i = 0; i < matrix.size(); i++) {
            final List<? extends T> vector = matrix.get(i);
            final List<T> fixedVector;
            if (vector.size() == cols) {
                fixedVector = new ArrayList<T>(vector);
            } else {
                fixedVector = new ArrayList<T>(cols);
                fixedVector.addAll(vector);
                for (int j = vector.size() - 1; j < cols; j++) {
                    fixedVector.add(missingValue);
                }
            }
            fixedMatrix.add(fixedVector);
        }
        return fixedMatrix;
    }

}
