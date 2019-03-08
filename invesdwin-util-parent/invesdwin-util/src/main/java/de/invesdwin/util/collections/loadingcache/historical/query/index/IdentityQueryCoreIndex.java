package de.invesdwin.util.collections.loadingcache.historical.query.index;

import javax.annotation.concurrent.Immutable;

@Immutable
public class IdentityQueryCoreIndex extends QueryCoreIndex {

    private final int queryCoreIdentityHashCode;

    public IdentityQueryCoreIndex(final int queryCoreIdentityHashCode, final QueryCoreIndex delegate) {
        super(delegate.getModCount(), delegate.getIndex());
        this.queryCoreIdentityHashCode = queryCoreIdentityHashCode;
    }

    public int getQueryCoreIdentityHashCode() {
        return queryCoreIdentityHashCode;
    }

}
