package de.invesdwin.util.collections.attributes;

import java.util.function.Supplier;

public interface IAttributesMap extends IAttributesMapContract {

    @Override
    <T> T getOrCreate(String key, Supplier<T> createSupplier);

    IAttributesMapContract getSoft();

    IAttributesMapContract getWeak();

}
