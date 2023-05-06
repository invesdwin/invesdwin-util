package de.invesdwin.util.marshallers.serde.lookup.response;

import de.invesdwin.util.marshallers.serde.ISerde;

public interface IResponseSerdeProvider {

    ISerde<Object> getSerde(Object[] requestArgs);

}
