package de.invesdwin.util.math;

import javax.annotation.concurrent.Immutable;

import org.roaringbitmap.longlong.Roaring64NavigableMap;

@Immutable
public final class Roaring64NavigableMaps {

    private Roaring64NavigableMaps() {}

    public static Roaring64NavigableMap and(final Roaring64NavigableMap[] bitmaps) {
        final Roaring64NavigableMap answer = bitmaps[0].clone();
        for (int i = 1; i < bitmaps.length; i++) {
            answer.and(bitmaps[i]);
        }
        return answer;
    }

    public static Roaring64NavigableMap andRange(final Roaring64NavigableMap[] bitmaps, final long fromInclusive,
            final long toExclusive) {
        //just ignore ranges and go through the whole range, as Roaring64NavigableMap does not support range-based operations
        final Roaring64NavigableMap result = and(bitmaps);
        remove(result, 0, fromInclusive);
        remove(result, toExclusive, result.last());
        return result;
    }

    public static Roaring64NavigableMap or(final Roaring64NavigableMap[] bitmaps) {
        final Roaring64NavigableMap answer = bitmaps[0].clone();
        for (int i = 1; i < bitmaps.length; i++) {
            answer.naivelazyor(bitmaps[i]);
        }
        answer.repairAfterLazy();
        return answer;
    }

    public static Roaring64NavigableMap orRange(final Roaring64NavigableMap[] bitmaps, final long fromInclusive,
            final long toExclusive) {
        //just ignore ranges and go through the whole range, as Roaring64NavigableMap does not support range-based operations
        final Roaring64NavigableMap result = or(bitmaps);
        remove(result, 0, fromInclusive);
        remove(result, toExclusive, result.last());
        return result;
    }

    public static void remove(final Roaring64NavigableMap bitmap, final long fromInclusive, final long toExclusive) {
        //just ignore ranges and go through the whole range, as Roaring64NavigableMap does not support range-based operations
        for (long i = fromInclusive; i < toExclusive; i++) {
            bitmap.removeLong(i);
        }
    }

}
