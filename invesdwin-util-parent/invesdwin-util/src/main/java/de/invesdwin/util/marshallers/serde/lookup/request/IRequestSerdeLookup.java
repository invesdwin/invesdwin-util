package de.invesdwin.util.marshallers.serde.lookup.request;

import java.lang.reflect.Method;

import de.invesdwin.util.marshallers.serde.ISerde;

public interface IRequestSerdeLookup {

    ISerde<Object[]> lookup(Method method);

}
