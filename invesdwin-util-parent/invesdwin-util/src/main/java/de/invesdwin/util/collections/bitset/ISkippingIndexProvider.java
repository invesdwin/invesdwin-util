package de.invesdwin.util.collections.bitset;

public interface ISkippingIndexProvider {

    int END = Integer.MAX_VALUE - 1;

    int next(int cur);

}
