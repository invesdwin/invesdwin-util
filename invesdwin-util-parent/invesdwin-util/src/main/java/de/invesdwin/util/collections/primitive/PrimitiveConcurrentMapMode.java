package de.invesdwin.util.collections.primitive;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum PrimitiveConcurrentMapMode {
    BUSY_WAITING,
    BLOCKING;

    public static final PrimitiveConcurrentMapMode DEFAULT = PrimitiveConcurrentMapMode.BLOCKING;
}