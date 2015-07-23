package de.invesdwin.util.bean;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.persistence.Transient;

import com.mysema.query.annotations.QuerySupertype;

import de.invesdwin.norva.beanpath.annotation.Hidden;
import de.invesdwin.util.lang.Objects;

/**
 * Standard implementation of {@link PropertyChangeSupported}. This class is thread-safe and uses a lazily initialized
 * {@link PropertyChangeSupport} instance to do the heavy lifting. Initialization happens in either of the
 * <code>addPropertyChangeListener</code> methods the first time a listener is added.
 * <p>
 * Implementing classes can safely declare serializability; implementors should be aware that the enclosed
 * <code>PropertyChangeSupport</code> is marked <code>transient</code>.
 * <p>
 * No effort is made to unload the <code>PropertyChangeSupport</code> after the last listener unsubscribes.
 * 
 * Taken from: http://java.sogeti.nl/JavaBlog/2008/03/12/lazily-initialized-propertychangesupport/
 * 
 * @author Barend Garvelink
 * @since 1.0
 */
@ThreadSafe
@QuerySupertype
public abstract class APropertyChangeSupported {

    /**
     * Used to track property change listeners. Lazily initialized. PropertyChangeSupport is itself threadsafe, so we
     * only need to synchronize when accessing this field. Once the caller has a local reference it can safely access
     * any of its methods.
     */
    @GuardedBy("propertyChangeSupportLock")
    @Transient
    private PropertyChangeSupport propertyChangeSupport;
    @Transient
    private final Object propertyChangeSupportLock = new Object();

    static {
        Objects.REFLECTION_EXCLUDED_FIELDS.add("propertyChangeSupportLock");
        //cannot exclude PropertyChangeSupport for equals check since DirtyTracker will not detect new instances otherwise
        Objects.ADDITIONAL_REFLECTION_TO_STRING_EXCLUDED_FIELDS.add("propertyChangeSupport");
    }

    /**
     * Returns the <code>propertyChangeSupport</code> for this instance, lazily initialized if necessary.
     * 
     * @param initializeIfNull
     *            if <code>true</code>, the instance field is initialized if it's <code>null</code>. If fase, the
     *            <code>null</code> is returned.
     * @return the <code>propertyChangeSupport</code> for this instance, which may be <code>null</code> if the
     *         <code>initializeIfNull</code> parameter is <code>false</code>.
     */
    private PropertyChangeSupport lazyGetPropertyChangeSupport(final boolean initializeIfNull) {
        synchronized (propertyChangeSupportLock) {
            if (initializeIfNull && propertyChangeSupport == null) {
                propertyChangeSupport = new PropertyChangeSupport(this);
            }
            return propertyChangeSupport;
        }
    }

    /**
     * @see PropertyChangeSupported#addPropertyChangeListener(java.beans.PropertyChangeListener)
     */
    @Hidden(skip = true)
    public final void addPropertyChangeListener(final PropertyChangeListener listener) {
        if (listener != null) {
            lazyGetPropertyChangeSupport(true).addPropertyChangeListener(listener);
        }
    }

    /**
     * @see PropertyChangeSupported#addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     */
    @Hidden(skip = true)
    public final void addPropertyChangeListener(final String property, final PropertyChangeListener listener) {
        if (listener != null) {
            lazyGetPropertyChangeSupport(true).addPropertyChangeListener(property, listener);
        }
    }

    /**
     * @see PropertyChangeSupported#removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    @Hidden(skip = true)
    public final void removePropertyChangeListener(final PropertyChangeListener listener) {
        final PropertyChangeSupport ref = lazyGetPropertyChangeSupport(false);
        if (ref != null) {
            ref.removePropertyChangeListener(listener);
        }
    }

    /**
     * @see PropertyChangeSupported#removePropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     */
    @Hidden(skip = true)
    public final void removePropertyChangeListener(final String property, final PropertyChangeListener listener) {
        final PropertyChangeSupport ref = lazyGetPropertyChangeSupport(false);
        if (ref != null) {
            ref.removePropertyChangeListener(property, listener);
        }
    }

    /**
     * @see PropertyChangeSupported#hasListeners(java.lang.String)
     */
    @Hidden(skip = true)
    public final boolean hasListeners(final String propertyName) {
        final PropertyChangeSupport ref = lazyGetPropertyChangeSupport(false);
        return ref != null && ref.hasListeners(propertyName);
    }

    @Hidden(skip = true)
    public boolean hasListeners() {
        final PropertyChangeSupport ref = lazyGetPropertyChangeSupport(false);
        return ref != null && ref.getPropertyChangeListeners().length > 0;
    }

    /**
     * @see PropertyChangeSupported#getPropertyChangeListeners()
     */
    @Transient
    @Hidden(skip = true)
    public final PropertyChangeListener[] getPropertyChangeListeners() {
        final PropertyChangeSupport ref = lazyGetPropertyChangeSupport(true);
        return ref.getPropertyChangeListeners();
    }

    /**
     * @see PropertyChangeSupported#getPropertyChangeListeners(java.lang.String)
     */
    @Transient
    @Hidden(skip = true)
    public final PropertyChangeListener[] getPropertyChangeListeners(final String propertyName) {
        final PropertyChangeSupport ref = lazyGetPropertyChangeSupport(true);
        return ref.getPropertyChangeListeners(propertyName);
    }

    /**
     * This is the same as in the original java beans implementation with the exception that changes from null to null
     * are not reported as property change events!
     * 
     * @see java.beans.PropertyChangeSupport#fireIndexedPropertyChange(java.lang.String, int, java.lang.Object,
     *      java.lang.Object)
     */
    public final void fireIndexedPropertyChange(final String propertyName, final int index, final Object oldValue,
            final Object newValue) {
        final PropertyChangeSupport ref = lazyGetPropertyChangeSupport(false);
        if (ref != null && !Objects.equals(oldValue, newValue)) {
            ref.firePropertyChange(new IndexedPropertyChangeEvent(this, propertyName, oldValue, newValue, index));
        }
    }

    /**
     * This is the same as in the original java beans implementation with the exception that changes from null to null
     * are not reported as property change events!
     * 
     * @see java.beans.PropertyChangeSupport#firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
     */
    public final void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
        final PropertyChangeSupport ref = lazyGetPropertyChangeSupport(false);
        if (ref != null && !Objects.equals(oldValue, newValue)) {
            ref.firePropertyChange(new PropertyChangeEvent(this, propertyName, oldValue, newValue));
        }
    }

}