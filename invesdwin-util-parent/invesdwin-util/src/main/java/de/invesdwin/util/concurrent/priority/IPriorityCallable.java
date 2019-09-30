package de.invesdwin.util.concurrent.priority;

import java.util.concurrent.Callable;

public interface IPriorityCallable<V> extends Callable<V>, IPriorityProvider {

}
