package de.invesdwin.util.collections.array.primitive.queue;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.array.primitive.queue.adapter.GenericPrimitiveQueueToJavaAdapter;
import de.invesdwin.util.collections.array.primitive.queue.adapter.JavaToGenericPrimitiveQueueAdapter;
import de.invesdwin.util.collections.array.primitive.queue.circular.CircularGenericPrimitiveArrayQueue;

@NotThreadSafe
public class GenericPrimitiveQueueJavaAdapterTest {

    @Test
    public void testJavaToGenericPrimitiveQueue() {
        final Queue<String> javaQueue = new ArrayDeque<>();
        final IGenericPrimitiveQueue<String> queue = new JavaToGenericPrimitiveQueueAdapter<String>(javaQueue);
        Assertions.assertThat(queue.isEmpty()).isTrue();
        Assertions.assertThat(queue.peek()).isNull();
        Assertions.assertThat(queue.poll()).isNull();
        Assertions.assertThatThrownBy(queue::element).isInstanceOf(NoSuchElementException.class);
        Assertions.assertThatThrownBy(queue::remove).isInstanceOf(NoSuchElementException.class);
        Assertions.assertThat(queue.offer("a")).isTrue();
        Assertions.assertThat(queue.add("b")).isTrue();
        Assertions.assertThat(queue.size()).isEqualTo(2);
        Assertions.assertThat(queue.element()).isEqualTo("a");
        Assertions.assertThat(queue.remove()).isEqualTo("a");
        Assertions.assertThat(queue.poll()).isEqualTo("b");
        Assertions.assertThat(queue.isEmpty()).isTrue();
        //the java.util.Queue view works on the same delegate
        javaQueue.add("c");
        Assertions.assertThat(queue.size()).isEqualTo(1);
        Assertions.assertThat(((Queue<String>) queue).peek()).isEqualTo("c");
        Assertions.assertThat(((Queue<String>) queue).remove()).isEqualTo("c");
        Assertions.assertThat(javaQueue).isEmpty();
        ((Queue<String>) queue).add("d");
        Assertions.assertThat(queue.poll()).isEqualTo("d");
        queue.clear();
        Assertions.assertThat(javaQueue).isEmpty();
    }

    @Test
    public void testGenericPrimitiveQueueToJava() {
        final CircularGenericPrimitiveArrayQueue<String> delegate = new CircularGenericPrimitiveArrayQueue<String>(2);
        final Queue<String> queue = new GenericPrimitiveQueueToJavaAdapter<String>(delegate);
        Assertions.assertThat(queue.isEmpty()).isTrue();
        Assertions.assertThat(queue.peek()).isNull();
        Assertions.assertThat(queue.poll()).isNull();
        //the missing value based element and remove of the delegate are used, so they signal no such element
        Assertions.assertThatThrownBy(queue::element).isInstanceOf(NoSuchElementException.class);
        Assertions.assertThatThrownBy(queue::remove).isInstanceOf(NoSuchElementException.class);
        Assertions.assertThat(queue.offer("a")).isTrue();
        Assertions.assertThat(queue.add("b")).isTrue();
        Assertions.assertThatThrownBy(() -> queue.add("c")).isInstanceOf(IllegalStateException.class);
        Assertions.assertThat(queue.size()).isEqualTo(2);
        Assertions.assertThat(queue.element()).isEqualTo("a");
        Assertions.assertThat(queue.remove()).isEqualTo("a");
        Assertions.assertThat(queue.poll()).isEqualTo("b");
        Assertions.assertThat(queue.isEmpty()).isTrue();
        //the circular queue view works on the same delegate
        delegate.circularAdd("c");
        Assertions.assertThat(queue.size()).isEqualTo(1);
        Assertions.assertThat(queue.peek()).isEqualTo("c");
        Assertions.assertThat(((IGenericPrimitiveQueue<String>) queue).remove()).isEqualTo("c");
        delegate.circularAdd("d");
        queue.clear();
        Assertions.assertThat(delegate.isEmpty()).isTrue();
    }

    @Test
    public void testGenericPrimitiveQueueToJavaCollectionMethodsAreStubbed() {
        final CircularGenericPrimitiveArrayQueue<String> delegate = new CircularGenericPrimitiveArrayQueue<String>(2);
        final Queue<String> queue = new GenericPrimitiveQueueToJavaAdapter<String>(delegate);
        queue.offer("a");
        //only the queue methods are delegated, the collection methods are no-ops
        Assertions.assertThat(queue.contains("a")).isFalse();
        Assertions.assertThat(queue.iterator().hasNext()).isFalse();
        Assertions.assertThat(queue.toArray()).isEmpty();
        Assertions.assertThat(queue.remove("a")).isFalse();
        Assertions.assertThat(queue.addAll(java.util.Collections.singletonList("b"))).isFalse();
        Assertions.assertThat(queue.size()).isEqualTo(1);
    }

}
