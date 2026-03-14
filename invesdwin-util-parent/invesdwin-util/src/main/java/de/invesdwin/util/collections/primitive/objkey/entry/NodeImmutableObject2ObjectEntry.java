package de.invesdwin.util.collections.primitive.objkey.entry;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator.INode;

@NotThreadSafe
public class NodeImmutableObject2ObjectEntry<K, V> extends ImmutableObject2ObjectEntry<K, V>
        implements INode<NodeImmutableObject2ObjectEntry<K, V>> {

    private NodeImmutableObject2ObjectEntry<K, V> next;
    private NodeImmutableObject2ObjectEntry<K, V> prev;

    protected NodeImmutableObject2ObjectEntry(final K key, final V value) {
        super(key, value);
    }

    public static <K, V> NodeImmutableObject2ObjectEntry<K, V> of(final K key, final V value) {
        return new NodeImmutableObject2ObjectEntry<K, V>(key, value);
    }

    @Override
    public NodeImmutableObject2ObjectEntry<K, V> getNext() {
        return next;
    }

    @Override
    public void setNext(final NodeImmutableObject2ObjectEntry<K, V> next) {
        this.next = next;
    }

    @Override
    public NodeImmutableObject2ObjectEntry<K, V> getPrev() {
        return prev;
    }

    @Override
    public void setPrev(final NodeImmutableObject2ObjectEntry<K, V> prev) {
        this.prev = prev;
    }

}
