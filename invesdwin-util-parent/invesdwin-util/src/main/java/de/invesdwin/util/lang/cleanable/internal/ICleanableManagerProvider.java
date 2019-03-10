package de.invesdwin.util.lang.cleanable.internal;

import de.invesdwin.util.lang.cleanable.ACleanableAction;
import de.invesdwin.util.lang.cleanable.ICleanableReference;

public interface ICleanableManagerProvider {

    String JAVA_CLEANER_CLASS = "java.lang.ref.Cleaner";

    ICleanableReference register(Object obj, ACleanableAction cleanableAction);

}
