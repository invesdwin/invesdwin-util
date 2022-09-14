package de.invesdwin.util.collections.circular;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class CircularGenericArrayTest {

    private static final int TURNS = 10;
    private static final int COUNT = 3;
    private static final int SIZE = 5;

    @Test
    public void testAppend() {
        final CircularGenericArray<Integer> array = new CircularGenericArray<>(SIZE);
        int added = 0;
        final List<Integer> fromEndList = new ArrayList<>();
        final List<Integer> fromStartList = new ArrayList<>();
        for (int turn = 0; turn < TURNS; turn++) {
            for (int i = 0; i < COUNT; i++) {
                added++;
                array.add(i);
                Assertions.assertThat(array.getReverse(0)).isEqualTo(i);
                fromStartList.add(i);
                if (fromStartList.size() > SIZE) {
                    fromStartList.remove(0);
                }
                fromEndList.add(0, i);
                if (fromEndList.size() > SIZE) {
                    fromEndList.remove(SIZE);
                }
                //CHECKSTYLE:OFF
                System.out.println(added + ": add=" + i + " toString=" + array.toString(0, array.size())
                        + " toStringReverse=" + array.toStringReverse(0, array.size()));
                //CHECKSTYLE:ON
                Assertions.assertThat(array.toString(0, array.size())).isEqualTo(fromStartList.toString());
                Assertions.assertThat(array.toStringReverse(0, array.size())).isEqualTo(fromEndList.toString());
                int fromEndExpected = i;
                for (int rel = 0; rel <= i; rel++) {
                    final Integer relValue = array.getReverse(rel);
                    //CHECKSTYLE:OFF
                    System.out.println("index=" + rel + " relExpected=" + fromEndExpected + " relValue=" + relValue);
                    //CHECKSTYLE:ON
                    Assertions.assertThat(relValue).isEqualTo(fromEndExpected);
                    fromEndExpected--;
                }
            }
        }
    }

}
