package de.invesdwin.util.collections.primitive.intkey.entry;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator.INode;

@NotThreadSafe
public class NodeImmutableInt2LongEntry extends ImmutableInt2LongEntry implements INode<NodeImmutableInt2LongEntry> {

    private NodeImmutableInt2LongEntry next;
    private NodeImmutableInt2LongEntry prev;

    protected NodeImmutableInt2LongEntry(final int key, final long value) {
        super(key, value);
    }

    public static NodeImmutableInt2LongEntry of(final int key, final long value) {
        return new NodeImmutableInt2LongEntry(key, value);
    }

    @Override
    public NodeImmutableInt2LongEntry getNext() {
        return next;
    }

    @Override
    public void setNext(final NodeImmutableInt2LongEntry next) {
        this.next = next;
    }

    @Override
    public NodeImmutableInt2LongEntry getPrev() {
        return prev;
    }

    @Override
    public void setPrev(final NodeImmutableInt2LongEntry prev) {
        this.prev = prev;
    }

}
