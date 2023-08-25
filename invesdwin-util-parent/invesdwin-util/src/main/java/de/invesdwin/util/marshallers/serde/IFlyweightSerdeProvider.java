package de.invesdwin.util.marshallers.serde;

/**
 * Marker interface to add to a serde to indicate that a flyweight option might be available (for storages where this is
 * supported).
 */
public interface IFlyweightSerdeProvider<E> {

    /**
     * Can return null here when flyweight serde is not actually available.
     */
    ISerde<E> asFlyweightSerde();

}
