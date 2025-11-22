package de.invesdwin.util.collections.attributes;

import java.util.Map;
import java.util.function.Supplier;

public interface IAttributesMapContract extends Map<String, Object> {

    <T> T getOrCreate(String key, Supplier<T> createSupplier);

}
