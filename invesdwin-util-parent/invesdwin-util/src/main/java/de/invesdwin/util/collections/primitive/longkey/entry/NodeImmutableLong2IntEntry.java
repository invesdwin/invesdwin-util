package de.invesdwin.util.collections.primitive.longkey.entry;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator.INode;

@NotThreadSafe
public class NodeImmutableLong2IntEntry extends ImmutableLong2IntEntry implements INode<NodeImmutableLong2IntEntry> {

    private NodeImmutableLong2IntEntry next;
    private NodeImmutableLong2IntEntry prev;

    protected NodeImmutableLong2IntEntry(final long key, final int value) {
        super(key, value);
    }

    public static NodeImmutableLong2IntEntry of(final long key, final int value) {
        return new NodeImmutableLong2IntEntry(key, value);
    }

    @Override
    public NodeImmutableLong2IntEntry getNext() {
        return next;
    }

    @Override
    public void setNext(final NodeImmutableLong2IntEntry next) {
        this.next = next;
    }

    @Override
    public NodeImmutableLong2IntEntry getPrev() {
        return prev;
    }

    @Override
    public void setPrev(final NodeImmutableLong2IntEntry prev) {
        this.prev = prev;
    }

}
