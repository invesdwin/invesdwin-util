package de.invesdwin.util.lang;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Is ascending internally
 */
@Immutable
public abstract class ADelegateComparator<E> implements Comparator<Object> {

    public static final ADelegateComparator<Object> DEFAULT_COMPARATOR = new ADelegateComparator<Object>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Object e) {
            return (Comparable<?>) e;
        }
    };

    private final Class<E> genericType;

    public ADelegateComparator() {
        this.genericType = findGenericType();
    }

    /**
     * Null never reaches this method. This is ensured internally.
     */
    protected abstract Comparable<?> getCompareCriteria(@Nonnull E e);

    private Comparable<?> getCompareCriteriaNullsafe(final E e) {
        if (e == null) {
            return null;
        } else {
            return getCompareCriteria(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public final int compare(final Object o1, final Object o2) {
        if (!genericType.isInstance(o1)) {
            return -1;
        } else if (!(genericType.isInstance(o2))) {
            return 1;
        }

        final E e1 = (E) o1;
        final E e2 = (E) o2;
        final Comparable<Object> c1 = (Comparable<Object>) getCompareCriteriaNullsafe(e1);
        final Comparable<Object> c2 = (Comparable<Object>) getCompareCriteriaNullsafe(e2);

        if (c1 == null) {
            if (isNullFirst()) {
                return 1;
            } else {
                return -1;
            }
        } else if (c2 == null) {
            if (isNullFirst()) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return compare(c1, c2);
        }
    }

    protected boolean isNullFirst() {
        return false;
    }

    protected int compare(final Comparable<Object> c1, final Comparable<Object> c2) {
        return c1.compareTo(c2);
    }

    public Comparator<Object> asDescending() {
        return new Comparator<Object>() {
            @Override
            public int compare(final Object o1, final Object o2) {
                final int compare = ADelegateComparator.this.compare(o1, o2);
                if (compare < 0) {
                    return 1;
                } else if (compare > 0) {
                    return -1;
                } else {
                    return 0;
                }
            };
        };
    }

    public <T extends E> void sort(final List<? extends T> list, final boolean ascending) {
        if (list == null || list.size() == 0) {
            return;
        }
        final Comparator<Object> comparator;
        if (ascending) {
            comparator = this;
        } else {
            comparator = asDescending();
        }

        Collections.sort(list, comparator);
    }

    /**
     * Checks all elements
     */
    public <T extends E> void assertOrder(final List<? extends T> list, final boolean ascending) {
        if (list == null || list.size() == 0) {
            return;
        }

        final Comparator<Object> comparator;
        if (ascending) {
            comparator = this;
        } else {
            comparator = asDescending();
        }

        T previousE = null;
        for (final T e : list) {
            if (previousE == null) {
                previousE = e;
            } else {
                final int compareResult = comparator.compare(e, previousE);
                if (compareResult < 0) {
                    org.assertj.core.api.Assertions.assertThat(compareResult)
                            .as("No %s order: previousE [%s], e [%s]", ascending ? "ascending" : "descending",
                                    previousE, e)
                            .isGreaterThanOrEqualTo(0);
                }
            }
        }
    }

    /**
     * Also does not allow the same element to appear twice
     */
    public <T extends E> void assertOrderStrict(final List<? extends T> list, final boolean ascending) {
        if (list == null || list.size() == 0) {
            return;
        }

        final Comparator<Object> comparator;
        if (ascending) {
            comparator = this;
        } else {
            comparator = asDescending();
        }

        T previousE = null;
        for (final T e : list) {
            if (previousE == null) {
                previousE = e;
            } else {
                final int compareResult = comparator.compare(e, previousE);
                if (compareResult <= 0) {
                    org.assertj.core.api.Assertions.assertThat(compareResult)
                            .as("No strict %s order: previousE [%s], e [%s]", ascending ? "ascending" : "descending",
                                    previousE, e)
                            .isGreaterThanOrEqualTo(0);
                }
            }
        }
    }

    /**
     * Just checks the first and last element.
     */
    public <T extends E> void assertOrderFast(final List<? extends T> list, final boolean ascending) {
        if (list == null || list.size() == 0) {
            return;
        }

        final Comparator<Object> comparator;
        if (ascending) {
            comparator = this;
        } else {
            comparator = asDescending();
        }

        final T firstE = list.get(0);
        final T lastE = list.get(list.size() - 1);
        final int compareResult = comparator.compare(lastE, firstE);
        if (compareResult < 0) {
            org.assertj.core.api.Assertions.assertThat(compareResult)
                    .as("No %s order!", ascending ? "ascending" : "descending")
                    .isGreaterThanOrEqualTo(0);
        }
    }

    /**
     * @see <a href="http://blog.xebia.com/2009/02/07/acessing-generic-types-at-runtime-in-java/">Source</a>
     */
    @SuppressWarnings("unchecked")
    protected Class<E> findGenericType() {
        return (Class<E>) org.springframework.core.GenericTypeResolver.resolveTypeArgument(getClass(),
                ADelegateComparator.class);
    }

    public void sortDescending(final List<? extends E> list) {
        sort(list, false);
    }

    public void sortAscending(final List<? extends E> list) {
        sort(list, true);
    }

}
