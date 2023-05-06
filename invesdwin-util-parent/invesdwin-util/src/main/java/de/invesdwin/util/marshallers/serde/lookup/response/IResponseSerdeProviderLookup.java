package de.invesdwin.util.marshallers.serde.lookup.response;

import java.lang.reflect.Method;

public interface IResponseSerdeProviderLookup {

    IResponseSerdeProvider lookup(Method method);

}
