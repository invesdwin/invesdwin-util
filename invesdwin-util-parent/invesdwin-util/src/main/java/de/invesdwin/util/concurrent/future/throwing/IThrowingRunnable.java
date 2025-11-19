package de.invesdwin.util.concurrent.future.throwing;

import de.invesdwin.util.error.Throwables;

public interface IThrowingRunnable {

    void run() throws Exception;

    default Runnable asRunnable() {
        return () -> {
            try {
                run();
            } catch (final Exception e) {
                throw Throwables.propagate(e);
            }
        };
    }

}
