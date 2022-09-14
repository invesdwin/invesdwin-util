package de.invesdwin.util.collections.circular;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class CircularGenericArray<E> {

    private final Object[] array;
    private final int capacity;
    private int size = 0;
    private int startArrayIndex = -1;
    private int endArrayIndex = -1;

    public CircularGenericArray(final int capacity) {
        this.array = new Object[capacity];
        this.capacity = array.length;
        if (capacity == 0) {
            throw new IllegalArgumentException("size should not be 0");
        }
    }

    /**
     * The last value has index 0, the value before the last has index 1.
     */
    @SuppressWarnings("unchecked")
    public E getReverse(final int index) {
        return (E) array[arrayIndexReverse(index)];
    }

    /**
     * The first value has index 0, the value after the first has index 1.
     */
    @SuppressWarnings("unchecked")
    public E get(final int index) {
        return (E) array[arrayIndex(index)];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return capacity;
    }

    public void add(final E value) {
        pretendAdd();
        array[endArrayIndex] = value;
    }

    public void pretendAdd() {
        endArrayIndex++;
        if (endArrayIndex >= capacity) {
            endArrayIndex = 0;
        }
        if (size == 0) {
            startArrayIndex = 0;
        }
        if (size == capacity) {
            startArrayIndex++;
            if (startArrayIndex >= capacity) {
                startArrayIndex = 0;
            }
        } else {
            size++;
        }
    }

    private int arrayIndexReverse(final int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException(
                    "index[" + index + "] should not be greater or equal to size[" + size + "]");
        }
        if (index < 0) {
            throw new IndexOutOfBoundsException("index[" + index + "] should not be negative");
        }
        final int arrayIndex = endArrayIndex - index;
        if (arrayIndex < 0) {
            return arrayIndex + size;
        } else {
            return arrayIndex;
        }
    }

    private int arrayIndex(final int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException(
                    "index[" + index + "] should not be greater or equal to size[" + size + "]");
        }
        if (index < 0) {
            throw new IndexOutOfBoundsException("index[" + index + "] should not be negative");
        }
        final int arrayIndex = startArrayIndex + index;
        if (arrayIndex >= size) {
            return arrayIndex - size;
        } else {
            return arrayIndex;
        }
    }

    @Override
    public String toString() {
        return toString(0, size);
    }

    public String toString(final int index, final int size) {
        final StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(get(i));
        }
        sb.append("]");
        return sb.toString();
    }

    public String toStringReverse(final int index, final int size) {
        final StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(getReverse(i));
        }
        sb.append("]");
        return sb.toString();
    }

}
