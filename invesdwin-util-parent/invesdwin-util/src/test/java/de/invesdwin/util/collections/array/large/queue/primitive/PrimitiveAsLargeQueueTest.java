package de.invesdwin.util.collections.array.large.queue.primitive;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.array.large.queue.IBooleanLargeQueue;
import de.invesdwin.util.collections.array.large.queue.IByteLargeQueue;
import de.invesdwin.util.collections.array.large.queue.IDoubleLargeQueue;
import de.invesdwin.util.collections.array.large.queue.IGenericLargeQueue;
import de.invesdwin.util.collections.array.large.queue.IIntegerLargeQueue;
import de.invesdwin.util.collections.array.large.queue.ILongLargeQueue;
import de.invesdwin.util.collections.array.primitive.queue.circular.CircularBooleanPrimitiveArrayQueue;
import de.invesdwin.util.collections.array.primitive.queue.circular.CircularByteBuffer;
import de.invesdwin.util.collections.array.primitive.queue.circular.CircularDoublePrimitiveArrayQueue;
import de.invesdwin.util.collections.array.primitive.queue.circular.CircularGenericPrimitiveArrayQueue;
import de.invesdwin.util.collections.array.primitive.queue.circular.CircularIntegerPrimitiveArrayQueue;
import de.invesdwin.util.collections.array.primitive.queue.circular.CircularLongPrimitiveArrayQueue;

@NotThreadSafe
public class PrimitiveAsLargeQueueTest {

    @Test
    public void testLong() {
        final CircularLongPrimitiveArrayQueue delegate = new CircularLongPrimitiveArrayQueue(2);
        final ILongLargeQueue queue = new LongPrimitiveAsLargeQueue(delegate);
        Assertions.assertThat(queue.isEmpty()).isTrue();
        Assertions.assertThat(queue.size()).isEqualTo(0L);
        Assertions.assertThat(queue.peek()).isEqualTo(ILongLargeQueue.MISSING_VALUE);
        Assertions.assertThatThrownBy(queue::element).isInstanceOf(NoSuchElementException.class);
        Assertions.assertThatThrownBy(queue::remove).isInstanceOf(NoSuchElementException.class);
        Assertions.assertThat(queue.offer(1L)).isTrue();
        Assertions.assertThat(queue.offer(2L)).isTrue();
        Assertions.assertThat(queue.offer(3L)).isFalse();
        Assertions.assertThat(queue.size()).isEqualTo(2L);
        Assertions.assertThat(queue.isEmpty()).isFalse();
        Assertions.assertThat(queue.peek()).isEqualTo(1L);
        Assertions.assertThat(queue.element()).isEqualTo(1L);
        Assertions.assertThat(queue.poll()).isEqualTo(1L);
        Assertions.assertThat(queue.remove()).isEqualTo(2L);
        Assertions.assertThat(queue.isEmpty()).isTrue();
        //the circular extras of the delegate are still visible
        delegate.circularAdd(4L);
        Assertions.assertThat(queue.peek()).isEqualTo(4L);
        queue.clear();
        Assertions.assertThat(delegate.isEmpty()).isTrue();
    }

    @Test
    public void testInteger() {
        final CircularIntegerPrimitiveArrayQueue delegate = new CircularIntegerPrimitiveArrayQueue(2);
        final IIntegerLargeQueue queue = new IntegerPrimitiveAsLargeQueue(delegate);
        Assertions.assertThat(queue.peek()).isEqualTo(IIntegerLargeQueue.MISSING_VALUE);
        Assertions.assertThat(queue.add(1)).isTrue();
        Assertions.assertThat(queue.add(2)).isTrue();
        Assertions.assertThatThrownBy(() -> queue.add(3)).isInstanceOf(IllegalStateException.class);
        Assertions.assertThat(queue.size()).isEqualTo(2L);
        Assertions.assertThat(queue.remove()).isEqualTo(1);
        Assertions.assertThat(queue.element()).isEqualTo(2);
        queue.clear();
        Assertions.assertThat(delegate.isEmpty()).isTrue();
    }

    @Test
    public void testDouble() {
        final CircularDoublePrimitiveArrayQueue delegate = new CircularDoublePrimitiveArrayQueue(2);
        final IDoubleLargeQueue queue = new DoublePrimitiveAsLargeQueue(delegate);
        Assertions.assertThat(Double.isNaN(queue.peek())).isTrue();
        Assertions.assertThat(Double.isNaN(queue.poll())).isTrue();
        Assertions.assertThat(queue.offer(Double.NaN)).isTrue();
        Assertions.assertThat(queue.offer(1.5D)).isTrue();
        //a NaN element is not empty, so element and remove must not fail
        Assertions.assertThat(queue.size()).isEqualTo(2L);
        Assertions.assertThat(Double.isNaN(queue.element())).isTrue();
        Assertions.assertThat(Double.isNaN(queue.remove())).isTrue();
        Assertions.assertThat(queue.poll()).isEqualTo(1.5D);
        Assertions.assertThat(queue.isEmpty()).isTrue();
    }

    @Test
    public void testBoolean() {
        final CircularBooleanPrimitiveArrayQueue delegate = new CircularBooleanPrimitiveArrayQueue(2);
        final IBooleanLargeQueue queue = new BooleanPrimitiveAsLargeQueue(delegate);
        Assertions.assertThat(queue.isEmpty()).isTrue();
        Assertions.assertThat(queue.peek()).isEqualTo(IBooleanLargeQueue.MISSING_VALUE);
        Assertions.assertThat(queue.offer(true)).isTrue();
        Assertions.assertThat(queue.offer(false)).isTrue();
        //false is an element, not the missing value
        Assertions.assertThat(queue.size()).isEqualTo(2L);
        Assertions.assertThat(queue.remove()).isTrue();
        Assertions.assertThat(queue.element()).isFalse();
        Assertions.assertThat(queue.poll()).isFalse();
        Assertions.assertThat(queue.isEmpty()).isTrue();
    }

    @Test
    public void testGeneric() {
        final CircularGenericPrimitiveArrayQueue<String> delegate = new CircularGenericPrimitiveArrayQueue<String>(2);
        final IGenericLargeQueue<String> queue = new GenericPrimitiveAsLargeQueue<String>(delegate);
        Assertions.assertThat(queue.isEmpty()).isTrue();
        Assertions.assertThat(queue.peek()).isNull();
        Assertions.assertThatThrownBy(queue::element).isInstanceOf(NoSuchElementException.class);
        Assertions.assertThat(queue.offer("a")).isTrue();
        Assertions.assertThat(queue.offer("b")).isTrue();
        Assertions.assertThat(queue.offer("c")).isFalse();
        Assertions.assertThat(queue.size()).isEqualTo(2L);
        Assertions.assertThat(queue.element()).isEqualTo("a");
        Assertions.assertThat(queue.remove()).isEqualTo("a");
        Assertions.assertThat(queue.poll()).isEqualTo("b");
        Assertions.assertThat(queue.isEmpty()).isTrue();
        queue.clear();
        Assertions.assertThat(delegate.isEmpty()).isTrue();
    }

    @Test
    public void testByte() {
        final CircularByteBuffer delegate = new CircularByteBuffer(2);
        final IByteLargeQueue queue = new BytePrimitiveAsLargeQueue(delegate);
        Assertions.assertThat(queue.isEmpty()).isTrue();
        Assertions.assertThat(queue.size()).isEqualTo(0L);
        Assertions.assertThat(queue.peek()).isEqualTo(IByteLargeQueue.MISSING_VALUE);
        Assertions.assertThatThrownBy(queue::element).isInstanceOf(NoSuchElementException.class);
        Assertions.assertThatThrownBy(queue::remove).isInstanceOf(NoSuchElementException.class);
        Assertions.assertThat(queue.offer((byte) 1)).isTrue();
        Assertions.assertThat(queue.offer((byte) 2)).isTrue();
        Assertions.assertThat(queue.offer((byte) 3)).isFalse();
        Assertions.assertThat(queue.size()).isEqualTo(2L);
        Assertions.assertThat(queue.peek()).isEqualTo((byte) 1);
        Assertions.assertThat(queue.poll()).isEqualTo((byte) 1);
        Assertions.assertThat(queue.remove()).isEqualTo((byte) 2);
        Assertions.assertThat(queue.isEmpty()).isTrue();
        //the bulk write of the delegate is still visible
        delegate.write((byte) 3);
        Assertions.assertThat(queue.element()).isEqualTo((byte) 3);
        //zero is a valid element, so only isEmpty() can distinguish it from a missing value
        queue.clear();
        queue.offer((byte) 0);
        Assertions.assertThat(queue.isEmpty()).isFalse();
        Assertions.assertThat(queue.peek()).isEqualTo(IByteLargeQueue.MISSING_VALUE);
        queue.clear();
        Assertions.assertThat(delegate.isEmpty()).isTrue();
    }

}
