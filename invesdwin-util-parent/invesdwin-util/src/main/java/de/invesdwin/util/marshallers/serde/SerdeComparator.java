package de.invesdwin.util.marshallers.serde;

import java.util.Comparator;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.streams.buffer.delegate.JavaDelegateByteBuffer;

@Immutable
public class SerdeComparator<O> implements Comparator<java.nio.ByteBuffer> {

    private final ISerde<O> serde;

    public SerdeComparator(final ISerde<O> serde) {
        this.serde = serde;
    }

    @Override
    public final int compare(final java.nio.ByteBuffer o1, final java.nio.ByteBuffer o2) {
        final boolean o1NullOrEmpty = o1 == null || o1.remaining() == 0;
        final boolean o2NullOrEmpty = o2 == null || o2.remaining() == 0;
        if (o1NullOrEmpty && o2NullOrEmpty) {
            return 0;
        }
        if (o1NullOrEmpty) {
            return -1;
        }
        if (o2NullOrEmpty) {
            // fix buffer underflow
            return 1;
        }
        final Comparable<Object> co1 = toComparable(
                serde.fromBuffer(new JavaDelegateByteBuffer(o1).newSliceFrom(o1.position()), o1.remaining()));
        final Comparable<Object> co2 = toComparable(
                serde.fromBuffer(new JavaDelegateByteBuffer(o2).newSliceFrom(o2.position()), o2.remaining()));
        return innerCompare(co1, co2);
    }

    /**
     * Override this to customize the comparation itself. E.g. inversing it.
     */
    protected int innerCompare(final Comparable<Object> co1, final Comparable<Object> co2) {
        return co1.compareTo(co2);
    }

    /**
     * Override this to customize the comparable object. E.g. getting an inner object.
     */
    @SuppressWarnings("unchecked")
    protected Comparable<Object> toComparable(final Object fromBytes) {
        return (Comparable<Object>) fromBytes;
    }

}
