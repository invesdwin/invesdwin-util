package de.invesdwin.util.math;

import javax.annotation.concurrent.Immutable;

import org.roaringbitmap.longlong.Roaring64Bitmap;

@Immutable
public final class Roaring64Bitmaps {

    private Roaring64Bitmaps() {}

    public static Roaring64Bitmap and(final Roaring64Bitmap[] bitmaps) {
        final Roaring64Bitmap answer = bitmaps[0].clone();
        for (int i = 1; i < bitmaps.length; i++) {
            answer.and(bitmaps[i]);
        }
        return answer;
    }

    public static Roaring64Bitmap andRange(final Roaring64Bitmap[] bitmaps, final long fromInclusive,
            final long toExclusive) {
        //just ignore ranges and go through the whole range, as Roaring64Bitmap does not support range-based operations
        final Roaring64Bitmap result = and(bitmaps);
        remove(result, 0, fromInclusive);
        remove(result, toExclusive, result.last());
        return result;
    }

    public static Roaring64Bitmap or(final Roaring64Bitmap[] bitmaps) {
        final Roaring64Bitmap answer = bitmaps[0].clone();
        for (int i = 1; i < bitmaps.length; i++) {
            answer.or(bitmaps[i]);
        }
        return answer;
    }

    public static Roaring64Bitmap orRange(final Roaring64Bitmap[] bitmaps, final long fromInclusive,
            final long toExclusive) {
        //just ignore ranges and go through the whole range, as Roaring64Bitmap does not support range-based operations
        final Roaring64Bitmap result = or(bitmaps);
        remove(result, 0, fromInclusive);
        remove(result, toExclusive, result.last());
        return result;
    }

    public static void remove(final Roaring64Bitmap bitmap, final long fromInclusive, final long toExclusive) {
        //just ignore ranges and go through the whole range, as Roaring64Bitmap does not support range-based operations
        for (long i = fromInclusive; i < toExclusive; i++) {
            bitmap.remove(i);
        }
    }

}
