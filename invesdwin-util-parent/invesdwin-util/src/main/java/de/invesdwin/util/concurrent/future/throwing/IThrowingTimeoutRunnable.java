package de.invesdwin.util.concurrent.future.throwing;

import java.util.concurrent.TimeoutException;

public interface IThrowingTimeoutRunnable extends IThrowingRunnable {

    @Override
    void run() throws TimeoutException;

}
