package de.invesdwin.util.math;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.bitset.IBitSet;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.math.internal.ABitSetsStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastBitSets;

@SuppressWarnings("restriction")
@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ABitSetsStaticFacade", targets = {
        CheckedCastBitSets.class })
@Immutable
public final class BitSets extends ABitSetsStaticFacade {

    public static final int ADDRESS_BITS_PER_WORD;
    public static final int BITS_PER_WORD;
    public static final int BIT_INDEX_MASK;

    public static final long BITSET_WORDS_OFFSET;
    public static final long BITSET_WORDS_IN_USE_OFFSET;
    public static final long BITSET_SIZE_IS_STICKY_OFFSET;

    static {
        try {
            final Field bitSetAddressBitsPerWordField = BitSet.class.getDeclaredField("ADDRESS_BITS_PER_WORD");
            final long bitSetAddressBitsPerWordFieldOffset = Reflections.getUnsafe()
                    .staticFieldOffset(bitSetAddressBitsPerWordField);
            ADDRESS_BITS_PER_WORD = Reflections.getUnsafe().getInt(BitSet.class, bitSetAddressBitsPerWordFieldOffset);

            final Field bitSetBitsPerWordField = BitSet.class.getDeclaredField("BITS_PER_WORD");
            final long bitSetBitsPerWordFieldOffset = Reflections.getUnsafe().staticFieldOffset(bitSetBitsPerWordField);
            BITS_PER_WORD = Reflections.getUnsafe().getInt(BitSet.class, bitSetBitsPerWordFieldOffset);

            final Field bitSetBitIndexMaskField = BitSet.class.getDeclaredField("BIT_INDEX_MASK");
            final long bitSetBitIndexMaskFieldOffset = Reflections.getUnsafe()
                    .staticFieldOffset(bitSetBitIndexMaskField);
            BIT_INDEX_MASK = Reflections.getUnsafe().getInt(BitSet.class, bitSetBitIndexMaskFieldOffset);

            final Field bitSetWordsField = Reflections.findField(BitSet.class, "words");
            BITSET_WORDS_OFFSET = Reflections.getUnsafe().objectFieldOffset(bitSetWordsField);

            final Field bitSetWordsInUseField = Reflections.findField(BitSet.class, "wordsInUse");
            BITSET_WORDS_IN_USE_OFFSET = Reflections.getUnsafe().objectFieldOffset(bitSetWordsInUseField);

            final Field bitSetSizeIsStickyField = Reflections.findField(BitSet.class, "sizeIsSticky");
            BITSET_SIZE_IS_STICKY_OFFSET = Reflections.getUnsafe().objectFieldOffset(bitSetSizeIsStickyField);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static IBitSet toArray(final Collection<Boolean> vector) {
        if (vector == null) {
            return null;
        }
        return checkedCastVector(vector);
    }

    public static IBitSet toArrayVector(final Collection<Boolean> vector) {
        return toArray(vector);
    }

    public static IBitSet[] toArrayMatrix(final List<? extends List<Boolean>> matrix) {
        if (matrix == null) {
            return null;
        }
        final IBitSet[] arrayMatrix = new IBitSet[matrix.size()];
        for (int i = 0; i < matrix.size(); i++) {
            final List<Boolean> vector = matrix.get(i);
            arrayMatrix[i] = toArrayVector(vector);
        }
        return arrayMatrix;
    }

    public static List<IBitSet> asListMatrix(final IBitSet[] matrix) {
        if (matrix == null) {
            return null;
        }
        final List<IBitSet> matrixAsList = new ArrayList<IBitSet>(matrix.length);
        for (final IBitSet vector : matrix) {
            matrixAsList.add(vector);
        }
        return matrixAsList;
    }

    public static void andRangeFast(final BitSet combinedInto, final BitSet other, final int fromInclusive,
            final int toExclusive) {
        //        public void and(BitSet set) {
        //            if (this == set)
        //                return;
        if (combinedInto == other) {
            return;
        }
        //
        try {
            final int thisWordsInUse = Reflections.getUnsafe().getInt(combinedInto, BITSET_WORDS_IN_USE_OFFSET);
            final long[] thisWords = (long[]) Reflections.getUnsafe().getObject(combinedInto, BITSET_WORDS_OFFSET);
            final int otherWordsInUse = Reflections.getUnsafe().getInt(other, BITSET_WORDS_IN_USE_OFFSET);
            final long[] otherWords = (long[]) Reflections.getUnsafe().getObject(other, BITSET_WORDS_OFFSET);
            //            while (wordsInUse > set.wordsInUse)
            //                words[--wordsInUse] = 0;
            final int toWordExclusive = Integers.min(thisWordsInUse, wordIndex(toExclusive) + 1, otherWordsInUse);

            final int fromWord = wordIndex(fromInclusive);
            if (toWordExclusive != thisWordsInUse) {
                Reflections.getUnsafe().putInt(combinedInto, BITSET_WORDS_IN_USE_OFFSET, toWordExclusive);
            }
            //
            //            // Perform logical AND on words in common
            //            for (int i = 0; i < wordsInUse; i++)
            //                words[i] &= set.words[i];

            for (int i = fromWord; i < toWordExclusive; i++) {
                thisWords[i] &= otherWords[i];
            }
            //
            //            recalculateWordsInUse();
            recalculateWordsInUse(combinedInto, thisWords);
            //            checkInvariants();
            //skipping that method
            //        }
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void orRangeFast(final BitSet combinedInto, final BitSet other, final int fromInclusive,
            final int toExclusive) {
        //        public void or(BitSet set) {
        //            if (this == set)
        //                return;
        if (combinedInto == other) {
            return;
        }
        try {
            int thisWordsInUse = Reflections.getUnsafe().getInt(combinedInto, BITSET_WORDS_IN_USE_OFFSET);
            final int otherWordsInUse = Reflections.getUnsafe().getInt(other, BITSET_WORDS_IN_USE_OFFSET);
            final long[] otherWords = (long[]) Reflections.getUnsafe().getObject(other, BITSET_WORDS_OFFSET);
            //        int wordsInCommon = Math.min(wordsInUse, set.wordsInUse);
            final int wordsInCommon = Math.min(thisWordsInUse, otherWordsInUse);

            //        if (wordsInUse < set.wordsInUse) {
            //            ensureCapacity(set.wordsInUse);
            //            wordsInUse = set.wordsInUse;
            //        }
            if (thisWordsInUse < otherWordsInUse) {
                ensureCapacity(combinedInto, otherWordsInUse);
                Reflections.getUnsafe().putInt(combinedInto, BITSET_WORDS_IN_USE_OFFSET, otherWordsInUse);
                thisWordsInUse = otherWordsInUse;
            }
            final long[] thisWords = (long[]) Reflections.getUnsafe().getObject(combinedInto, BITSET_WORDS_OFFSET);

            // Perform logical OR on words in common
            //        for (int i = 0; i < wordsInCommon; i++)
            //            words[i] |= set.words[i];
            final int fromWord = wordIndex(fromInclusive);
            final int toWordInCommonExclusive = Integers.min(wordIndex(toExclusive) + 1, wordsInCommon);
            for (int i = fromWord; i < toWordInCommonExclusive; i++) {
                thisWords[i] |= otherWords[i];
            }

            final int toWordExclusive = Integers.min(wordIndex(toExclusive) + 1,
                    Integers.max(thisWordsInUse, otherWordsInUse));
            // Copy any remaining words
            //        if (wordsInCommon < set.wordsInUse)
            //            System.arraycopy(set.words, wordsInCommon,
            //                             words, wordsInCommon,
            //                             wordsInUse - wordsInCommon);
            if (wordsInCommon < toWordExclusive) {
                System.arraycopy(otherWords, wordsInCommon, thisWords, wordsInCommon, thisWordsInUse - toWordExclusive);
            }
            if (toWordExclusive != thisWordsInUse) {
                Reflections.getUnsafe().putInt(combinedInto, BITSET_WORDS_IN_USE_OFFSET, toWordExclusive);
            }

            //         recalculateWordsInUse() is unnecessary
            //        checkInvariants();
            //skipping that method

        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Given a bit index, return word index containing it.
     */
    public static int wordIndex(final int bitIndex) {
        return bitIndex >> ADDRESS_BITS_PER_WORD;
    }

    private static void recalculateWordsInUse(final BitSet bitSet, final long[] words) {
        final int prevWordsInUse = Reflections.getUnsafe().getInt(bitSet, BITSET_WORDS_IN_USE_OFFSET);

        // Traverse the bitset until a used word is found
        int i;
        for (i = prevWordsInUse - 1; i >= 0; i--) {
            if (words[i] != 0) {
                break;
            }
        }

        final int newWordsInUse = i + 1; // The new logical size
        Reflections.getUnsafe().putInt(bitSet, BITSET_WORDS_IN_USE_OFFSET, newWordsInUse);
    }

    private static void ensureCapacity(final BitSet bitSet, final int wordsRequired) {
        final long[] words = (long[]) Reflections.getUnsafe().getObject(bitSet, BITSET_WORDS_OFFSET);
        if (words.length < wordsRequired) {
            // Allocate larger of doubled size or required size
            final int request = Math.max(2 * words.length, wordsRequired);
            final long[] newWords = Arrays.copyOf(words, request);
            Reflections.getUnsafe().putObject(bitSet, BITSET_WORDS_OFFSET, newWords);
            Reflections.getUnsafe().putBoolean(bitSet, BITSET_SIZE_IS_STICKY_OFFSET, false);
        }
    }

}
