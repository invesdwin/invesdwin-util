package de.invesdwin.util.math;

import java.util.BitSet;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;
import org.roaringbitmap.FastAggregation;
import org.roaringbitmap.RoaringBitmap;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.bitset.IBitSet;
import de.invesdwin.util.collections.bitset.ISkippingIndexProvider;
import de.invesdwin.util.collections.bitset.JavaBitSet;
import de.invesdwin.util.collections.bitset.RoaringBitSet;

@NotThreadSafe
public class BitSetsTest {

    @Test
    public void testAndRangeFast() throws Exception {
        final int size = 1000000;
        final boolean[] bool = new boolean[size];
        final boolean[] bool2 = new boolean[size];
        final boolean[] boolAnd = new boolean[size];
        final BitSet bitSet = new BitSet(size);
        final BitSet bitSet2 = new BitSet(size);
        final RoaringBitmap roaringBitmap = new RoaringBitmap();
        final RoaringBitmap roaringBitmap2 = new RoaringBitmap();
        for (int i = 0; i < size; i++) {
            final boolean value = i % 2 == 0;
            bool[i] = value;
            final boolean value2 = i % 3 == 0;
            bool2[i] = value2;
            if (value) {
                bitSet.set(i);
                roaringBitmap.add(i);
            }
            if (value2) {
                bitSet2.set(i);
                roaringBitmap2.add(i);
            }
            boolAnd[i] = value && value2;
        }
        final RoaringBitmap roaringBitmapAnd = FastAggregation.and(roaringBitmap, roaringBitmap2);
        final BitSet bitSetAnd = (BitSet) bitSet.clone();
        bitSetAnd.and(bitSet2);

        final RoaringBitmap roaringBitmapAndFast = RoaringBitmap
                .and(Arrays.asList(roaringBitmap, roaringBitmap2).iterator(), (long) 0, (long) bool.length);
        final BitSet bitSetAndFast = (BitSet) bitSet.clone();
        BitSets.andRangeFast(bitSetAndFast, bitSet2, 0, bool.length);

        //test contains with and working properly
        for (int i = 0; i < bool.length; i++) {
            Assertions.assertThat(bitSet.get(i)).as("i: " + i).isEqualTo(bool[i]);
            Assertions.assertThat(roaringBitmap.contains(i)).as("i: " + i).isEqualTo(bool[i]);
            Assertions.assertThat(bitSet2.get(i)).as("i: " + i).isEqualTo(bool2[i]);
            Assertions.assertThat(roaringBitmap2.contains(i)).as("i: " + i).isEqualTo(bool2[i]);

            Assertions.assertThat(bitSetAnd.get(i)).as("i: " + i).isEqualTo(boolAnd[i]);
            Assertions.assertThat(roaringBitmapAnd.contains(i)).as("i: " + i).isEqualTo(boolAnd[i]);

            Assertions.assertThat(bitSetAndFast.get(i)).as("i: " + i).isEqualTo(boolAnd[i]);
            Assertions.assertThat(roaringBitmapAndFast.contains(i)).as("i: " + i).isEqualTo(boolAnd[i]);
        }

        //test anRange working properly
        final int startSub = 1000;
        final int endSub = 10000;
        final RoaringBitmap roaringBitmapAndFastSub = RoaringBitmap
                .and(Arrays.asList(roaringBitmap, roaringBitmap2).iterator(), startSub, (long) endSub + 1);
        final BitSet bitSetAndFastSub = (BitSet) bitSet.clone();
        BitSets.andRangeFast(bitSetAndFastSub, bitSet2, startSub, endSub);

        final BitSet bitSetAndFastSubGet = bitSet.get(startSub, endSub);
        for (int i = 0; i < endSub - startSub; i++) {
            Assertions.assertThat(bitSetAndFastSubGet.get(i)).as("i: " + i).isEqualTo(bool[i + startSub]);
        }
        final BitSet bitSet2SubGet = bitSet2.get(startSub, endSub);
        for (int i = 0; i < endSub - startSub; i++) {
            Assertions.assertThat(bitSet2SubGet.get(i)).as("i: " + i).isEqualTo(bool2[i + startSub]);
        }
        bitSetAndFastSubGet.and(bitSet2SubGet);

        for (int i = startSub; i <= endSub; i++) {
            Assertions.assertThat(bitSetAndFastSub.get(i)).as("i: " + i).isEqualTo(boolAnd[i]);
            Assertions.assertThat(roaringBitmapAndFastSub.contains(i)).as("i: " + i).isEqualTo(boolAnd[i]);
        }
        //explicitly not accurate
        //        for (int i = 0; i < bool.length; i++) {
        //            Assertions.assertThat(roaringBitmapAndFastSub.contains(i)).as("i: " + i).isEqualTo(bitSetAndFastSub.get(i));
        //        }
        for (int i = 0; i < endSub - startSub; i++) {
            Assertions.assertThat(bitSetAndFastSubGet.get(i)).as("i: " + i).isEqualTo(boolAnd[i + startSub]);
        }
    }

    @Test
    public void testOrRangeFast() throws Exception {
        final int size = 1000000;
        final boolean[] bool = new boolean[size];
        final boolean[] bool2 = new boolean[size];
        final boolean[] boolOr = new boolean[size];
        final BitSet bitSet = new BitSet(size);
        final BitSet bitSet2 = new BitSet(size);
        final RoaringBitmap roaringBitmap = new RoaringBitmap();
        final RoaringBitmap roaringBitmap2 = new RoaringBitmap();
        for (int i = 0; i < size; i++) {
            final boolean value = i % 2 == 0;
            bool[i] = value;
            final boolean value2 = i % 3 == 0;
            bool2[i] = value2;
            if (value) {
                bitSet.set(i);
                roaringBitmap.add(i);
            }
            if (value2) {
                bitSet2.set(i);
                roaringBitmap2.add(i);
            }
            boolOr[i] = value || value2;
        }
        final RoaringBitmap roaringBitmapOr = FastAggregation.or(roaringBitmap, roaringBitmap2);
        final BitSet bitSetOr = (BitSet) bitSet.clone();
        bitSetOr.or(bitSet2);

        final RoaringBitmap roaringBitmapOrFast = RoaringBitmap
                .or(Arrays.asList(roaringBitmap, roaringBitmap2).iterator(), (long) 0, (long) bool.length);
        final BitSet bitSetOrFast = (BitSet) bitSet.clone();
        BitSets.orRangeFast(bitSetOrFast, bitSet2, 0, bool.length);

        //test contains with or working properly
        for (int i = 0; i < bool.length; i++) {
            Assertions.assertThat(bitSet.get(i)).as("i: " + i).isEqualTo(bool[i]);
            Assertions.assertThat(roaringBitmap.contains(i)).as("i: " + i).isEqualTo(bool[i]);
            Assertions.assertThat(bitSet2.get(i)).as("i: " + i).isEqualTo(bool2[i]);
            Assertions.assertThat(roaringBitmap2.contains(i)).as("i: " + i).isEqualTo(bool2[i]);

            Assertions.assertThat(bitSetOr.get(i)).as("i: " + i).isEqualTo(boolOr[i]);
            Assertions.assertThat(roaringBitmapOr.contains(i)).as("i: " + i).isEqualTo(boolOr[i]);

            Assertions.assertThat(bitSetOrFast.get(i)).as("i: " + i).isEqualTo(boolOr[i]);
            Assertions.assertThat(roaringBitmapOrFast.contains(i)).as("i: " + i).isEqualTo(boolOr[i]);
        }

        //test orRange working properly
        final int startSub = 1000;
        final int endSub = 10000;
        final RoaringBitmap roaringBitmapOrFastSub = RoaringBitmap
                .or(Arrays.asList(roaringBitmap, roaringBitmap2).iterator(), startSub, (long) endSub + 1);
        final BitSet bitSetOrFastSub = (BitSet) bitSet.clone();
        BitSets.orRangeFast(bitSetOrFastSub, bitSet2, startSub, endSub);

        final BitSet bitSetOrFastSubGet = bitSet.get(startSub, endSub);
        for (int i = 0; i < endSub - startSub; i++) {
            Assertions.assertThat(bitSetOrFastSubGet.get(i)).as("i: " + i).isEqualTo(bool[i + startSub]);
        }
        final BitSet bitSet2SubGet = bitSet2.get(startSub, endSub);
        for (int i = 0; i < endSub - startSub; i++) {
            Assertions.assertThat(bitSet2SubGet.get(i)).as("i: " + i).isEqualTo(bool2[i + startSub]);
        }
        bitSetOrFastSubGet.or(bitSet2SubGet);

        for (int i = startSub; i <= endSub; i++) {
            Assertions.assertThat(bitSetOrFastSub.get(i)).as("i: " + i).isEqualTo(boolOr[i]);
            Assertions.assertThat(roaringBitmapOrFastSub.contains(i)).as("i: " + i).isEqualTo(boolOr[i]);
        }
        //explicitly not accurate
        //        for (int i = 0; i < bool.length; i++) {
        //            Assertions.assertThat(roaringBitmapOrFastSub.contains(i)).as("i: " + i).isEqualTo(bitSetOrFastSub.get(i));
        //        }
        for (int i = 0; i < endSub - startSub; i++) {
            Assertions.assertThat(bitSetOrFastSubGet.get(i)).as("i: " + i).isEqualTo(boolOr[i + startSub]);
        }
    }

    @Test
    public void testNegate() {
        final int size = 1000000;
        final boolean[] bool = new boolean[size];
        final boolean[] boolNegated = new boolean[size];
        final JavaBitSet bitSet = new JavaBitSet(size);
        final RoaringBitSet roaringBitmap = new RoaringBitSet(size);
        for (int i = 0; i < size; i++) {
            final boolean value = i % 2 == 0;
            bool[i] = value;
            boolNegated[i] = !value;
            if (value) {
                bitSet.add(i);
                roaringBitmap.add(i);
            }
        }
        final IBitSet bitSetNegated = bitSet.negate();
        final IBitSet roaringBitmapNegated = roaringBitmap.negate();
        final IBitSet bitSetNegatedShallow = bitSet.negateShallow();
        final IBitSet roaringBitmapNegatedShallow = roaringBitmap.negateShallow();
        for (int i = 0; i < bool.length; i++) {
            Assertions.assertThat(bitSet.contains(i)).as("i: " + i).isEqualTo(bool[i]);
            Assertions.assertThat(roaringBitmap.contains(i)).as("i: " + i).isEqualTo(bool[i]);

            Assertions.assertThat(bitSetNegated.contains(i)).as("i: " + i).isEqualTo(boolNegated[i]);
            Assertions.assertThat(roaringBitmapNegated.contains(i)).as("i: " + i).isEqualTo(boolNegated[i]);

            Assertions.assertThat(bitSetNegatedShallow.contains(i)).as("i: " + i).isEqualTo(boolNegated[i]);
            Assertions.assertThat(roaringBitmapNegatedShallow.contains(i)).as("i: " + i).isEqualTo(boolNegated[i]);
        }

        final ISkippingIndexProvider bitSetSkipping = bitSet.newSkippingIndexProvider();
        final ISkippingIndexProvider roaringBitmapSkipping = roaringBitmap.newSkippingIndexProvider();

        final ISkippingIndexProvider bitSetNegatedSkipping = bitSetNegated.newSkippingIndexProvider();
        final ISkippingIndexProvider bitSetNegatedShallowSkipping = bitSetNegated.newSkippingIndexProvider();

        final ISkippingIndexProvider roaringBitmapNegatedSkipping = roaringBitmapNegated.newSkippingIndexProvider();
        final ISkippingIndexProvider roaringBitmapNegatedShallowSkipping = roaringBitmapNegatedShallow
                .newSkippingIndexProvider();
        for (int i = 0; i < bool.length; i++) {
            final int nextBitSetSkipping = bitSetSkipping.next(i);
            final int nextRoaringBitmapSkipping = roaringBitmapSkipping.next(i);
            Assertions.assertThat(nextBitSetSkipping).isEqualTo(nextRoaringBitmapSkipping);

            final int nextBitSetNegatedSkipping = bitSetNegatedSkipping.next(i);
            final int nextRoaringBitmapNegatedSkipping = roaringBitmapNegatedSkipping.next(i);
            Assertions.assertThat(nextBitSetNegatedSkipping).isEqualTo(nextRoaringBitmapNegatedSkipping);

            final int nextBitSetNegatedShallowSkipping = bitSetNegatedShallowSkipping.next(i);
            final int nextRoaringBitmapNegatedShallowSkipping = roaringBitmapNegatedShallowSkipping.next(i);
            Assertions.assertThat(nextBitSetNegatedShallowSkipping).isEqualTo(nextRoaringBitmapNegatedShallowSkipping);

            Assertions.assertThat(nextBitSetNegatedSkipping).isEqualTo(nextBitSetNegatedShallowSkipping);
            Assertions.assertThat(nextRoaringBitmapNegatedSkipping).isEqualTo(nextRoaringBitmapNegatedShallowSkipping);
        }

    }

}
