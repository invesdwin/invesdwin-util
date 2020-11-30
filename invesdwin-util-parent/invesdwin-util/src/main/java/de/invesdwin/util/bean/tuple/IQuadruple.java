package de.invesdwin.util.bean.tuple;

public interface IQuadruple<FIRST, SECOND, THIRD, FOURTH> extends ITriple<FIRST, SECOND, THIRD> {

    FOURTH getFourth();

}
