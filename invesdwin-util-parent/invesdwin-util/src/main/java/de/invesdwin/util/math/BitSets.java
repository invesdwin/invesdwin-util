package de.invesdwin.util.math;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.LongArrayLargeBitSetBase;
import de.invesdwin.util.collections.array.large.bitset.skippingindex.ISkippingLargeIndexProvider;
import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.LongArrayPrimitiveBitSetBase;
import de.invesdwin.util.collections.array.primitive.bitset.skippingindex.ISkippingPrimitiveIndexProvider;
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
    public static final int WORD_MASK;

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

            final Field bitSetWordMaskField = BitSet.class.getDeclaredField("WORD_MASK");
            final long bitSetWordMaskFieldOffset = Reflections.getUnsafe().staticFieldOffset(bitSetWordMaskField);
            WORD_MASK = Reflections.getUnsafe().getInt(BitSet.class, bitSetWordMaskFieldOffset);

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

    public static IPrimitiveBitSet toArray(final Collection<Boolean> vector) {
        if (vector == null) {
            return null;
        }
        return checkedCastVector(vector);
    }

    public static IPrimitiveBitSet toArrayVector(final Collection<Boolean> vector) {
        return toArray(vector);
    }

    public static IPrimitiveBitSet[] toArrayMatrix(final List<? extends List<Boolean>> matrix) {
        if (matrix == null) {
            return null;
        }
        final IPrimitiveBitSet[] arrayMatrix = new IPrimitiveBitSet[matrix.size()];
        for (int i = 0; i < matrix.size(); i++) {
            final List<Boolean> vector = matrix.get(i);
            arrayMatrix[i] = toArrayVector(vector);
        }
        return arrayMatrix;
    }

    public static List<IPrimitiveBitSet> asListMatrix(final IPrimitiveBitSet[] matrix) {
        if (matrix == null) {
            return null;
        }
        final List<IPrimitiveBitSet> matrixAsList = new ArrayList<IPrimitiveBitSet>(matrix.length);
        for (final IPrimitiveBitSet vector : matrix) {
            matrixAsList.add(vector);
        }
        return matrixAsList;
    }

    public static void and(final BitSet combinedInto, final BitSet other) {
        combinedInto.and(other);
    }

    public static void andRange(final BitSet combinedInto, final BitSet other, final int fromInclusive,
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

    public static void or(final BitSet combinedInto, final BitSet other) {
        combinedInto.or(other);
    }

    public static void orRange(final BitSet combinedInto, final BitSet other, final int fromInclusive,
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
        return LongArrayPrimitiveBitSetBase.wordIndex(bitIndex);
    }

    public static long wordIndex(final long bitIndex) {
        return LongArrayLargeBitSetBase.wordIndex(bitIndex);
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

    public static void or(final IPrimitiveBitSet combinedInto, final IPrimitiveBitSet other) {
        final ISkippingPrimitiveIndexProvider skip = other.newSkippingIndexProvider();
        int cur = 0;
        while (true) {
            cur = skip.next(cur);
            if (cur == ISkippingPrimitiveIndexProvider.END) {
                break;
            }
            combinedInto.add(cur);
            cur++;
            if (cur >= combinedInto.size()) {
                break;
            }
        }
    }

    public static void orRange(final IPrimitiveBitSet combinedInto, final IPrimitiveBitSet other,
            final int fromInclusive, final int toExclusive) {
        final ISkippingPrimitiveIndexProvider skip = other.newSkippingIndexProvider();
        int cur = fromInclusive;
        while (true) {
            cur = skip.next(cur);
            if (cur == ISkippingPrimitiveIndexProvider.END) {
                break;
            }
            combinedInto.add(cur);
            cur++;
            if (cur >= toExclusive) {
                break;
            }
        }
    }

    public static void and(final IPrimitiveBitSet combinedInto, final IPrimitiveBitSet other) {
        final ISkippingPrimitiveIndexProvider skipCombined = combinedInto.newSkippingIndexProvider();
        final ISkippingPrimitiveIndexProvider skipOther = other.newSkippingIndexProvider();
        int curCombined = skipCombined.next(0);
        int curOther = skipOther.next(0);

        while (curCombined != ISkippingPrimitiveIndexProvider.END && curOther != ISkippingPrimitiveIndexProvider.END) {
            if (curCombined < curOther) {
                combinedInto.clear(curCombined, curOther - curCombined);
                curCombined = skipCombined.next(curCombined + 1);
            } else if (curCombined > curOther) {
                curOther = skipOther.next(curOther + 1);
            } else {
                curCombined = skipCombined.next(curCombined + 1);
                curOther = skipOther.next(curOther + 1);
            }
        }

        if (curCombined != ISkippingPrimitiveIndexProvider.END) {
            combinedInto.clear(curCombined, combinedInto.size() - curCombined);
        }
    }

    public static void andRange(final IPrimitiveBitSet combinedInto, final IPrimitiveBitSet other,
            final int fromInclusive, final int toExclusive) {
        final ISkippingPrimitiveIndexProvider skipCombined = combinedInto.newSkippingIndexProvider();
        final ISkippingPrimitiveIndexProvider skipOther = other.newSkippingIndexProvider();
        int curCombined = skipCombined.next(fromInclusive);
        int curOther = skipOther.next(fromInclusive);

        while (curCombined != ISkippingPrimitiveIndexProvider.END && curOther != ISkippingPrimitiveIndexProvider.END
                && curCombined < toExclusive && curOther < toExclusive) {
            if (curCombined < curOther) {
                final int clearEnd = Math.min(curOther, toExclusive);
                combinedInto.clear(curCombined, clearEnd - curCombined);
                curCombined = skipCombined.next(clearEnd);
            } else if (curCombined > curOther) {
                curOther = skipOther.next(curOther + 1);
            } else {
                curCombined = skipCombined.next(curCombined + 1);
                curOther = skipOther.next(curOther + 1);
            }
        }

        if (curCombined != ISkippingPrimitiveIndexProvider.END && curCombined < toExclusive) {
            combinedInto.clear(curCombined, toExclusive - curCombined);
        }
    }

    public static void or(final ILargeBitSet combinedInto, final ILargeBitSet other) {
        final ISkippingLargeIndexProvider skip = other.newSkippingIndexProvider();
        long cur = 0;
        while (true) {
            cur = skip.next(cur);
            if (cur == ISkippingLargeIndexProvider.END) {
                break;
            }
            combinedInto.add(cur);
            cur++;
            if (cur >= combinedInto.size()) {
                break;
            }
        }
    }

    public static void orRange(final ILargeBitSet combinedInto, final ILargeBitSet other, final long fromInclusive,
            final long toExclusive) {
        final ISkippingLargeIndexProvider skip = other.newSkippingIndexProvider();
        long cur = fromInclusive;
        while (true) {
            cur = skip.next(cur);
            if (cur == ISkippingLargeIndexProvider.END) {
                break;
            }
            combinedInto.add(cur);
            cur++;
            if (cur >= toExclusive) {
                break;
            }
        }
    }

    public static void and(final ILargeBitSet combinedInto, final ILargeBitSet other) {
        final ISkippingLargeIndexProvider skipCombined = combinedInto.newSkippingIndexProvider();
        final ISkippingLargeIndexProvider skipOther = other.newSkippingIndexProvider();
        long curCombined = skipCombined.next(0);
        long curOther = skipOther.next(0);

        while (curCombined != ISkippingLargeIndexProvider.END && curOther != ISkippingLargeIndexProvider.END) {
            if (curCombined < curOther) {
                combinedInto.clear(curCombined, curOther - curCombined);
                curCombined = skipCombined.next(curCombined + 1);
            } else if (curCombined > curOther) {
                curOther = skipOther.next(curOther + 1);
            } else {
                curCombined = skipCombined.next(curCombined + 1);
                curOther = skipOther.next(curOther + 1);
            }
        }

        if (curCombined != ISkippingLargeIndexProvider.END) {
            combinedInto.clear(curCombined, combinedInto.size() - curCombined);
        }
    }

    public static void andRange(final ILargeBitSet combinedInto, final ILargeBitSet other, final long fromInclusive,
            final long toExclusive) {
        final ISkippingLargeIndexProvider skipCombined = combinedInto.newSkippingIndexProvider();
        final ISkippingLargeIndexProvider skipOther = other.newSkippingIndexProvider();
        long curCombined = skipCombined.next(fromInclusive);
        long curOther = skipOther.next(fromInclusive);

        while (curCombined != ISkippingLargeIndexProvider.END && curOther != ISkippingLargeIndexProvider.END
                && curCombined < toExclusive && curOther < toExclusive) {
            if (curCombined < curOther) {
                final long clearEnd = Math.min(curOther, toExclusive);
                combinedInto.clear(curCombined, clearEnd - curCombined);
                curCombined = skipCombined.next(clearEnd);
            } else if (curCombined > curOther) {
                curOther = skipOther.next(curOther + 1);
            } else {
                curCombined = skipCombined.next(curCombined + 1);
                curOther = skipOther.next(curOther + 1);
            }
        }

        if (curCombined != ISkippingLargeIndexProvider.END && curCombined < toExclusive) {
            combinedInto.clear(curCombined, toExclusive - curCombined);
        }
    }

    public static void getBooleans(final IPrimitiveBitSet src, final int srcPos, final IPrimitiveBitSet dest,
            final int destPos, final int length) {
        final int srcEnd = srcPos + length;
        int currentSrc = srcPos;
        int currentDest = destPos;

        // Use skipping index provider for efficient iteration
        final ISkippingPrimitiveIndexProvider skipProvider = src.newSkippingIndexProvider();
        int nextSetBit = skipProvider.next(srcPos);

        while (currentSrc < srcEnd) {
            if (nextSetBit == currentSrc) {
                // Set bit
                dest.add(currentDest);
                nextSetBit = skipProvider.next(currentSrc + 1);
                currentSrc++;
                currentDest++;
            } else {
                // Clear consecutive unset bits in bulk
                final int clearStart = currentDest;
                int clearEnd = currentDest;

                // Find the next set bit or end of range
                while (currentSrc < srcEnd && nextSetBit != currentSrc) {
                    clearEnd++;
                    currentSrc++;
                    currentDest++;
                    if (nextSetBit != ISkippingPrimitiveIndexProvider.END && currentSrc < srcEnd) {
                        nextSetBit = skipProvider.next(currentSrc);
                    }
                }

                // Bulk clear the range of unset bits
                dest.clear(clearStart, clearEnd - clearStart);
            }
        }
    }

    public static void getBooleans(final ILargeBitSet src, final long srcPos, final ILargeBitSet dest,
            final long destPos, final long length) {
        final long srcEnd = srcPos + length;
        long currentSrc = srcPos;
        long currentDest = destPos;

        // Use skipping index provider for efficient iteration
        final ISkippingLargeIndexProvider skipProvider = src.newSkippingIndexProvider();
        long nextSetBit = skipProvider.next(srcPos);

        while (currentSrc < srcEnd) {
            if (nextSetBit == currentSrc) {
                // Set bit
                dest.add(currentDest);
                nextSetBit = skipProvider.next(currentSrc + 1);
                currentSrc++;
                currentDest++;
            } else {
                // Clear consecutive unset bits in bulk
                final long clearStart = currentDest;
                long clearEnd = currentDest;

                // Find the next set bit or end of range
                while (currentSrc < srcEnd && nextSetBit != currentSrc) {
                    clearEnd++;
                    currentSrc++;
                    currentDest++;
                    if (nextSetBit != ISkippingLargeIndexProvider.END && currentSrc < srcEnd) {
                        nextSetBit = skipProvider.next(currentSrc);
                    }
                }

                // Bulk clear the range of unset bits
                dest.clear(clearStart, clearEnd - clearStart);
            }
        }
    }

}
