package de.invesdwin.util.collections.primitive.intkey.entry;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator.INode;

@NotThreadSafe
public class NodeImmutableInt2ObjectEntry<V> extends ImmutableInt2ObjectEntry<V>
        implements INode<NodeImmutableInt2ObjectEntry<V>> {

    private NodeImmutableInt2ObjectEntry<V> next;
    private NodeImmutableInt2ObjectEntry<V> prev;

    protected NodeImmutableInt2ObjectEntry(final int key, final V value) {
        super(key, value);
    }

    public static <V> NodeImmutableInt2ObjectEntry<V> of(final int key, final V value) {
        return new NodeImmutableInt2ObjectEntry<V>(key, value);
    }

    @Override
    public NodeImmutableInt2ObjectEntry<V> getNext() {
        return next;
    }

    @Override
    public void setNext(final NodeImmutableInt2ObjectEntry<V> next) {
        this.next = next;
    }

    @Override
    public NodeImmutableInt2ObjectEntry<V> getPrev() {
        return prev;
    }

    @Override
    public void setPrev(final NodeImmutableInt2ObjectEntry<V> prev) {
        this.prev = prev;
    }

}
