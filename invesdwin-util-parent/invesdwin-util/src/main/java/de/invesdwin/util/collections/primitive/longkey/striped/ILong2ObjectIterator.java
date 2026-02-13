package de.invesdwin.util.collections.primitive.longkey.striped;

import java.util.function.Consumer;
import java.util.function.LongConsumer;

import org.jspecify.annotations.Nullable;

import de.invesdwin.util.collections.primitive.objkey.striped.IObjectIterator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSpliterator;

/**
 * @see IObjectIterator
 * @see it.unimi.dsi.fastutil.longs.LongIterator
 * @see it.unimi.dsi.fastutil.longs.LongSpliterator
 */
public interface ILong2ObjectIterator extends IObjectIterator<Long>, LongIterator, LongSpliterator {
    @Override
    default @Nullable ILong2ObjectIterator trySplit() {
        return null;// cannot be split
    }

    @Override
    @Deprecated
    default void forEachRemaining(final Consumer<? super Long> action) {
        LongIterator.super.forEachRemaining(action);
    }

    @Override
    default void forEachRemaining(final LongConsumer action) {
        LongSpliterator.super.forEachRemaining(action);
    }

    @Override
    default void forEachRemaining(final it.unimi.dsi.fastutil.longs.LongConsumer action) {
        LongSpliterator.super.forEachRemaining((LongConsumer) action);
    }

    @Override
    default int skip(final int n) {
        return LongIterator.super.skip(n);
    }

    @Override
    default long skip(final long n) {
        return LongSpliterator.super.skip(n);
    }

    @Override
    default boolean tryAdvance(final LongConsumer action) {
        if (hasNext()) {
            action.accept(nextLong());
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Deprecated
    default boolean tryAdvance(final Consumer<? super Long> action) {
        return LongSpliterator.super.tryAdvance(action);
    }
}