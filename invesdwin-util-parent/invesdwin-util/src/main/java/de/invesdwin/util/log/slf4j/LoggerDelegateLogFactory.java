package de.invesdwin.util.log.slf4j;

import javax.annotation.concurrent.Immutable;

import org.apache.logging.log4j.LogManager;

import de.invesdwin.util.log.ILog;
import de.invesdwin.util.log.ILogFactory;

@Immutable
public final class LoggerDelegateLogFactory implements ILogFactory {

    public static final LoggerDelegateLogFactory INSTANCE = new LoggerDelegateLogFactory();

    private LoggerDelegateLogFactory() {}

    @Override
    public ILog getLog(final String name) {
        return new LoggerDelegateLog(LogManager.getLogger(name));
    }

}
