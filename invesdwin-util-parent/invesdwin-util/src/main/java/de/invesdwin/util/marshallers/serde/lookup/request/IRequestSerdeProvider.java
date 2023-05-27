package de.invesdwin.util.marshallers.serde.lookup.request;

import de.invesdwin.util.marshallers.serde.ISerde;

public interface IRequestSerdeProvider {

    ISerde<Object> getSerde(Object[] requestArgs, int index);

}
