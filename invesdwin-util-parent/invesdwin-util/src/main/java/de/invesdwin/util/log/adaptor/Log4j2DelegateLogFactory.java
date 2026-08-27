package de.invesdwin.util.log.adaptor;

import javax.annotation.concurrent.Immutable;

import org.apache.logging.log4j.LogManager;

import de.invesdwin.util.log.ILog;
import de.invesdwin.util.log.ILogFactory;

@Immutable
public final class Log4j2DelegateLogFactory implements ILogFactory {

    public static final Log4j2DelegateLogFactory INSTANCE = new Log4j2DelegateLogFactory();

    private Log4j2DelegateLogFactory() {}

    @Override
    public ILog getLog(final String name) {
        return new Log4j2DelegateLog(LogManager.getLogger(name));
    }

}
