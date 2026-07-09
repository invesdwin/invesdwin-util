package de.invesdwin.util.streams.log;

import javax.annotation.concurrent.ThreadSafe;

import org.zeroturnaround.exec.stream.LogOutputStream;

import de.invesdwin.util.log.ILog;
import de.invesdwin.util.log.ILogLevel;

@ThreadSafe
public class LogLevelOutputStream extends LogOutputStream {

    private final ILogLevel logLevel;
    private final ILog log;

    public LogLevelOutputStream(final ILogLevel logLevel, final ILog log) {
        this.logLevel = logLevel;
        this.log = log;
    }

    public ILog getLog() {
        return log;
    }

    @Override
    protected void processLine(final String line) {
        logLevel.log(log, line);
    }

}
