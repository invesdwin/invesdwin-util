package de.invesdwin.util.time.date.clock;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.time.date.IFDateData;

@NotThreadSafe
public class MutableFDate implements IFDateData, IFDateUpdater {

    private long millis;
    private int picos;

    public MutableFDate() {}

    public MutableFDate(final IFDateData copyOf) {
        this.millis = copyOf.millisValue();
        this.picos = copyOf.picosValue();
    }

    @Override
    public long millisValue() {
        return millis;
    }

    @Override
    public int picosValue() {
        return picos;
    }

    @Override
    public void update(final long millis, final int picos) {
        this.millis = millis;
        this.picos = picos;
    }

}
