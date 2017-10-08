package de.invesdwin.util.collections;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class ListsTest {

    @Test
    public void testPackages() {
        final List<Integer> integers = new ArrayList<Integer>();
        for (int i = 0; i < 80; i++) {
            integers.add(i);
        }
        final List<? extends List<Integer>> packages = Lists.splitIntoPackageCount(integers, 8);
        Assertions.assertThat(packages).hasSize(8);
        for (final List<Integer> p : packages) {
            Assertions.assertThat(p).hasSize(10);
        }
    }

}
