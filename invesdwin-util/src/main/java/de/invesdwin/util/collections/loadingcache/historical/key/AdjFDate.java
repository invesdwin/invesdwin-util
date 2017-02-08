package de.invesdwin.util.collections.loadingcache.historical.key;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class AdjFDate extends FDate {

    public AdjFDate(final FDate key) {
        super(key);
    }

}
