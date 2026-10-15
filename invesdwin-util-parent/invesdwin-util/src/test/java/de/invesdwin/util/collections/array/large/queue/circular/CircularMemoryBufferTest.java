package de.invesdwin.util.collections.array.large.queue.circular;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.array.large.queue.IByteLargeQueue;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.MemoryBuffers;

@NotThreadSafe
public class CircularMemoryBufferTest {

    static {
        Reflections.disableJavaModuleSystemRestrictions();
    }

    private static final byte[] INPUT = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    @Test
    public void testWriteThrough() {
        final byte[] output = new byte[INPUT.length];
        final CircularMemoryBuffer circular = new CircularMemoryBuffer(2);
        final int written1 = circular.writeThrough(INPUT, 0, output, 0, INPUT.length);
        Assertions.assertThat(written1).isEqualTo(INPUT.length - (int) circular.capacity());
        Assertions.assertThat(circular.size()).isEqualTo(circular.capacity());
        Assertions.assertThat(Bytes.subArray(output, 0, written1)).isEqualTo(Bytes.subArray(INPUT, 0, written1));
        Assertions.assertThat(asBytes(circular)).isEqualTo(Bytes.subArray(INPUT, written1, INPUT.length));

        final int written2 = circular.writeThrough(INPUT, 0, output, written1, (int) circular.capacity() * 2);
        Assertions.assertThat(written2).isEqualTo((int) circular.capacity());
        Assertions.assertThat(circular.size()).isEqualTo(circular.capacity());
        Assertions.assertThat(Bytes.subArray(output, 0, written2)).isEqualTo(Bytes.subArray(INPUT, 0, written2));
        Assertions.assertThat(asBytes(circular))
                .isEqualTo(Bytes.subArray(INPUT, 0, (int) circular.capacity()));
    }

    @Test
    public void testWriteThroughTargetOffset() {
        final byte[] output = new byte[10];
        final CircularMemoryBuffer circular = new CircularMemoryBuffer(2);
        final int written = circular.writeThrough(INPUT, 0, output, 3, 7);
        Assertions.assertThat(written).isEqualTo(5);
        Assertions.assertThat(circular.size()).isEqualTo(2L);
        Assertions.assertThat(Bytes.subArray(output, 3, 8)).isEqualTo(Bytes.subArray(INPUT, 0, 5));
        Assertions.assertThat(asBytes(circular)).isEqualTo(Bytes.subArray(INPUT, 5, 7));
    }

    @Test
    public void testPeekDoesNotConsume() {
        final CircularMemoryBuffer circular = new CircularMemoryBuffer(3);
        circular.write((byte) 1);
        circular.write((byte) 2);
        Assertions.assertThat(circular.peek()).isEqualTo((byte) 1);
        Assertions.assertThat(circular.peek()).isEqualTo((byte) 1);
        Assertions.assertThat(circular.size()).isEqualTo(2L);
        Assertions.assertThat(circular.read()).isEqualTo((byte) 1);
        Assertions.assertThat(circular.read()).isEqualTo((byte) 2);
        Assertions.assertThat(circular.isEmpty()).isTrue();
        //after being drained, writing and reading should still work on the same positions
        circular.write((byte) 3);
        Assertions.assertThat(circular.peek()).isEqualTo((byte) 3);
        Assertions.assertThat(circular.read()).isEqualTo((byte) 3);
    }

    @Test
    public void testQueueContract() {
        final CircularMemoryBuffer circular = new CircularMemoryBuffer(2);
        Assertions.assertThat(circular.isEmpty()).isTrue();
        Assertions.assertThat(circular.peek()).isEqualTo(IByteLargeQueue.MISSING_VALUE);
        Assertions.assertThat(circular.poll()).isEqualTo(IByteLargeQueue.MISSING_VALUE);
        Assertions.assertThatThrownBy(circular::element).isInstanceOf(NoSuchElementException.class);
        Assertions.assertThatThrownBy(circular::remove).isInstanceOf(NoSuchElementException.class);
        //read and write keep their throwing buffer style contract
        Assertions.assertThatThrownBy(circular::read).isInstanceOf(IllegalStateException.class);
        Assertions.assertThat(circular.offer((byte) 1)).isTrue();
        Assertions.assertThat(circular.offer((byte) 2)).isTrue();
        Assertions.assertThat(circular.offer((byte) 3)).isFalse();
        Assertions.assertThatThrownBy(() -> circular.add((byte) 3)).isInstanceOf(IllegalStateException.class);
        Assertions.assertThat(circular.size()).isEqualTo(2L);
        Assertions.assertThat(circular.isFull()).isTrue();
        Assertions.assertThat(circular.peek()).isEqualTo((byte) 1);
        Assertions.assertThat(circular.element()).isEqualTo((byte) 1);
        Assertions.assertThat(circular.poll()).isEqualTo((byte) 1);
        Assertions.assertThat(circular.remove()).isEqualTo((byte) 2);
        Assertions.assertThat(circular.isEmpty()).isTrue();
        //zero is a valid element, so only isEmpty() can distinguish it from a missing value
        circular.offer((byte) 0);
        Assertions.assertThat(circular.isEmpty()).isFalse();
        Assertions.assertThat(circular.peek()).isEqualTo(IByteLargeQueue.MISSING_VALUE);
        Assertions.assertThat(circular.remove()).isEqualTo((byte) 0);
        circular.clear();
        Assertions.assertThat(circular.isEmpty()).isTrue();
    }

    @Test
    public void testMemoryBufferIo() {
        final CircularMemoryBuffer circular = new CircularMemoryBuffer(8);
        final IMemoryBuffer source = MemoryBuffers.allocate(INPUT.length);
        source.putBytes(0, INPUT);
        circular.write(source, 2, 5);
        Assertions.assertThat(circular.size()).isEqualTo(5L);
        Assertions.assertThat(asBytes(circular)).isEqualTo(Bytes.subArray(INPUT, 2, 7));

        final IMemoryBuffer target = MemoryBuffers.allocate(5);
        circular.peek(target, 0, 5);
        Assertions.assertThat(getBytes(target, 0, 5)).isEqualTo(Bytes.subArray(INPUT, 2, 7));
        Assertions.assertThat(circular.size()).isEqualTo(5L);

        circular.read(target, 0, 5);
        Assertions.assertThat(getBytes(target, 0, 5)).isEqualTo(Bytes.subArray(INPUT, 2, 7));
        Assertions.assertThat(circular.isEmpty()).isTrue();
    }

    @Test
    public void testWriteThroughMemoryBuffer() {
        final IMemoryBuffer source = MemoryBuffers.allocate(INPUT.length);
        source.putBytes(0, INPUT);
        final CircularMemoryBuffer circular = new CircularMemoryBuffer(4);

        final IMemoryBuffer target = MemoryBuffers.allocate(INPUT.length);
        final long written1 = circular.writeThrough(source, 0, target, 0, INPUT.length);
        Assertions.assertThat(written1).isEqualTo(INPUT.length - circular.capacity());
        Assertions.assertThat(getBytes(target, 0, written1)).isEqualTo(Bytes.subArray(INPUT, 0, (int) written1));
        Assertions.assertThat(circular.size()).isEqualTo(circular.capacity());
        Assertions.assertThat(asBytes(circular))
                .isEqualTo(Bytes.subArray(INPUT, (int) written1, INPUT.length));

        final IMemoryBuffer target2 = MemoryBuffers.allocate(14);
        final long written2 = circular.writeThrough(source, 0, target2, 6, 8);
        Assertions.assertThat(written2).isEqualTo(circular.capacity());
        Assertions.assertThat(getBytes(target2, 6, written2))
                .isEqualTo(Bytes.subArray(INPUT, (int) written1, INPUT.length));
        Assertions.assertThat(circular.size()).isEqualTo(circular.capacity());
        Assertions.assertThat(asBytes(circular))
                .isEqualTo(Bytes.subArray(INPUT, 0, (int) circular.capacity()));
    }

    /**
     * asByteArrayCopy is not available on the large variant, so the copy is requested with an explicit size.
     */
    private static byte[] asBytes(final CircularMemoryBuffer circular) {
        return getBytes(circular.asMemoryBufferCopy(), 0, circular.size());
    }

    private static byte[] getBytes(final IMemoryBuffer buffer, final long offset, final long length) {
        final byte[] bytes = new byte[(int) length];
        buffer.getBytes(offset, bytes);
        return bytes;
    }

}
