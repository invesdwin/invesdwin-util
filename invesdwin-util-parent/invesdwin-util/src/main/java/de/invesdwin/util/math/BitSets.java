package de.invesdwin.util.math;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.math.internal.ABitSetsStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastBitSets;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ABitSetsStaticFacade", targets = {
        CheckedCastBitSets.class })
@Immutable
public final class BitSets extends ABitSetsStaticFacade {

    public static final int ADDRESS_BITS_PER_WORD;
    public static final int BITS_PER_WORD;
    public static final int BIT_INDEX_MASK;

    private static final MethodHandle BITSET_WORDS_GETTER;
    private static final MethodHandle BITSET_WORDS_IN_USE_GETTER;
    private static final MethodHandle BITSET_WORDS_IN_USE_SETTER;
    private static final MethodHandle BITSET_RECALCULATE_WORDS_IN_USE_METHOD;

    static {
        try {
            final Field bitSetAddressBitsPerWordField = BitSet.class.getDeclaredField("ADDRESS_BITS_PER_WORD");
            Reflections.makeAccessible(bitSetAddressBitsPerWordField);
            ADDRESS_BITS_PER_WORD = bitSetAddressBitsPerWordField.getInt(null);

            final Field bitSetBitsPerWordField = BitSet.class.getDeclaredField("BITS_PER_WORD");
            Reflections.makeAccessible(bitSetBitsPerWordField);
            BITS_PER_WORD = bitSetBitsPerWordField.getInt(null);

            final Field bitSetBitIndexMaskField = BitSet.class.getDeclaredField("BIT_INDEX_MASK");
            Reflections.makeAccessible(bitSetBitIndexMaskField);
            BIT_INDEX_MASK = bitSetBitIndexMaskField.getInt(null);

            final Lookup lookup = MethodHandles.lookup();

            final Field bitSetWordsField = Reflections.findField(BitSet.class, "words");
            Reflections.makeAccessible(bitSetWordsField);
            BITSET_WORDS_GETTER = lookup.unreflectGetter(bitSetWordsField);

            final Field bitSetWordsInUseField = Reflections.findField(BitSet.class, "wordsInUse");
            Reflections.makeAccessible(bitSetWordsInUseField);
            BITSET_WORDS_IN_USE_GETTER = lookup.unreflectGetter(bitSetWordsInUseField);
            BITSET_WORDS_IN_USE_SETTER = lookup.unreflectSetter(bitSetWordsInUseField);

            final Method bitSetRecalculateWordsInUseMethod = Reflections.findMethod(BitSet.class,
                    "recalculateWordsInUse");
            Reflections.makeAccessible(bitSetRecalculateWordsInUseMethod);
            BITSET_RECALCULATE_WORDS_IN_USE_METHOD = lookup.unreflect(bitSetRecalculateWordsInUseMethod);

        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static BitSet toArray(final Collection<Boolean> vector) {
        if (vector == null) {
            return null;
        }
        return checkedCastVector(vector);
    }

    public static BitSet toArrayVector(final Collection<Boolean> vector) {
        return toArray(vector);
    }

    public static BitSet[] toArrayMatrix(final List<? extends List<Boolean>> matrix) {
        if (matrix == null) {
            return null;
        }
        final BitSet[] arrayMatrix = new BitSet[matrix.size()];
        for (int i = 0; i < matrix.size(); i++) {
            final List<Boolean> vector = matrix.get(i);
            arrayMatrix[i] = toArrayVector(vector);
        }
        return arrayMatrix;
    }

    public static List<BitSet> asListMatrix(final BitSet[] matrix) {
        if (matrix == null) {
            return null;
        }
        final List<BitSet> matrixAsList = new ArrayList<BitSet>(matrix.length);
        for (final BitSet vector : matrix) {
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
            final int thisWordsInUse = (int) BITSET_WORDS_IN_USE_GETTER.invoke(combinedInto);
            final long[] thisWords = (long[]) BITSET_WORDS_GETTER.invoke(combinedInto);
            final int otherWordsInUse = (int) BITSET_WORDS_IN_USE_GETTER.invoke(other);
            final long[] otherWords = (long[]) BITSET_WORDS_GETTER.invoke(other);
            //            while (wordsInUse > set.wordsInUse)
            //                words[--wordsInUse] = 0;
            final int toWordExclusive = Integers.min(thisWordsInUse, wordIndex(toExclusive) + 1, otherWordsInUse);

            final int fromWord = wordIndex(fromInclusive);
            BITSET_WORDS_IN_USE_SETTER.invoke(combinedInto, toWordExclusive);
            //
            //            // Perform logical AND on words in common
            //            for (int i = 0; i < wordsInUse; i++)
            //                words[i] &= set.words[i];

            for (int i = fromWord; i < toWordExclusive; i++) {
                thisWords[i] &= otherWords[i];
            }
            //
            //            recalculateWordsInUse();
            BITSET_RECALCULATE_WORDS_IN_USE_METHOD.invoke(combinedInto);
            //            checkInvariants();
            //skipping that method
            //        }
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

}
