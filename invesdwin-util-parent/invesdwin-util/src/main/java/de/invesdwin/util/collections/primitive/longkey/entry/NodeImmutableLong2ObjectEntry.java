package de.invesdwin.util.collections.primitive.longkey.entry;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator.INode;

@NotThreadSafe
public class NodeImmutableLong2ObjectEntry<V> extends ImmutableLong2ObjectEntry<V>
        implements INode<NodeImmutableLong2ObjectEntry<V>> {

    private NodeImmutableLong2ObjectEntry<V> next;
    private NodeImmutableLong2ObjectEntry<V> prev;

    protected NodeImmutableLong2ObjectEntry(final long key, final V value) {
        super(key, value);
    }

    public static <V> NodeImmutableLong2ObjectEntry<V> of(final long key, final V value) {
        return new NodeImmutableLong2ObjectEntry<V>(key, value);
    }

    @Override
    public NodeImmutableLong2ObjectEntry<V> getNext() {
        return next;
    }

    @Override
    public void setNext(final NodeImmutableLong2ObjectEntry<V> next) {
        this.next = next;
    }

    @Override
    public NodeImmutableLong2ObjectEntry<V> getPrev() {
        return prev;
    }

    @Override
    public void setPrev(final NodeImmutableLong2ObjectEntry<V> prev) {
        this.prev = prev;
    }

}
