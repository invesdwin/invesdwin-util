package de.invesdwin.util.collections.delegate.unmodifiable;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Arrays;

/**
 * Pins the equals/hashCode contract of ConcatenatedUnmodifiableList: equals is JDK List content
 * equality (partition-insensitive), so per the JDK List contract the hash must be the content
 * sequence hash — [1,2]+[] must equal AND hash-equal [1]+[2] and any JDK list with that content.
 */
@NotThreadSafe
public class ConcatenatedUnmodifiableListTest {

    @Test
    public void testPartitionInsensitiveEqualPairsAgreeOnHash() {
        final ConcatenatedUnmodifiableList<Integer> a = new ConcatenatedUnmodifiableList<>(Arrays.asList(1, 2),
                Arrays.<Integer>asList());
        final ConcatenatedUnmodifiableList<Integer> b = new ConcatenatedUnmodifiableList<>(Arrays.asList(1),
                Arrays.asList(2));
        Assertions.assertThat(a.equals(b)).isTrue();
        Assertions.assertThat(b.equals(a)).isTrue();
        Assertions.assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    public void testJdkListContractAgreement() {
        final ConcatenatedUnmodifiableList<Integer> a = new ConcatenatedUnmodifiableList<>(Arrays.asList(1),
                Arrays.asList(2));
        final List<Integer> jdkList = Arrays.asList(1, 2);
        Assertions.assertThat(a.equals(jdkList)).isTrue();
        Assertions.assertThat(a.hashCode()).isEqualTo(jdkList.hashCode());
    }

    @Test
    public void testEmptyPairsAgreeOnHash() {
        final ConcatenatedUnmodifiableList<Integer> a = new ConcatenatedUnmodifiableList<>(Arrays.<Integer>asList(),
                Arrays.<Integer>asList());
        Assertions.assertThat(a.equals(Arrays.<Integer>asList())).isTrue();
        Assertions.assertThat(a.hashCode()).isEqualTo(Arrays.<Integer>asList().hashCode());
    }

    @Test
    public void testDifferentContentIsUnequal() {
        final ConcatenatedUnmodifiableList<Integer> a = new ConcatenatedUnmodifiableList<>(Arrays.asList(1, 2),
                Arrays.<Integer>asList());
        final ConcatenatedUnmodifiableList<Integer> b = new ConcatenatedUnmodifiableList<>(Arrays.asList(1, 3),
                Arrays.<Integer>asList());
        Assertions.assertThat(a.equals(b)).isFalse();
        Assertions.assertThat(b.equals(a)).isFalse();
    }

}
