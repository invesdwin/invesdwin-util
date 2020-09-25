package de.invesdwin.util.collections.bitset;

public interface IBitSet {

    void add(int index);

    void remove(int index);

    boolean contains(int index);

    IBitSet optimize();

    IBitSet and(IBitSet... others);

    /**
     * WARNING: The resulting bitset will not be accurate outside of the given range due to performance reasons. The
     * resulting values might be randomg outside of the range depending on the implementation. Indexes are preserved.
     */
    IBitSet andRange(int fromInclusive, int toExclusive, IBitSet[] others);

    int getTrueCount();

    boolean isEmpty();

    ISkippingIndexProvider newSkippingIndexProvider();

    IBitSet unwrap();

}
