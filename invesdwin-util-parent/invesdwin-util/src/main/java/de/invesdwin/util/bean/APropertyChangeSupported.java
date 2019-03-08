package de.invesdwin.util.bean;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Field;
import java.util.Map;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QuerySupertype;

import de.invesdwin.norva.beanpath.annotation.Hidden;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.Reflections;

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
    @JsonIgnore
    private transient PropertyChangeSupport propertyChangeSupport;
    @Transient
    @JsonIgnore
    private final Object propertyChangeSupportLock = new Object();

    static {
        Objects.REFLECTION_EXCLUDED_FIELDS.add("propertyChangeSupportLock");
        Objects.REFLECTION_EXCLUDED_FIELDS.add("propertyChangeSupport");
        //need to remove listeners from innerMergeFrom
        Objects.REFLECTION_EXCLUDED_FIELDS.add("propertyChangeListeners");

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
    @JsonIgnore
    public final PropertyChangeListener[] getPropertyChangeListeners() {
        final PropertyChangeSupport ref = lazyGetPropertyChangeSupport(true);
        return ref.getPropertyChangeListeners();
    }

    /**
     * @see PropertyChangeSupported#getPropertyChangeListeners(java.lang.String)
     */
    @Transient
    @Hidden(skip = true)
    @JsonIgnore
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
    public void fireIndexedPropertyChange(final String propertyName, final int index, final Object oldValue,
            final Object newValue) {
        final PropertyChangeSupport ref = lazyGetPropertyChangeSupport(false);
        if (ref != null && (!Objects.equalsProperty(oldValue, newValue)
                || !equalsPropertyChangeListeners(oldValue, newValue))) {
            final IndexedPropertyChangeEvent event = new IndexedPropertyChangeEvent(this, propertyName, oldValue,
                    newValue, index);
            fireEvent(ref, propertyName, event);
        }
    }

    /**
     * This is the same as in the original java beans implementation with the exception that changes from null to null
     * are not reported as property change events!
     * 
     * @see java.beans.PropertyChangeSupport#firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
     */
    public void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
        final PropertyChangeSupport ref = lazyGetPropertyChangeSupport(false);
        if (ref != null && (!Objects.equalsProperty(oldValue, newValue)
                || !equalsPropertyChangeListeners(oldValue, newValue))) {
            final PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
            fireEvent(ref, propertyName, event);
        }
    }

    /**
     * Performance optimization to just fire the events instead of having PropertyChangeSupport call equals so often.
     * Also with this we don't have to rely on equals alone to fire an event.
     */
    @SuppressWarnings("unchecked")
    private static void fireEvent(final PropertyChangeSupport ref, final String propertyName,
            final PropertyChangeEvent event) {
        final Field mapField = Reflections.findField(PropertyChangeSupport.class, "map");
        Reflections.makeAccessible(mapField);
        final Object map = Reflections.getField(mapField, ref);
        final Field mapMapField = Reflections.findField(map.getClass(), "map");
        Reflections.makeAccessible(mapMapField);
        final Map<String, PropertyChangeListener[]> mapMap = (Map<String, PropertyChangeListener[]>) Reflections
                .getField(mapMapField, map);
        if (mapMap == null) {
            return;
        }
        final PropertyChangeListener[] common = mapMap.get(null);
        fireEvent(common, event);
        if (propertyName != null) {
            final PropertyChangeListener[] named = mapMap.get(propertyName);
            fireEvent(named, event);
        }
    }

    private static void fireEvent(final PropertyChangeListener[] listeners, final PropertyChangeEvent event) {
        if (listeners != null) {
            for (final PropertyChangeListener listener : listeners) {
                listener.propertyChange(event);
            }
        }
    }

    private static boolean equalsPropertyChangeListeners(final Object oldValue, final Object newValue) {
        final APropertyChangeSupported cOldValue;
        if (oldValue instanceof APropertyChangeSupported) {
            cOldValue = (APropertyChangeSupported) oldValue;
        } else {
            cOldValue = null;
        }
        final APropertyChangeSupported cNewValue;
        if (newValue instanceof APropertyChangeSupported) {
            cNewValue = (APropertyChangeSupported) newValue;
        } else {
            cNewValue = null;
        }
        //one of them null
        if ((cOldValue == null) != (cNewValue == null)) {
            return false;
        }
        //both null
        if (cOldValue == null) {
            return true;
        }
        final PropertyChangeSupport oldValueRef = cOldValue.lazyGetPropertyChangeSupport(false);
        final PropertyChangeSupport newValueRef = cNewValue.lazyGetPropertyChangeSupport(false);
        //one of them null
        if ((oldValueRef == null) != (newValueRef == null)) {
            return false;
        }
        //both null
        if (oldValueRef == null) {
            return true;
        }
        return Objects.equals(oldValueRef.getPropertyChangeListeners(), newValueRef.getPropertyChangeListeners());
    }

}