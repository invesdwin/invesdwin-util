package de.invesdwin.util.collections.bitset;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.array.ILongArray;
import de.invesdwin.util.math.Integers;

/**
 * Adapted from java.util.BitSet to use a custom ILongArray storage for off-heap and memory memory storage.
 */
@NotThreadSafe
public class LongArrayBitSetBase {
    private static final int MAX_INITIAL_CAPACITY = Integer.MAX_VALUE - 8;
    private static final int ADDRESS_BITS_PER_WORD = 6;
    private static final int BITS_PER_WORD = 1 << ADDRESS_BITS_PER_WORD;

    /* Used to shift left or right for a partial word mask */
    private static final long WORD_MASK = 0xffffffffffffffffL;

    private final ILongArray words;

    public LongArrayBitSetBase(final int nbits) {
        // nbits can't be negative; size 0 is OK
        if (nbits < 0) {
            throw new NegativeArraySizeException("nbits < 0: " + nbits);
        }

        words = ILongArray.newInstance(wordIndex(nbits - 1) + 1);
    }

    public LongArrayBitSetBase(final ILongArray words, final int expectedSize) {
        this.words = words;
    }

    public ILongArray getWords() {
        return words;
    }

    private static int wordIndex(final int bitIndex) {
        return bitIndex >> ADDRESS_BITS_PER_WORD;
    }

    private void ensureCapacity(final int wordsRequired) {
        if (words.size() < wordsRequired) {
            throw new IndexOutOfBoundsException(
                    "wordsRequired=" + wordsRequired + " is beyond wordsSize=" + words.size());
        }
    }

    private void expandTo(final int wordIndex) {
        final int wordsRequired = wordIndex + 1;
        ensureCapacity(wordsRequired);
    }

    private static void checkRange(final int fromIndex, final int toIndex) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex < 0: " + fromIndex);
        }
        if (toIndex < 0) {
            throw new IndexOutOfBoundsException("toIndex < 0: " + toIndex);
        }
        if (fromIndex > toIndex) {
            throw new IndexOutOfBoundsException("fromIndex: " + fromIndex + " > toIndex: " + toIndex);
        }
    }

    public void flip(final int bitIndex) {
        if (bitIndex < 0) {
            throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);
        }

        final int wordIndex = wordIndex(bitIndex);
        expandTo(wordIndex);

        words.set(wordIndex, words.get(wordIndex) ^ (1L << bitIndex));
    }

    public void flip(final int fromIndex, final int toIndex) {
        checkRange(fromIndex, toIndex);

        if (fromIndex == toIndex) {
            return;
        }

        final int startWordIndex = wordIndex(fromIndex);
        final int endWordIndex = wordIndex(toIndex - 1);
        expandTo(endWordIndex);

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
            for (int i = startWordIndex + 1; i < endWordIndex; i++) {
                words.set(i, words.get(i) ^ WORD_MASK);
            }

            // Handle last word
            words.set(endWordIndex, words.get(endWordIndex) ^ lastWordMask);
        }
    }

    public void set(final int bitIndex) {
        if (bitIndex < 0) {
            throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);
        }

        final int wordIndex = wordIndex(bitIndex);
        expandTo(wordIndex);

        words.set(wordIndex, words.get(wordIndex) | (1L << bitIndex)); // Restores invariants
    }

    public void set(final int bitIndex, final boolean value) {
        if (value) {
            set(bitIndex);
        } else {
            clear(bitIndex);
        }
    }

    public void set(final int fromIndex, final int toIndex) {
        checkRange(fromIndex, toIndex);

        if (fromIndex == toIndex) {
            return;
        }

        // Increase capacity if necessary
        final int startWordIndex = wordIndex(fromIndex);
        final int endWordIndex = wordIndex(toIndex - 1);
        expandTo(endWordIndex);

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
            for (int i = startWordIndex + 1; i < endWordIndex; i++) {
                words.set(i, WORD_MASK);
            }

            // Handle last word (restores invariants)
            words.set(endWordIndex, words.get(endWordIndex) | lastWordMask);
        }
    }

    public void set(final int fromIndex, final int toIndex, final boolean value) {
        if (value) {
            set(fromIndex, toIndex);
        } else {
            clear(fromIndex, toIndex);
        }
    }

    public void clear(final int bitIndex) {
        if (bitIndex < 0) {
            throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);
        }

        final int wordIndex = wordIndex(bitIndex);
        if (wordIndex >= words.size()) {
            return;
        }

        words.set(wordIndex, words.get(wordIndex) & ~(1L << bitIndex));
    }

    //CHECKSTYLE:OFF final param
    public void clear(final int fromIndex, int toIndex) {
        //CHECKSTYLE:ON
        checkRange(fromIndex, toIndex);

        if (fromIndex == toIndex) {
            return;
        }

        final int startWordIndex = wordIndex(fromIndex);
        if (startWordIndex >= words.size()) {
            return;
        }

        int endWordIndex = wordIndex(toIndex - 1);
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
            for (int i = startWordIndex + 1; i < endWordIndex; i++) {
                words.set(i, 0);
            }

            // Handle last word
            words.set(endWordIndex, words.get(endWordIndex) & ~lastWordMask);
        }
    }

    public void clear() {
        for (int i = words.size() - 1; i >= 0; i--) {
            words.set(i, 0);
        }
    }

    public boolean get(final int bitIndex) {
        if (bitIndex < 0) {
            throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);
        }

        final int wordIndex = wordIndex(bitIndex);
        return (wordIndex < words.size()) && ((words.get(wordIndex) & (1L << bitIndex)) != 0);
    }

    public int nextSetBit(final int fromIndex) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex < 0: " + fromIndex);
        }

        int u = wordIndex(fromIndex);
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

    public int nextClearBit(final int fromIndex) {
        // Neither spec nor implementation handle bitsets of maximal length.
        // See 4816253.
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex < 0: " + fromIndex);
        }

        int u = wordIndex(fromIndex);
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

    public int previousSetBit(final int fromIndex) {
        if (fromIndex < 0) {
            if (fromIndex == -1) {
                return -1;
            }
            throw new IndexOutOfBoundsException("fromIndex < -1: " + fromIndex);
        }

        int u = wordIndex(fromIndex);
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

    public int previousClearBit(final int fromIndex) {
        if (fromIndex < 0) {
            if (fromIndex == -1) {
                return -1;
            }
            throw new IndexOutOfBoundsException("fromIndex < -1: " + fromIndex);
        }

        int u = wordIndex(fromIndex);
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

    public int length() {
        if (words.size() == 0) {
            return 0;
        }

        return BITS_PER_WORD * (words.size() - 1)
                + (BITS_PER_WORD - Long.numberOfLeadingZeros(words.get(words.size() - 1)));
    }

    public boolean isEmpty() {
        return words.size() == 0;
    }

    public boolean intersects(final LongArrayBitSetBase set) {
        for (int i = Math.min(words.size(), set.words.size()) - 1; i >= 0; i--) {
            if ((words.get(i) & set.words.get(i)) != 0) {
                return true;
            }
        }
        return false;
    }

    public int cardinality() {
        int sum = 0;
        for (int i = 0; i < words.size(); i++) {
            sum += Long.bitCount(words.get(i));
        }
        return sum;
    }

    public void and(final LongArrayBitSetBase set) {
        if (this == set) {
            return;
        }

        int wordsInUse = words.size();
        while (wordsInUse > set.words.size()) {
            words.set(--wordsInUse, 0);
        }

        // Perform logical AND on words in common
        for (int i = 0; i < words.size(); i++) {
            words.set(i, words.get(i) & set.words.get(i));
        }
    }

    public void or(final LongArrayBitSetBase set) {
        if (this == set) {
            return;
        }

        final int wordsInCommon = Math.min(words.size(), set.words.size());

        if (words.size() < set.words.size()) {
            ensureCapacity(set.words.size());
        }

        // Perform logical OR on words in common
        for (int i = 0; i < wordsInCommon; i++) {
            words.set(i, words.get(i) | set.words.get(i));
        }

        // Copy any remaining words
        if (wordsInCommon < set.words.size()) {
            set.words.getLongs(wordsInCommon, words, wordsInCommon, words.size() - wordsInCommon);
        }
    }

    public void xor(final LongArrayBitSetBase set) {
        final int wordsInCommon = Math.min(words.size(), set.words.size());

        if (words.size() < set.words.size()) {
            ensureCapacity(set.words.size());
        }

        // Perform logical XOR on words in common
        for (int i = 0; i < wordsInCommon; i++) {
            words.set(i, words.get(i) ^ set.words.get(i));
        }

        // Copy any remaining words
        if (wordsInCommon < set.words.size()) {
            set.words.getLongs(wordsInCommon, words, wordsInCommon, set.words.size() - wordsInCommon);
        }
    }

    public void andNot(final LongArrayBitSetBase set) {
        // Perform logical (a & !b) on words in common
        for (int i = Math.min(words.size(), set.words.size()) - 1; i >= 0; i--) {
            words.set(i, words.get(i) & ~set.words.get(i));
        }
    }

    @Override
    public int hashCode() {
        long h = 1234;
        for (int i = words.size(); --i >= 0;) {
            h ^= words.get(i) * (i + 1);
        }

        return (int) ((h >> 32) ^ h);
    }

    public int size() {
        return words.size() * BITS_PER_WORD;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof LongArrayBitSetBase)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        final LongArrayBitSetBase set = (LongArrayBitSetBase) obj;

        if (words.size() != set.words.size()) {
            return false;
        }

        // Check words in use by both BitSets
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i) != set.words.get(i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        final int numBits = (words.size() > 128) ? cardinality() : words.size() * BITS_PER_WORD;
        // Avoid overflow in the case of a humongous numBits
        final int initialCapacity = (numBits <= (MAX_INITIAL_CAPACITY - 2) / 6) ? 6 * numBits + 2
                : MAX_INITIAL_CAPACITY;
        final StringBuilder b = new StringBuilder(initialCapacity);
        b.append('{');

        int i = nextSetBit(0);
        if (i != -1) {
            b.append(i);
            while (true) {
                if (++i < 0) {
                    break;
                }
                //CHECKSTYLE:OFF inner assign
                if ((i = nextSetBit(i)) < 0) {
                    //CHECKSTYLE:ON
                    break;
                }
                final int endOfRun = nextClearBit(i);
                do {
                    b.append(", ").append(i);
                } while (++i != endOfRun);
            }
        }

        b.append('}');
        return b.toString();
    }

    public IntStream stream() {
        class BitSetSpliterator implements Spliterator.OfInt {
            private int index; // current bit index for a set bit
            private int fence; // -1 until used; then one past last bit index
            private int est; // size estimate
            private boolean root; // true if root and not split
            // root == true then size estimate is accurate
            // index == -1 or index >= fence if fully traversed
            // Special case when the max bit set is Integer.MAX_VALUE

            BitSetSpliterator(final int origin, final int fence, final int est, final boolean root) {
                this.index = origin;
                this.fence = fence;
                this.est = est;
                this.root = root;
            }

            private int getFence() {
                int hi;
                //CHECKSTYLE:OFF inner assign
                if ((hi = fence) < 0) {
                    // Round up fence to maximum cardinality for allocated words
                    // This is sufficient and cheap for sequential access
                    // When splitting this value is lowered
                    hi = fence = (words.size() >= wordIndex(Integer.MAX_VALUE)) ? Integer.MAX_VALUE
                            : words.size() << ADDRESS_BITS_PER_WORD;
                    //CHECKSTYLE:ON
                    est = cardinality();
                    index = nextSetBit(0);
                }
                return hi;
            }

            @Override
            public boolean tryAdvance(final IntConsumer action) {
                Assertions.checkNotNull(action);

                final int hi = getFence();
                final int i = index;
                if (i < 0 || i >= hi) {
                    // Check if there is a final bit set for Integer.MAX_VALUE
                    if (i == Integer.MAX_VALUE && hi == Integer.MAX_VALUE) {
                        index = -1;
                        action.accept(Integer.MAX_VALUE);
                        return true;
                    }
                    return false;
                }

                index = nextSetBit(i + 1, wordIndex(hi - 1));
                action.accept(i);
                return true;
            }

            @Override
            public void forEachRemaining(final IntConsumer action) {
                Assertions.checkNotNull(action);

                final int hi = getFence();
                int i = index;
                index = -1;

                if (i >= 0 && i < hi) {
                    action.accept(i++);

                    int u = wordIndex(i); // next lower word bound
                    final int v = wordIndex(hi - 1); // upper word bound

                    //CHECKSTYLE:OFF for loop var
                    words_loop: for (; u <= v && i <= hi; u++, i = u << ADDRESS_BITS_PER_WORD) {
                        //CHECKSTYLE:ON
                        long word = words.get(u) & (WORD_MASK << i);
                        while (word != 0) {
                            i = (u << ADDRESS_BITS_PER_WORD) + Long.numberOfTrailingZeros(word);
                            if (i >= hi) {
                                // Break out of outer loop to ensure check of
                                // Integer.MAX_VALUE bit set
                                break words_loop;
                            }

                            // Flip the set bit
                            word &= ~(1L << i);

                            action.accept(i);
                        }
                    }
                }

                // Check if there is a final bit set for Integer.MAX_VALUE
                if (i == Integer.MAX_VALUE && hi == Integer.MAX_VALUE) {
                    action.accept(Integer.MAX_VALUE);
                }
            }

            @Override
            public OfInt trySplit() {
                int hi = getFence();
                final int lo = index;
                if (lo < 0) {
                    return null;
                }

                // Lower the fence to be the upper bound of last bit set
                // The index is the first bit set, thus this spliterator
                // covers one bit and cannot be split, or two or more
                // bits
                //CHECKSTYLE:OFF inner assign
                hi = fence = (hi < Integer.MAX_VALUE || !get(Integer.MAX_VALUE)) ? previousSetBit(hi - 1) + 1
                        : Integer.MAX_VALUE;
                //CHECKSTYLE:ON

                // Find the mid point
                final int mid = (lo + hi) >>> 1;
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
            public Comparator<? super Integer> getComparator() {
                return null;
            }
        }
        return StreamSupport.intStream(new BitSetSpliterator(0, -1, 0, true), false);
    }

    private int nextSetBit(final int fromIndex, final int toWordIndex) {
        int u = wordIndex(fromIndex);
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

    public void andRangeFast(final LongArrayBitSetBase other, final int fromInclusive, final int toExclusive) {
        //        public void and(BitSet set) {
        //            if (this == set)
        //                return;
        if (this == other) {
            return;
        }
        //
        try {
            final int otherWordsInUse = other.words.size();
            final ILongArray otherWords = other.words;
            //            while (wordsInUse > set.wordsInUse)
            //                words[--wordsInUse] = 0;
            final int toWordExclusive = Integers.min(words.size(), wordIndex(toExclusive) + 1, otherWordsInUse);

            final int fromWord = wordIndex(fromInclusive);
            //
            //            // Perform logical AND on words in common
            //            for (int i = 0; i < wordsInUse; i++)
            //                words[i] &= set.words[i];

            for (int i = fromWord; i < toWordExclusive; i++) {
                words.set(i, words.get(i) & otherWords.get(i));
            }
            //
            //        }
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void orRangeFast(final LongArrayBitSetBase other, final int fromInclusive, final int toExclusive) {
        //        public void or(BitSet set) {
        //            if (this == set)
        //                return;
        if (this == other) {
            return;
        }
        try {
            final int otherWordsInUse = other.words.size();
            final ILongArray otherWords = other.words;
            //        int wordsInCommon = Math.min(wordsInUse, set.wordsInUse);
            final int wordsInCommon = Math.min(words.size(), otherWordsInUse);

            //        if (wordsInUse < set.wordsInUse) {
            //            ensureCapacity(set.wordsInUse);
            //            wordsInUse = set.wordsInUse;
            //        }
            if (words.size() < otherWordsInUse) {
                ensureCapacity(otherWordsInUse);
            }
            // Perform logical OR on words in common
            //        for (int i = 0; i < wordsInCommon; i++)
            //            words[i] |= set.words[i];
            final int fromWord = wordIndex(fromInclusive);
            final int toWordInCommonExclusive = Integers.min(wordIndex(toExclusive) + 1, wordsInCommon);
            for (int i = fromWord; i < toWordInCommonExclusive; i++) {
                words.set(i, words.get(i) | otherWords.get(i));
            }

            final int toWordExclusive = Integers.min(wordIndex(toExclusive) + 1,
                    Integers.max(words.size(), otherWordsInUse));
            // Copy any remaining words
            //        if (wordsInCommon < set.wordsInUse)
            //            System.arraycopy(set.words, wordsInCommon,
            //                             words, wordsInCommon,
            //                             wordsInUse - wordsInCommon);
            if (wordsInCommon < toWordExclusive) {
                System.arraycopy(otherWords, wordsInCommon, words, wordsInCommon, words.size() - toWordExclusive);
            }
            //skipping that method

        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
