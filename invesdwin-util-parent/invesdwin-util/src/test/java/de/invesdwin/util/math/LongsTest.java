package de.invesdwin.util.math;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class LongsTest {

    @Test
    public void testIsPowerOfTwo() {
        Assertions.assertThat(Longs.isPowerOfTwo(0L)).isFalse();
        Assertions.assertThat(Longs.isPowerOfTwo(-1L)).isFalse();
        Assertions.assertThat(Longs.isPowerOfTwo(Long.MIN_VALUE)).isFalse();
        Assertions.assertThat(Longs.isPowerOfTwo(1L)).isTrue();
        Assertions.assertThat(Longs.isPowerOfTwo(2L)).isTrue();
        Assertions.assertThat(Longs.isPowerOfTwo(6L)).isFalse();
        Assertions.assertThat(Longs.isPowerOfTwo(1L << 30)).isTrue();
        //pow2 beyond the int range are legitimate sizes (long-indexed geometry):
        Assertions.assertThat(Longs.isPowerOfTwo(1L << 31)).isTrue();
        Assertions.assertThat(Longs.isPowerOfTwo(1L << 62)).isTrue();
        Assertions.assertThat(Longs.isPowerOfTwo(Long.MAX_VALUE)).isFalse();
    }

    @Test
    public void testCeilPow2() {
        Assertions.assertThat(Longs.ceilPow2(Long.MIN_VALUE)).isEqualTo(1L);
        Assertions.assertThat(Longs.ceilPow2(0L)).isEqualTo(1L);
        Assertions.assertThat(Longs.ceilPow2(1L)).isEqualTo(1L);
        Assertions.assertThat(Longs.ceilPow2(2L)).isEqualTo(2L);
        Assertions.assertThat(Longs.ceilPow2(6L)).isEqualTo(8L);
        Assertions.assertThat(Longs.ceilPow2(7L)).isEqualTo(8L);
        Assertions.assertThat(Longs.ceilPow2(8L)).isEqualTo(8L);
        Assertions.assertThat(Longs.ceilPow2((1L << 30) + 1)).isEqualTo(1L << 31);
        Assertions.assertThat(Longs.ceilPow2(1L << 62)).isEqualTo(1L << 62);
    }

    @Test
    public void testCeilPow2Saturates() {
        //no power of two above 2^62 fits a long: saturate, never wrap
        Assertions.assertThat(Longs.ceilPow2((1L << 62) + 1)).isEqualTo(Long.MAX_VALUE);
        Assertions.assertThat(Longs.ceilPow2(Long.MAX_VALUE)).isEqualTo(Long.MAX_VALUE);
        Assertions.assertThat(Longs.isPowerOfTwo(Longs.ceilPow2((1L << 62) + 1))).isFalse();
    }

}
