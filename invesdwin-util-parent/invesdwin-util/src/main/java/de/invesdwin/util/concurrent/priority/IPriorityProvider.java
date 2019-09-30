package de.invesdwin.util.concurrent.priority;

public interface IPriorityProvider {

    double MISSING_PRIORITY = Double.NaN;

    /**
     * Lower value has higher priority, not implementing this interfaces results in Double.NaN which means lowest
     * priority.
     */
    double getPriority();

}
