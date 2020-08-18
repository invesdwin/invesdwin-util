package de.invesdwin.util.collections.bitset;

public interface IBitSet {

    void add(int index);

    void remove(int index);

    boolean contains(int index);

    void optimize();

}
