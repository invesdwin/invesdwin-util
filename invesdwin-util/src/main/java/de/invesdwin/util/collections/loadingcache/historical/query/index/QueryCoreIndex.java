package de.invesdwin.util.collections.loadingcache.historical.query.index;

import javax.annotation.concurrent.Immutable;

@Immutable
public class QueryCoreIndex {

    private final int modCount;
    private final int index;

    public QueryCoreIndex(final int modCount, final int index) {
        this.modCount = modCount;
        this.index = index;
    }

    public int getModCount() {
        return modCount;
    }

    public int getIndex() {
        return index;
    }

}
