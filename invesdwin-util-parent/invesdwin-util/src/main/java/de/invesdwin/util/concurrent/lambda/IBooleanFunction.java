package de.invesdwin.util.concurrent.lambda;

@FunctionalInterface
public interface IBooleanFunction<T> {

    boolean apply(T t);

}
