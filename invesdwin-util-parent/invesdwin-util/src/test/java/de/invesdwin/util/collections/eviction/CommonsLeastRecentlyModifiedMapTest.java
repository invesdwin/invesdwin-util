package de.invesdwin.util.collections.eviction;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class CommonsLeastRecentlyModifiedMapTest {

    @Test
    public void testLeastRecentlyModifiedIsRemoved() {
        final CommonsLeastRecentlyModifiedMap<String, Integer> map = new CommonsLeastRecentlyModifiedMap<>(3);
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
        Assertions.assertThat(map.get("4")).isNull();
    }

}
