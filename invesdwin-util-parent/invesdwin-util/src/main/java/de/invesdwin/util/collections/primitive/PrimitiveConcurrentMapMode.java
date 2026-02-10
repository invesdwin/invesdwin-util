package de.invesdwin.util.collections.primitive;

import javax.annotation.concurrent.Immutable;

/**
 * TODO: could implement an alternative that uses ASpinWait for a slightly less cpu intensive alternative
 */
@Immutable
public enum PrimitiveConcurrentMapMode {
    BUSY_WAITING,
    BLOCKING;

    public static final PrimitiveConcurrentMapMode DEFAULT = PrimitiveConcurrentMapMode.BLOCKING;
}