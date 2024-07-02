package de.invesdwin.util.concurrent.reference.lazy;

import de.invesdwin.util.concurrent.reference.IMutableReference;

public interface ILazyReference<T> extends IMutableReference<T> {

    T getIfNotNull();

}
