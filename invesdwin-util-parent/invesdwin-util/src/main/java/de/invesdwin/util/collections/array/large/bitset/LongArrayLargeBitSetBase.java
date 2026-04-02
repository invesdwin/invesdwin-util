package de.invesdwin.util.collections.array.large.bitset;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.array.large.ILargeArrayId;
import de.invesdwin.util.collections.array.large.ILongLargeArray;
import de.invesdwin.util.error.FastIndexOutOfBoundsException;
import de.invesdwin.util.math.BitSets;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

/**
 * Adapted from java.util.BitSet to use a custom ILongArray storage for off-heap and memory memory storage.
 */
@NotThreadSafe
public class LongArrayLargeBitSetBase implements ILargeArrayId {
    private static final int MAX_INITIAL_CAPACITY = ByteBuffers.MAX_TO_STRING_COUNT;
    private static final int ADDRESS_BITS_PER_WORD = BitSets.ADDRESS_BITS_PER_WORD;
    private static final int BITS_PER_WORD = BitSets.BITS_PER_WORD;

    /* Used to shift left or right for a partial word mask */
    private static final long WORD_MASK = BitSets.WORD_MASK;

    private final ILongLargeArray words;
    private final long size;

    //    public LongArrayLargeBitSetBase(final long size) {
    //        // nbits can't be negative; size 0 is OK
    //        if (size < 0) {
    //            throw new NegativeArraySizeException("nbits < 0: " + size);
    //        }
    //
    //        words = ILongLargeArray.newInstance(wordIndex(size - 1) + 1);
    //        this.size = size;
    //    }

    public LongArrayLargeBitSetBase(final ILongLargeArray words, final long size) {
        this.words = words;
        this.size = size;
    }

    @Override
    public int getId() {
        return words.getId();
    }

    public ILongLargeArray getWords() {
        return words;
    }

    public static long wordIndex(final long bitIndex) {
        return bitIndex >> ADDRESS_BITS_PER_WORD;
    }

    private void ensureCapacity(final long wordsRequired) {
        if (words.size() < wordsRequired) {
            throw FastIndexOutOfBoundsException.getInstance("wordsRequired=%s is beyond wordsSize=%s", wordsRequired,
                    words.size());
        }
    }

    private static void checkRange(final long fromIndex, final long toIndex) {
        if (fromIndex < 0) {
            throw FastIndexOutOfBoundsException.getInstance("fromIndex < 0: %s", fromIndex);
        }
        if (toIndex < 0) {
            throw FastIndexOutOfBoundsException.getInstance("toIndex < 0: %s", toIndex);
        }
        if (fromIndex > toIndex) {
            throw FastIndexOutOfBoundsException.getInstance("fromIndex: %s > toIndex: %s", fromIndex, toIndex);
        }
    }

    public void flip(final long bitIndex) {
        if (bitIndex < 0) {
            throw FastIndexOutOfBoundsException.getInstance("bitIndex < 0: %s", bitIndex);
        }

        final long wordIndex = wordIndex(bitIndex);
        ensureCapacity(wordIndex);

        words.set(wordIndex, words.get(wordIndex) ^ (1L << bitIndex));
    }

    public void flip(final long fromIndex, final long toIndex) {
        checkRange(fromIndex, toIndex);

        if (fromIndex == toIndex) {
            return;
        }

        final long startWordIndex = wordIndex(fromIndex);
        final long endWordIndex = wordIndex(toIndex - 1);
        ensureCapacity(endWordIndex);

        final long firstWordMask = WORD_MASK << fromIndex;
        final long lastWordMask = WORD_MASK >>> -toIndex;
        if (startWordIndex == endWordIndex) {
            // Case 1: One word
            words.set(startWordIndex, words.get(startWordIndex) ^ (firstWordMask & lastWordMask));
        } else {
            // Case 2: Multiple words
            // Handle first word
            words.set(startWordIndex, words.get(startWordIndex) ^ firstWordMask);

            // Handle intermediate words, if any
            for (long i = startWordIndex + 1; i < endWordIndex; i++) {
                words.set(i, words.get(i) ^ WORD_MASK);
            }

            // Handle last word
            words.set(endWordIndex, words.get(endWordIndex) ^ lastWordMask);
        }
    }

    public void set(final long bitIndex) {
        if (bitIndex < 0) {
            throw FastIndexOutOfBoundsException.getInstance("bitIndex < 0: %s", bitIndex);
        }

        final long wordIndex = wordIndex(bitIndex);
        ensureCapacity(wordIndex);

        words.set(wordIndex, words.get(wordIndex) | (1L << bitIndex)); // Restores invariants
    }

    public void set(final long bitIndex, final boolean value) {
        if (value) {
            set(bitIndex);
        } else {
            clear(bitIndex);
        }
    }

    public void set(final long fromIndex, final long toIndex) {
        checkRange(fromIndex, toIndex);

        if (fromIndex == toIndex) {
            return;
        }

        // Increase capacity if necessary
        final long startWordIndex = wordIndex(fromIndex);
        final long endWordIndex = wordIndex(toIndex - 1);
        ensureCapacity(endWordIndex);

        final long firstWordMask = WORD_MASK << fromIndex;
        final long lastWordMask = WORD_MASK >>> -toIndex;
        if (startWordIndex == endWordIndex) {
            // Case 1: One word
            words.set(startWordIndex, words.get(startWordIndex) | (firstWordMask & lastWordMask));
        } else {
            // Case 2: Multiple words
            // Handle first word
            words.set(startWordIndex, words.get(startWordIndex) | firstWordMask);

            // Handle intermediate words, if any
            for (long i = startWordIndex + 1; i < endWordIndex; i++) {
                words.set(i, WORD_MASK);
            }

            // Handle last word (restores invariants)
            words.set(endWordIndex, words.get(endWordIndex) | lastWordMask);
        }
    }

    public void set(final long fromIndex, final long toIndex, final boolean value) {
        if (value) {
            set(fromIndex, toIndex);
        } else {
            clear(fromIndex, toIndex);
        }
    }

    public void clear(final long bitIndex) {
        if (bitIndex < 0) {
            throw FastIndexOutOfBoundsException.getInstance("bitIndex < 0: %s", bitIndex);
        }

        final long wordIndex = wordIndex(bitIndex);
        if (wordIndex >= words.size()) {
            return;
        }

        words.set(wordIndex, words.get(wordIndex) & ~(1L << bitIndex));
    }

    //CHECKSTYLE:OFF final param
    public void clear(final long fromIndex, long toIndex) {
        //CHECKSTYLE:ON
        checkRange(fromIndex, toIndex);

        if (fromIndex == toIndex) {
            return;
        }

        final long startWordIndex = wordIndex(fromIndex);
        if (startWordIndex >= words.size()) {
            return;
        }

        long endWordIndex = wordIndex(toIndex - 1);
        if (endWordIndex >= words.size()) {
            //CHECKSTYLE:OFF final param
            toIndex = length();
            //CHECKSTYLE:ON
            endWordIndex = words.size() - 1;
        }

        final long firstWordMask = WORD_MASK << fromIndex;
        final long lastWordMask = WORD_MASK >>> -toIndex;
        if (startWordIndex == endWordIndex) {
            // Case 1: One word
            words.set(startWordIndex, words.get(startWordIndex) & ~(firstWordMask & lastWordMask));
        } else {
            // Case 2: Multiple words
            // Handle first word
            words.set(startWordIndex, words.get(startWordIndex) & ~firstWordMask);

            // Handle intermediate words, if any
            for (long i = startWordIndex + 1; i < endWordIndex; i++) {
                words.set(i, 0);
            }

            // Handle last word
            words.set(endWordIndex, words.get(endWordIndex) & ~lastWordMask);
        }
    }

    public void clear() {
        words.clear();
    }

    public boolean get(final long bitIndex) {
        if (bitIndex < 0) {
            throw FastIndexOutOfBoundsException.getInstance("bitIndex < 0: %s", bitIndex);
        }

        final long wordIndex = wordIndex(bitIndex);
        return (wordIndex < words.size()) && ((words.get(wordIndex) & (1L << bitIndex)) != 0);
    }

    public long nextSetBit(final long fromIndex) {
        if (fromIndex < 0) {
            throw FastIndexOutOfBoundsException.getInstance("fromIndex < 0: %s", fromIndex);
        }

        long u = wordIndex(fromIndex);
        if (u >= words.size()) {
            return -1;
        }

        long word = words.get(u) & (WORD_MASK << fromIndex);

        while (true) {
            if (word != 0) {
                return (u * BITS_PER_WORD) + Long.numberOfTrailingZeros(word);
            }
            if (++u == words.size()) {
                return -1;
            }
            word = words.get(u);
        }
    }

    public long nextClearBit(final long fromIndex) {
        // Neither spec nor implementation handle bitsets of maximal length.
        // See 4816253.
        if (fromIndex < 0) {
            throw FastIndexOutOfBoundsException.getInstance("fromIndex < 0: %s", fromIndex);
        }

        long u = wordIndex(fromIndex);
        if (u >= words.size()) {
            return fromIndex;
        }

        long word = ~words.get(u) & (WORD_MASK << fromIndex);

        while (true) {
            if (word != 0) {
                return (u * BITS_PER_WORD) + Long.numberOfTrailingZeros(word);
            }
            if (++u == words.size()) {
                return words.size() * BITS_PER_WORD;
            }
            word = ~words.get(u);
        }
    }

    public long previousSetBit(final long fromIndex) {
        if (fromIndex < 0) {
            if (fromIndex == -1) {
                return -1;
            }
            throw FastIndexOutOfBoundsException.getInstance("fromIndex < -1: %s", fromIndex);
        }

        long u = wordIndex(fromIndex);
        if (u >= words.size()) {
            return length() - 1;
        }

        long word = words.get(u) & (WORD_MASK >>> -(fromIndex + 1));

        while (true) {
            if (word != 0) {
                return (u + 1) * BITS_PER_WORD - 1 - Long.numberOfLeadingZeros(word);
            }
            if (u-- == 0) {
                return -1;
            }
            word = words.get(u);
        }
    }

    public long previousClearBit(final long fromIndex) {
        if (fromIndex < 0) {
            if (fromIndex == -1) {
                return -1;
            }
            throw FastIndexOutOfBoundsException.getInstance("fromIndex < -1: %s", fromIndex);
        }

        long u = wordIndex(fromIndex);
        if (u >= words.size()) {
            return fromIndex;
        }

        long word = ~words.get(u) & (WORD_MASK >>> -(fromIndex + 1));

        while (true) {
            if (word != 0) {
                return (u + 1) * BITS_PER_WORD - 1 - Long.numberOfLeadingZeros(word);
            }
            if (u-- == 0) {
                return -1;
            }
            word = ~words.get(u);
        }
    }

    public long length() {
        if (words.size() == 0) {
            return 0;
        }

        return BITS_PER_WORD * (words.size() - 1)
                + (BITS_PER_WORD - Long.numberOfLeadingZeros(words.get(words.size() - 1)));
    }

    public boolean isEmpty() {
        return words.size() == 0;
    }

    public boolean intersects(final LongArrayLargeBitSetBase set) {
        for (long i = Math.min(words.size(), set.words.size()) - 1; i >= 0; i--) {
            if ((words.get(i) & set.words.get(i)) != 0) {
                return true;
            }
        }
        return false;
    }

    public long cardinality() {
        long sum = 0;
        for (long i = 0; i < words.size(); i++) {
            sum += Long.bitCount(words.get(i));
        }
        return sum;
    }

    public void and(final LongArrayLargeBitSetBase set) {
        if (this == set) {
            return;
        }

        long wordsInUse = words.size();
        while (wordsInUse > set.words.size()) {
            words.set(--wordsInUse, 0);
        }

        // Perform logical AND on words in common
        for (long i = 0; i < words.size(); i++) {
            words.set(i, words.get(i) & set.words.get(i));
        }
    }

    public void or(final LongArrayLargeBitSetBase set) {
        if (this == set) {
            return;
        }

        final long wordsInCommon = Math.min(words.size(), set.words.size());

        if (words.size() < set.words.size()) {
            ensureCapacity(set.words.size());
        }

        // Perform logical OR on words in common
        for (long i = 0; i < wordsInCommon; i++) {
            words.set(i, words.get(i) | set.words.get(i));
        }

        // Copy any remaining words
        if (wordsInCommon < set.words.size()) {
            set.words.getLongs(wordsInCommon, words, wordsInCommon, words.size() - wordsInCommon);
        }
    }

    public void xor(final LongArrayLargeBitSetBase set) {
        final long wordsInCommon = Math.min(words.size(), set.words.size());

        if (words.size() < set.words.size()) {
            ensureCapacity(set.words.size());
        }

        // Perform logical XOR on words in common
        for (long i = 0; i < wordsInCommon; i++) {
            words.set(i, words.get(i) ^ set.words.get(i));
        }

        // Copy any remaining words
        if (wordsInCommon < set.words.size()) {
            set.words.getLongs(wordsInCommon, words, wordsInCommon, set.words.size() - wordsInCommon);
        }
    }

    public void andNot(final LongArrayLargeBitSetBase set) {
        // Perform logical (a & !b) on words in common
        for (long i = Math.min(words.size(), set.words.size()) - 1; i >= 0; i--) {
            words.set(i, words.get(i) & ~set.words.get(i));
        }
    }

    @Override
    public int hashCode() {
        long h = 1234;
        for (long i = words.size(); --i >= 0;) {
            h ^= words.get(i) * (i + 1);
        }

        return (int) ((h >> 32) ^ h);
    }

    public long size() {
        return size;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LongArrayLargeBitSetBase)) {
            return false;
        }
        final LongArrayLargeBitSetBase set = (LongArrayLargeBitSetBase) obj;

        if (words.size() != set.words.size()) {
            return false;
        }

        // Check words in use by both BitSets
        for (long i = 0; i < words.size(); i++) {
            if (words.get(i) != set.words.get(i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        final int numBits = ByteBuffers
                .checkedCastNoOverflow((words.size() > 128) ? cardinality() : words.size() * BITS_PER_WORD);
        // Avoid overflow in the case of a humongous numBits
        final int initialCapacity = (numBits <= (MAX_INITIAL_CAPACITY - 2) / 6) ? 6 * numBits + 2
                : MAX_INITIAL_CAPACITY;
        final StringBuilder b = new StringBuilder(initialCapacity);
        b.append('{');

        int count = 0;
        long i = nextSetBit(0);
        if (i != -1) {
            b.append(i);
            count++;
            OUTER: while (true) {
                if (++i < 0) {
                    break;
                }
                //CHECKSTYLE:OFF inner assign
                if ((i = nextSetBit(i)) < 0) {
                    //CHECKSTYLE:ON
                    break;
                }
                final long endOfRun = nextClearBit(i);
                do {
                    if (count >= ByteBuffers.MAX_TO_STRING_COUNT) {
                        b.append(", ...");
                        break OUTER;
                    }
                    b.append(", ").append(i);
                    count++;
                } while (++i != endOfRun);
            }
        }

        b.append('}');
        return b.toString();
    }

    public LongStream stream() {
        class BitSetSpliterator implements Spliterator.OfLong {
            private long index; // current bit index for a set bit
            private long fence; // -1 until used; then one past last bit index
            private long est; // size estimate
            private boolean root; // true if root and not split
            // root == true then size estimate is accurate
            // index == -1 or index >= fence if fully traversed
            // Special case when the max bit set is Long.MAX_VALUE

            BitSetSpliterator(final long origin, final long fence, final long est, final boolean root) {
                this.index = origin;
                this.fence = fence;
                this.est = est;
                this.root = root;
            }

            private long getFence() {
                long hi;
                //CHECKSTYLE:OFF inner assign
                if ((hi = fence) < 0) {
                    // Round up fence to maximum cardinality for allocated words
                    // This is sufficient and cheap for sequential access
                    // When splitting this value is lowered
                    hi = fence = (words.size() >= wordIndex(Long.MAX_VALUE)) ? Long.MAX_VALUE
                            : words.size() << ADDRESS_BITS_PER_WORD;
                    //CHECKSTYLE:ON
                    est = cardinality();
                    index = nextSetBit(0);
                }
                return hi;
            }

            @Override
            public boolean tryAdvance(final LongConsumer action) {
                Assertions.checkNotNull(action);

                final long hi = getFence();
                final long i = index;
                if (i < 0 || i >= hi) {
                    // Check if there is a final bit set for Long.MAX_VALUE
                    if (i == Long.MAX_VALUE && hi == Long.MAX_VALUE) {
                        index = -1;
                        action.accept(Long.MAX_VALUE);
                        return true;
                    }
                    return false;
                }

                index = nextSetBit(i + 1, wordIndex(hi - 1));
                action.accept(i);
                return true;
            }

            @Override
            public void forEachRemaining(final LongConsumer action) {
                Assertions.checkNotNull(action);

                final long hi = getFence();
                long i = index;
                index = -1;

                if (i >= 0 && i < hi) {
                    action.accept(i++);

                    long u = wordIndex(i); // next lower word bound
                    final long v = wordIndex(hi - 1); // upper word bound

                    //CHECKSTYLE:OFF for loop var
                    words_loop: for (; u <= v && i <= hi; u++, i = u << ADDRESS_BITS_PER_WORD) {
                        //CHECKSTYLE:ON
                        long word = words.get(u) & (WORD_MASK << i);
                        while (word != 0) {
                            i = (u << ADDRESS_BITS_PER_WORD) + Long.numberOfTrailingZeros(word);
                            if (i >= hi) {
                                // Break out of outer loop to ensure check of
                                // Long.MAX_VALUE bit set
                                break words_loop;
                            }

                            // Flip the set bit
                            word &= ~(1L << i);

                            action.accept(i);
                        }
                    }
                }

                // Check if there is a final bit set for Long.MAX_VALUE
                if (i == Long.MAX_VALUE && hi == Long.MAX_VALUE) {
                    action.accept(Long.MAX_VALUE);
                }
            }

            @Override
            public OfLong trySplit() {
                long hi = getFence();
                final long lo = index;
                if (lo < 0) {
                    return null;
                }

                // Lower the fence to be the upper bound of last bit set
                // The index is the first bit set, thus this spliterator
                // covers one bit and cannot be split, or two or more
                // bits
                //CHECKSTYLE:OFF inner assign
                hi = fence = (hi < Long.MAX_VALUE || !get(Long.MAX_VALUE)) ? previousSetBit(hi - 1) + 1
                        : Long.MAX_VALUE;
                //CHECKSTYLE:ON

                // Find the mid point
                final long mid = (lo + hi) >>> 1;
                if (lo >= mid) {
                    return null;
                }

                // Raise the index of this spliterator to be the next set bit
                // from the mid point
                index = nextSetBit(mid, wordIndex(hi - 1));
                root = false;

                // Don't lower the fence (mid point) of the returned spliterator,
                // traversal or further splitting will do that work
                //CHECKSTYLE:OFF inner assign
                return new BitSetSpliterator(lo, mid, est >>>= 1, false);
                //CHECKSTYLE:ON
            }

            @Override
            public long estimateSize() {
                getFence(); // force init
                return est;
            }

            @Override
            public int characteristics() {
                // Only sized when root and not split
                return (root ? Spliterator.SIZED : 0) | Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.SORTED;
            }

            @Override
            public Comparator<? super Long> getComparator() {
                return null;
            }
        }
        return StreamSupport.longStream(new BitSetSpliterator(0, -1, 0, true), false);
    }

    private long nextSetBit(final long fromIndex, final long toWordIndex) {
        long u = wordIndex(fromIndex);
        // Check if out of bounds
        if (u > toWordIndex) {
            return -1;
        }

        long word = words.get(u) & (WORD_MASK << fromIndex);

        while (true) {
            if (word != 0) {
                return (u * BITS_PER_WORD) + Long.numberOfTrailingZeros(word);
            }
            // Check if out of bounds
            if (++u > toWordIndex) {
                return -1;
            }
            word = words.get(u);
        }
    }

    /************************** EXTENSIONS ***************************/

    public void andRange(final LongArrayLargeBitSetBase other, final long fromInclusive, final long toExclusive) {
        //        public void and(BitSet set) {
        //            if (this == set)
        //                return;
        if (this == other) {
            return;
        }
        //
        try {
            final long otherWordsInUse = other.words.size();
            final ILongLargeArray otherWords = other.words;
            //            while (wordsInUse > set.wordsInUse)
            //                words[--wordsInUse] = 0;
            final long toWordExclusive = Longs.min(words.size(), wordIndex(toExclusive) + 1, otherWordsInUse);

            final long fromWord = wordIndex(fromInclusive);
            //
            //            // Perform logical AND on words in common
            //            for (long i = 0; i < wordsInUse; i++)
            //                words[i] &= set.words[i];

            for (long i = fromWord; i < toWordExclusive; i++) {
                words.set(i, words.get(i) & otherWords.get(i));
            }
            //
            //        }
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void orRange(final LongArrayLargeBitSetBase other, final long fromInclusive, final long toExclusive) {
        //        public void or(BitSet set) {
        //            if (this == set)
        //                return;
        if (this == other) {
            return;
        }
        try {
            final long otherWordsInUse = other.words.size();
            final ILongLargeArray otherWords = other.words;
            //        long wordsInCommon = Math.min(wordsInUse, set.wordsInUse);
            final long wordsInCommon = Math.min(words.size(), otherWordsInUse);

            //        if (wordsInUse < set.wordsInUse) {
            //            ensureCapacity(set.wordsInUse);
            //            wordsInUse = set.wordsInUse;
            //        }
            if (words.size() < otherWordsInUse) {
                ensureCapacity(otherWordsInUse);
            }
            // Perform logical OR on words in common
            //        for (long i = 0; i < wordsInCommon; i++)
            //            words[i] |= set.words[i];
            final long fromWord = wordIndex(fromInclusive);
            final long toWordInCommonExclusive = Longs.min(wordIndex(toExclusive) + 1, wordsInCommon);
            for (long i = fromWord; i < toWordInCommonExclusive; i++) {
                words.set(i, words.get(i) | otherWords.get(i));
            }

            final long toWordExclusive = Longs.min(wordIndex(toExclusive) + 1,
                    Longs.max(words.size(), otherWordsInUse));
            // Copy any remaining words
            //        if (wordsInCommon < set.wordsInUse)
            //            System.arraycopy(set.words, wordsInCommon,
            //                             words, wordsInCommon,
            //                             wordsInUse - wordsInCommon);
            if (wordsInCommon < toWordExclusive) {
                otherWords.getLongs(wordsInCommon, otherWords, wordsInCommon, words.size() - toWordExclusive);
            }
            //skipping that method

        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
