package de.invesdwin.util.lang;

import java.io.Serializable;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * This can be used to display a specific String in a table, but let the auto sort work on an uderlying comparable
 * object.
 */
@NotThreadSafe
@SuppressWarnings("rawtypes")
public class ComparableString<E extends Comparable> implements Comparable<Object>, Serializable, CharSequence {

    protected final E comparable;
    protected final String str;

    public ComparableString(final E comparable) {
        this.comparable = comparable;
        this.str = Strings.asString(comparable);
    }

    public ComparableString(final E comparable, final String str) {
        this.comparable = comparable;
        this.str = Strings.asString(str);
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
        return str.length();
    }

    @Override
    public char charAt(final int index) {
        return str.charAt(index);
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
        return str.subSequence(start, end);
    }

    public static <T extends Comparable> ComparableString<T> valueOf(final T comparable, final String str) {
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

}
