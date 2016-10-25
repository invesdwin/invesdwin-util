package de.invesdwin.util.collections.loadingcache.historical.query.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.bean.tuple.ImmutableEntry;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FDateBuilder;

@NotThreadSafe
public class FilterDuplicateKeysListTest {

    @Test
    public void testAdd() {
        final FilterDuplicateKeysList<Integer> list = new FilterDuplicateKeysList<Integer>(10);
        list.add(ImmutableEntry.of(FDateBuilder.newDate(0), 0));
        list.add(ImmutableEntry.of(FDateBuilder.newDate(0), 0));
        list.add(ImmutableEntry.of(FDateBuilder.newDate(1), 1));
        list.add(ImmutableEntry.of(FDateBuilder.newDate(2), 2));
        list.add(ImmutableEntry.of(FDateBuilder.newDate(2), 2));
        Assertions.assertThat(list).hasSize(3);
    }

    @Test
    public void testAddReverse() {
        final FilterDuplicateKeysList<Integer> list = new FilterDuplicateKeysList<Integer>(10);
        list.add(ImmutableEntry.of(FDateBuilder.newDate(2), 2));
        list.add(ImmutableEntry.of(FDateBuilder.newDate(2), 2));
        list.add(ImmutableEntry.of(FDateBuilder.newDate(1), 1));
        list.add(ImmutableEntry.of(FDateBuilder.newDate(0), 0));
        list.add(ImmutableEntry.of(FDateBuilder.newDate(0), 0));
        Assertions.assertThat(list).hasSize(3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddAll() {
        final List<Entry<FDate, Integer>> input = new ArrayList<Entry<FDate, Integer>>();
        input.add(ImmutableEntry.of(FDateBuilder.newDate(0), 0));
        input.add(ImmutableEntry.of(FDateBuilder.newDate(0), 0));
        input.add(ImmutableEntry.of(FDateBuilder.newDate(1), 1));
        input.add(ImmutableEntry.of(FDateBuilder.newDate(2), 2));
        input.add(ImmutableEntry.of(FDateBuilder.newDate(2), 2));
        final FilterDuplicateKeysList<Integer> list = new FilterDuplicateKeysList<Integer>(10);
        //can not use addAll in that direction, also not needed right now
        list.addAll(input);
        Assertions.assertThat(list).hasSize(3);
    }

    @Test
    public void testAddAllReverse() {
        final List<Entry<FDate, Integer>> input = new ArrayList<Entry<FDate, Integer>>();
        input.add(ImmutableEntry.of(FDateBuilder.newDate(2), 2));
        input.add(ImmutableEntry.of(FDateBuilder.newDate(2), 2));
        input.add(ImmutableEntry.of(FDateBuilder.newDate(1), 1));
        input.add(ImmutableEntry.of(FDateBuilder.newDate(0), 0));
        input.add(ImmutableEntry.of(FDateBuilder.newDate(0), 0));
        final FilterDuplicateKeysList<Integer> list = new FilterDuplicateKeysList<Integer>(10);
        list.addAll(input);
        Assertions.assertThat(list).hasSize(3);
    }

    @Test
    public void testAddAllReverseAfterAddFirst() {
        final List<Entry<FDate, Integer>> input = new ArrayList<Entry<FDate, Integer>>();
        input.add(ImmutableEntry.of(FDateBuilder.newDate(2), 2));
        input.add(ImmutableEntry.of(FDateBuilder.newDate(2), 2));
        input.add(ImmutableEntry.of(FDateBuilder.newDate(1), 1));
        input.add(ImmutableEntry.of(FDateBuilder.newDate(0), 0));
        input.add(ImmutableEntry.of(FDateBuilder.newDate(0), 0));
        final FilterDuplicateKeysList<Integer> list = new FilterDuplicateKeysList<Integer>(10);
        list.add(ImmutableEntry.of(FDateBuilder.newDate(2), 2));
        list.addAll(input);
        Assertions.assertThat(list).hasSize(3);
    }

    @Test
    public void testAddAllReverseAfterAddLast() {
        final List<Entry<FDate, Integer>> input = new ArrayList<Entry<FDate, Integer>>();
        input.add(ImmutableEntry.of(FDateBuilder.newDate(2), 2));
        input.add(ImmutableEntry.of(FDateBuilder.newDate(2), 2));
        input.add(ImmutableEntry.of(FDateBuilder.newDate(1), 1));
        input.add(ImmutableEntry.of(FDateBuilder.newDate(0), 0));
        input.add(ImmutableEntry.of(FDateBuilder.newDate(0), 0));
        final FilterDuplicateKeysList<Integer> list = new FilterDuplicateKeysList<Integer>(10);
        list.add(ImmutableEntry.of(FDateBuilder.newDate(0), 0));
        list.addAll(0, input);
        Assertions.assertThat(list).hasSize(3);
    }

}
