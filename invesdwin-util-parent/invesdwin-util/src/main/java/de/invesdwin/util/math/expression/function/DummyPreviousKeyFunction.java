package de.invesdwin.util.math.expression.function;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.fdate.IFDateProvider;

@Immutable
public final class DummyPreviousKeyFunction implements IPreviousKeyFunction {

    public static final DummyPreviousKeyFunction INSTANCE = new DummyPreviousKeyFunction();

    private DummyPreviousKeyFunction() {
    }

    @Override
    public IFDateProvider getPreviousKey(final IFDateProvider key, final int index) {
        return key;
    }

    @Override
    public int getPreviousKey(final int key, final int index) {
        return key;
    }

}
