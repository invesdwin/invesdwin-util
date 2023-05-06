package de.invesdwin.util.marshallers.serde.lookup;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.lookup.request.DefaultRequestSerdeLookup;
import de.invesdwin.util.marshallers.serde.lookup.request.IRequestSerdeLookup;
import de.invesdwin.util.marshallers.serde.lookup.response.DefaultResponseSerdeProviderLookup;
import de.invesdwin.util.marshallers.serde.lookup.response.IResponseSerdeProviderLookup;

@Immutable
public class SerdeLookupConfig {

    public static final SerdeLookupConfig DEFAULT = new SerdeLookupConfig(
            DefaultRequestSerdeLookup.INSTANCE, DefaultResponseSerdeProviderLookup.INSTANCE);

    private final IRequestSerdeLookup requestLookup;
    private final IResponseSerdeProviderLookup responseLookup;

    public SerdeLookupConfig(final IRequestSerdeLookup requestLookup,
            final IResponseSerdeProviderLookup responseLookup) {
        this.requestLookup = requestLookup;
        this.responseLookup = responseLookup;
    }

    public IRequestSerdeLookup getRequestLookup() {
        return requestLookup;
    }

    public IResponseSerdeProviderLookup getResponseLookup() {
        return responseLookup;
    }

}
