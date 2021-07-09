package de.invesdwin.util.time.date;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class EmptyFDateProvider implements IFDateProvider {

    public static final EmptyFDateProvider INSTANCE = new EmptyFDateProvider();

    private EmptyFDateProvider() {
    }

    @Override
    public FDate asFDate() {
        return null;
    }

}
