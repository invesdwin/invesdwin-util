package de.invesdwin.util.collections.array;

import java.util.List;

import javax.annotation.Nullable;

import de.invesdwin.util.lang.Objects;

public interface IPrimitiveArrayId {

    int ID_CLOSED = -1;
    int ID_EMPTY = 0;
    int ID_DISABLED = 1;
    int ID_ZERO = 10;

    /**
     * For storage associated to the java process (OnHeap/OffHeap) this can be the System.identityHashCode of the
     * underlying array or buffer. For storage that can be reused between processes, this has to be a stored id that is
     * unique to this primitive array and stays the same between process restarts or when multiple processes share the
     * same storage (see FlyweightPrimitiveArrayAllocator).
     * 
     * Derived/Delegate/Wrapped/Sliced instances around another share the same storage id without modifications. When
     * multiple primitive arrays are combined, the IDs are hashed deterministically with IPrimitiveArrayId.newId(...).
     */
    int getId();

    static int getId(final IPrimitiveArrayId provider) {
        if (provider == null) {
            return 0;
        } else {
            return provider.getId();
        }
    }

    static int newId(final IPrimitiveArrayId o1, final IPrimitiveArrayId o2) {
        return Objects.hashCode(getId(o1), getId(o2));
    }

    static int newId(final IPrimitiveArrayId o1, final IPrimitiveArrayId o2, final IPrimitiveArrayId o3) {
        return Objects.hashCode(getId(o1), getId(o2), getId(o3), getId(o3));
    }

    static int newId(final IPrimitiveArrayId o1, final IPrimitiveArrayId o2, final IPrimitiveArrayId o3,
            final IPrimitiveArrayId o4) {
        return Objects.hashCode(getId(o1), getId(o2), getId(o3), getId(o3), getId(o4));
    }

    static int newId(final IPrimitiveArrayId o1, final IPrimitiveArrayId o2, final IPrimitiveArrayId o3,
            final IPrimitiveArrayId o4, final IPrimitiveArrayId o5) {
        return Objects.hashCode(getId(o1), getId(o2), getId(o3), getId(o3), getId(o4), getId(o5));
    }

    static int newId(final IPrimitiveArrayId o1, final IPrimitiveArrayId o2, final IPrimitiveArrayId o3,
            final IPrimitiveArrayId o4, final IPrimitiveArrayId o5, final IPrimitiveArrayId o6) {
        return Objects.hashCode(getId(o1), getId(o2), getId(o3), getId(o3), getId(o4), getId(o5), getId(o6));
    }

    static int newId(@Nullable final IPrimitiveArrayId... objects) {
        if (objects == null) {
            return 0;
        }
        final int prime = 31;
        int result = 1;
        for (int i = 0; i < objects.length; i++) {
            final IPrimitiveArrayId element = objects[i];
            result = prime * result + Objects.hashCode(getId(element));
        }
        return result;
    }

    static int newId(@Nullable final List<? extends IPrimitiveArrayId> objects) {
        if (objects == null) {
            return 0;
        }
        final int prime = 31;
        int result = 1;
        for (int i = 0; i < objects.size(); i++) {
            final IPrimitiveArrayId element = objects.get(i);
            result = prime * result + Objects.hashCode(getId(element));
        }
        return result;
    }

}
