package de.invesdwin.util.marshallers.serde;

import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;

@ThreadSafe
public final class FlyweightSerdeProviders {

    private static final Set<String> FLYWEIGHT_SERDE_WARNINGS = ILockCollectionFactory.getInstance(true)
            .newConcurrentSet();
    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory
            .getXLogger(FlyweightSerdeProviders.class);

    private FlyweightSerdeProviders() {}

    public static <T> ISerde<T> extractSerde(final ISerde<T> serdeProvider) {
        final ISerde<T> flyweightSerde = extractFlyweightSerde(serdeProvider);
        if (flyweightSerde != null) {
            return flyweightSerde;
        } else {
            return serdeProvider;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> ISerde<T> extractFlyweightSerde(final ISerde<T> serdeProvider) {
        if (serdeProvider instanceof IFlyweightSerdeProvider) {
            final IFlyweightSerdeProvider<T> flyweightSerdeProvider = (IFlyweightSerdeProvider<T>) serdeProvider;
            final ISerde<T> flyweightSerde = flyweightSerdeProvider.asFlyweightSerde();
            if (flyweightSerde != null) {
                return flyweightSerde;
            }
        }
        final String str = serdeProvider.toString();
        if (FLYWEIGHT_SERDE_WARNINGS.add(str)) {
            //CHECKSTYLE:OFF
            LOG.warn("Not a {}: {}", IFlyweightSerdeProvider.class.getSimpleName(), str);
            //CHECKSTYLE:ON
        }
        return null;
    }

}
