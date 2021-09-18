package de.invesdwin.util.concurrent;

@FunctionalInterface
public interface IPendingCountListener {

    IPendingCountListener[] EMPTY_ARRAY = new IPendingCountListener[0];

    void onPendingCountChanged(int currentPendingCount);

}
