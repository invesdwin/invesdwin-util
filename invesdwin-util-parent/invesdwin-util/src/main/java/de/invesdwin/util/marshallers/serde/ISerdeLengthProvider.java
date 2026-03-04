package de.invesdwin.util.marshallers.serde;

public interface ISerdeLengthProvider<O> {

    int getLength(O obj);

}
