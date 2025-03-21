package de.invesdwin.util.collections.circular;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.random.IRandomGenerator;
import de.invesdwin.util.math.random.PseudoRandomGenerators;

@NotThreadSafe
public class CircularGenericArrayQueueTest {

    private static final int TURNS = 10;
    private static final int COUNT = 3;
    private static final int SIZE = 5;

    @Test
    public void testAppend() {
        final CircularGenericArrayQueue<Integer> array = new CircularGenericArrayQueue<>(SIZE);
        int step = 0;
        final List<Integer> fromEndList = new ArrayList<>();
        final List<Integer> fromStartList = new ArrayList<>();
        for (int turn = 0; turn < TURNS; turn++) {
            for (int i = 0; i < COUNT; i++) {
                array.circularAdd(i);
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
                System.out.println(step + ": add=" + i + " toString=" + array.toString(0, array.size())
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
                step++;
            }
        }
    }

    @Test
    public void testPrepend() {
        final CircularGenericArrayQueue<Integer> array = new CircularGenericArrayQueue<>(SIZE);
        int step = 0;
        final List<Integer> fromEndList = new ArrayList<>();
        final List<Integer> fromStartList = new ArrayList<>();
        for (int turn = 0; turn < TURNS; turn++) {
            for (int i = 0; i < COUNT; i++) {
                array.circularPrepend(i);
                Assertions.assertThat(array.get(0)).isEqualTo(i);
                fromStartList.add(0, i);
                if (fromStartList.size() > SIZE) {
                    fromStartList.remove(SIZE);
                }
                fromEndList.add(i);
                if (fromEndList.size() > SIZE) {
                    fromEndList.remove(0);
                }
                //CHECKSTYLE:OFF
                System.out.println(step + ": add=" + i + " toString=" + array.toString(0, array.size())
                        + " toStringReverse=" + array.toStringReverse(0, array.size()));
                //CHECKSTYLE:ON
                Assertions.assertThat(array.toString(0, array.size())).isEqualTo(fromStartList.toString());
                Assertions.assertThat(array.toStringReverse(0, array.size())).isEqualTo(fromEndList.toString());
                int fromEndExpected = i;
                for (int rel = 0; rel <= i; rel++) {
                    final Integer relValue = array.get(rel);
                    //CHECKSTYLE:OFF
                    System.out.println("index=" + rel + " relExpected=" + fromEndExpected + " relValue=" + relValue);
                    //CHECKSTYLE:ON
                    Assertions.assertThat(relValue).isEqualTo(fromEndExpected);
                    fromEndExpected--;
                }
                step++;
            }
        }
    }

    @Test
    public void testQueue() {
        final IRandomGenerator random = PseudoRandomGenerators.newPseudoRandom();
        final int iterations = TURNS * COUNT;
        final boolean[] actions = new boolean[iterations];
        for (int i = 0; i < actions.length; i++) {
            actions[i] = random.nextBoolean();
        }
        actions[0] = true;
        actions[1] = true;
        actions[2] = false;
        actions[3] = true;
        actions[4] = true;
        actions[5] = true;
        actions[6] = false;
        actions[7] = false;
        actions[8] = true;
        final CircularGenericArrayQueue<Integer> array = new CircularGenericArrayQueue<>(SIZE);
        int step = 0;
        final List<Integer> fromEndList = new ArrayList<>();
        final List<Integer> fromStartList = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            final boolean add = actions[step];
            if (array.isFull() || !add && !array.isEmpty()) {
                array.remove();
                fromEndList.remove(fromEndList.size() - 1);
                fromStartList.remove(0);
            } else if (array.isEmpty() || add) {
                array.add(i);
                fromEndList.add(0, i);
                fromStartList.add(i);
            }
            //CHECKSTYLE:OFF
            System.out.println(step + ": " + (add ? "add" : "remove") + " value=" + i + " toString="
                    + array.toString(0, array.size()) + " toStringReverse=" + array.toStringReverse(0, array.size()));
            //CHECKSTYLE:ON
            Assertions.assertThat(array.toString(0, array.size())).isEqualTo(fromStartList.toString());
            Assertions.assertThat(array.toStringReverse(0, array.size())).isEqualTo(fromEndList.toString());
            for (int rel = 0; rel <= i && rel < array.size(); rel++) {
                final Integer relValue = array.getReverse(rel);
                final Integer fromEndExpected = fromEndList.get(rel);
                //CHECKSTYLE:OFF
                System.out.println("index=" + rel + " relExpected=" + fromEndExpected + " relValue=" + relValue);
                //CHECKSTYLE:ON
                Assertions.assertThat(relValue).isEqualTo(fromEndExpected);
            }
            step++;
        }
    }

}
