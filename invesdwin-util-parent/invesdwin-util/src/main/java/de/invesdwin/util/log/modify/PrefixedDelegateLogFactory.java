package de.invesdwin.util.log.modify;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.log.ILog;
import de.invesdwin.util.log.ILogFactory;

@Immutable
public class PrefixedDelegateLogFactory implements ILogFactory {

    private final ILogFactory delegate;
    private final String prefix;

    public PrefixedDelegateLogFactory(final ILogFactory delegate, final String prefix) {
        this.delegate = delegate;
        this.prefix = prefix;
    }

    @Override
    public ILog getLog(final String name) {
        return new PrefixedDelegateLog(delegate.getLog(name), prefix);
    }

}
