package de.invesdwin.util.marshallers.serde.large;

public interface ILargeSerdeLengthProvider<O> {

    long getLength(O obj);

}
