package de.invesdwin.util.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.roaringbitmap.FastAggregation;
import org.roaringbitmap.RoaringBitmap;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.collections.iterable.collection.ArrayCloseableIterator;
import de.invesdwin.util.math.internal.ARoaringBitmapsStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastRoaringBitmaps;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ARoaringBitmapsStaticFacade", targets = {
        CheckedCastRoaringBitmaps.class })
@Immutable
public final class RoaringBitmaps extends ARoaringBitmapsStaticFacade {

    public static RoaringBitmap toArray(final Collection<Boolean> vector) {
        if (vector == null) {
            return null;
        }
        return checkedCastVector(vector);
    }

    public static RoaringBitmap toArrayVector(final Collection<Boolean> vector) {
        return toArray(vector);
    }

    public static RoaringBitmap[] toArrayMatrix(final List<? extends List<Boolean>> matrix) {
        if (matrix == null) {
            return null;
        }
        final RoaringBitmap[] arrayMatrix = new RoaringBitmap[matrix.size()];
        for (int i = 0; i < matrix.size(); i++) {
            final List<Boolean> vector = matrix.get(i);
            arrayMatrix[i] = toArrayVector(vector);
        }
        return arrayMatrix;
    }

    public static List<RoaringBitmap> asListMatrix(final RoaringBitmap[] matrix) {
        if (matrix == null) {
            return null;
        }
        final List<RoaringBitmap> matrixAsList = new ArrayList<RoaringBitmap>(matrix.length);
        for (final RoaringBitmap vector : matrix) {
            matrixAsList.add(vector);
        }
        return matrixAsList;
    }

    public static RoaringBitmap and(final RoaringBitmap[] bitmaps) {
        return FastAggregation.and(bitmaps);
    }

    public static RoaringBitmap andRange(final RoaringBitmap[] bitmaps, final int fromInclusive,
            final int toExclusive) {
        return RoaringBitmap.and(new ArrayCloseableIterator<>(bitmaps), Integer.toUnsignedLong(fromInclusive),
                Integer.toUnsignedLong(toExclusive));
    }

    public static RoaringBitmap andRange(final RoaringBitmap[] bitmaps, final long fromInclusive,
            final long toExclusive) {
        return RoaringBitmap.and(new ArrayCloseableIterator<>(bitmaps), fromInclusive, toExclusive);
    }

    public static RoaringBitmap or(final RoaringBitmap[] bitmaps) {
        return FastAggregation.or(bitmaps);
    }

    public static RoaringBitmap orRange(final RoaringBitmap[] bitmaps, final int fromInclusive, final int toExclusive) {
        return RoaringBitmap.or(new ArrayCloseableIterator<>(bitmaps), Integer.toUnsignedLong(fromInclusive),
                Integer.toUnsignedLong(toExclusive));
    }

    public static RoaringBitmap orRange(final RoaringBitmap[] bitmaps, final long fromInclusive,
            final long toExclusive) {
        return RoaringBitmap.or(new ArrayCloseableIterator<>(bitmaps), fromInclusive, toExclusive);
    }

    public static void remove(final RoaringBitmap bitmap, final int fromInclusive, final int toExclusive) {
        bitmap.remove(Integer.toUnsignedLong(fromInclusive), Integer.toUnsignedLong(toExclusive));
    }

    public static void remove(final RoaringBitmap bitmap, final long fromInclusive, final long toExclusive) {
        bitmap.remove(fromInclusive, toExclusive);
    }

    public static void flip(final RoaringBitmap bitSet, final int fromInclusive, final int toExclusive) {
        bitSet.flip(Integer.toUnsignedLong(fromInclusive), Integer.toUnsignedLong(toExclusive));
    }

    public static void flip(final RoaringBitmap bitSet, final long fromInclusive, final long toExclusive) {
        bitSet.flip(fromInclusive, toExclusive);
    }

}
