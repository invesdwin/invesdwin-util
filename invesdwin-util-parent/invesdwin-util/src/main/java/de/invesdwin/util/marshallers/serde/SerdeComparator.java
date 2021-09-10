package de.invesdwin.util.marshallers.serde;

import java.util.Comparator;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.streams.buffer.delegate.NettyDelegateByteBuffer;
import io.netty.buffer.ByteBuf;

@Immutable
public class SerdeComparator<O> implements Comparator<ByteBuf> {

    private final ISerde<O> serde;

    public SerdeComparator(final ISerde<O> serde) {
        this.serde = serde;
    }

    @Override
    public final int compare(final ByteBuf o1, final ByteBuf o2) {
        final boolean o1NullOrEmpty = o1 == null || o1.readableBytes() == 0;
        final boolean o2NullOrEmpty = o2 == null || o2.readableBytes() == 0;
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
                serde.fromBuffer(new NettyDelegateByteBuffer(o1), o1.readableBytes()));
        final Comparable<Object> co2 = toComparable(
                serde.fromBuffer(new NettyDelegateByteBuffer(o2), o2.readableBytes()));
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
