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

    private final String str;
    private final E comparable;

    public ComparableString(final String str, final E comparable) {
        this.str = str;
        this.comparable = comparable;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int compareTo(final Object o) {
        return comparable.compareTo(maybeUnwrapComparable(o));
    }

    @Override
    public int hashCode() {
        return comparable.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return comparable.equals(maybeUnwrapComparable(obj));
    }

    @Override
    public String toString() {
        return str;
    }

    public E getComparable() {
        return comparable;
    }

    public static Comparable maybeUnwrapComparable(final Object obj) {
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

}
