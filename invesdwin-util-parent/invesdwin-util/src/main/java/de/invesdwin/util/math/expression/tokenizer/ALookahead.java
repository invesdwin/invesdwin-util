package de.invesdwin.util.math.expression.tokenizer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ALookahead<T> {

    protected List<T> itemBuffer = new ArrayList<T>();

    protected boolean endReached = false;

    protected T endOfInputIndicator;

    private T current;

    protected void init() {
        itemBuffer.clear();
        endReached = false;
        current = null;
    }

    public T current() {
        if (current == null) {
            current = next(0);
        }
        return current;
    }

    public T next() {
        return next(1);
    }

    public T next(final int offset) {
        if (offset < 0) {
            throw new IllegalArgumentException("offset < 0");
        } else if (offset > 0) {
            current = null;
        }
        while (itemBuffer.size() <= offset && !endReached) {
            final T item = fetch();
            if (item != null) {
                itemBuffer.add(item);
            } else {
                endReached = true;
            }
        }
        if (offset >= itemBuffer.size()) {
            if (endOfInputIndicator == null) {
                endOfInputIndicator = endOfInput();
            }
            return endOfInputIndicator;
        } else {
            return itemBuffer.get(offset);
        }
    }

    protected abstract T endOfInput();

    protected abstract T fetch();

    public T consume() {
        final T result = current();
        consume(1);
        return result;
    }

    //CHECKSTYLE:OFF
    public void consume(int numberOfItems) {
        current = null;
        if (numberOfItems < 0) {
            throw new IllegalArgumentException("numberOfItems < 0");
        }
        while (numberOfItems-- > 0) {
            //CHECKSTYLE:ON
            if (!itemBuffer.isEmpty()) {
                itemBuffer.remove(0);
            } else {
                if (endReached) {
                    return;
                }
                final T item = fetch();
                if (item == null) {
                    endReached = true;
                }
            }
        }
    }

}
