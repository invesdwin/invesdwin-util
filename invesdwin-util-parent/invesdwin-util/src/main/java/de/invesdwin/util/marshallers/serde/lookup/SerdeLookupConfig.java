package de.invesdwin.util.marshallers.serde.lookup;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.lookup.request.DefaultRequestSerdeProviderLookup;
import de.invesdwin.util.marshallers.serde.lookup.request.IRequestSerdeProviderLookup;
import de.invesdwin.util.marshallers.serde.lookup.response.DefaultResponseSerdeProviderLookup;
import de.invesdwin.util.marshallers.serde.lookup.response.IResponseSerdeProviderLookup;

@Immutable
public class SerdeLookupConfig {

    public static final SerdeLookupConfig DEFAULT = new SerdeLookupConfig(
            DefaultRequestSerdeProviderLookup.INSTANCE, DefaultResponseSerdeProviderLookup.INSTANCE);

    private final IRequestSerdeProviderLookup requestLookup;
    private final IResponseSerdeProviderLookup responseLookup;

    public SerdeLookupConfig(final IRequestSerdeProviderLookup requestLookup,
            final IResponseSerdeProviderLookup responseLookup) {
        this.requestLookup = requestLookup;
        this.responseLookup = responseLookup;
    }

    public IRequestSerdeProviderLookup getRequestLookup() {
        return requestLookup;
    }

    public IResponseSerdeProviderLookup getResponseLookup() {
        return responseLookup;
    }

}
