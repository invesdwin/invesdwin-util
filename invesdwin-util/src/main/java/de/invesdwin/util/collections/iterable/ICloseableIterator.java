package de.invesdwin.util.collections.iterable;

import java.io.Closeable;
import java.util.Iterator;

public interface ICloseableIterator<E> extends Iterator<E>, Closeable {

}
