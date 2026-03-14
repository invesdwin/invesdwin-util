package de.invesdwin.util.collections.primitive.intkey.entry;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator.INode;

@NotThreadSafe
public class NodeImmutableInt2IntEntry extends ImmutableInt2IntEntry implements INode<NodeImmutableInt2IntEntry> {

    private NodeImmutableInt2IntEntry next;
    private NodeImmutableInt2IntEntry prev;

    protected NodeImmutableInt2IntEntry(final int key, final int value) {
        super(key, value);
    }

    public static NodeImmutableInt2IntEntry of(final int key, final int value) {
        return new NodeImmutableInt2IntEntry(key, value);
    }

    @Override
    public NodeImmutableInt2IntEntry getNext() {
        return next;
    }

    @Override
    public void setNext(final NodeImmutableInt2IntEntry next) {
        this.next = next;
    }

    @Override
    public NodeImmutableInt2IntEntry getPrev() {
        return prev;
    }

    @Override
    public void setPrev(final NodeImmutableInt2IntEntry prev) {
        this.prev = prev;
    }

}
