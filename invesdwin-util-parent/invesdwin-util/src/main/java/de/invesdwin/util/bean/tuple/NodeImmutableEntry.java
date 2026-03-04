package de.invesdwin.util.bean.tuple;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator.INode;

@NotThreadSafe
public class NodeImmutableEntry<K, V> extends ImmutableEntry<K, V> implements INode<NodeImmutableEntry<K, V>> {

    private NodeImmutableEntry<K, V> next;
    private NodeImmutableEntry<K, V> prev;

    protected NodeImmutableEntry(final K key, final V value) {
        super(key, value);
    }

    public static <K, V> NodeImmutableEntry<K, V> of(final K key, final V value) {
        return new NodeImmutableEntry<K, V>(key, value);
    }

    @Override
    public NodeImmutableEntry<K, V> getNext() {
        return next;
    }

    @Override
    public void setNext(final NodeImmutableEntry<K, V> next) {
        this.next = next;
    }

    @Override
    public NodeImmutableEntry<K, V> getPrev() {
        return prev;
    }

    @Override
    public void setPrev(final NodeImmutableEntry<K, V> prev) {
        this.prev = prev;
    }

}
