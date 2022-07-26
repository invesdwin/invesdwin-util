package de.invesdwin.util.streams.buffer.bytes.circular;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.Bytes;

@NotThreadSafe
public class CircularByteBufferTest {

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

}
