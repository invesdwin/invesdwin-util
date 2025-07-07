package de.invesdwin.util.log.disabled;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.log.ILog;
import de.invesdwin.util.log.ILogFactory;

@Immutable
public final class DisabledLogFactory implements ILogFactory {

    public static final DisabledLogFactory INSTANCE = new DisabledLogFactory();

    private DisabledLogFactory() {}

    @Override
    public ILog getLog(final String name) {
        return DisabledLog.INSTANCE;
    }

}
