package de.invesdwin.util.collections.loadingcache.historical.listener;

import javax.annotation.concurrent.Immutable;

@Immutable
public class HistoricalCacheIncreaseMaximumSizeListenerSupport implements IHistoricalCacheIncreaseMaximumSizeListener {

    @Override
    public void increaseMaximumSize(final int maximumSize, final String reason) {}

}
