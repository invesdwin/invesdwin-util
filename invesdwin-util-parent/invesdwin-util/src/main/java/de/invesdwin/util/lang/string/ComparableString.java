package de.invesdwin.util.lang.string;

import javax.annotation.concurrent.NotThreadSafe;

import org.danekja.java.util.function.serializable.SerializableSupplier;

import de.invesdwin.norva.beanpath.annotation.BeanPathEndPoint;
import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.lang.Objects;

/**
 * This can be used to display a specific String in a table, but let the auto sort work on an uderlying comparable
 * object.
 */
@NotThreadSafe
@SuppressWarnings("rawtypes")
@BeanPathEndPoint
public class ComparableString<E extends Comparable>
        implements Comparable<Object>, ISerializableValueObject, CharSequence {

    protected final E comparable;
    protected final SerializableSupplier<Object> strSupplier;
    protected String str;

    public ComparableString(final E comparable) {
        this.comparable = comparable;
        this.strSupplier = () -> comparable;
    }

    public ComparableString(final E comparable, final SerializableSupplier<Object> strSupplier) {
        this.comparable = comparable;
        this.strSupplier = strSupplier;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int compareTo(final Object o) {
        final Comparable oComparable = maybeUnwrapComparable(o);
        if (comparable == null && oComparable == null) {
            return 0;
        } else if (comparable == null) {
            return -1;
        } else if (oComparable == null) {
            return 1;
        }
        if (comparable.getClass().isAssignableFrom(oComparable.getClass())) {
            return compareToTypeSafe((E) oComparable);
        } else {
            return compareToFallback(o);
        }
    }

    @SuppressWarnings("unchecked")
    protected int compareToTypeSafe(final E oComparable) {
        return comparable.compareTo(oComparable);
    }

    protected int compareToFallback(final Object o) {
        //fallback to string comparison
        final String oStr = Strings.asString(o);
        final String str = toString();
        if (str == null && oStr == null) {
            return 0;
        } else if (str == null) {
            return -1;
        } else if (oStr == null) {
            return 1;
        }
        //sort it below a real value (below a negative number for example)
        return -str.compareTo(oStr);
    }

    @Override
    public int hashCode() {
        return comparable.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return Objects.equals(comparable, maybeUnwrapComparable(obj));
    }

    @Override
    public String toString() {
        if (str == null) {
            str = Strings.asString(strSupplier.get());
        }
        return str;
    }

    public E getComparable() {
        return comparable;
    }

    private static Comparable maybeUnwrapComparable(final Object obj) {
        if (obj instanceof ComparableString) {
            final ComparableString cObj = (ComparableString) obj;
            return cObj.comparable;
        } else {
            return (Comparable) obj;
        }
    }

    @Override
    public int length() {
        return toString().length();
    }

    @Override
    public char charAt(final int index) {
        return toString().charAt(index);
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
        return toString().subSequence(start, end);
    }

    public static <T extends Comparable> ComparableString<T> valueOf(final T comparable,
            final SerializableSupplier<Object> str) {
        if (comparable == null && str == null) {
            return null;
        } else {
            return new ComparableString<>(comparable, str);
        }
    }

    public static <T extends Comparable> ComparableString<T> valueOf(final T comparable) {
        if (comparable == null) {
            return null;
        } else {
            return new ComparableString<>(comparable);
        }
    }

    public static <T extends Comparable> T extractComparable(final ComparableString<T> comparableString) {
        if (comparableString != null) {
            return comparableString.getComparable();
        } else {
            return null;
        }
    }

}
