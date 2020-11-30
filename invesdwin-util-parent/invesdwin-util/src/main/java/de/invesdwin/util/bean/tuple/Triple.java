package de.invesdwin.util.bean.tuple;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;

/**
 * Understands a tuple of size 3.
 * 
 * @param <FIRST>
 *            the generic type of the 1st. value in this tuple.
 * @param <SECOND>
 *            the generic type of the 2nd. value in this tuple.
 * @param <THIRD>
 *            the generic type of the 3rd. value in this tuple.
 * 
 * @author Alex Ruiz
 */
@SuppressWarnings("serial")
@Immutable
public class Triple<FIRST, SECOND, THIRD> extends Pair<FIRST, SECOND> implements ITriple<FIRST, SECOND, THIRD> {

    /** The third value in this tuple. */
    private final THIRD third;

    /**
     * Creates a new </code>{@link Triple}</code>.
     * 
     * @param first
     *            the 1st. value in this tuple.
     * @param second
     *            the 2nd. value in this tuple.
     * @param third
     *            the 3rd. value in this tuple.
     */
    protected Triple(final FIRST first, final SECOND second, final THIRD third) {
        super(first, second);
        this.third = third;
    }

    @Override
    public THIRD getThird() {
        return third;
    }

    public static <FIRST, SECOND, THIRD> Triple<FIRST, SECOND, THIRD> of(final FIRST first, final SECOND second,
            final THIRD third) {
        return new Triple<FIRST, SECOND, THIRD>(first, second, third);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ITriple.class, getFirst(), getSecond(), getThird());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ITriple) {
            final ITriple<?, ?, ?> castObj = (ITriple<?, ?, ?>) obj;
            return Objects.equals(getFirst(), castObj.getFirst()) && Objects.equals(getSecond(), castObj.getSecond())
                    && Objects.equals(getThird(), castObj.getThird());
        } else {
            return false;
        }
    }

}