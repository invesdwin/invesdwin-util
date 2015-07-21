package de.invesdwin.util.collections.loadingcache.historical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.Test;

import com.google.common.collect.Iterables;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FDateBuilder;

// CHECKSTYLE:OFF abstract
@ThreadSafe
public class AGapHistoricalCacheTest {
    //CHECKSTYLE:ON

    //marker object
    private final List<FDate> entities;

    private int countReadAllValuesAscendingFrom;
    private int countReadNewestValueTo;
    private boolean returnNullInReadNewestValueTo;
    private boolean returnAllInReadAllValuesAscendingFrom;
    private Integer returnMaxResults;
    private final int testReturnMaxResultsValue = 2;

    private final TestGapHistoricalCache cache = new TestGapHistoricalCache();

    public AGapHistoricalCacheTest() {
        this.entities = new ArrayList<FDate>();
        entities.add(FDateBuilder.newDate(1990, 1, 1));
        entities.add(FDateBuilder.newDate(1991, 1, 1));
        entities.add(FDateBuilder.newDate(1992, 1, 1));
        entities.add(FDateBuilder.newDate(1993, 1, 1));
        entities.add(FDateBuilder.newDate(1994, 1, 1));
        entities.add(FDateBuilder.newDate(1995, 1, 1));
    }

    @Test
    public void testInconsistentGapKey() {
        FDate searchedKey = entities.get(0);
        FDate value = cache.query().getValue(searchedKey);
        Assertions.assertThat(value).isEqualTo(searchedKey);

        searchedKey = entities.get(1);
        value = cache.query().getValue(searchedKey.addDays(1));
        Assertions.assertThat(value).isEqualTo(searchedKey);
    }

    @Test
    public void testGaps() {
        //once through the complete list
        final List<FDate> liste = new ArrayList<FDate>();
        for (final FDate entity : entities) {
            final FDate cachedEntity = cache.query().getValue(entity);
            liste.add(cachedEntity);
            Assertions.assertThat(cachedEntity).isNotNull();
        }
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(1);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(2);
        Assertions.assertThat(liste).isEqualTo(entities);

        //new maxKey without new db results
        for (final FDate entity : entities) {
            Assertions.assertThat(cache.query().getValue(entity.addDays(5))).isNotNull();
        }
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(1);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(2);

        //new minKey without new db limit
        for (final FDate entity : entities) {
            Assertions.assertThat(cache.query().withFuture().getValue(entity.addYears(-5))).isNotNull();
        }
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(2);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(6);

        //again in the same limit
        for (final FDate entity : entities) {
            Assertions.assertThat(cache.query().getValue(entity.addDays(2))).isNotNull();
        }
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(2);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(6);

        //random order
        for (final FDate entity : new HashSet<FDate>(entities)) {
            Assertions.assertThat(cache.query().getValue(entity.addDays(2))).isNotNull();
        }
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(2);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(6);

        //simulate cache eviction
        cache.clear();
        for (final FDate entity : entities) {
            Assertions.assertThat(cache.query().getValue(entity.addDays(2))).isNotNull();
        }
        cache.clear();
        for (final FDate entity : entities) {
            Assertions.assertThat(cache.query().getValue(entity.addDays(2))).isNotNull();
        }
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(2);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(10);
    }

    @Test
    public void testGapsWithReturnMaxResults() {
        returnMaxResults = testReturnMaxResultsValue;

        //once through the complete list
        final List<FDate> liste = new ArrayList<FDate>();
        for (final FDate entity : entities) {
            final FDate cachedEntity = cache.query().getValue(entity);
            liste.add(cachedEntity);
            Assertions.assertThat(cachedEntity).isNotNull();
        }
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(3);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(2);
        Assertions.assertThat(liste).isEqualTo(entities);

        //new maxKey without new db results
        for (final FDate entity : entities) {
            Assertions.assertThat(cache.query().getValue(entity.addDays(5))).isNotNull();
        }
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(8);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(2);

        //new minKey without new db limit
        for (final FDate entity : entities) {
            Assertions.assertThat(cache.query().withFuture().getValue(entity.addYears(-5))).isNotNull();
        }
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(9);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(6);

        //again in the same limit
        for (final FDate entity : entities) {
            Assertions.assertThat(cache.query().getValue(entity.addDays(2))).isNotNull();
        }
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(13);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(6);

        //random order
        for (final FDate entity : new HashSet<FDate>(entities)) {
            Assertions.assertThat(cache.query().getValue(entity.addDays(2))).isNotNull();
        }
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(13);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(6);

        //simulate cache eviction
        cache.clear();
        for (final FDate entity : entities) {
            Assertions.assertThat(cache.query().getValue(entity.addDays(2))).isNotNull();
        }
        cache.clear();
        for (final FDate entity : entities) {
            Assertions.assertThat(cache.query().getValue(entity.addDays(2))).isNotNull();
        }
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(23);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(10);
    }

    @Test
    public void testOneResult() {
        for (final FDate entity : entities) {
            Assertions.assertThat(cache.query().getValue(entity.addYears(5))).isNotNull();
        }
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(1);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(2);
    }

    @Test
    public void testNoResultsUp() {
        for (final FDate entity : entities) {
            Assertions.assertThat(cache.query().getValue(entity.addYears(100))).isNotNull();
        }
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(0);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(2);
    }

    @Test
    public void testNoResultsDown() {
        for (final FDate entity : entities) {
            Assertions.assertThat(cache.query().withFuture().getValue(entity.addYears(-100))).isNotNull();
        }
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(1);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(2);
    }

    /**
     * for reverse sorting this is less efficient, this costs O(n) for queries.
     */
    @Test
    public void testInverseOrder() {
        final List<FDate> ents = new ArrayList<FDate>(entities);
        Collections.reverse(ents);
        for (final FDate entity : ents) {
            Assertions.assertThat(cache.query().getValue(entity)).isNotNull();
        }
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(2);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(2);
    }

    @Test
    public void testInverseOrderWithReturnMaxResults() {
        returnMaxResults = testReturnMaxResultsValue;

        final List<FDate> ents = new ArrayList<FDate>(entities);
        Collections.reverse(ents);
        for (final FDate entity : ents) {
            Assertions.assertThat(cache.query().getValue(entity)).isNotNull();
        }
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(4);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(4);
    }

    @Test
    public void testPreviousKey() {
        FDate previousKey = cache.query().getPreviousKey(new FDate(), entities.size());
        Assertions.assertThat(previousKey).isSameAs(entities.get(0));
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(2);
        //loading newest entity is faster than always loading all entities
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(4);

        previousKey = cache.query().getPreviousKey(new FDate(), 1);
        Assertions.assertThat(previousKey).isSameAs(entities.get(entities.size() - 2));
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(2);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(4);
    }

    @Test
    public void testPreviousValueWithDistance() {
        FDate previousValue = cache.query().getPreviousValue(new FDate(), entities.size());
        Assertions.assertThat(previousValue).isSameAs(entities.get(0));
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(2);
        //loading newest entity is faster than always loading all entities
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(4);

        previousValue = cache.query().getPreviousValue(new FDate(), 1);
        Assertions.assertThat(previousValue).isSameAs(entities.get(entities.size() - 2));
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(2);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(4);
    }

    @Test
    public void testPreviousValueWithoutDistance() {
        FDate previousValue = cache.query()

        .getPreviousValue(entities.get(entities.size() - 1), entities.size());
        Assertions.assertThat(previousValue).isSameAs(entities.get(0));
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(2);
        //loading newest entity is faster than always loading all entities
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(3);

        previousValue = cache.query().getPreviousValue(entities.get(entities.size() - 1), 1);
        Assertions.assertThat(previousValue).isSameAs(entities.get(entities.size() - 2));
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(2);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(3);
    }

    @Test
    public void testPreviousKeys() {
        final Collection<FDate> previousKeys = asList(cache.query().getPreviousKeys(new FDate(), entities.size()));
        Assertions.assertThat(previousKeys).isEqualTo(entities);
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(2);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(3);
    }

    private <T> List<T> asList(final Iterable<T> iterable) {
        final List<T> list = new ArrayList<T>();
        Iterables.addAll(list, iterable);
        return list;
    }

    @Test
    public void testKeys() {
        final Iterable<FDate> iterable = cache.query().getKeys(FDate.MIN_DATE, FDate.MAX_DATE);
        final List<FDate> previousKeys = new ArrayList<FDate>();
        for (final FDate d : iterable) {
            previousKeys.add(d);
        }
        Assertions.assertThat(previousKeys).isEqualTo(entities);
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(1);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(2);
    }

    @Test
    public void testKeysWithReturnMaxResults() {
        returnMaxResults = testReturnMaxResultsValue;

        final Iterable<FDate> iterable = cache.query().getKeys(FDate.MIN_DATE, FDate.MAX_DATE);
        final List<FDate> previousKeys = new ArrayList<FDate>();
        for (final FDate d : iterable) {
            previousKeys.add(d);
        }
        Assertions.assertThat(previousKeys).isEqualTo(entities);
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(3);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(2);
    }

    @Test
    public void testPreviousValuesWithDistance() {
        final Collection<FDate> previousValues = asList(cache.query().getPreviousValues(new FDate(), entities.size()));
        Assertions.assertThat(previousValues).isEqualTo(entities);
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(2);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(3);
    }

    @Test
    public void testPreviousValuesWithoutDistance() {
        final Collection<FDate> previousValues = asList(cache.query().getPreviousValues(
                entities.get(entities.size() - 1), entities.size()));
        Assertions.assertThat(previousValues).isEqualTo(entities);
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(2);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(2);
    }

    @Test
    public void testPreviousValuesGetsFilledDownWithDistance() {
        final Collection<FDate> previousValues = asList(cache.query()
                .withFilterDuplicateKeys(false)
                .withFuture()
                .getPreviousValues(FDate.MIN_DATE, entities.size()));
        Assertions.assertThat(previousValues.size()).isEqualTo(entities.size());
        for (final FDate d : previousValues) {
            Assertions.assertThat(d).isSameAs(entities.get(0));
        }
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(1);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(2);
    }

    @Test
    public void testPreviousValuesGetsFilledDownWithoutDistance() {
        final Collection<FDate> previousValues = asList(cache.query()
                .withFilterDuplicateKeys(false)
                .getPreviousValues(entities.get(0), entities.size()));
        Assertions.assertThat(previousValues.size()).isEqualTo(entities.size());
        for (final FDate d : previousValues) {
            Assertions.assertThat(d).isSameAs(entities.get(0));
        }
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(2);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(2);
    }

    @Test
    public void testNoData() {
        final List<FDate> liste = new ArrayList<FDate>(entities);
        entities.clear();
        for (final FDate entity : liste) {
            final FDate cachedEntity = cache.query().getValue(entity);
            Assertions.assertThat(cachedEntity).isNull();
        }
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(1);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(1);

        //new minKey limit gets tested
        final Collection<FDate> values = asList(cache.query().getPreviousValues(FDate.MIN_DATE, 5));
        Assertions.assertThat(values).isEmpty();
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(2);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(1);
    }

    @Test
    public void testNoDataInReadAllValuesAscendingFrom() {
        returnNullInReadNewestValueTo = true;
        for (final FDate entity : entities) {
            final FDate cachedEntity = cache.query().getValue(entity);
            Assertions.assertThat(cachedEntity).isNotNull();
        }
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(1);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(1);

        //new minKey limit gets tested
        final Collection<FDate> values = asList(cache.query().withFuture().getPreviousValues(FDate.MIN_DATE, 5));
        for (final FDate d : values) {
            Assertions.assertThat(d).isEqualTo(entities.get(0));
        }
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(2);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(1);
    }

    @Test
    public void testPreviousKeyWithAllValues() {
        returnAllInReadAllValuesAscendingFrom = true;

        FDate previousKey = cache.query().getPreviousKey(new FDate(), entities.size());
        Assertions.assertThat(previousKey).isSameAs(entities.get(0));
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(1);
        //loading newest entity is faster than always loading all entities
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(3);

        previousKey = cache.query().getPreviousKey(new FDate(), 1);
        Assertions.assertThat(previousKey).isSameAs(entities.get(entities.size() - 2));
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(1);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(3);
    }

    @Test
    public void testPreviousKeyWithReturnMaxResults() {
        returnMaxResults = testReturnMaxResultsValue;

        FDate previousKey = cache.query().getPreviousKey(new FDate(), entities.size());
        Assertions.assertThat(previousKey).isSameAs(entities.get(0));
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(4);
        //loading newest entity is faster than always loading all entities
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(7);

        previousKey = cache.query().getPreviousKey(new FDate(), 1);
        Assertions.assertThat(previousKey).isSameAs(entities.get(entities.size() - 2));
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(4);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(7);
    }

    @Test
    public void testPreviousKeyWithAllValuesAndNullInReadNewestValueTo() {
        returnAllInReadAllValuesAscendingFrom = true;
        returnNullInReadNewestValueTo = true;

        FDate previousKey = cache.query().getPreviousKey(new FDate(), entities.size());
        Assertions.assertThat(previousKey).isSameAs(entities.get(0));
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(2);
        //loading newest entity is faster than always loading all entities
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(1);

        previousKey = cache.query().getPreviousKey(new FDate(), 1);
        Assertions.assertThat(previousKey).isSameAs(entities.get(entities.size() - 2));
        Assertions.assertThat(countReadAllValuesAscendingFrom).isEqualTo(2);
        Assertions.assertThat(countReadNewestValueTo).isEqualTo(1);
    }

    @Test
    public void testPreviousKeysFilterDuplicateKeys() {
        Assertions.assertThat(
                asList(cache.query().withFilterDuplicateKeys(false).getPreviousKeys(new FDate(), 100)).size())
                .isSameAs(100);
        Assertions.assertThat(asList(cache.query().getPreviousKeys(new FDate(), 100)).size())
        .isEqualTo(entities.size());
    }

    @Test
    public void testPreviousValuesFilterDuplicateKeys() {
        Assertions.assertThat(
                asList(cache.query().withFilterDuplicateKeys(false).getPreviousValues(new FDate(), 100)).size())
                .isSameAs(100);
        Assertions.assertThat(asList(cache.query().getPreviousValues(new FDate(), 100)).size()).isEqualTo(
                entities.size());
    }

    @Test
    public void testPreviousValueKey() {
        for (final FDate entity : entities) {
            final FDate foundKey = cache.query().getPreviousValueKey(FDate.MIN_DATE, FDate.MAX_DATE, entity);
            Assertions.assertThat(foundKey).isEqualTo(entity);
        }
    }

    private class TestGapHistoricalCache extends AGapHistoricalCache<FDate> {

        @Override
        protected List<FDate> readAllValuesAscendingFrom(final FDate key) {
            countReadAllValuesAscendingFrom++;
            if (returnMaxResults != null) {
                Assertions.assertThat(returnAllInReadAllValuesAscendingFrom).isFalse();
                Assertions.assertThat(returnNullInReadNewestValueTo).isFalse();
            }
            List<FDate> result;
            if (returnAllInReadAllValuesAscendingFrom && returnMaxResults == null) {
                result = new ArrayList<FDate>(entities);
            } else {
                final List<FDate> list = new ArrayList<FDate>();
                for (final FDate d : entities) {
                    if (!d.isBefore(key)) {
                        list.add(d);
                    }
                }
                result = list;
            }
            if (returnMaxResults != null && !result.isEmpty()) {
                result = result.subList(0, Math.min(result.size() - 1, returnMaxResults));
            }
            return result;
        }

        @Override
        protected FDate innerExtractKey(final FDate key, final FDate entity) {
            return entity;
        }

        @Override
        protected FDate readLatestValueFor(final FDate key) {
            countReadNewestValueTo++;
            if (returnNullInReadNewestValueTo) {
                return null;
            } else {
                FDate previousE = null;
                for (final FDate e : entities) {
                    if (previousE == null) {
                        previousE = e;
                    } else {
                        if (key.isAfter(e)) {
                            previousE = e;
                        } else {
                            break;
                        }
                    }
                }
                return previousE;
            }
        }

        @Override
        protected FDate innerCalculatePreviousKey(final FDate key) {
            return key.addDays(-1);
        }

        @Override
        protected FDate innerCalculateNextKey(final FDate key) {
            //            return FDates.addDays(FDates.addYears(key, 1), 1); //this causes a bug which should be fixed sometime when it is needed that unprecise values may be returned here
            return key.addYears(1);
        }

    }
}
