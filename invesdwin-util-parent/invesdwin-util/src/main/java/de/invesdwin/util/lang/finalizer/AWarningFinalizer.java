package de.invesdwin.util.lang.finalizer;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.Throwables;

@NotThreadSafe
public abstract class AWarningFinalizer extends AFinalizer {

    private Exception initStackTrace;

    public AWarningFinalizer() {
        if (Throwables.isDebugStackTraceEnabled()) {
            initStackTrace = new Exception();
            initStackTrace.fillInStackTrace();
        } else {
            initStackTrace = null;
        }
    }

    @Override
    protected void onFinalize() {
        super.onFinalize();
        String warning = "Finalizing unclosed " + newTypeInfo();
        final Exception stackTrace = initStackTrace;
        if (stackTrace != null) {
            warning += " from stacktrace:\n" + Throwables.getFullStackTrace(stackTrace);
        }
        org.slf4j.ext.XLoggerFactory.getXLogger(getClass()).warn(warning);
    }

    @Override
    protected void onClean() {
        super.onClean();
        initStackTrace = null;
    }

    protected String newTypeInfo() {
        return getClass().getSimpleName();
    }

}
