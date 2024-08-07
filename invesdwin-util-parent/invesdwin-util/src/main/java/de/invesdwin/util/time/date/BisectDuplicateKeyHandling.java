package de.invesdwin.util.time.date;

import java.util.List;
import java.util.function.Function;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.array.IGenericArray;
import de.invesdwin.util.lang.comparator.IComparator;

@Immutable
public enum BisectDuplicateKeyHandling {
    LOWEST {
        @Override
        public <T> int apply(final List<T> values, final IComparator<T> comparator, final int potentialIndex,
                final T potentialKey) {
            int adjPotentialLowIndex = potentialIndex;
            for (int i = potentialIndex - 1; i >= 0; i--) {
                final T prevPotentialLowTime = values.get(i);
                if (comparator.compareTypedNotNullSafe(prevPotentialLowTime, potentialKey) == 0) {
                    adjPotentialLowIndex = i;
                } else {
                    break;
                }
            }
            return adjPotentialLowIndex;
        }

        @Override
        public <T> int apply(final Function<T, FDate> extractKey, final List<T> values, final int potentialIndex,
                final FDate potentialKey) {
            int adjPotentialLowIndex = potentialIndex;
            for (int i = potentialIndex - 1; i >= 0; i--) {
                final FDate prevPotentialLowTime = extractKey.apply(values.get(i));
                if (prevPotentialLowTime.equalsNotNullSafe(potentialKey)) {
                    adjPotentialLowIndex = i;
                } else {
                    break;
                }
            }
            return adjPotentialLowIndex;
        }

        @Override
        public int apply(final FDate[] keys, final int potentialIndex, final FDate potentialKey) {
            int adjPotentialLowIndex = potentialIndex;
            for (int i = potentialIndex - 1; i >= 0; i--) {
                final FDate prevPotentialLowTime = keys[i];
                if (prevPotentialLowTime.equalsNotNullSafe(potentialKey)) {
                    adjPotentialLowIndex = i;
                } else {
                    break;
                }
            }
            return adjPotentialLowIndex;
        }

        @Override
        public int apply(final IGenericArray<? extends FDate> keys, final int potentialIndex,
                final FDate potentialKey) {
            int adjPotentialLowIndex = potentialIndex;
            for (int i = potentialIndex - 1; i >= 0; i--) {
                final FDate prevPotentialLowTime = keys.get(i);
                if (prevPotentialLowTime.equalsNotNullSafe(potentialKey)) {
                    adjPotentialLowIndex = i;
                } else {
                    break;
                }
            }
            return adjPotentialLowIndex;
        }
    },
    HIGHEST {
        @Override
        public <T> int apply(final List<T> values, final IComparator<T> comparator, final int potentialIndex,
                final T potentialKey) {
            int adjPotentialLowIndex = potentialIndex;
            for (int i = potentialIndex + 1; i < values.size(); i++) {
                final T prevPotentialLowTime = values.get(i);
                if (comparator.compareTypedNotNullSafe(prevPotentialLowTime, potentialKey) == 0) {
                    adjPotentialLowIndex = i;
                } else {
                    break;
                }
            }
            return adjPotentialLowIndex;
        }

        @Override
        public <T> int apply(final Function<T, FDate> extractKey, final List<T> values, final int potentialIndex,
                final FDate potentialKey) {
            int adjPotentialLowIndex = potentialIndex;
            for (int i = potentialIndex + 1; i < values.size(); i++) {
                final FDate prevPotentialLowTime = extractKey.apply(values.get(i));
                if (prevPotentialLowTime.equalsNotNullSafe(potentialKey)) {
                    adjPotentialLowIndex = i;
                } else {
                    break;
                }
            }
            return adjPotentialLowIndex;
        }

        @Override
        public int apply(final FDate[] keys, final int potentialIndex, final FDate potentialKey) {
            int adjPotentialLowIndex = potentialIndex;
            for (int i = potentialIndex + 1; i < keys.length; i++) {
                final FDate prevPotentialLowTime = keys[i];
                if (prevPotentialLowTime.equalsNotNullSafe(potentialKey)) {
                    adjPotentialLowIndex = i;
                } else {
                    break;
                }
            }
            return adjPotentialLowIndex;
        }

        @Override
        public int apply(final IGenericArray<? extends FDate> keys, final int potentialIndex,
                final FDate potentialKey) {
            int adjPotentialLowIndex = potentialIndex;
            for (int i = potentialIndex + 1; i < keys.size(); i++) {
                final FDate prevPotentialLowTime = keys.get(i);
                if (prevPotentialLowTime.equalsNotNullSafe(potentialKey)) {
                    adjPotentialLowIndex = i;
                } else {
                    break;
                }
            }
            return adjPotentialLowIndex;
        }
    },
    UNDEFINED {
        @Override
        public <T> int apply(final List<T> values, final IComparator<T> comparator, final int potentialIndex,
                final T potentialKey) {
            return potentialIndex;
        }

        @Override
        public <T> int apply(final Function<T, FDate> extractKey, final List<T> values, final int potentialIndex,
                final FDate potentialKey) {
            return potentialIndex;
        }

        @Override
        public int apply(final FDate[] keys, final int potentialIndex, final FDate potentialKey) {
            return potentialIndex;
        }

        @Override
        public int apply(final IGenericArray<? extends FDate> keys, final int potentialIndex,
                final FDate potentialKey) {
            return potentialIndex;
        }
    };

    public abstract <T> int apply(Function<T, FDate> extractKey, List<T> values, int potentialIndex,
            FDate potentialKey);

    public abstract int apply(FDate[] keys, int potentialIndex, FDate potentialKey);

    public abstract int apply(IGenericArray<? extends FDate> keys, int potentialIndex, FDate potentialKey);

    public abstract <T> int apply(List<T> values, IComparator<T> comparator, int potentialIndex, T potentialKey);
}
