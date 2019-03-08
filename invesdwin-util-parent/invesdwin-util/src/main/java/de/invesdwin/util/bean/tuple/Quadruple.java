package de.invesdwin.util.bean.tuple;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;

@SuppressWarnings("serial")
@Immutable
public class Quadruple<FIRST, SECOND, THIRD, FOURTH> extends Triple<FIRST, SECOND, THIRD> {

    private final FOURTH fourth;

    protected Quadruple(final FIRST first, final SECOND second, final THIRD third, final FOURTH fourth) {
        super(first, second, third);
        this.fourth = fourth;
    }

    public FOURTH getFourth() {
        return fourth;
    }

    public static <FIRST, SECOND, THIRD, FOURTH> Quadruple<FIRST, SECOND, THIRD, FOURTH> of(final FIRST first,
            final SECOND second, final THIRD third, final FOURTH fourth) {
        return new Quadruple<FIRST, SECOND, THIRD, FOURTH>(first, second, third, fourth);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getClass(), getFirst(), getSecond(), getThird(), getFourth());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Quadruple) {
            final Quadruple<?, ?, ?, ?> castObj = (Quadruple<?, ?, ?, ?>) obj;
            return Objects.equals(getFirst(), castObj.getFirst()) && Objects.equals(getSecond(), castObj.getSecond())
                    && Objects.equals(getThird(), castObj.getThird())
                    && Objects.equals(getFourth(), castObj.getFourth());
        } else {
            return false;
        }
    }

}
