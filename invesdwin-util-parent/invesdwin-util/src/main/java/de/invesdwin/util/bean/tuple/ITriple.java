package de.invesdwin.util.bean.tuple;

public interface ITriple<FIRST, SECOND, THIRD> extends IPair<FIRST, SECOND> {

    THIRD getThird();

}
