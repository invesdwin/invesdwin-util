package de.invesdwin.util.concurrent.lambda;

import java.util.function.Supplier;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.concurrent.loop.LoopInterruptedCheck;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public final class ThrottledSupplier<T> implements Supplier<T> {

    private final LoopInterruptedCheck throttleCheck;
    private final Supplier<T> supplier;
    private T throttledValue;

    private ThrottledSupplier(final Duration throttleInterval, final Supplier<T> supplier) {
        this.throttleCheck = new LoopInterruptedCheck(throttleInterval) {
            @Override
            protected long getInitialNanoTime() {
                return Long.MIN_VALUE;
            }
        };
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (throttleCheck.checkNoInterrupt()) {
            throttledValue = supplier.get();
        }
        return throttledValue;
    }

    public static <T> ThrottledSupplier<T> of(final Duration throttleInterval, final Supplier<T> supplier) {
        return new ThrottledSupplier<>(throttleInterval, supplier);
    }

}
