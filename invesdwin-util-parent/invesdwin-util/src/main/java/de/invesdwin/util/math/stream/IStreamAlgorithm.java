package de.invesdwin.util.math.stream;

public interface IStreamAlgorithm<I, O> {

    O process(I value);

}
