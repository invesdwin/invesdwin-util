package de.invesdwin.util.collections.eviction;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class CommonsLeastRecentlyUsedMapTest {

    @Test
    public void testLeastRecentlyUsedIsRemoved() {
        final CommonsLeastRecentlyUsedMap<String, Integer> map = new CommonsLeastRecentlyUsedMap<>(3);
        map.put("1", 1);
        map.put("2", 2);
        map.put("3", 3);
        Assertions.assertThat(map).hasSize(3);
        map.put("4", 4);
        Assertions.assertThat(map).hasSize(3);
        Assertions.assertThat(map.get("1")).isNull();
        map.put("4", 4);
        map.put("3", 3);
        map.put("2", 2);
        map.get("4");
        map.get("2");
        Assertions.assertThat(map).hasSize(3);
        map.put("5", 5);
        Assertions.assertThat(map).hasSize(3);
        Assertions.assertThat(map.get("1")).isNull();
        Assertions.assertThat(map.get("3")).isNull();
    }

}
