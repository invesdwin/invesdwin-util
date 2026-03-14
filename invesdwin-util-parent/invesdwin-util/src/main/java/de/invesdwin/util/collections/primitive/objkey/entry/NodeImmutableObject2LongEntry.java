package de.invesdwin.util.collections.primitive.objkey.entry;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator.INode;

@NotThreadSafe
public class NodeImmutableObject2LongEntry<K> extends ImmutableObject2LongEntry<K>
        implements INode<NodeImmutableObject2LongEntry<K>> {

    private NodeImmutableObject2LongEntry<K> next;
    private NodeImmutableObject2LongEntry<K> prev;

    protected NodeImmutableObject2LongEntry(final K key, final long value) {
        super(key, value);
    }

    public static <K> NodeImmutableObject2LongEntry<K> of(final K key, final long value) {
        return new NodeImmutableObject2LongEntry<K>(key, value);
    }

    @Override
    public NodeImmutableObject2LongEntry<K> getNext() {
        return next;
    }

    @Override
    public void setNext(final NodeImmutableObject2LongEntry<K> next) {
        this.next = next;
    }

    @Override
    public NodeImmutableObject2LongEntry<K> getPrev() {
        return prev;
    }

    @Override
    public void setPrev(final NodeImmutableObject2LongEntry<K> prev) {
        this.prev = prev;
    }

}
