package de.invesdwin.util.collections.array.primitive.queue.circular;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.array.primitive.queue.IBytePrimitiveQueue;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.math.Bytes;

@NotThreadSafe
public class CircularByteBufferTest {

    static {
        Reflections.disableJavaModuleSystemRestrictions();
    }

    @Test
    public void testWriteThrough() {
        final byte[] input = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        final byte[] output = new byte[input.length];
        final CircularByteBuffer circular = new CircularByteBuffer(2);
        final int written1 = circular.writeThrough(input, 0, output, 0, input.length);
        Assertions.assertThat(written1).isEqualTo(input.length - circular.capacity());
        Assertions.assertThat(circular.size()).isEqualTo(circular.capacity());
        Assertions.assertThat(Bytes.subArray(output, 0, written1)).isEqualTo(Bytes.subArray(input, 0, written1));
        Assertions.assertThat(circular.asByteArrayCopy()).isEqualTo(Bytes.subArray(input, written1, input.length));

        final int written2 = circular.writeThrough(input, 0, output, written1, circular.capacity() * 2);
        Assertions.assertThat(written2).isEqualTo(circular.capacity());
        Assertions.assertThat(circular.size()).isEqualTo(circular.capacity());
        Assertions.assertThat(Bytes.subArray(output, 0, written2)).isEqualTo(Bytes.subArray(input, 0, written2));
        Assertions.assertThat(circular.asByteArrayCopy()).isEqualTo(Bytes.subArray(input, 0, circular.capacity()));
    }

    @Test
    public void testWriteThroughTargetOffset() {
        final byte[] input = new byte[] { 0, 1, 2, 3, 4, 5, 6 };
        final byte[] output = new byte[10];
        final CircularByteBuffer circular = new CircularByteBuffer(2);
        final int written = circular.writeThrough(input, 0, output, 3, 7);
        Assertions.assertThat(written).isEqualTo(5);
        Assertions.assertThat(circular.size()).isEqualTo(2);
        Assertions.assertThat(Bytes.subArray(output, 3, 8)).isEqualTo(Bytes.subArray(input, 0, 5));
        Assertions.assertThat(circular.asByteArrayCopy()).isEqualTo(Bytes.subArray(input, 5, 7));
    }

    @Test
    public void testPeekDoesNotConsume() {
        final CircularByteBuffer circular = new CircularByteBuffer(3);
        circular.write((byte) 1);
        circular.write((byte) 2);
        Assertions.assertThat(circular.peek()).isEqualTo((byte) 1);
        Assertions.assertThat(circular.peek()).isEqualTo((byte) 1);
        Assertions.assertThat(circular.size()).isEqualTo(2);
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
        final CircularByteBuffer circular = new CircularByteBuffer(2);
        Assertions.assertThat(circular.isEmpty()).isTrue();
        Assertions.assertThat(circular.peek()).isEqualTo(IBytePrimitiveQueue.MISSING_VALUE);
        Assertions.assertThat(circular.poll()).isEqualTo(IBytePrimitiveQueue.MISSING_VALUE);
        Assertions.assertThatThrownBy(circular::element).isInstanceOf(NoSuchElementException.class);
        Assertions.assertThatThrownBy(circular::remove).isInstanceOf(NoSuchElementException.class);
        //read and write keep their throwing buffer style contract
        Assertions.assertThatThrownBy(circular::read).isInstanceOf(IllegalStateException.class);
        Assertions.assertThat(circular.offer((byte) 1)).isTrue();
        Assertions.assertThat(circular.offer((byte) 2)).isTrue();
        Assertions.assertThat(circular.offer((byte) 3)).isFalse();
        Assertions.assertThatThrownBy(() -> circular.add((byte) 3)).isInstanceOf(IllegalStateException.class);
        Assertions.assertThat(circular.size()).isEqualTo(2);
        Assertions.assertThat(circular.isFull()).isTrue();
        Assertions.assertThat(circular.peek()).isEqualTo((byte) 1);
        Assertions.assertThat(circular.element()).isEqualTo((byte) 1);
        Assertions.assertThat(circular.poll()).isEqualTo((byte) 1);
        Assertions.assertThat(circular.remove()).isEqualTo((byte) 2);
        Assertions.assertThat(circular.isEmpty()).isTrue();
        //zero is a valid element, so only isEmpty() can distinguish it from a missing value
        circular.offer((byte) 0);
        Assertions.assertThat(circular.isEmpty()).isFalse();
        Assertions.assertThat(circular.peek()).isEqualTo(IBytePrimitiveQueue.MISSING_VALUE);
        Assertions.assertThat(circular.remove()).isEqualTo((byte) 0);
        circular.clear();
        Assertions.assertThat(circular.isEmpty()).isTrue();
    }

}
