package de.invesdwin.util.collections.primitive.longkey.entry;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator.INode;

@NotThreadSafe
public class NodeImmutableLong2FloatEntry extends ImmutableLong2FloatEntry
        implements INode<NodeImmutableLong2FloatEntry> {

    private NodeImmutableLong2FloatEntry next;
    private NodeImmutableLong2FloatEntry prev;

    protected NodeImmutableLong2FloatEntry(final long key, final float value) {
        super(key, value);
    }

    public static NodeImmutableLong2FloatEntry of(final long key, final float value) {
        return new NodeImmutableLong2FloatEntry(key, value);
    }

    @Override
    public NodeImmutableLong2FloatEntry getNext() {
        return next;
    }

    @Override
    public void setNext(final NodeImmutableLong2FloatEntry next) {
        this.next = next;
    }

    @Override
    public NodeImmutableLong2FloatEntry getPrev() {
        return prev;
    }

    @Override
    public void setPrev(final NodeImmutableLong2FloatEntry prev) {
        this.prev = prev;
    }

}
