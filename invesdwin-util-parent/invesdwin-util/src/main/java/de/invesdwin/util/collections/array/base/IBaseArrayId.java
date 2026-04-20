package de.invesdwin.util.collections.array.base;

import java.util.List;

import javax.annotation.Nullable;

import de.invesdwin.util.lang.Objects;

public interface IBaseArrayId {

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

    static int getId(final IBaseArrayId provider) {
        if (provider == null) {
            return 0;
        } else {
            return provider.getId();
        }
    }

    static int newId(final IBaseArrayId o1, final IBaseArrayId o2) {
        return Objects.hashCode(getId(o1), getId(o2));
    }

    static int newId(final IBaseArrayId o1, final IBaseArrayId o2, final IBaseArrayId o3) {
        return Objects.hashCode(getId(o1), getId(o2), getId(o3), getId(o3));
    }

    static int newId(final IBaseArrayId o1, final IBaseArrayId o2, final IBaseArrayId o3,
            final IBaseArrayId o4) {
        return Objects.hashCode(getId(o1), getId(o2), getId(o3), getId(o3), getId(o4));
    }

    static int newId(final IBaseArrayId o1, final IBaseArrayId o2, final IBaseArrayId o3,
            final IBaseArrayId o4, final IBaseArrayId o5) {
        return Objects.hashCode(getId(o1), getId(o2), getId(o3), getId(o3), getId(o4), getId(o5));
    }

    static int newId(final IBaseArrayId o1, final IBaseArrayId o2, final IBaseArrayId o3,
            final IBaseArrayId o4, final IBaseArrayId o5, final IBaseArrayId o6) {
        return Objects.hashCode(getId(o1), getId(o2), getId(o3), getId(o3), getId(o4), getId(o5), getId(o6));
    }

    static int newId(@Nullable final IBaseArrayId... objects) {
        if (objects == null) {
            return 0;
        }
        final int prime = 31;
        int result = 1;
        for (int i = 0; i < objects.length; i++) {
            final IBaseArrayId element = objects[i];
            result = prime * result + Objects.hashCode(getId(element));
        }
        return result;
    }

    static int newId(@Nullable final List<? extends IBaseArrayId> objects) {
        if (objects == null) {
            return 0;
        }
        final int prime = 31;
        int result = 1;
        for (int i = 0; i < objects.size(); i++) {
            final IBaseArrayId element = objects.get(i);
            result = prime * result + Objects.hashCode(getId(element));
        }
        return result;
    }

}
