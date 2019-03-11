package de.invesdwin.util.lang.finalizer.internal;

import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.lang.finalizer.IFinalizerReference;

public interface IFinalizerManagerProvider {

    String JAVA_CLEANER_CLASS = "java.lang.ref.Cleaner";

    IFinalizerReference register(Object obj, AFinalizer finalizer);

}
