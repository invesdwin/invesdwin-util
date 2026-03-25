package de.invesdwin.util.collections.array.primitive.bitset.skippingindex;

public interface ISkippingPrimitiveIndexProvider {

    int END = Integer.MAX_VALUE - 1;

    int next(int nextCandidate);

}
