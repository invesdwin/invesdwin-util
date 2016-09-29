package de.invesdwin.util.bean.tuple;

import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.lang.Objects;

@SuppressWarnings("serial")
@Immutable
public class KeyIdentityEntry<K, V> extends AValueObject implements Entry<K, V> {

    private final K key;
    private final V value;

    protected KeyIdentityEntry(final K key, final V value) {
        this.key = key;
        this.value = value;
    }

    public static <K, V> KeyIdentityEntry<K, V> of(final K key, final V value) {
        return new KeyIdentityEntry<K, V>(key, value);
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        //a bit inaccurate but combining hashcode with class is too slow here when working with FDate in AHistoricalCache
        return getKey().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof KeyIdentityEntry) {
            final KeyIdentityEntry<?, ?> castObj = (KeyIdentityEntry<?, ?>) obj;
            return Objects.equals(getKey(), castObj.getKey());
        } else {
            return false;
        }
    }

    @Override
    public V setValue(final V value) {
        throw new UnsupportedOperationException();
    }

}
