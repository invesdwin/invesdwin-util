package de.invesdwin.util.collections.list;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.reflection.Reflections;

@NotThreadSafe
public class ListsTest {

    static {
        Reflections.disableJavaModuleSystemRestrictions();
    }

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

    @Test
    public void testRemoveRange() {
        final List<Integer> integers = new ArrayList<Integer>();
        for (int i = 0; i < 80; i++) {
            integers.add(i);
        }
        Assertions.checkEquals(Lists.removeRange(integers, 0, 0), 0);
        Assertions.checkEquals(80, integers.size());
        Assertions.checkEquals(Lists.removeRange(integers, 10, 20), 10);
        Assertions.checkEquals(70, integers.size());
    }

}
