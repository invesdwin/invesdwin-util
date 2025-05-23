package de.invesdwin.util.collections.loadingcache.historical;

import java.util.Map.Entry;

import de.invesdwin.util.collections.iterable.ATransformingIterable;
import de.invesdwin.util.collections.iterable.ATransformingIterator;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.skip.ASkippingIterable;
import de.invesdwin.util.collections.iterable.skip.ASkippingIterator;
import de.invesdwin.util.time.date.FDate;

public interface IHistoricalEntry<V> extends Entry<FDate, V> {

    @Deprecated
    @Override
    default V setValue(final V value) {
        throw new UnsupportedOperationException();
    }

    default boolean isValuePresent() {
        return true;
    }

    default V getValueIfPresent() {
        return getValue();
    }

    static <V> V unwrapEntryValue(final IHistoricalEntry<V> entry) {
        if (entry == null) {
            return null;
        } else {
            return entry.getValue();
        }
    }

    static <V> ICloseableIterable<FDate> unwrapEntryKeys(final ICloseableIterable<IHistoricalEntry<V>> entries) {
        if (entries == null) {
            return null;
        } else {
            return new ATransformingIterable<IHistoricalEntry<V>, FDate>(entries) {
                @Override
                protected FDate transform(final IHistoricalEntry<V> value) {
                    return value.getKey();
                }
            };
        }
    }

    static <V> ICloseableIterable<V> unwrapEntryValues(final ICloseableIterable<IHistoricalEntry<V>> entries) {
        if (entries == null) {
            return null;
        } else {
            return new ATransformingIterable<IHistoricalEntry<V>, V>(entries) {
                @Override
                protected V transform(final IHistoricalEntry<V> value) {
                    return value.getValue();
                }
            };
        }
    }

    static <V> ICloseableIterator<FDate> unwrapEntryKeys(final ICloseableIterator<IHistoricalEntry<V>> entries) {
        if (entries == null) {
            return null;
        } else {
            return new ATransformingIterator<IHistoricalEntry<V>, FDate>(entries) {
                @Override
                protected FDate transform(final IHistoricalEntry<V> value) {
                    return value.getKey();
                }
            };
        }
    }

    static <V> ICloseableIterator<V> unwrapEntryValues(final ICloseableIterator<IHistoricalEntry<V>> entries) {
        if (entries == null) {
            return null;
        } else {
            return new ATransformingIterator<IHistoricalEntry<V>, V>(entries) {
                @Override
                protected V transform(final IHistoricalEntry<V> value) {
                    return value.getValue();
                }
            };
        }
    }

    static <V> FDate unwrapEntryKey(final IHistoricalEntry<V> entry) {
        if (entry == null) {
            return null;
        } else {
            return entry.getKey(); //internalAssertValue already has made sure that the entry key is the valueKey
        }
    }

    static <V> ICloseableIterator<IHistoricalEntry<V>> skipEmpty(
            final ICloseableIterator<IHistoricalEntry<V>> entries) {
        if (entries == null) {
            return null;
        } else {
            return new ASkippingIterator<IHistoricalEntry<V>>(entries) {
                @Override
                protected boolean skip(final IHistoricalEntry<V> element) {
                    //explicitly load value
                    return element.getValue() == null;
                }
            };
        }
    }

    static <V> ICloseableIterable<IHistoricalEntry<V>> skipEmpty(
            final ICloseableIterable<IHistoricalEntry<V>> entries) {
        if (entries == null) {
            return null;
        } else {
            return new ASkippingIterable<IHistoricalEntry<V>>(entries) {
                @Override
                protected boolean skip(final IHistoricalEntry<V> element) {
                    //explicitly load value
                    return element.getValue() == null;
                }
            };
        }
    }

}
