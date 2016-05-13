package de.invesdwin.util.bean;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.persistence.Transient;

import com.mysema.query.annotations.QuerySupertype;

import de.invesdwin.norva.apt.constants.BeanPathRoot;
import de.invesdwin.norva.beanpath.annotation.Hidden;
import de.invesdwin.norva.beanpath.impl.object.BeanObjectContext;
import de.invesdwin.norva.beanpath.impl.object.BeanObjectProcessor;
import de.invesdwin.norva.beanpath.spi.element.IPropertyBeanPathElement;
import de.invesdwin.norva.beanpath.spi.visitor.SimpleBeanPathVisitorSupport;
import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.bean.internal.ValueObjectMerge;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.Objects;

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
    private DirtyTracker dirtyTracker;

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
            final BeanObjectContext ctxThis = new BeanObjectContext(this);
            try {
                new BeanObjectProcessor(ctxThis, new SimpleBeanPathVisitorSupport(ctxThis) {
                    @Override
                    public void visitProperty(final IPropertyBeanPathElement e) {
                        final Object valueThis = e.getModifier().getValue();
                        final Object valueObj = e.getModifier().getValueFromRoot(obj);
                        if (!Objects.equals(valueThis, valueObj)) {
                            throw new NotEqualRuntimeException();
                        }
                    }
                }).process();
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

    private static class NotEqualRuntimeException extends RuntimeException {
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
        innerMergeFrom(o, overwrite, false, new HashSet<String>());
    }

    protected void innerMergeFrom(final Object o, final boolean overwrite, final boolean clone,
            final Set<String> recursionFilter) {
        new ValueObjectMerge(this, overwrite, clone, new HashSet<String>()).merge(o);
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
        clone.innerMergeFrom(this, true, true, new HashSet<String>());
        return clone;
    }

    @Hidden(skip = true)
    @Transient
    public synchronized DirtyTracker dirtyTracker() {
        if (dirtyTracker == null) {
            dirtyTracker = new DirtyTracker(this);
        }
        return dirtyTracker;
    }

}
