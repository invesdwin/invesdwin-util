package de.invesdwin.util.lang.finalizer.internal;

import de.invesdwin.util.lang.finalizer.IFinalizerReference;

@FunctionalInterface
public interface IFinalizerManagerProvider {

    String JAVA_CLEANER_CLASS = "java.lang.ref.Cleaner";

    IFinalizerReference register(Object obj, Runnable finalizer);

}
