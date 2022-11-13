package de.invesdwin.util.bean;

import java.beans.PropertyChangeSupport;

import javax.annotation.concurrent.ThreadSafe;

import com.querydsl.core.annotations.QuerySupertype;

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
@SuppressWarnings("restriction")
@ThreadSafe
@QuerySupertype
public abstract class APropertyChangeSupported extends APropertyChangeSupportedBase {

}