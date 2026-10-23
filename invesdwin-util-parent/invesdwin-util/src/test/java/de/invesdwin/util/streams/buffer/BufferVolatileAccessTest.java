package de.invesdwin.util.streams.buffer;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.random.IRandomGenerator;
import de.invesdwin.util.math.random.PseudoRandomGenerators;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.ListByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.OrderedDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.SegmentedByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.SlicedDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.UnsafeByteBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.MemoryBuffers;
import de.invesdwin.util.streams.buffer.memory.delegate.ByteDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.ListMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.OrderedDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.SegmentedMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.SlicedDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.extend.UnsafeMemoryBuffer;

/**
 * Tests the volatile/acquire/release/ordered memory-order family of the buffer implementations: float/double volatile
 * raw-bits round trips including NaN payload preservation, int/long barrier round trips, the refusal of cross-part
 * volatile wide access in the composite buffers, and forwarding consistency of the delegation wrappers. Also tests the
 * little-endian Reverse twins of that family: the barrier-then-swap/swap-then-barrier contract, the trio round trips,
 * the cross-part refusal of the Reverse wide access, and Reverse forwarding through the byte-delegate and slice
 * wrappers.
 */
@NotThreadSafe
public class BufferVolatileAccessTest {

    private static final int PART_SIZE = 16;
    private static final float FLOAT_VALUE = -12.25f;
    private static final double DOUBLE_VALUE = 1.7976931348623157E308;
    private static final int NAN_FLOAT_BITS = 0x7fc00abc;
    private static final long NAN_DOUBLE_BITS = 0x7ff800000000abcdL;
    private static final int INT_VALUE = 0x11223344;
    private static final long LONG_VALUE = -0x7fffffffffffffffL;

    @Test
    public void testFloatDoubleVolatileRawBitsRoundTrip() {
        final UnsafeMemoryBuffer mem = new UnsafeMemoryBuffer(new byte[64]);
        final UnsafeByteBuffer bytes = new UnsafeByteBuffer(new byte[64]);

        mem.putFloatVolatile(8, FLOAT_VALUE);
        Assertions.assertThat(mem.getFloatVolatile(8)).isEqualTo(FLOAT_VALUE);
        mem.putDoubleVolatile(16, DOUBLE_VALUE);
        Assertions.assertThat(mem.getDoubleVolatile(16)).isEqualTo(DOUBLE_VALUE);

        bytes.putFloatVolatile(8, FLOAT_VALUE);
        Assertions.assertThat(bytes.getFloatVolatile(8)).isEqualTo(FLOAT_VALUE);
        bytes.putDoubleVolatile(16, DOUBLE_VALUE);
        Assertions.assertThat(bytes.getDoubleVolatile(16)).isEqualTo(DOUBLE_VALUE);

        //NaN payloads must survive the raw-bits volatile round trip bit-exactly
        mem.putFloatVolatile(24, Float.intBitsToFloat(NAN_FLOAT_BITS));
        Assertions.assertThat(Float.floatToRawIntBits(mem.getFloatVolatile(24))).isEqualTo(NAN_FLOAT_BITS);
        mem.putDoubleVolatile(32, Double.longBitsToDouble(NAN_DOUBLE_BITS));
        Assertions.assertThat(Double.doubleToRawLongBits(mem.getDoubleVolatile(32))).isEqualTo(NAN_DOUBLE_BITS);

        bytes.putFloatVolatile(24, Float.intBitsToFloat(NAN_FLOAT_BITS));
        Assertions.assertThat(Float.floatToRawIntBits(bytes.getFloatVolatile(24))).isEqualTo(NAN_FLOAT_BITS);
        bytes.putDoubleVolatile(32, Double.longBitsToDouble(NAN_DOUBLE_BITS));
        Assertions.assertThat(Double.doubleToRawLongBits(bytes.getDoubleVolatile(32))).isEqualTo(NAN_DOUBLE_BITS);

        //plain and volatile access must agree on the encoding at the same index
        mem.putIntVolatile(0, INT_VALUE);
        Assertions.assertThat(mem.getInt(0)).isEqualTo(INT_VALUE);
        mem.putLongVolatile(40, LONG_VALUE);
        Assertions.assertThat(mem.getLong(40)).isEqualTo(LONG_VALUE);
        bytes.putIntVolatile(0, INT_VALUE);
        Assertions.assertThat(bytes.getInt(0)).isEqualTo(INT_VALUE);
        bytes.putLongVolatile(40, LONG_VALUE);
        Assertions.assertThat(bytes.getLong(40)).isEqualTo(LONG_VALUE);
    }

    @Test
    public void testUnsafeReverseVolatileEqualsReverseBytes() {
        //barrier-then-swap contract: getXVolatileReverse equals reverseBytes(getXVolatile) over the same bytes
        final UnsafeMemoryBuffer mem = new UnsafeMemoryBuffer(new byte[64]);
        mem.putIntVolatile(0, INT_VALUE);
        Assertions.assertThat(mem.getIntVolatileReverse(0)).isEqualTo(Integer.reverseBytes(INT_VALUE));
        mem.putLongVolatile(8, LONG_VALUE);
        Assertions.assertThat(mem.getLongVolatileReverse(8)).isEqualTo(Long.reverseBytes(LONG_VALUE));
        mem.putCharVolatile(16, 'x');
        Assertions.assertThat(mem.getCharVolatileReverse(16)).isEqualTo(Character.reverseBytes('x'));
        mem.putShortVolatile(18, (short) 7);
        Assertions.assertThat(mem.getShortVolatileReverse(18)).isEqualTo(Short.reverseBytes((short) 7));
        mem.putFloatVolatile(20, FLOAT_VALUE);
        Assertions.assertThat(mem.getFloatVolatileReverse(20)).isEqualTo(
                Float.intBitsToFloat(Integer.reverseBytes(Float.floatToRawIntBits(FLOAT_VALUE))));
        mem.putDoubleVolatile(24, DOUBLE_VALUE);
        //bit-level comparison: DOUBLE_VALUE is NaN, and the contract under test IS NaN-payload
        //preservation through the raw-bits chain — long-bits equality is the strongest form
        Assertions.assertThat(Double.doubleToRawLongBits(mem.getDoubleVolatileReverse(24))).isEqualTo(
                Double.doubleToRawLongBits(Double.longBitsToDouble(
                        Long.reverseBytes(Double.doubleToRawLongBits(DOUBLE_VALUE)))));

        //swap-then-barrier store: the little-endian volatile store lands byte-swapped under the plain reads
        mem.putIntVolatileReverse(32, INT_VALUE);
        Assertions.assertThat(mem.getInt(32)).isEqualTo(Integer.reverseBytes(INT_VALUE));
        Assertions.assertThat(mem.getIntVolatile(32)).isEqualTo(Integer.reverseBytes(INT_VALUE));
        Assertions.assertThat(mem.getIntReverse(32)).isEqualTo(INT_VALUE);
        mem.putLongVolatileReverse(40, LONG_VALUE);
        Assertions.assertThat(mem.getLong(40)).isEqualTo(Long.reverseBytes(LONG_VALUE));
        Assertions.assertThat(mem.getLongReverse(40)).isEqualTo(LONG_VALUE);

        //NaN payloads must survive the reverse-raw-bits chain bit-exactly in both directions
        mem.putFloatVolatile(48, Float.intBitsToFloat(NAN_FLOAT_BITS));
        Assertions.assertThat(Float.floatToRawIntBits(mem.getFloatVolatileReverse(48)))
                .isEqualTo(Integer.reverseBytes(NAN_FLOAT_BITS));
        mem.putFloatVolatileReverse(52, Float.intBitsToFloat(NAN_FLOAT_BITS));
        Assertions.assertThat(Float.floatToRawIntBits(mem.getFloat(52)))
                .isEqualTo(Integer.reverseBytes(NAN_FLOAT_BITS));
        Assertions.assertThat(Float.floatToRawIntBits(mem.getFloatVolatileReverse(52)))
                .isEqualTo(NAN_FLOAT_BITS);
        mem.putDoubleVolatile(56, Double.longBitsToDouble(NAN_DOUBLE_BITS));
        Assertions.assertThat(Double.doubleToRawLongBits(mem.getDoubleVolatileReverse(56)))
                .isEqualTo(Long.reverseBytes(NAN_DOUBLE_BITS));
        mem.putDoubleVolatileReverse(0, Double.longBitsToDouble(NAN_DOUBLE_BITS));
        Assertions.assertThat(Double.doubleToRawLongBits(mem.getDouble(0)))
                .isEqualTo(Long.reverseBytes(NAN_DOUBLE_BITS));
        Assertions.assertThat(Double.doubleToRawLongBits(mem.getDoubleVolatileReverse(0)))
                .isEqualTo(NAN_DOUBLE_BITS);

        //the bytes twin obeys the same contract
        final UnsafeByteBuffer bytes = new UnsafeByteBuffer(new byte[64]);
        bytes.putIntVolatile(0, INT_VALUE);
        Assertions.assertThat(bytes.getIntVolatileReverse(0)).isEqualTo(Integer.reverseBytes(INT_VALUE));
        bytes.putLongVolatile(8, LONG_VALUE);
        Assertions.assertThat(bytes.getLongVolatileReverse(8)).isEqualTo(Long.reverseBytes(LONG_VALUE));
        bytes.putFloatVolatile(16, FLOAT_VALUE);
        Assertions.assertThat(bytes.getFloatVolatileReverse(16)).isEqualTo(
                Float.intBitsToFloat(Integer.reverseBytes(Float.floatToRawIntBits(FLOAT_VALUE))));
        bytes.putDoubleVolatile(24, DOUBLE_VALUE);
        //bit-level: the swapped MAX_VALUE pattern is a NaN — == is the wrong comparison, raw bits are exact
        Assertions.assertThat(Double.doubleToRawLongBits(bytes.getDoubleVolatileReverse(24))).isEqualTo(
                Double.doubleToRawLongBits(Double.longBitsToDouble(
                        Long.reverseBytes(Double.doubleToRawLongBits(DOUBLE_VALUE)))));
        bytes.putIntVolatileReverse(32, INT_VALUE);
        Assertions.assertThat(bytes.getInt(32)).isEqualTo(Integer.reverseBytes(INT_VALUE));
        bytes.putLongVolatileReverse(40, LONG_VALUE);
        Assertions.assertThat(bytes.getLong(40)).isEqualTo(Long.reverseBytes(LONG_VALUE));
        bytes.putFloatVolatileReverse(48, Float.intBitsToFloat(NAN_FLOAT_BITS));
        Assertions.assertThat(Float.floatToRawIntBits(bytes.getFloat(48)))
                .isEqualTo(Integer.reverseBytes(NAN_FLOAT_BITS));
        Assertions.assertThat(Float.floatToRawIntBits(bytes.getFloatVolatileReverse(48)))
                .isEqualTo(NAN_FLOAT_BITS);
        bytes.putDoubleVolatileReverse(56, Double.longBitsToDouble(NAN_DOUBLE_BITS));
        Assertions.assertThat(Double.doubleToRawLongBits(bytes.getDouble(56)))
                .isEqualTo(Long.reverseBytes(NAN_DOUBLE_BITS));
        Assertions.assertThat(Double.doubleToRawLongBits(bytes.getDoubleVolatileReverse(56)))
                .isEqualTo(NAN_DOUBLE_BITS);
    }

    @Test
    public void testIntLongReverseTrioRoundTrip() {
        final IRandomGenerator random = PseudoRandomGenerators.newPseudoRandom(1337L);
        final int randomInt = random.nextInt();
        final long randomLong = random.nextLong();

        final UnsafeMemoryBuffer mem = new UnsafeMemoryBuffer(new byte[64]);
        testIntLongReverseTrioRoundTripMemory(mem, randomInt, randomLong);

        final UnsafeByteBuffer bytes = new UnsafeByteBuffer(new byte[64]);
        testIntLongReverseTrioRoundTripBytes(bytes, randomInt, randomLong);
    }

    private void testIntLongReverseTrioRoundTripMemory(final UnsafeMemoryBuffer mem, final int randomInt,
            final long randomLong) {
        //volatile Reverse pair round-trips
        mem.putIntVolatileReverse(0, INT_VALUE);
        Assertions.assertThat(mem.getIntVolatileReverse(0)).isEqualTo(INT_VALUE);
        mem.putLongVolatileReverse(16, LONG_VALUE);
        Assertions.assertThat(mem.getLongVolatileReverse(16)).isEqualTo(LONG_VALUE);

        //release Reverse store is seen by the acquire Reverse load and by the volatile Reverse load
        mem.putIntReleaseReverse(4, randomInt);
        Assertions.assertThat(mem.getIntAcquireReverse(4)).isEqualTo(randomInt);
        Assertions.assertThat(mem.getIntVolatileReverse(4)).isEqualTo(randomInt);
        mem.putLongReleaseReverse(24, randomLong);
        Assertions.assertThat(mem.getLongAcquireReverse(24)).isEqualTo(randomLong);
        Assertions.assertThat(mem.getLongVolatileReverse(24)).isEqualTo(randomLong);

        //ordered Reverse store is seen by the volatile Reverse load and lands byte-swapped under plain big-endian reads
        mem.putIntOrderedReverse(8, -randomInt);
        Assertions.assertThat(mem.getIntVolatileReverse(8)).isEqualTo(-randomInt);
        Assertions.assertThat(mem.getInt(8)).isEqualTo(Integer.reverseBytes(-randomInt));
        mem.putLongOrderedReverse(32, -randomLong);
        Assertions.assertThat(mem.getLongVolatileReverse(32)).isEqualTo(-randomLong);
        Assertions.assertThat(mem.getLong(32)).isEqualTo(Long.reverseBytes(-randomLong));
    }

    private void testIntLongReverseTrioRoundTripBytes(final UnsafeByteBuffer bytes, final int randomInt,
            final long randomLong) {
        bytes.putIntVolatileReverse(0, INT_VALUE);
        Assertions.assertThat(bytes.getIntVolatileReverse(0)).isEqualTo(INT_VALUE);
        bytes.putLongVolatileReverse(16, LONG_VALUE);
        Assertions.assertThat(bytes.getLongVolatileReverse(16)).isEqualTo(LONG_VALUE);

        bytes.putIntReleaseReverse(4, randomInt);
        Assertions.assertThat(bytes.getIntAcquireReverse(4)).isEqualTo(randomInt);
        Assertions.assertThat(bytes.getIntVolatileReverse(4)).isEqualTo(randomInt);
        bytes.putLongReleaseReverse(24, randomLong);
        Assertions.assertThat(bytes.getLongAcquireReverse(24)).isEqualTo(randomLong);
        Assertions.assertThat(bytes.getLongVolatileReverse(24)).isEqualTo(randomLong);

        bytes.putIntOrderedReverse(8, -randomInt);
        Assertions.assertThat(bytes.getIntVolatileReverse(8)).isEqualTo(-randomInt);
        Assertions.assertThat(bytes.getInt(8)).isEqualTo(Integer.reverseBytes(-randomInt));
        bytes.putLongOrderedReverse(32, -randomLong);
        Assertions.assertThat(bytes.getLongVolatileReverse(32)).isEqualTo(-randomLong);
        Assertions.assertThat(bytes.getLong(32)).isEqualTo(Long.reverseBytes(-randomLong));
    }

    @Test
    public void testListMemoryBufferReverseVolatileSinglePartAndCrossPart() {
        final List<IMemoryBuffer> parts = new ArrayList<>();
        parts.add(MemoryBuffers.wrap(new byte[PART_SIZE]));
        parts.add(MemoryBuffers.wrap(new byte[PART_SIZE]));
        final ListMemoryBuffer buffer = new ListMemoryBuffer(parts);

        testReverseVolatileSinglePartAndCrossPartMemory(buffer);
    }

    @Test
    public void testSegmentedMemoryBufferReverseVolatileSinglePartAndCrossPart() {
        final SegmentedMemoryBuffer buffer = new SegmentedMemoryBuffer(
                new IMemoryBuffer[] {MemoryBuffers.wrap(new byte[PART_SIZE]), MemoryBuffers.wrap(new byte[PART_SIZE])},
                PART_SIZE);

        testReverseVolatileSinglePartAndCrossPartMemory(buffer);
    }

    private void testReverseVolatileSinglePartAndCrossPartMemory(final IMemoryBuffer buffer) {
        //single-part Reverse volatile access works and round-trips
        buffer.putIntVolatileReverse(4, INT_VALUE);
        Assertions.assertThat(buffer.getIntVolatileReverse(4)).isEqualTo(INT_VALUE);
        Assertions.assertThat(buffer.getInt(4)).isEqualTo(Integer.reverseBytes(INT_VALUE));
        buffer.putLongVolatileReverse(8, LONG_VALUE);
        Assertions.assertThat(buffer.getLongVolatileReverse(8)).isEqualTo(LONG_VALUE);
        buffer.putFloatVolatileReverse(12, FLOAT_VALUE);
        Assertions.assertThat(buffer.getFloatVolatileReverse(12)).isEqualTo(FLOAT_VALUE);
        buffer.putDoubleVolatileReverse(16, DOUBLE_VALUE);
        Assertions.assertThat(buffer.getDoubleVolatileReverse(16)).isEqualTo(DOUBLE_VALUE);
        buffer.putIntReleaseReverse(0, INT_VALUE);
        Assertions.assertThat(buffer.getIntAcquireReverse(0)).isEqualTo(INT_VALUE);
        buffer.putCharVolatileReverse(14, 'x');
        Assertions.assertThat(buffer.getCharVolatileReverse(14)).isEqualTo('x');

        //cross-part wide Reverse volatile access is refused, not silently torn
        assertSplitThrowsMemory(buffer, "putIntVolatileReverse",
                () -> buffer.putIntVolatileReverse(PART_SIZE - 2, INT_VALUE));
        assertSplitThrowsMemory(buffer, "getIntVolatileReverse",
                () -> buffer.getIntVolatileReverse(PART_SIZE - 2));
        assertSplitThrowsMemory(buffer, "putLongVolatileReverse",
                () -> buffer.putLongVolatileReverse(PART_SIZE - 4, LONG_VALUE));
        assertSplitThrowsMemory(buffer, "getLongVolatileReverse",
                () -> buffer.getLongVolatileReverse(PART_SIZE - 4));
        assertSplitThrowsMemory(buffer, "putFloatVolatileReverse",
                () -> buffer.putFloatVolatileReverse(PART_SIZE - 3, FLOAT_VALUE));
        assertSplitThrowsMemory(buffer, "getFloatVolatileReverse",
                () -> buffer.getFloatVolatileReverse(PART_SIZE - 3));
        assertSplitThrowsMemory(buffer, "putDoubleVolatileReverse",
                () -> buffer.putDoubleVolatileReverse(PART_SIZE - 5, DOUBLE_VALUE));
        assertSplitThrowsMemory(buffer, "getDoubleVolatileReverse",
                () -> buffer.getDoubleVolatileReverse(PART_SIZE - 5));
        assertSplitThrowsMemory(buffer, "putCharVolatileReverse",
                () -> buffer.putCharVolatileReverse(PART_SIZE - 1, 'x'));
        assertSplitThrowsMemory(buffer, "getCharVolatileReverse",
                () -> buffer.getCharVolatileReverse(PART_SIZE - 1));
        assertSplitThrowsMemory(buffer, "putShortVolatileReverse",
                () -> buffer.putShortVolatileReverse(PART_SIZE - 1, (short) 7));
        assertSplitThrowsMemory(buffer, "getShortVolatileReverse",
                () -> buffer.getShortVolatileReverse(PART_SIZE - 1));
        assertSplitThrowsMemory(buffer, "putIntReleaseReverse",
                () -> buffer.putIntReleaseReverse(PART_SIZE - 2, INT_VALUE));
        assertSplitThrowsMemory(buffer, "putIntOrderedReverse",
                () -> buffer.putIntOrderedReverse(PART_SIZE - 2, INT_VALUE));
        assertSplitThrowsMemory(buffer, "putLongReleaseReverse",
                () -> buffer.putLongReleaseReverse(PART_SIZE - 4, LONG_VALUE));
        assertSplitThrowsMemory(buffer, "putLongOrderedReverse",
                () -> buffer.putLongOrderedReverse(PART_SIZE - 4, LONG_VALUE));
        assertSplitThrowsMemory(buffer, "getIntAcquireReverse",
                () -> buffer.getIntAcquireReverse(PART_SIZE - 2));
        assertSplitThrowsMemory(buffer, "getLongAcquireReverse",
                () -> buffer.getLongAcquireReverse(PART_SIZE - 4));
    }

    @Test
    public void testListByteBufferReverseVolatileSinglePartAndCrossPart() {
        final List<IByteBuffer> parts = new ArrayList<>();
        parts.add(new UnsafeByteBuffer(new byte[PART_SIZE]));
        parts.add(new UnsafeByteBuffer(new byte[PART_SIZE]));
        final ListByteBuffer buffer = new ListByteBuffer(parts);

        testReverseVolatileSinglePartAndCrossPartBytes(buffer);
    }

    @Test
    public void testSegmentedByteBufferReverseVolatileSinglePartAndCrossPart() {
        final SegmentedByteBuffer buffer = new SegmentedByteBuffer(
                new IByteBuffer[] {new UnsafeByteBuffer(new byte[PART_SIZE]),
                        new UnsafeByteBuffer(new byte[PART_SIZE])},
                PART_SIZE);

        testReverseVolatileSinglePartAndCrossPartBytes(buffer);
    }

    private void testReverseVolatileSinglePartAndCrossPartBytes(final IByteBuffer buffer) {
        buffer.putIntVolatileReverse(4, INT_VALUE);
        Assertions.assertThat(buffer.getIntVolatileReverse(4)).isEqualTo(INT_VALUE);
        Assertions.assertThat(buffer.getInt(4)).isEqualTo(Integer.reverseBytes(INT_VALUE));
        buffer.putLongVolatileReverse(8, LONG_VALUE);
        Assertions.assertThat(buffer.getLongVolatileReverse(8)).isEqualTo(LONG_VALUE);
        buffer.putFloatVolatileReverse(12, FLOAT_VALUE);
        Assertions.assertThat(buffer.getFloatVolatileReverse(12)).isEqualTo(FLOAT_VALUE);
        buffer.putDoubleVolatileReverse(16, DOUBLE_VALUE);
        Assertions.assertThat(buffer.getDoubleVolatileReverse(16)).isEqualTo(DOUBLE_VALUE);
        buffer.putIntReleaseReverse(0, INT_VALUE);
        Assertions.assertThat(buffer.getIntAcquireReverse(0)).isEqualTo(INT_VALUE);
        buffer.putCharVolatileReverse(14, 'x');
        Assertions.assertThat(buffer.getCharVolatileReverse(14)).isEqualTo('x');

        assertSplitThrowsBytes(buffer, "putIntVolatileReverse",
                () -> buffer.putIntVolatileReverse(PART_SIZE - 2, INT_VALUE));
        assertSplitThrowsBytes(buffer, "getIntVolatileReverse",
                () -> buffer.getIntVolatileReverse(PART_SIZE - 2));
        assertSplitThrowsBytes(buffer, "putLongVolatileReverse",
                () -> buffer.putLongVolatileReverse(PART_SIZE - 4, LONG_VALUE));
        assertSplitThrowsBytes(buffer, "getLongVolatileReverse",
                () -> buffer.getLongVolatileReverse(PART_SIZE - 4));
        assertSplitThrowsBytes(buffer, "putFloatVolatileReverse",
                () -> buffer.putFloatVolatileReverse(PART_SIZE - 3, FLOAT_VALUE));
        assertSplitThrowsBytes(buffer, "getFloatVolatileReverse",
                () -> buffer.getFloatVolatileReverse(PART_SIZE - 3));
        assertSplitThrowsBytes(buffer, "putDoubleVolatileReverse",
                () -> buffer.putDoubleVolatileReverse(PART_SIZE - 5, DOUBLE_VALUE));
        assertSplitThrowsBytes(buffer, "getDoubleVolatileReverse",
                () -> buffer.getDoubleVolatileReverse(PART_SIZE - 5));
        assertSplitThrowsBytes(buffer, "putCharVolatileReverse",
                () -> buffer.putCharVolatileReverse(PART_SIZE - 1, 'x'));
        assertSplitThrowsBytes(buffer, "getCharVolatileReverse",
                () -> buffer.getCharVolatileReverse(PART_SIZE - 1));
        assertSplitThrowsBytes(buffer, "putShortVolatileReverse",
                () -> buffer.putShortVolatileReverse(PART_SIZE - 1, (short) 7));
        assertSplitThrowsBytes(buffer, "getShortVolatileReverse",
                () -> buffer.getShortVolatileReverse(PART_SIZE - 1));
        assertSplitThrowsBytes(buffer, "putIntReleaseReverse",
                () -> buffer.putIntReleaseReverse(PART_SIZE - 2, INT_VALUE));
        assertSplitThrowsBytes(buffer, "putIntOrderedReverse",
                () -> buffer.putIntOrderedReverse(PART_SIZE - 2, INT_VALUE));
        assertSplitThrowsBytes(buffer, "putLongReleaseReverse",
                () -> buffer.putLongReleaseReverse(PART_SIZE - 4, LONG_VALUE));
        assertSplitThrowsBytes(buffer, "putLongOrderedReverse",
                () -> buffer.putLongOrderedReverse(PART_SIZE - 4, LONG_VALUE));
        assertSplitThrowsBytes(buffer, "getIntAcquireReverse",
                () -> buffer.getIntAcquireReverse(PART_SIZE - 2));
        assertSplitThrowsBytes(buffer, "getLongAcquireReverse",
                () -> buffer.getLongAcquireReverse(PART_SIZE - 4));
    }

    @Test
    public void testDelegatesReverseVolatileRoundTripParity() {
        //memory buffer delegating to a bytes buffer: the Reverse twins must route through the delegate's Reverse path
        final UnsafeByteBuffer underlying = new UnsafeByteBuffer(new byte[64]);
        final ByteDelegateMemoryBuffer mem = new ByteDelegateMemoryBuffer(underlying);

        mem.putIntVolatileReverse(8, INT_VALUE);
        Assertions.assertThat(underlying.getIntVolatileReverse(8)).isEqualTo(INT_VALUE);
        Assertions.assertThat(underlying.getInt(8)).isEqualTo(Integer.reverseBytes(INT_VALUE));
        Assertions.assertThat(mem.getIntVolatileReverse(8)).isEqualTo(INT_VALUE);
        underlying.putLongVolatileReverse(16, LONG_VALUE);
        Assertions.assertThat(mem.getLongVolatileReverse(16)).isEqualTo(LONG_VALUE);
        Assertions.assertThat(mem.getLong(16)).isEqualTo(Long.reverseBytes(LONG_VALUE));
        mem.putFloatVolatileReverse(24, Float.intBitsToFloat(NAN_FLOAT_BITS));
        Assertions.assertThat(Float.floatToRawIntBits(underlying.getFloat(24)))
                .isEqualTo(Integer.reverseBytes(NAN_FLOAT_BITS));
        Assertions.assertThat(Float.floatToRawIntBits(underlying.getFloatVolatileReverse(24)))
                .isEqualTo(NAN_FLOAT_BITS);
        underlying.putDoubleVolatileReverse(32, Double.longBitsToDouble(NAN_DOUBLE_BITS));
        Assertions.assertThat(Double.doubleToRawLongBits(mem.getDouble(32)))
                .isEqualTo(Long.reverseBytes(NAN_DOUBLE_BITS));
        Assertions.assertThat(Double.doubleToRawLongBits(mem.getDoubleVolatileReverse(32)))
                .isEqualTo(NAN_DOUBLE_BITS);

        //sliced memory buffer: the Reverse twins must land at the shifted delegate coordinates
        final UnsafeMemoryBuffer underlyingMem = new UnsafeMemoryBuffer(new byte[64]);
        final SlicedDelegateMemoryBuffer slice = new SlicedDelegateMemoryBuffer(underlyingMem, 8, 48);
        slice.putIntVolatileReverse(4, INT_VALUE);
        Assertions.assertThat(underlyingMem.getIntVolatileReverse(12)).isEqualTo(INT_VALUE);
        Assertions.assertThat(underlyingMem.getInt(12)).isEqualTo(Integer.reverseBytes(INT_VALUE));
        Assertions.assertThat(slice.getIntVolatileReverse(4)).isEqualTo(INT_VALUE);
        underlyingMem.putLongVolatileReverse(20, LONG_VALUE);
        Assertions.assertThat(slice.getLongVolatileReverse(12)).isEqualTo(LONG_VALUE);
        Assertions.assertThat(slice.getLong(12)).isEqualTo(Long.reverseBytes(LONG_VALUE));
        slice.putFloatVolatileReverse(24, Float.intBitsToFloat(NAN_FLOAT_BITS));
        Assertions.assertThat(Float.floatToRawIntBits(underlyingMem.getFloat(32)))
                .isEqualTo(Integer.reverseBytes(NAN_FLOAT_BITS));

        //sliced bytes buffer: same parity through the bytes slice
        final UnsafeByteBuffer underlyingBytes = new UnsafeByteBuffer(new byte[64]);
        final SlicedDelegateByteBuffer bytesSlice = new SlicedDelegateByteBuffer(underlyingBytes, 8, 48);
        bytesSlice.putIntVolatileReverse(4, INT_VALUE);
        Assertions.assertThat(underlyingBytes.getIntVolatileReverse(12)).isEqualTo(INT_VALUE);
        Assertions.assertThat(bytesSlice.getIntVolatileReverse(4)).isEqualTo(INT_VALUE);
        underlyingBytes.putDoubleVolatileReverse(16, Double.longBitsToDouble(NAN_DOUBLE_BITS));
        Assertions.assertThat(Double.doubleToRawLongBits(bytesSlice.getDoubleVolatileReverse(8)))
                .isEqualTo(NAN_DOUBLE_BITS);
        Assertions.assertThat(Double.doubleToRawLongBits(bytesSlice.getDouble(8)))
                .isEqualTo(Long.reverseBytes(NAN_DOUBLE_BITS));
    }

    @Test
    public void testIntLongAcquireReleaseOrderedRoundTrip() {
        final UnsafeMemoryBuffer mem = new UnsafeMemoryBuffer(new byte[64]);
        final UnsafeByteBuffer bytes = new UnsafeByteBuffer(new byte[64]);

        mem.putIntRelease(0, INT_VALUE);
        Assertions.assertThat(mem.getIntAcquire(0)).isEqualTo(INT_VALUE);
        mem.putIntOrdered(4, -INT_VALUE);
        Assertions.assertThat(mem.getIntVolatile(4)).isEqualTo(-INT_VALUE);
        mem.putLongRelease(8, LONG_VALUE);
        Assertions.assertThat(mem.getLongAcquire(8)).isEqualTo(LONG_VALUE);
        mem.putLongOrdered(16, -LONG_VALUE);
        Assertions.assertThat(mem.getLongVolatile(16)).isEqualTo(-LONG_VALUE);

        bytes.putIntRelease(0, INT_VALUE);
        Assertions.assertThat(bytes.getIntAcquire(0)).isEqualTo(INT_VALUE);
        bytes.putIntOrdered(4, -INT_VALUE);
        Assertions.assertThat(bytes.getIntVolatile(4)).isEqualTo(-INT_VALUE);
        bytes.putLongRelease(8, LONG_VALUE);
        Assertions.assertThat(bytes.getLongAcquire(8)).isEqualTo(LONG_VALUE);
        bytes.putLongOrdered(16, -LONG_VALUE);
        Assertions.assertThat(bytes.getLongVolatile(16)).isEqualTo(-LONG_VALUE);

        //the acquire reads see the plain stores of the same index as well
        mem.putInt(24, INT_VALUE);
        Assertions.assertThat(mem.getIntAcquire(24)).isEqualTo(INT_VALUE);
        bytes.putLong(24, LONG_VALUE);
        Assertions.assertThat(bytes.getLongAcquire(24)).isEqualTo(LONG_VALUE);
    }

    @Test
    public void testListMemoryBufferVolatileSinglePartAndCrossPart() {
        final List<IMemoryBuffer> parts = new ArrayList<>();
        parts.add(MemoryBuffers.wrap(new byte[PART_SIZE]));
        parts.add(MemoryBuffers.wrap(new byte[PART_SIZE]));
        final ListMemoryBuffer buffer = new ListMemoryBuffer(parts);

        testVolatileSinglePartAndCrossPartMemory(buffer);
    }

    @Test
    public void testSegmentedMemoryBufferVolatileSinglePartAndCrossPart() {
        final SegmentedMemoryBuffer buffer = new SegmentedMemoryBuffer(
                new IMemoryBuffer[] {MemoryBuffers.wrap(new byte[PART_SIZE]), MemoryBuffers.wrap(new byte[PART_SIZE])},
                PART_SIZE);

        testVolatileSinglePartAndCrossPartMemory(buffer);
    }

    private void testVolatileSinglePartAndCrossPartMemory(final IMemoryBuffer buffer) {
        //single-part volatile access works
        buffer.putIntVolatile(4, INT_VALUE);
        Assertions.assertThat(buffer.getIntVolatile(4)).isEqualTo(INT_VALUE);
        buffer.putLongVolatile(8, LONG_VALUE);
        Assertions.assertThat(buffer.getLongVolatile(8)).isEqualTo(LONG_VALUE);
        buffer.putFloatVolatile(12, FLOAT_VALUE);
        Assertions.assertThat(buffer.getFloatVolatile(12)).isEqualTo(FLOAT_VALUE);
        buffer.putDoubleVolatile(20, DOUBLE_VALUE);
        Assertions.assertThat(buffer.getDoubleVolatile(20)).isEqualTo(DOUBLE_VALUE);
        buffer.putIntRelease(0, INT_VALUE);
        Assertions.assertThat(buffer.getIntAcquire(0)).isEqualTo(INT_VALUE);

        //cross-part wide volatile access is refused, not silently torn
        assertSplitThrowsMemory(buffer, "putIntVolatile", () -> buffer.putIntVolatile(PART_SIZE - 2, INT_VALUE));
        assertSplitThrowsMemory(buffer, "getIntVolatile", () -> buffer.getIntVolatile(PART_SIZE - 2));
        assertSplitThrowsMemory(buffer, "putCharVolatile", () -> buffer.putCharVolatile(PART_SIZE - 1, 'x'));
        assertSplitThrowsMemory(buffer, "getCharVolatile", () -> buffer.getCharVolatile(PART_SIZE - 1));
        assertSplitThrowsMemory(buffer, "putShortVolatile", () -> buffer.putShortVolatile(PART_SIZE - 1, (short) 7));
        assertSplitThrowsMemory(buffer, "getShortVolatile", () -> buffer.getShortVolatile(PART_SIZE - 1));
        assertSplitThrowsMemory(buffer, "putLongVolatile", () -> buffer.putLongVolatile(PART_SIZE - 4, LONG_VALUE));
        assertSplitThrowsMemory(buffer, "getLongVolatile", () -> buffer.getLongVolatile(PART_SIZE - 4));
        assertSplitThrowsMemory(buffer, "putFloatVolatile",
                () -> buffer.putFloatVolatile(PART_SIZE - 3, FLOAT_VALUE));
        assertSplitThrowsMemory(buffer, "getFloatVolatile", () -> buffer.getFloatVolatile(PART_SIZE - 3));
        assertSplitThrowsMemory(buffer, "putDoubleVolatile",
                () -> buffer.putDoubleVolatile(PART_SIZE - 5, DOUBLE_VALUE));
        assertSplitThrowsMemory(buffer, "getDoubleVolatile", () -> buffer.getDoubleVolatile(PART_SIZE - 5));
        assertSplitThrowsMemory(buffer, "putIntRelease", () -> buffer.putIntRelease(PART_SIZE - 2, INT_VALUE));
        assertSplitThrowsMemory(buffer, "putIntOrdered", () -> buffer.putIntOrdered(PART_SIZE - 2, INT_VALUE));
        assertSplitThrowsMemory(buffer, "putLongRelease", () -> buffer.putLongRelease(PART_SIZE - 4, LONG_VALUE));
        assertSplitThrowsMemory(buffer, "putLongOrdered", () -> buffer.putLongOrdered(PART_SIZE - 4, LONG_VALUE));
        assertSplitThrowsMemory(buffer, "getIntAcquire", () -> buffer.getIntAcquire(PART_SIZE - 2));
        assertSplitThrowsMemory(buffer, "getLongAcquire", () -> buffer.getLongAcquire(PART_SIZE - 4));

        //byte volatile is unaffected by the split rule
        buffer.putByteVolatile(PART_SIZE - 1, (byte) -3);
        Assertions.assertThat(buffer.getByteVolatile(PART_SIZE - 1)).isEqualTo((byte) -3);
        buffer.putByteVolatile(PART_SIZE, (byte) 3);
        Assertions.assertThat(buffer.getByteVolatile(PART_SIZE)).isEqualTo((byte) 3);
    }

    private void assertSplitThrowsMemory(final IMemoryBuffer buffer, final String method,
            final ThrowingCallable access) {
        Assertions.assertThatThrownBy(access).isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining(method);
    }

    @Test
    public void testListByteBufferVolatileSinglePartAndCrossPart() {
        final List<IByteBuffer> parts = new ArrayList<>();
        parts.add(new UnsafeByteBuffer(new byte[PART_SIZE]));
        parts.add(new UnsafeByteBuffer(new byte[PART_SIZE]));
        final ListByteBuffer buffer = new ListByteBuffer(parts);

        testVolatileSinglePartAndCrossPartBytes(buffer);
    }

    @Test
    public void testSegmentedByteBufferVolatileSinglePartAndCrossPart() {
        final SegmentedByteBuffer buffer = new SegmentedByteBuffer(
                new IByteBuffer[] {new UnsafeByteBuffer(new byte[PART_SIZE]),
                        new UnsafeByteBuffer(new byte[PART_SIZE])},
                PART_SIZE);

        testVolatileSinglePartAndCrossPartBytes(buffer);
    }

    private void testVolatileSinglePartAndCrossPartBytes(final IByteBuffer buffer) {
        buffer.putIntVolatile(4, INT_VALUE);
        Assertions.assertThat(buffer.getIntVolatile(4)).isEqualTo(INT_VALUE);
        buffer.putLongVolatile(8, LONG_VALUE);
        Assertions.assertThat(buffer.getLongVolatile(8)).isEqualTo(LONG_VALUE);
        buffer.putFloatVolatile(12, FLOAT_VALUE);
        Assertions.assertThat(buffer.getFloatVolatile(12)).isEqualTo(FLOAT_VALUE);
        buffer.putDoubleVolatile(20, DOUBLE_VALUE);
        Assertions.assertThat(buffer.getDoubleVolatile(20)).isEqualTo(DOUBLE_VALUE);
        buffer.putIntRelease(0, INT_VALUE);
        Assertions.assertThat(buffer.getIntAcquire(0)).isEqualTo(INT_VALUE);

        assertSplitThrowsBytes(buffer, "putIntVolatile", () -> buffer.putIntVolatile(PART_SIZE - 2, INT_VALUE));
        assertSplitThrowsBytes(buffer, "getIntVolatile", () -> buffer.getIntVolatile(PART_SIZE - 2));
        assertSplitThrowsBytes(buffer, "putCharVolatile", () -> buffer.putCharVolatile(PART_SIZE - 1, 'x'));
        assertSplitThrowsBytes(buffer, "getCharVolatile", () -> buffer.getCharVolatile(PART_SIZE - 1));
        assertSplitThrowsBytes(buffer, "putShortVolatile", () -> buffer.putShortVolatile(PART_SIZE - 1, (short) 7));
        assertSplitThrowsBytes(buffer, "getShortVolatile", () -> buffer.getShortVolatile(PART_SIZE - 1));
        assertSplitThrowsBytes(buffer, "putLongVolatile", () -> buffer.putLongVolatile(PART_SIZE - 4, LONG_VALUE));
        assertSplitThrowsBytes(buffer, "getLongVolatile", () -> buffer.getLongVolatile(PART_SIZE - 4));
        assertSplitThrowsBytes(buffer, "putFloatVolatile",
                () -> buffer.putFloatVolatile(PART_SIZE - 3, FLOAT_VALUE));
        assertSplitThrowsBytes(buffer, "getFloatVolatile", () -> buffer.getFloatVolatile(PART_SIZE - 3));
        assertSplitThrowsBytes(buffer, "putDoubleVolatile",
                () -> buffer.putDoubleVolatile(PART_SIZE - 5, DOUBLE_VALUE));
        assertSplitThrowsBytes(buffer, "getDoubleVolatile", () -> buffer.getDoubleVolatile(PART_SIZE - 5));
        assertSplitThrowsBytes(buffer, "putIntRelease", () -> buffer.putIntRelease(PART_SIZE - 2, INT_VALUE));
        assertSplitThrowsBytes(buffer, "putIntOrdered", () -> buffer.putIntOrdered(PART_SIZE - 2, INT_VALUE));
        assertSplitThrowsBytes(buffer, "putLongRelease", () -> buffer.putLongRelease(PART_SIZE - 4, LONG_VALUE));
        assertSplitThrowsBytes(buffer, "putLongOrdered", () -> buffer.putLongOrdered(PART_SIZE - 4, LONG_VALUE));
        assertSplitThrowsBytes(buffer, "getIntAcquire", () -> buffer.getIntAcquire(PART_SIZE - 2));
        assertSplitThrowsBytes(buffer, "getLongAcquire", () -> buffer.getLongAcquire(PART_SIZE - 4));

        buffer.putByteVolatile(PART_SIZE - 1, (byte) -3);
        Assertions.assertThat(buffer.getByteVolatile(PART_SIZE - 1)).isEqualTo((byte) -3);
        buffer.putByteVolatile(PART_SIZE, (byte) 3);
        Assertions.assertThat(buffer.getByteVolatile(PART_SIZE)).isEqualTo((byte) 3);
    }

    private void assertSplitThrowsBytes(final IByteBuffer buffer, final String method,
            final ThrowingCallable access) {
        Assertions.assertThatThrownBy(access).isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining(method);
    }

    @Test
    public void testByteDelegateMemoryBufferForwardConsistency() {
        //memory buffer delegating to a bytes buffer: volatile access must route through the delegate's volatile path
        final UnsafeByteBuffer underlying = new UnsafeByteBuffer(new byte[64]);
        final ByteDelegateMemoryBuffer mem = new ByteDelegateMemoryBuffer(underlying);

        mem.putIntVolatile(8, INT_VALUE);
        Assertions.assertThat(underlying.getIntVolatile(8)).isEqualTo(INT_VALUE);
        Assertions.assertThat(mem.getIntVolatile(8)).isEqualTo(INT_VALUE);

        underlying.putLongVolatile(16, LONG_VALUE);
        Assertions.assertThat(mem.getLongVolatile(16)).isEqualTo(LONG_VALUE);

        mem.putFloatVolatile(24, FLOAT_VALUE);
        Assertions.assertThat(underlying.getFloatVolatile(24)).isEqualTo(FLOAT_VALUE);
        mem.putDoubleVolatile(32, DOUBLE_VALUE);
        Assertions.assertThat(underlying.getDoubleVolatile(32)).isEqualTo(DOUBLE_VALUE);

        mem.putIntRelease(40, INT_VALUE);
        Assertions.assertThat(underlying.getIntAcquire(40)).isEqualTo(INT_VALUE);
        underlying.putLongRelease(48, LONG_VALUE);
        Assertions.assertThat(mem.getLongAcquire(48)).isEqualTo(LONG_VALUE);
    }

    @Test
    public void testOrderedDelegateVolatilePlainParity() {
        //the reversal wrapper only activates when order != delegate order; then plain and volatile encodings must agree
        final UnsafeMemoryBuffer underlying = new UnsafeMemoryBuffer(new byte[64]);
        final IMemoryBuffer mem = OrderedDelegateMemoryBuffer.maybeWrap(underlying, ByteOrder.BIG_ENDIAN);

        mem.putIntVolatile(0, INT_VALUE);
        Assertions.assertThat(mem.getInt(0)).isEqualTo(INT_VALUE);
        Assertions.assertThat(mem.getIntVolatile(0)).isEqualTo(INT_VALUE);
        mem.putLongVolatile(8, LONG_VALUE);
        Assertions.assertThat(mem.getLong(8)).isEqualTo(LONG_VALUE);
        Assertions.assertThat(mem.getLongVolatile(8)).isEqualTo(LONG_VALUE);
        mem.putFloatVolatile(16, FLOAT_VALUE);
        Assertions.assertThat(mem.getFloat(16)).isEqualTo(FLOAT_VALUE);
        Assertions.assertThat(mem.getFloatVolatile(16)).isEqualTo(FLOAT_VALUE);
        mem.putDoubleVolatile(24, DOUBLE_VALUE);
        Assertions.assertThat(mem.getDouble(24)).isEqualTo(DOUBLE_VALUE);
        Assertions.assertThat(mem.getDoubleVolatile(24)).isEqualTo(DOUBLE_VALUE);
        mem.putIntRelease(32, INT_VALUE);
        Assertions.assertThat(mem.getInt(32)).isEqualTo(INT_VALUE);
        Assertions.assertThat(mem.getIntAcquire(32)).isEqualTo(INT_VALUE);
        mem.putLongOrdered(40, LONG_VALUE);
        Assertions.assertThat(mem.getLong(40)).isEqualTo(LONG_VALUE);
        Assertions.assertThat(mem.getLongVolatile(40)).isEqualTo(LONG_VALUE);

        final UnsafeByteBuffer underlyingBytes = new UnsafeByteBuffer(new byte[64]);
        final IByteBuffer bytes = OrderedDelegateByteBuffer.maybeWrap(underlyingBytes, ByteOrder.BIG_ENDIAN);
        bytes.putIntVolatile(0, INT_VALUE);
        Assertions.assertThat(bytes.getInt(0)).isEqualTo(INT_VALUE);
        bytes.putDoubleVolatile(8, DOUBLE_VALUE);
        Assertions.assertThat(bytes.getDouble(8)).isEqualTo(DOUBLE_VALUE);
        bytes.putFloatVolatile(16, FLOAT_VALUE);
        Assertions.assertThat(bytes.getFloatVolatile(16)).isEqualTo(FLOAT_VALUE);
        bytes.putLongRelease(24, LONG_VALUE);
        Assertions.assertThat(bytes.getLongAcquire(24)).isEqualTo(LONG_VALUE);
    }
}
