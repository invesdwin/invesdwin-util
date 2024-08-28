package de.invesdwin.util.log.slf4j;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.log.ILog;
import de.invesdwin.util.log.ILogFactory;

@Immutable
public final class XLoggerDelegateLogFactory implements ILogFactory {

    public static final XLoggerDelegateLogFactory INSTANCE = new XLoggerDelegateLogFactory();

    private XLoggerDelegateLogFactory() {}

    @Override
    public ILog getLog(final String name) {
        return new XLoggerDelegateLog(org.slf4j.ext.XLoggerFactory.getXLogger(name));
    }

}
