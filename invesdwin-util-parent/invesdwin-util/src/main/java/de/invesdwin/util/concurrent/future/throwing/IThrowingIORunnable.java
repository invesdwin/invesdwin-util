package de.invesdwin.util.concurrent.future.throwing;

import java.io.IOException;

public interface IThrowingIORunnable extends IThrowingRunnable {

    @Override
    void run() throws IOException;

}
