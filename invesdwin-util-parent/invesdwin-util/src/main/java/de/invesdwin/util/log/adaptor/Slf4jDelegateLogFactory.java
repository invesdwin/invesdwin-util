package de.invesdwin.util.log.adaptor;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.log.ILog;
import de.invesdwin.util.log.ILogFactory;

@Immutable
public final class Slf4jDelegateLogFactory implements ILogFactory {

    public static final Slf4jDelegateLogFactory INSTANCE = new Slf4jDelegateLogFactory();

    private Slf4jDelegateLogFactory() {}

    @Override
    public ILog getLog(final String name) {
        return new Slf4jDelegateLog(org.slf4j.ext.XLoggerFactory.getXLogger(name));
    }

}
