package de.invesdwin.util.math;

import java.util.Arrays;
import java.util.BitSet;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;
import org.roaringbitmap.FastAggregation;
import org.roaringbitmap.RoaringBitmap;

import de.invesdwin.util.assertions.Assertions;

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
    
            final int startSub = 1000;
            final int endSub = 10000;
            final RoaringBitmap roaringBitmapAndFastSub = RoaringBitmap
                    .and(Arrays.asList(roaringBitmap, roaringBitmap2).iterator(), (long) startSub, (long) endSub);
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

}
