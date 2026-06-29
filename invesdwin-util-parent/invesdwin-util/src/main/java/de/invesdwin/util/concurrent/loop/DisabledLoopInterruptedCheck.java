package de.invesdwin.util.concurrent.loop;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class DisabledLoopInterruptedCheck implements ILoopInterruptedCheck {

    public static final DisabledLoopInterruptedCheck INSTANCE = new DisabledLoopInterruptedCheck();

    private DisabledLoopInterruptedCheck() {}

    @Override
    public void resetInterval() {
        //noop
    }

    @Override
    public boolean check() throws InterruptedException {
        return true;
    }

    @Override
    public boolean checkClock() throws InterruptedException {
        return true;
    }

}
