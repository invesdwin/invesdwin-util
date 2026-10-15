package de.invesdwin.util.collections.array.primitive.queue.large;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.array.large.queue.IBooleanLargeQueue;
import de.invesdwin.util.collections.array.large.queue.circular.CircularBooleanLargeArrayQueue;
import de.invesdwin.util.collections.array.large.queue.circular.CircularDoubleLargeArrayQueue;
import de.invesdwin.util.collections.array.large.queue.circular.CircularGenericLargeArrayQueue;
import de.invesdwin.util.collections.array.large.queue.circular.CircularIntegerLargeArrayQueue;
import de.invesdwin.util.collections.array.large.queue.circular.CircularLongLargeArrayQueue;
import de.invesdwin.util.collections.array.large.queue.circular.CircularMemoryBuffer;
import de.invesdwin.util.collections.array.primitive.queue.IBooleanPrimitiveQueue;
import de.invesdwin.util.collections.array.primitive.queue.IBytePrimitiveQueue;
import de.invesdwin.util.collections.array.primitive.queue.IDoublePrimitiveQueue;
import de.invesdwin.util.collections.array.primitive.queue.IGenericPrimitiveQueue;
import de.invesdwin.util.collections.array.primitive.queue.IIntegerPrimitiveQueue;
import de.invesdwin.util.collections.array.primitive.queue.ILongPrimitiveQueue;

@NotThreadSafe
public class LargeAsPrimitiveQueueTest {

    @Test
    public void testLong() {
        final CircularLongLargeArrayQueue delegate = new CircularLongLargeArrayQueue(2);
        final ILongPrimitiveQueue queue = new LongLargeAsPrimitiveQueue(delegate);
        Assertions.assertThat(queue.isEmpty()).isTrue();
        Assertions.assertThat(queue.size()).isEqualTo(0);
        Assertions.assertThat(queue.peek()).isEqualTo(ILongPrimitiveQueue.MISSING_VALUE);
        Assertions.assertThatThrownBy(queue::element).isInstanceOf(NoSuchElementException.class);
        Assertions.assertThatThrownBy(queue::remove).isInstanceOf(NoSuchElementException.class);
        Assertions.assertThat(queue.offer(1L)).isTrue();
        Assertions.assertThat(queue.offer(2L)).isTrue();
        Assertions.assertThat(queue.offer(3L)).isFalse();
        Assertions.assertThat(queue.size()).isEqualTo(2);
        Assertions.assertThat(queue.peek()).isEqualTo(1L);
        Assertions.assertThat(queue.poll()).isEqualTo(1L);
        Assertions.assertThat(queue.remove()).isEqualTo(2L);
        Assertions.assertThat(queue.isEmpty()).isTrue();
        //the circular extras of the delegate are still visible
        delegate.circularAdd(4L);
        Assertions.assertThat(queue.element()).isEqualTo(4L);
        queue.clear();
        Assertions.assertThat(delegate.isEmpty()).isTrue();
    }

    @Test
    public void testInteger() {
        final CircularIntegerLargeArrayQueue delegate = new CircularIntegerLargeArrayQueue(2);
        final IIntegerPrimitiveQueue queue = new IntegerLargeAsPrimitiveQueue(delegate);
        Assertions.assertThat(queue.peek()).isEqualTo(IIntegerPrimitiveQueue.MISSING_VALUE);
        Assertions.assertThat(queue.add(1)).isTrue();
        Assertions.assertThat(queue.add(2)).isTrue();
        Assertions.assertThatThrownBy(() -> queue.add(3)).isInstanceOf(IllegalStateException.class);
        Assertions.assertThat(queue.size()).isEqualTo(2);
        Assertions.assertThat(queue.remove()).isEqualTo(1);
        Assertions.assertThat(queue.element()).isEqualTo(2);
        queue.clear();
        Assertions.assertThat(delegate.isEmpty()).isTrue();
    }

    @Test
    public void testDouble() {
        final CircularDoubleLargeArrayQueue delegate = new CircularDoubleLargeArrayQueue(2);
        final IDoublePrimitiveQueue queue = new DoubleLargeAsPrimitiveQueue(delegate);
        Assertions.assertThat(Double.isNaN(queue.peek())).isTrue();
        Assertions.assertThat(Double.isNaN(queue.poll())).isTrue();
        Assertions.assertThat(queue.offer(Double.NaN)).isTrue();
        Assertions.assertThat(queue.offer(1.5D)).isTrue();
        //a NaN element is not empty, so element and remove must not fail
        Assertions.assertThat(queue.size()).isEqualTo(2);
        Assertions.assertThat(Double.isNaN(queue.element())).isTrue();
        Assertions.assertThat(Double.isNaN(queue.remove())).isTrue();
        Assertions.assertThat(queue.poll()).isEqualTo(1.5D);
        Assertions.assertThat(queue.isEmpty()).isTrue();
    }

    @Test
    public void testBoolean() {
        final CircularBooleanLargeArrayQueue delegate = new CircularBooleanLargeArrayQueue(2);
        final IBooleanPrimitiveQueue queue = new BooleanLargeAsPrimitiveQueue(delegate);
        Assertions.assertThat(queue.isEmpty()).isTrue();
        Assertions.assertThat(queue.peek()).isEqualTo(IBooleanLargeQueue.MISSING_VALUE);
        Assertions.assertThat(queue.offer(true)).isTrue();
        Assertions.assertThat(queue.offer(false)).isTrue();
        //false is an element, not the missing value
        Assertions.assertThat(queue.size()).isEqualTo(2);
        Assertions.assertThat(queue.remove()).isTrue();
        Assertions.assertThat(queue.element()).isFalse();
        Assertions.assertThat(queue.poll()).isFalse();
        Assertions.assertThat(queue.isEmpty()).isTrue();
    }

    @Test
    public void testGeneric() {
        final CircularGenericLargeArrayQueue<String> delegate = new CircularGenericLargeArrayQueue<String>(
                String.class, 2);
        final IGenericPrimitiveQueue<String> queue = new GenericLargeAsPrimitiveQueue<String>(delegate);
        Assertions.assertThat(queue.isEmpty()).isTrue();
        Assertions.assertThat(queue.peek()).isNull();
        Assertions.assertThatThrownBy(queue::element).isInstanceOf(NoSuchElementException.class);
        Assertions.assertThat(queue.offer("a")).isTrue();
        Assertions.assertThat(queue.offer("b")).isTrue();
        Assertions.assertThat(queue.offer("c")).isFalse();
        Assertions.assertThat(queue.size()).isEqualTo(2);
        Assertions.assertThat(queue.element()).isEqualTo("a");
        Assertions.assertThat(queue.remove()).isEqualTo("a");
        Assertions.assertThat(queue.poll()).isEqualTo("b");
        Assertions.assertThat(queue.isEmpty()).isTrue();
        queue.clear();
        Assertions.assertThat(delegate.isEmpty()).isTrue();
    }

    @Test
    public void testByte() {
        final CircularMemoryBuffer delegate = new CircularMemoryBuffer(2);
        final IBytePrimitiveQueue queue = new ByteLargeAsPrimitiveQueue(delegate);
        Assertions.assertThat(queue.isEmpty()).isTrue();
        Assertions.assertThat(queue.size()).isEqualTo(0);
        Assertions.assertThat(queue.peek()).isEqualTo(IBytePrimitiveQueue.MISSING_VALUE);
        Assertions.assertThatThrownBy(queue::element).isInstanceOf(NoSuchElementException.class);
        Assertions.assertThatThrownBy(queue::remove).isInstanceOf(NoSuchElementException.class);
        Assertions.assertThat(queue.offer((byte) 1)).isTrue();
        Assertions.assertThat(queue.offer((byte) 2)).isTrue();
        Assertions.assertThat(queue.offer((byte) 3)).isFalse();
        Assertions.assertThat(queue.size()).isEqualTo(2);
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
        Assertions.assertThat(queue.peek()).isEqualTo(IBytePrimitiveQueue.MISSING_VALUE);
        queue.clear();
        Assertions.assertThat(delegate.isEmpty()).isTrue();
    }

}
