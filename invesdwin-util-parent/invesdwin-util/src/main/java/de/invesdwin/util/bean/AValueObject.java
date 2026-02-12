package de.invesdwin.util.bean;

import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QuerySupertype;

import de.invesdwin.norva.apt.constants.BeanPathRoot;
import de.invesdwin.norva.beanpath.annotation.Hidden;
import de.invesdwin.norva.beanpath.impl.clazz.BeanClassProcessor;
import de.invesdwin.norva.beanpath.impl.clazz.BeanClassProcessorConfig;
import de.invesdwin.norva.beanpath.spi.element.IPropertyBeanPathElement;
import de.invesdwin.norva.beanpath.spi.visitor.SimpleBeanPathVisitorSupport;
import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.bean.internal.ValueObjectMerge;
import de.invesdwin.util.collections.factory.pool.set.ICloseableSet;
import de.invesdwin.util.collections.factory.pool.set.PooledSet;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.Objects;
import jakarta.persistence.Transient;

/**
 * ValueObjects are non persistent Entities. They do not contain any logic, but they contain data and verifications.
 * 
 * This class implements toString, hashCode, equals und compareTo methods via reflection.
 * 
 * Clone is done by serialization to ensure deep copies.
 * 
 * @author subes
 * 
 */
@SuppressWarnings("serial")
@ThreadSafe
@QuerySupertype
@BeanPathRoot
public abstract class AValueObject extends APropertyChangeSupported
        implements Comparable<Object>, Cloneable, ISerializableValueObject {

    @GuardedBy("this")
    @Transient
    @JsonIgnore
    private transient DirtyTracker dirtyTracker;

    static {
        Objects.REFLECTION_EXCLUDED_FIELDS.add("beanUtilsBean");
        Objects.REFLECTION_EXCLUDED_FIELDS.add("dirtyTracker");
    }

    @Override
    public String toString() {
        return Objects.toString(this);
    }

    @Hidden(skip = true)
    public String toStringMultiline() {
        return Objects.toStringMultiline(this);
    }

    @Override
    public int hashCode() {
        return Objects.reflectionHashCode(this);
    }

    @Override
    public boolean equals(final Object obj) {
        return Objects.reflectionEquals(this, obj);
    }

    /**
     * This method checks values by bean path which are not hidden by annotations or utility methods. Thus can be used
     * to compare values of entities without regards to id, time and version properties.
     */
    @Hidden(skip = true)
    public boolean equalsByBeanPathValues(final Object obj) {
        if (obj != null && getClass().isAssignableFrom(obj.getClass())) {
            try {
                BeanClassProcessor.process(BeanClassProcessorConfig.getDefault(AValueObject.this.getClass()),
                        new SimpleBeanPathVisitorSupport() {
                            @Override
                            public void visitProperty(final IPropertyBeanPathElement e) {
                                final Object valueThis = e.getModifier().getValueFromRoot(AValueObject.this);
                                final Object valueObj = e.getModifier().getValueFromRoot(obj);
                                if (!Objects.equals(valueThis, valueObj)) {
                                    throw new NotEqualRuntimeException();
                                }
                            }
                        });
                return true;
            } catch (final Throwable t) {
                if (Throwables.isCausedByType(t, NotEqualRuntimeException.class)) {
                    return false;
                } else {
                    throw Throwables.propagate(t);
                }
            }
        } else {
            return false;
        }
    }

    private static final class NotEqualRuntimeException extends RuntimeException {
        @Override
        public synchronized Throwable fillInStackTrace() {
            if (Throwables.isDebugStackTraceEnabled()) {
                return super.fillInStackTrace();
            } else {
                return this; // no stack trace for performance
            }
        }

    }

    @Override
    public int compareTo(final Object o) {
        return Objects.reflectionCompareTo(this, o);
    }

    /**
     * Same as mergeFrom(o, true).
     */
    @Hidden(skip = true)
    public void mergeFrom(final Object o) {
        mergeFrom(o, true);
    }

    /**
     * Copies via reflection all matching getters and setters that are public.
     * 
     * Null values do not get copied.
     * 
     * If overwrite=true, then non null fields will be overwritten aswell. Else they are kept as they are.
     * 
     * The values of AValueObject fields are recursively copied if already set. Else the AValueObject field is directly
     * copied.
     * 
     * Entity values like id, version, created etc are preserved on merge.
     */
    @Hidden(skip = true)
    public final void mergeFrom(final Object o, final boolean overwrite) {
        try (ICloseableSet<String> recursionFilter = PooledSet.getInstance()) {
            innerMergeFrom(o, overwrite, false, recursionFilter);
        }
    }

    protected void innerMergeFrom(final Object o, final boolean overwrite, final boolean clone,
            final Set<String> recursionFilter) {
        new ValueObjectMerge(this, overwrite, clone, recursionFilter).merge(o);
    }

    /**
     * Per convention this creates a deep copy; this default implementation might be a bit slow because of serialization
     * being used. But on the other hand this reduces development effort by a manifold and optimizations need only be
     * done where needed.
     */
    @Override
    public AValueObject clone() { //SUPPRESS CHECKSTYLE super.clone()
        return Objects.deepClone(this);
    }

    @Hidden(skip = true)
    @SuppressWarnings("unchecked")
    public final <T extends AValueObject> T cloneGeneric() {
        return (T) clone();
    }

    /**
     * Can be used if a faster implementation of clone() if needed.
     */
    @Hidden(skip = true)
    public AValueObject shallowClone() {
        try {
            return (AValueObject) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Hidden(skip = true)
    public AValueObject shallowCloneReflective() {
        final AValueObject clone = shallowClone();
        try (ICloseableSet<String> recursionFilter = PooledSet.getInstance()) {
            clone.innerMergeFrom(this, true, true, recursionFilter);
        }
        return clone;
    }

    @Hidden(skip = true)
    @Transient
    @JsonIgnore
    public synchronized DirtyTracker dirtyTracker() {
        if (dirtyTracker == null) {
            dirtyTracker = new DirtyTracker(this);
        }
        return dirtyTracker;
    }

}
