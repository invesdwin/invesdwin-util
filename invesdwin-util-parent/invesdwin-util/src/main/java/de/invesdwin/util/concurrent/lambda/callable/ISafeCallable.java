package de.invesdwin.util.concurrent.lambda.callable;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public interface ISafeCallable<E> extends Callable<E>, Supplier<E> {

    @Override
    E call();

    @Override
    default E get() {
        return call();
    }

}
