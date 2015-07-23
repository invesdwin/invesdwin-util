package de.invesdwin.util.bean;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.persistence.Transient;

import org.apache.commons.beanutils.BeanUtilsBean;

import com.mysema.query.annotations.QuerySupertype;

import de.invesdwin.norva.apt.constants.BeanPathRoot;
import de.invesdwin.norva.beanpath.annotation.Hidden;
import de.invesdwin.norva.beanpath.impl.object.BeanObjectContext;
import de.invesdwin.norva.beanpath.impl.object.BeanObjectProcessor;
import de.invesdwin.norva.beanpath.spi.element.APropertyBeanPathElement;
import de.invesdwin.norva.beanpath.spi.visitor.SimpleBeanPathVisitorSupport;
import de.invesdwin.norva.marker.ISerializableValueObject;
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
public abstract class AValueObject extends APropertyChangeSupported implements Comparable<Object>, Cloneable,
        ISerializableValueObject {

    /**
     * @see <a
     *      href="http://apache-commons.680414.n4.nabble.com/Setting-null-on-Integer-property-via-BeanUtils-setProperty-td955955.html">Null
     *      handling</a>
     */
    @GuardedBy("this")
    private static BeanUtilsBean beanUtilsBean;
    @GuardedBy("this")
    @Transient
    private DirtyTracker dirtyTracker;

    static {
        Objects.REFLECTION_EXCLUDED_FIELDS.add("beanUtilsBean");
        Objects.REFLECTION_EXCLUDED_FIELDS.add("dirtyTracker");
    }

    private static synchronized BeanUtilsBean getBeanUtilsBean() {
        if (beanUtilsBean == null) {
            beanUtilsBean = new BeanUtilsBean();
            //Set defaults for BeanUtils.
            beanUtilsBean.getConvertUtils().register(false, true, 0);
        }
        return beanUtilsBean;
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
                    public void visitProperty(final APropertyBeanPathElement e) {
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
                    throw t;
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
        innerMergeFrom(o, overwrite, new HashSet<String>());
    }

    protected void innerMergeFrom(final Object o, final boolean overwrite, final Set<String> recursionFilter) {
        final BeanUtilsBean beanUtilsBean = getBeanUtilsBean();
        for (final PropertyDescriptor thereDesc : beanUtilsBean.getPropertyUtils().getPropertyDescriptors(o)) {
            try {
                final Object valueThere = thereDesc.getReadMethod().invoke(o);
                if (valueThere != null) {
                    final PropertyDescriptor thisDesc = beanUtilsBean.getPropertyUtils().getPropertyDescriptor(this,
                            thereDesc.getName());
                    if (thisDesc == null) {
                        continue;
                    }

                    copyValue(overwrite, recursionFilter, thisDesc, thereDesc, valueThere);
                }
            } catch (final Throwable e) { //SUPPRESS CHECKSTYLE empty statement
                //ignore (no setter for property class etc, or getter without setter)
            }
        }
    }

    private void copyValue(final boolean overwrite, final Set<String> recursionFilter,
            final PropertyDescriptor thisDesc, final PropertyDescriptor thereDesc, final Object valueThere)
            throws IllegalAccessException, InvocationTargetException {
        boolean copy = true;
        final Object valueHere = thisDesc.getReadMethod().invoke(this);
        if (valueHere != null) {
            if (valueHere instanceof AValueObject) {
                final AValueObject vo = (AValueObject) valueHere;
                if (recursionFilter.add(Objects.toStringIdentity(vo))) {
                    vo.innerMergeFrom(valueThere, overwrite, recursionFilter);
                }
                copy = false;
            } else {
                copy = overwrite;
            }
        }

        if (copy) {
            getBeanUtilsBean().copyProperty(this, thereDesc.getName(), valueThere);
        }
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
    @Transient
    public synchronized DirtyTracker dirtyTracker() {
        if (dirtyTracker == null) {
            dirtyTracker = new DirtyTracker(this);
        }
        return dirtyTracker;
    }

}
