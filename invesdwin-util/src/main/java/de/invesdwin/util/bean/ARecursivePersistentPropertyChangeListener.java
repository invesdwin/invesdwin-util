package de.invesdwin.util.bean;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.norva.beanpath.BeanPathReflections;
import de.invesdwin.norva.beanpath.impl.object.BeanObjectContext;
import de.invesdwin.norva.beanpath.impl.object.BeanObjectProcessor;
import de.invesdwin.norva.beanpath.spi.BeanPathUtil;
import de.invesdwin.norva.beanpath.spi.element.AChoiceBeanPathElement;
import de.invesdwin.norva.beanpath.spi.element.IPropertyBeanPathElement;
import de.invesdwin.norva.beanpath.spi.element.ITableColumnBeanPathElement;
import de.invesdwin.norva.beanpath.spi.visitor.SimpleBeanPathVisitorSupport;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Reflections;
import de.invesdwin.util.lang.Strings;

/**
 * This PropertyChangeListener adds itself recursively to all child bean paths.
 */
@Immutable
@ThreadSafe
public abstract class ARecursivePersistentPropertyChangeListener implements PropertyChangeListener {

    private final String sourceBeanPath;
    private final APropertyChangeSupported source;
    @GuardedBy("this")
    private final Set<ChildRecursivePersistentPropertyChangeListener> children = Collections
            .newSetFromMap(new WeakHashMap<ChildRecursivePersistentPropertyChangeListener, Boolean>());

    public ARecursivePersistentPropertyChangeListener(final APropertyChangeSupported source) {
        this("", source);
    }

    private ARecursivePersistentPropertyChangeListener(final String sourceBeanPath,
            final APropertyChangeSupported source) {
        this.sourceBeanPath = sourceBeanPath;
        this.source = source;
    }

    public APropertyChangeSupported getSource() {
        return source;
    }

    public String getSourceBeanPath() {
        return sourceBeanPath;
    }

    @Override
    public final void propertyChange(final PropertyChangeEvent evt) {
        if (!shouldIgnoreEvent(evt) && !maybeRemoveObsoleteChild(evt)) {
            final String propertyName = evt.getPropertyName();
            final Object oldValue = evt.getOldValue();
            final Object newValue = evt.getNewValue();
            if (oldValue != newValue && BeanPathUtil.isShallowBeanPath(propertyName)) {
                maybeAddChildPropertyChangeListeners(propertyName, newValue);
                maybeRemoveChildPropertyChangeListeners(oldValue);
            }
            if (!(evt instanceof ChildPropertyChangeEvent)) {
                //first event needs to be handled aswell
                onPropertyChangeOnAnyLevel(evt);
            }
            final PropertyChangeEvent propagatedEvent = getPropagatedEvent(evt);
            onPropertyChangeOnAnyLevel(propagatedEvent);
            onPropertyChangeOnLastLevel(propagatedEvent);
        }
    }

    protected boolean shouldIgnoreEvent(final PropertyChangeEvent evt) {
        return false;
    }

    protected PropertyChangeEvent getPropagatedEvent(final PropertyChangeEvent evt) {
        return new PropertyChangeEvent(source, evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }

    /**
     * Remove a child that was moved outside of this parent without a setter being called on this parent for it.
     * 
     * Then register the listener on the new value, since we found it now because of that.
     */
    private boolean maybeRemoveObsoleteChild(final PropertyChangeEvent evt) {
        if (evt instanceof ChildPropertyChangeEvent && isOriginatingFromDirectChild(evt)) {
            final ChildPropertyChangeEvent cEvt = (ChildPropertyChangeEvent) evt;
            final ChildRecursivePersistentPropertyChangeListener evtOriginChild = cEvt.getOriginChild();
            //compare source of event with current value of the getter of the child
            final String methodName = BeanPathReflections.PROPERTY_GET_METHOD_PREFIX
                    + Strings.capitalize(evtOriginChild.getBeanPathFragment());
            final Method getterMethod = Reflections.findMethod(source.getClass(), methodName);
            if (getterMethod != null) {
                final Object oldSource = evt.getSource();
                final Object newSource = Reflections.invokeMethod(getterMethod, source);
                if (oldSource != newSource) {
                    maybeAddChildPropertyChangeListeners(evtOriginChild.getBeanPathFragment(), newSource);
                    maybeRemoveChildPropertyChangeListeners(oldSource);
                }
            }
        }
        return false;
    }

    private void maybeRemoveChildPropertyChangeListeners(final Object oldSource) {
        if (oldSource instanceof Iterable) {
            final Iterable<?> it = (Iterable<?>) oldSource;
            for (final Object value : it) {
                maybeRemoveChildPropertyChangeListenersSimpleValue(value);
            }
        } else {
            maybeRemoveChildPropertyChangeListenersSimpleValue(oldSource);
        }
    }

    private void maybeAddChildPropertyChangeListeners(final String beanPathFragment, final Object newSource) {
        if (newSource instanceof Iterable) {
            final Iterable<?> it = (Iterable<?>) newSource;
            for (final Object value : it) {
                maybeAddChildPropertyChangeListenersSimpleValue(beanPathFragment, value);
            }
        } else {
            maybeAddChildPropertyChangeListenersSimpleValue(beanPathFragment, newSource);
        }
    }

    private boolean isOriginatingFromDirectChild(final PropertyChangeEvent evt) {
        return Strings.countMatches(evt.getPropertyName(), BeanPathUtil.BEAN_PATH_SEPARATOR) == 1;
    }

    protected abstract void onPropertyChangeOnLastLevel(final PropertyChangeEvent evt);

    protected abstract void onPropertyChangeOnAnyLevel(final PropertyChangeEvent evt);

    private void maybeAddChildPropertyChangeListenersSimpleValue(final String beanPathFragment, final Object newValue) {
        if (newValue != null && newValue instanceof APropertyChangeSupported) {
            //register listener on newly added bean
            final APropertyChangeSupported cNewValue = (APropertyChangeSupported) newValue;
            final PropertyChangeListener[] listeners = cNewValue.getPropertyChangeListeners();
            boolean alreadyAdded = false;
            for (final PropertyChangeListener listener : listeners) {
                if (listener instanceof ChildRecursivePersistentPropertyChangeListener) {
                    final ChildRecursivePersistentPropertyChangeListener cListener = (ChildRecursivePersistentPropertyChangeListener) listener;
                    if (cListener.getParent() == this) {
                        assertSameSource(newValue, cListener);
                        alreadyAdded = true;
                        break;
                    }
                }
            }
            if (!alreadyAdded) {
                final ChildRecursivePersistentPropertyChangeListener child = new ChildRecursivePersistentPropertyChangeListener(
                        cNewValue, this, beanPathFragment);
                child.addListenersToSourceHierarchy();
            }
        }
    }

    public final void addListenersToSourceHierarchy() {
        source.addPropertyChangeListener(this);
        onListenerAdded(this);
        final BeanObjectContext context = new BeanObjectContext(source);
        new BeanObjectProcessor(context, new SimpleBeanPathVisitorSupport(context) {
            @Override
            public void visitProperty(final IPropertyBeanPathElement e) {
                if (e.getAccessor().hasPublicGetterOrField() && !(e instanceof ITableColumnBeanPathElement)) {
                    final Object value = e.getModifier().getValue();
                    internalAddListenersToSourceHierarchy(e, value);
                    if (e instanceof AChoiceBeanPathElement) {
                        final AChoiceBeanPathElement choice = (AChoiceBeanPathElement) e;
                        if (!choice.isChoiceOnly()) {
                            final List<?> choiceValue = choice.getChoiceModifier().getValue();
                            internalAddListenersToSourceHierarchy(e, choiceValue);
                        }
                    }
                }
            }

            private void internalAddListenersToSourceHierarchy(final IPropertyBeanPathElement e,
                    final Object newSource) {
                if (newSource instanceof Iterable) {
                    final Iterable<?> it = (Iterable<?>) newSource;
                    for (final Object value : it) {
                        internalAddListenersToSourceHierarchySimpleValue(e, value);
                    }
                } else {
                    internalAddListenersToSourceHierarchySimpleValue(e, newSource);
                }
            }

            private void internalAddListenersToSourceHierarchySimpleValue(final IPropertyBeanPathElement e,
                    final Object value) {
                if (value != null && value instanceof APropertyChangeSupported) {
                    Assertions.assertThat(BeanPathUtil.isShallowBeanPath(e.getBeanPath())).isTrue();
                    final APropertyChangeSupported cValue = (APropertyChangeSupported) value;
                    final ChildRecursivePersistentPropertyChangeListener child = new ChildRecursivePersistentPropertyChangeListener(
                            cValue, ARecursivePersistentPropertyChangeListener.this,
                            e.getAccessor().getBeanPathFragment());
                    child.addListenersToSourceHierarchy();
                }
            }

        }).withShallowOnly().process();
    }

    private void maybeRemoveChildPropertyChangeListenersSimpleValue(final Object oldValue) {
        if (oldValue != null && oldValue instanceof APropertyChangeSupported) {
            final APropertyChangeSupported cOldValue = (APropertyChangeSupported) oldValue;
            final PropertyChangeListener[] listeners = cOldValue.getPropertyChangeListeners();
            for (final PropertyChangeListener listener : listeners) {
                if (listener instanceof ChildRecursivePersistentPropertyChangeListener) {
                    final ChildRecursivePersistentPropertyChangeListener cListener = (ChildRecursivePersistentPropertyChangeListener) listener;
                    if (cListener.getParent() == this) {
                        assertSameSource(oldValue, cListener);
                        cListener.removeListenersFromSourceHierarchy();
                        break;
                    }
                }
            }
        }
    }

    private void assertSameSource(final Object value, final ARecursivePersistentPropertyChangeListener listener) {
        if (listener.source != value) {
            throw new IllegalStateException("Wrong source on listener while parent is correct?!?");
        }
    }

    public final synchronized void removeListenersFromSourceHierarchy() {
        source.removePropertyChangeListener(this);
        onListenerRemoved(this);
        for (final ChildRecursivePersistentPropertyChangeListener child : children) {
            child.removeListenersFromSourceHierarchy();
        }
        children.clear();
    }

    protected abstract void onListenerAdded(ARecursivePersistentPropertyChangeListener listener);

    protected abstract void onListenerRemoved(ARecursivePersistentPropertyChangeListener listener);

    private static class ChildRecursivePersistentPropertyChangeListener
            extends ARecursivePersistentPropertyChangeListener {

        private final WeakReference<ARecursivePersistentPropertyChangeListener> parentRef;
        private final String beanPathFragment;

        ChildRecursivePersistentPropertyChangeListener(final APropertyChangeSupported source,
                final ARecursivePersistentPropertyChangeListener parent, final String beanPathFragment) {
            super(buildSourceBeanPath(parent, beanPathFragment), source);
            this.parentRef = new WeakReference<ARecursivePersistentPropertyChangeListener>(parent);
            this.beanPathFragment = beanPathFragment;
            synchronized (parent) {
                parent.children.add(this);
            }
        }

        @Override
        protected boolean shouldIgnoreEvent(final PropertyChangeEvent evt) {
            final ARecursivePersistentPropertyChangeListener parent = getParent();
            if (parent != null) {
                return parent.shouldIgnoreEvent(evt);
            } else {
                return true;
            }
        }

        public ARecursivePersistentPropertyChangeListener getParent() {
            final ARecursivePersistentPropertyChangeListener parent = parentRef.get();
            if (parent == null) {
                //it seems the parent is gone, thus this listener is obsolete
                removeListenersFromSourceHierarchy();
            }
            return parent;
        }

        public String getBeanPathFragment() {
            return beanPathFragment;
        }

        private static String buildSourceBeanPath(final ARecursivePersistentPropertyChangeListener parent,
                final String beanPathFragment) {
            if (Strings.isBlank(parent.sourceBeanPath)) {
                return beanPathFragment;
            } else {
                return parent.sourceBeanPath + BeanPathUtil.BEAN_PATH_SEPARATOR + beanPathFragment;
            }
        }

        @Override
        protected void onPropertyChangeOnLastLevel(final PropertyChangeEvent evt) {
            //ignore
        }

        @Override
        protected void onPropertyChangeOnAnyLevel(final PropertyChangeEvent evt) {
            final ARecursivePersistentPropertyChangeListener parent = getParent();
            if (parent != null) {
                parent.onPropertyChangeOnAnyLevel(evt);
            }
        }

        @Override
        protected void onListenerAdded(final ARecursivePersistentPropertyChangeListener listener) {
            final ARecursivePersistentPropertyChangeListener parent = getParent();
            if (parent != null) {
                parent.onListenerAdded(listener);
            }
        }

        @Override
        protected void onListenerRemoved(final ARecursivePersistentPropertyChangeListener listener) {
            final ARecursivePersistentPropertyChangeListener parent = getParent();
            if (parent != null) {
                parent.onListenerRemoved(listener);
            }
        }

        @Override
        protected PropertyChangeEvent getPropagatedEvent(final PropertyChangeEvent evt) {
            return new ChildPropertyChangeEvent(this, evt);
        }

    }

    private static class ChildPropertyChangeEvent extends PropertyChangeEvent {

        private final ChildRecursivePersistentPropertyChangeListener originChild;

        ChildPropertyChangeEvent(final ChildRecursivePersistentPropertyChangeListener originChild,
                final PropertyChangeEvent propagatedEvent) {
            super(extractParentSource(originChild),
                    originChild.getBeanPathFragment() + BeanPathUtil.BEAN_PATH_SEPARATOR
                            + propagatedEvent.getPropertyName(),
                    propagatedEvent.getOldValue(), propagatedEvent.getNewValue());
            this.originChild = originChild;
        }

        private static APropertyChangeSupported extractParentSource(
                final ChildRecursivePersistentPropertyChangeListener originChild) {
            final ARecursivePersistentPropertyChangeListener parent = originChild.getParent();
            if (parent != null) {
                return parent.getSource();
            } else {
                //since getSource will return null on propagation aswell, the event will be ignored
                return null;
            }
        }

        public ChildRecursivePersistentPropertyChangeListener getOriginChild() {
            return originChild;
        }

    }
}
