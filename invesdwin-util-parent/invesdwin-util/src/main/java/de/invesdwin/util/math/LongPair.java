package de.invesdwin.util.math;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.bean.tuple.IPair;
import de.invesdwin.util.lang.Objects;

@Immutable
public class LongPair extends AValueObject implements IPair<Long, Long> {

    private static final LongPair EMPTY = LongPair.of(Longs.DEFAULT_MISSING_VALUE, Longs.DEFAULT_MISSING_VALUE);

    /** The first value in this tuple. */
    private final long first;

    /** The second value in this tuple. */
    private final long second;

    /**
     * Creates a new </code>{@link LongPair}</code>.
     * 
     * @param first
     *            the 1st. value in this tuple.
     * @param second
     *            the 2nd. value in this tuple.
     */
    protected LongPair(final long first, final long second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public Long getFirst() {
        return first;
    }

    public long getFirstValue() {
        return first;
    }

    @Override
    public Long getSecond() {
        return second;
    }

    public long getSecondValue() {
        return second;
    }

    public static LongPair of(final long first, final long second) {
        return new LongPair(first, second);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(IPair.class, getFirst(), getSecond());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof LongPair) {
            final LongPair castObj = (LongPair) obj;
            return getFirstValue() == castObj.getFirstValue() && getSecondValue() == castObj.getSecondValue();
        } else {
            return false;
        }
    }

    public static LongPair empty() {
        return EMPTY;
    }

}
