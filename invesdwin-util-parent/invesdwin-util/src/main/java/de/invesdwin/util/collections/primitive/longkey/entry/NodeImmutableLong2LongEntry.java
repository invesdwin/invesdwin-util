package de.invesdwin.util.collections.primitive.longkey.entry;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator.INode;

@NotThreadSafe
public class NodeImmutableLong2LongEntry extends ImmutableLong2LongEntry implements INode<NodeImmutableLong2LongEntry> {

    private NodeImmutableLong2LongEntry next;
    private NodeImmutableLong2LongEntry prev;

    protected NodeImmutableLong2LongEntry(final long key, final long value) {
        super(key, value);
    }

    public static NodeImmutableLong2LongEntry of(final long key, final long value) {
        return new NodeImmutableLong2LongEntry(key, value);
    }

    @Override
    public NodeImmutableLong2LongEntry getNext() {
        return next;
    }

    @Override
    public void setNext(final NodeImmutableLong2LongEntry next) {
        this.next = next;
    }

    @Override
    public NodeImmutableLong2LongEntry getPrev() {
        return prev;
    }

    @Override
    public void setPrev(final NodeImmutableLong2LongEntry prev) {
        this.prev = prev;
    }

}
