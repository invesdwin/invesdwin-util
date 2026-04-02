package de.invesdwin.util.collections.array.large;

import java.util.List;

import javax.annotation.Nullable;

import de.invesdwin.util.collections.array.base.IBaseArrayId;
import de.invesdwin.util.lang.Objects;

public interface ILargeArrayId extends IBaseArrayId {

    static int getId(final ILargeArrayId provider) {
        if (provider == null) {
            return 0;
        } else {
            return provider.getId();
        }
    }

    static int newId(final ILargeArrayId o1, final ILargeArrayId o2) {
        return Objects.hashCode(getId(o1), getId(o2));
    }

    static int newId(final ILargeArrayId o1, final ILargeArrayId o2, final ILargeArrayId o3) {
        return Objects.hashCode(getId(o1), getId(o2), getId(o3), getId(o3));
    }

    static int newId(final ILargeArrayId o1, final ILargeArrayId o2, final ILargeArrayId o3, final ILargeArrayId o4) {
        return Objects.hashCode(getId(o1), getId(o2), getId(o3), getId(o3), getId(o4));
    }

    static int newId(final ILargeArrayId o1, final ILargeArrayId o2, final ILargeArrayId o3, final ILargeArrayId o4,
            final ILargeArrayId o5) {
        return Objects.hashCode(getId(o1), getId(o2), getId(o3), getId(o3), getId(o4), getId(o5));
    }

    static int newId(final ILargeArrayId o1, final ILargeArrayId o2, final ILargeArrayId o3, final ILargeArrayId o4,
            final ILargeArrayId o5, final ILargeArrayId o6) {
        return Objects.hashCode(getId(o1), getId(o2), getId(o3), getId(o3), getId(o4), getId(o5), getId(o6));
    }

    static int newId(@Nullable final ILargeArrayId... objects) {
        if (objects == null) {
            return 0;
        }
        final int prime = 31;
        int result = 1;
        for (int i = 0; i < objects.length; i++) {
            final ILargeArrayId element = objects[i];
            result = prime * result + Objects.hashCode(getId(element));
        }
        return result;
    }

    static int newId(@Nullable final List<? extends ILargeArrayId> objects) {
        if (objects == null) {
            return 0;
        }
        final int prime = 31;
        int result = 1;
        for (int i = 0; i < objects.size(); i++) {
            final ILargeArrayId element = objects.get(i);
            result = prime * result + Objects.hashCode(getId(element));
        }
        return result;
    }

}
