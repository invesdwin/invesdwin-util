package de.invesdwin.util.concurrent.pool;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

// CHECKSTYLE:OFF
@NotThreadSafe
public class LimitedObjectPoolTest {
    //CHECKSTYLE:ON

    private static final Duration TIMEOUT = new Duration(200, FTimeUnit.MILLISECONDS);
    private static final long MAX_BORROW_WAIT_MILLIS = 5000;

    @Test
    public void testBorrowObjectTimesOutAfterTimeout() throws Exception {
        final LimitedObjectPool<String> pool = new LimitedObjectPool<>(new DummyPool(), 1, TIMEOUT);
        final String firstLease = pool.borrowObject();
        Assertions.assertThat(firstLease).isNotNull();
        //The pool is fully leased now, so the next borrowObject must fail after the timeout instead of waiting unbounded.
        final AtomicReference<Throwable> borrowOutcome = new AtomicReference<>();
        final Thread borrower = new Thread(() -> {
            try {
                borrowOutcome.set(new IllegalStateException(
                        "No exception thrown: " + pool.borrowObject()));
            } catch (final Throwable t) {
                borrowOutcome.set(t);
            }
        }, "LimitedObjectPoolTest-borrower");
        borrower.setDaemon(true);
        final Instant waitingSince = new Instant();
        borrower.start();
        borrower.join(MAX_BORROW_WAIT_MILLIS);
        Assertions.assertThat(borrower.isAlive()).describedAs(
                "borrowObject must not wait beyond its timeout of " + TIMEOUT + ", it waited "
                        + waitingSince.toDuration()).isFalse();
        Assertions.assertThat(waitingSince.toDuration()).isLessThan(Duration.ONE_SECOND);
        Assertions.assertThat(borrowOutcome.get()).isInstanceOf(RuntimeException.class);
        Assertions.assertThat(((RuntimeException) borrowOutcome.get()).getCause()).isInstanceOf(
                TimeoutException.class);
        pool.returnObject(firstLease);
    }

    private static final class DummyPool implements IObjectPool<String> {

        private final Deque<String> objects = new ArrayDeque<>();

        private DummyPool() {
            objects.add("object");
        }

        @Override
        public String borrowObject() {
            return objects.poll();
        }

        @Override
        public void returnObject(final String element) {
            objects.add(element);
        }

        @Override
        public void clear() {
            objects.clear();
        }

        @Override
        public void invalidateObject(final String element) {
            objects.remove(element);
        }

        @Override
        public int size() {
            return objects.size();
        }
    }

}
