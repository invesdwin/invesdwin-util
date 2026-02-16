package de.invesdwin.util.collections.primitive.intkey.entry;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator.INode;

@NotThreadSafe
public class NodeImmutableInt2FloatEntry extends ImmutableInt2FloatEntry implements INode<NodeImmutableInt2FloatEntry> {

    private NodeImmutableInt2FloatEntry next;
    private NodeImmutableInt2FloatEntry prev;

    protected NodeImmutableInt2FloatEntry(final int key, final float value) {
        super(key, value);
    }

    public static NodeImmutableInt2FloatEntry of(final int key, final float value) {
        return new NodeImmutableInt2FloatEntry(key, value);
    }

    @Override
    public NodeImmutableInt2FloatEntry getNext() {
        return next;
    }

    @Override
    public void setNext(final NodeImmutableInt2FloatEntry next) {
        this.next = next;
    }

    @Override
    public NodeImmutableInt2FloatEntry getPrev() {
        return prev;
    }

    @Override
    public void setPrev(final NodeImmutableInt2FloatEntry prev) {
        this.prev = prev;
    }

}
