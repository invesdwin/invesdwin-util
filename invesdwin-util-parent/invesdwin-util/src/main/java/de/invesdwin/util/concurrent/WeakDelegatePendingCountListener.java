package de.invesdwin.util.concurrent;

import java.lang.ref.WeakReference;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.fast.IFastIterableSet;

@Immutable
public class WeakDelegatePendingCountListener implements IPendingCountListener {

    private final IFastIterableSet<IPendingCountListener> listeners;
    private final WeakReference<IPendingCountListener> delegateHolder;

    public WeakDelegatePendingCountListener(final IPendingCountListener delegate,
            final IFastIterableSet<IPendingCountListener> listeners) {
        this.delegateHolder = new WeakReference<IPendingCountListener>(delegate);
        this.listeners = listeners;
    }

    @Override
    public void onPendingCountChanged(final int currentPendingCount) {
        final IPendingCountListener delegate = delegateHolder.get();
        if (delegate == null) {
            listeners.remove(this);
        } else {
            delegate.onPendingCountChanged(currentPendingCount);
        }
    }

}
