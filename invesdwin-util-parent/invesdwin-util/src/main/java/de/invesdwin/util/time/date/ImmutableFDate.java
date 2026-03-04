package de.invesdwin.util.time.date;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class ImmutableFDate extends FDate {

    private ImmutableFDate(final FDate time) {
        super(time);
    }

    private ImmutableFDate(final long time) {
        super(time);
    }

    @Deprecated
    @Override
    public Object getExtension() {
        return null;
    }

    @Deprecated
    @Override
    public void setExtension(final Object extension) {
        //noop
    }

    public static ImmutableFDate valueOf(final FDate time) {
        if (time == null) {
            return null;
        }
        if (time instanceof ImmutableFDate) {
            return (ImmutableFDate) time;
        }
        return new ImmutableFDate(time);
    }

    public static ImmutableFDate valueOf(final long time) {
        return new ImmutableFDate(time);
    }

}
