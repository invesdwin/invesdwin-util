package de.invesdwin.util.log.modify;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.log.ILog;
import de.invesdwin.util.log.ILogFactory;

@Immutable
public class FormattedDelegateLogFactory implements ILogFactory {

    private final ILogFactory delegate;

    public FormattedDelegateLogFactory(final ILogFactory delegate) {
        this.delegate = delegate;
    }

    @Override
    public ILog getLog(final String name) {
        return new FormattedDelegateLog(delegate.getLog(name));
    }

}
