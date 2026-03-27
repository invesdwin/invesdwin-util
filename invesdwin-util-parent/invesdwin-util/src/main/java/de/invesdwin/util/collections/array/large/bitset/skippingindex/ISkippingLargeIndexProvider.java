package de.invesdwin.util.collections.array.large.bitset.skippingindex;

public interface ISkippingLargeIndexProvider {

    long END = Long.MAX_VALUE - 1;

    long next(long nextCandidate);

}
