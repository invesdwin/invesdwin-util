package de.invesdwin.util.bean.tuple;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.lang.Objects;

/**
 * Understands a tuple of size 2.
 * 
 * @param <FIRST>
 *            the generic type of the 1st. value in this tuple.
 * @param <SECOND>
 *            the generic type of the 2nd. value in this tuple.
 * 
 * @author Yvonne Wang
 * @author Alex Ruiz
 */
@SuppressWarnings("serial")
@Immutable
public class Pair<FIRST, SECOND> extends AValueObject {

    /** The first value in this tuple. */
    private final FIRST first;

    /** The second value in this tuple. */
    private final SECOND second;

    private Integer cachedHashCode;

    /**
     * Creates a new </code>{@link Pair}</code>.
     * 
     * @param first
     *            the 1st. value in this tuple.
     * @param second
     *            the 2nd. value in this tuple.
     */
    protected Pair(final FIRST first, final SECOND second) {
        this.first = first;
        this.second = second;
    }

    public FIRST getFirst() {
        return first;
    }

    public SECOND getSecond() {
        return second;
    }

    public static <FIRST, SECOND> Pair<FIRST, SECOND> of(final FIRST first, final SECOND second) {
        return new Pair<FIRST, SECOND>(first, second);
    }

    @Override
    public final int hashCode() {
        if (cachedHashCode == null) {
            cachedHashCode = internalHashCode();
        }
        return cachedHashCode;
    }

    protected int internalHashCode() {
        return Objects.hashCode(getClass(), getFirst(), getSecond());
    }

    @Override
    public final boolean equals(final Object obj) {
        return internalEquals(obj);
    }

    protected boolean internalEquals(final Object obj) {
        if (obj instanceof Pair) {
            final Pair<?, ?> castObj = (Pair<?, ?>) obj;
            return Objects.equals(getFirst(), castObj.getFirst()) && Objects.equals(getSecond(), castObj.getSecond());
        } else {
            return false;
        }
    }

}
