package de.invesdwin.util.collections.delegate.unmodifiable;

import java.util.Set;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class AUnmodifiableSet<E> extends AUnmodifiableCollection<E> implements Set<E> {

}
