package de.invesdwin.util.bean;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.norva.beanpath.impl.object.BeanObjectContext;
import de.invesdwin.norva.beanpath.impl.object.BeanObjectProcessor;
import de.invesdwin.norva.beanpath.spi.BeanPathUtil;
import de.invesdwin.norva.beanpath.spi.element.IPropertyBeanPathElement;
import de.invesdwin.norva.beanpath.spi.element.table.column.ITableColumnBeanPathElement;
import de.invesdwin.norva.beanpath.spi.visitor.SimpleBeanPathVisitorSupport;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.fast.concurrent.ASynchronizedFastIterableDelegateSet;
import de.invesdwin.util.collections.fast.concurrent.SynchronizedSet;
import de.invesdwin.util.lang.Strings;

/**
 * The DirtyTracker does its best effort to detect and heal broken links between children and parents, though to provide
 * best results, it is recommended to always change value objects via their setters.
 */
@ThreadSafe
public class DirtyTracker implements Serializable {

    private final AValueObject root;
    private final Set<String> beanPaths;
    private final ASynchronizedFastIterableDelegateSet<IDirtyTrackerListener> listeners = new ASynchronizedFastIterableDelegateSet<IDirtyTrackerListener>() {
        @Override
        protected Set<IDirtyTrackerListener> newDelegate() {
            return new LinkedHashSet<IDirtyTrackerListener>();
        }
    };
    @GuardedBy("this")
    private final Set<String> changedBeanPaths = new LinkedHashSet<String>();

    @GuardedBy("this")
    private boolean trackingChangesDirectly;
    @GuardedBy("this")
    private transient TrackingChangesPropagatingRecursivePersistentPropertyChangeListener directTracker;
    @GuardedBy("this")
    private transient Map<TrackingChangesPropagatingRecursivePersistentPropertyChangeListener, String> registeredTracker_sourceBeanPath;
    @GuardedBy("this")
    private transient WeakReference<TrackingChangesPropagatingRecursivePersistentPropertyChangeListener> leadingRegisteredTrackerRef;

    public DirtyTracker(final AValueObject root) {
        this.root = root;
        final Set<String> beanPaths = new HashSet<String>();
        final BeanObjectContext context = new BeanObjectContext(root);
        new BeanObjectProcessor(context, new SimpleBeanPathVisitorSupport(context) {
            @Override
            public void visitProperty(final IPropertyBeanPathElement e) {
                Assertions.assertThat(beanPaths.add(e.getBeanPath())).isTrue();
            }
        }).process();
        this.beanPaths = Collections.unmodifiableSet(beanPaths);
    }

    private synchronized Map<TrackingChangesPropagatingRecursivePersistentPropertyChangeListener, String> getRegisteredTrackers() {
        if (registeredTracker_sourceBeanPath == null) {
            registeredTracker_sourceBeanPath = new WeakHashMap<TrackingChangesPropagatingRecursivePersistentPropertyChangeListener, String>();
        }
        return registeredTracker_sourceBeanPath;
    }

    /**
     * Starts tracking changes directly.
     */
    public synchronized void startTrackingChangesDirectly() {
        if (!trackingChangesDirectly) {
            trackingChangesDirectly = true;
            if (directTracker == null) {
                addDirectTracker();
            }
        }
    }

    /**
     * Stops tracking changes directly.
     */
    public synchronized void stopTrackingChangesDirectly() {
        if (trackingChangesDirectly) {
            trackingChangesDirectly = false;
            if (directTracker != null) {
                removeDirectTracker();
            }
        }
    }

    /**
     * Tells if this tracker is currently being updated by PropertyChangeEvents of its properties and children
     * properties. The tracking might be enabled by a parent that is interested in events here. Thus tracking might stop
     * after that parent stops tracking if startTrackingChanges() was not called on this tracker directly.
     */
    public synchronized boolean isTrackingChanges() {
        return trackingChangesDirectly
                || (registeredTracker_sourceBeanPath != null && !registeredTracker_sourceBeanPath.isEmpty());
    }

    /**
     * Tells if this tracker is currently being updated by PropertyChangeEvents of its properties and children
     * properties directly. This method checks of startTrackingChanges() was actually called on this instance and the
     * tracking is not happening because of a parent object. Or atleast that the tracking will continue even after a
     * parent object stops tracking.
     */
    public synchronized boolean isTrackingChangesDirectly() {
        return trackingChangesDirectly;
    }

    public Set<IDirtyTrackerListener> getListeners() {
        return listeners;
    }

    public synchronized boolean isDirty(final String... beanPathPrefixes) {
        if (beanPathPrefixes == null || beanPathPrefixes.length == 0) {
            return !changedBeanPaths.isEmpty();
        } else {
            for (final String beanPath : changedBeanPaths) {
                if (Strings.startsWithAny(beanPath, beanPathPrefixes)) {
                    return true;
                }
            }
            return false;
        }
    }

    public synchronized Set<String> getChangedBeanPaths() {
        return Collections.unmodifiableSet(new SynchronizedSet<String>(changedBeanPaths, this));
    }

    /**
     * WARNING: if a parent is not tracking changes, it is not known on this level and thus won't receive changes made
     * here. Always make sure to call "startTrackingChangesDirectly()" on the root object to prevent any problems.
     */
    public synchronized boolean markDirty(final String... beanPathPrefixes) {
        final boolean changed = directMarkDirty(false, beanPathPrefixes);
        //manual changes are more expensive than tracked ones, since they don't propagate via the tracker on all levels
        //notify parents
        for (final Entry<TrackingChangesPropagatingRecursivePersistentPropertyChangeListener, String> entry : getRegisteredTrackers()
                .entrySet()) {
            final TrackingChangesPropagatingRecursivePersistentPropertyChangeListener registeredTracker = entry
                    .getKey();
            final String sourceBeanPath = entry.getValue();
            if (registeredTracker != directTracker) {
                final String[] parentBeanPathPrefixes = adjustBeanPathPrefixesForParent(sourceBeanPath,
                        beanPathPrefixes);
                Assertions.assertThat(registeredTracker.onManualMarkDirty(parentBeanPathPrefixes)).isEqualTo(changed);
            }
        }
        childrenMarkDirty(beanPathPrefixes);
        return changed;
    }

    private void childrenMarkDirty(final String... beanPathPrefixes) {
        //notify children
        final BeanObjectContext context = new BeanObjectContext(root);
        new BeanObjectProcessor(context, new SimpleBeanPathVisitorSupport(context) {
            @Override
            public void visitProperty(final IPropertyBeanPathElement e) {
                final String[] childBeanPathPrefixes = adjustBeanPathPrefixesForChildren(e.getBeanPath(),
                        beanPathPrefixes);
                if (childBeanPathPrefixes != null && e.getAccessor().hasPublicGetterOrField()
                        && !(e instanceof ITableColumnBeanPathElement)) {
                    final Object value = e.getModifier().getValue();
                    if (value != null && value instanceof AValueObject) {
                        final AValueObject cValue = (AValueObject) value;
                        final DirtyTracker childDirtyTracker = cValue.dirtyTracker();
                        detectAndHealBrokenChildTrackers(childDirtyTracker);
                        childDirtyTracker.directMarkDirty(false, childBeanPathPrefixes);
                        childDirtyTracker.childrenMarkDirty(childBeanPathPrefixes);
                    }
                }
            }
        }).withShallowOnly().process();
    }

    /**
     * WARNING: if a parent is not tracking changes, it is not known on this level and thus won't receive changes made
     * here. Always make sure to call "startTrackingChangesDirectly()" on the root object to prevent any problems.
     */
    public synchronized boolean markClean(final String... beanPathPrefixes) {
        final boolean changed = directMarkClean(false, beanPathPrefixes);
        //manual changes are more expensive than tracked ones, since they don't propagate via the tracker on all levels
        //notify parents
        for (final Entry<TrackingChangesPropagatingRecursivePersistentPropertyChangeListener, String> entry : getRegisteredTrackers()
                .entrySet()) {
            final TrackingChangesPropagatingRecursivePersistentPropertyChangeListener registeredTracker = entry
                    .getKey();
            final String sourceBeanPath = entry.getValue();
            if (registeredTracker != directTracker) {
                final String[] parentBeanPathPrefixes = adjustBeanPathPrefixesForParent(sourceBeanPath,
                        beanPathPrefixes);
                Assertions.assertThat(registeredTracker.onManualMarkClean(parentBeanPathPrefixes)).isEqualTo(changed);
            }
        }
        childrenMarkClean(beanPathPrefixes);
        return changed;
    }

    private void childrenMarkClean(final String... beanPathPrefixes) {
        //notify children
        final BeanObjectContext context = new BeanObjectContext(root);
        new BeanObjectProcessor(context, new SimpleBeanPathVisitorSupport(context) {
            @Override
            public void visitProperty(final IPropertyBeanPathElement e) {
                final String[] childBeanPathPrefixes = adjustBeanPathPrefixesForChildren(e.getBeanPath(),
                        beanPathPrefixes);
                if (childBeanPathPrefixes != null && e.getAccessor().hasPublicGetterOrField()
                        && !(e instanceof ITableColumnBeanPathElement)) {
                    final Object value = e.getModifier().getValue();
                    if (value != null && value instanceof AValueObject) {
                        final AValueObject cValue = (AValueObject) value;
                        final DirtyTracker childDirtyTracker = cValue.dirtyTracker();
                        detectAndHealBrokenChildTrackers(childDirtyTracker);
                        childDirtyTracker.directMarkClean(false, childBeanPathPrefixes);
                        childDirtyTracker.childrenMarkClean(childBeanPathPrefixes);
                    }
                }
            }

        }).withShallowOnly().process();
    }

    private synchronized void detectAndHealBrokenChildTrackers(final DirtyTracker childDirtyTracker) {
        synchronized (childDirtyTracker) {
            final Map<TrackingChangesPropagatingRecursivePersistentPropertyChangeListener, String> childRegisteredTrackers = childDirtyTracker
                    .getRegisteredTrackers();
            for (final TrackingChangesPropagatingRecursivePersistentPropertyChangeListener tracker : getRegisteredTrackers()
                    .keySet()) {
                if (!childRegisteredTrackers.containsKey(tracker)) {
                    //there seems to be a new child that was not added via a setter, thus reattach everything from scratch
                    tracker.removeListenersFromSourceHierarchy();
                    tracker.addListenersToSourceHierarchy();
                }
            }
        }
    }

    private String[] adjustBeanPathPrefixesForParent(final String sourceBeanPath, final String[] beanPathPrefixes) {
        if (beanPathPrefixes == null || beanPathPrefixes.length == 0) {
            /*
             * if everything here is marked, we don't want the parent to mark any more than is done here. thus we put
             * the path under which the parent knows this here.
             * 
             * We add a separator at the end to now change the dirty flag of the parent accessor to this value.
             */
            return new String[] { sourceBeanPath + BeanPathUtil.BEAN_PATH_SEPARATOR };
        } else {
            final String[] newBeanPathPrefixes = new String[beanPathPrefixes.length];
            for (int i = 0; i < beanPathPrefixes.length; i++) {
                newBeanPathPrefixes[i] = sourceBeanPath + BeanPathUtil.BEAN_PATH_SEPARATOR + beanPathPrefixes[i];
            }
            return newBeanPathPrefixes;
        }
    }

    /**
     * this method might return null to indicate that this child should be skipped
     */
    private String[] adjustBeanPathPrefixesForChildren(final String childBeanPath, final String[] beanPathPrefixes) {
        if (beanPathPrefixes == null) {
            //convert null to empty since this might also be a parameter from the outside that wants to update all
            return new String[0];
        } else if (beanPathPrefixes.length == 0) {
            return beanPathPrefixes;
        } else {
            boolean childBeanPathFound = false;
            final String[] newBeanPathPrefixes = new String[beanPathPrefixes.length];
            for (int i = 0; i < beanPathPrefixes.length; i++) {
                if (Strings.startsWith(beanPathPrefixes[i], childBeanPath)
                        && Strings.contains(beanPathPrefixes[i], BeanPathUtil.BEAN_PATH_SEPARATOR)) {
                    newBeanPathPrefixes[i] = Strings.substringAfter(beanPathPrefixes[i],
                            BeanPathUtil.BEAN_PATH_SEPARATOR);
                    childBeanPathFound = true;
                } else {
                    //children will skip null bean path prefixes
                    newBeanPathPrefixes[i] = null;
                }
            }
            if (childBeanPathFound) {
                return newBeanPathPrefixes;
            } else {
                return null;
            }
        }
    }

    private synchronized boolean directMarkDirty(final boolean absoluteBeanPath, final String... beanPathPrefixes) {
        boolean changed = false;
        for (final String beanPath : beanPaths) {
            if (beanPath == null) {
                continue;
            }
            if (beanPathPrefixes == null || beanPathPrefixes.length == 0
                    || BeanPathUtil.startsWithAnyBeanPath(absoluteBeanPath, beanPath, beanPathPrefixes)) {
                if (changedBeanPaths.add(beanPath)) {
                    changed = true;
                    for (final IDirtyTrackerListener listener : getListeners()) {
                        listener.onDirty(beanPath);
                    }
                }
            }
        }
        return changed;
    }

    private synchronized boolean directMarkClean(final boolean absoluteBeanPath, final String... beanPathPrefixes) {
        boolean changed = false;
        final List<String> changedBeanPathsCopy = new ArrayList<String>(changedBeanPaths);
        for (final String beanPath : changedBeanPathsCopy) {
            if (beanPath == null) {
                continue;
            }
            if (beanPathPrefixes == null || beanPathPrefixes.length == 0
                    || BeanPathUtil.startsWithAnyBeanPath(absoluteBeanPath, beanPath, beanPathPrefixes)) {
                if (changedBeanPaths.remove(beanPath)) {
                    changed = true;
                    for (final IDirtyTrackerListener listener : getListeners()) {
                        listener.onClean(beanPath);
                    }
                }
            }
        }
        return changed;
    }

    /**
     * Need to add tracker again after deserialization of a new tree.
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        synchronized (this) {
            if (trackingChangesDirectly) {
                addDirectTracker();
            }
        }
    }

    private synchronized void addDirectTracker() {
        Assertions.assertThat(trackingChangesDirectly).isTrue();
        Assertions.assertThat(directTracker).isNull();
        directTracker = new TrackingChangesPropagatingRecursivePersistentPropertyChangeListener();
        directTracker.addListenersToSourceHierarchy();
    }

    private synchronized void removeDirectTracker() {
        Assertions.assertThat(trackingChangesDirectly).isFalse();
        Assertions.assertThat(directTracker).isNotNull();
        directTracker.removeListenersFromSourceHierarchy();
        directTracker = null;
    }

    private class TrackingChangesPropagatingRecursivePersistentPropertyChangeListener
            extends ARecursivePersistentPropertyChangeListener {

        TrackingChangesPropagatingRecursivePersistentPropertyChangeListener() {
            super(root);
        }

        @Override
        protected void onPropertyChangeOnLastLevel(final PropertyChangeEvent evt) {
            //ignore
        }

        @Override
        protected void onPropertyChangeOnAnyLevel(final PropertyChangeEvent evt) {
            final Object source = evt.getSource();
            Assertions.assertThat(source).isInstanceOf(AValueObject.class);
            final AValueObject cSource = (AValueObject) source;
            final DirtyTracker sourceDirtyTracker = cSource.dirtyTracker();
            final TrackingChangesPropagatingRecursivePersistentPropertyChangeListener sourceLeadingRegisteredTracker = sourceDirtyTracker
                    .getOrUpdateLeadingRegisteredTracker(this);
            Assertions.assertThat(sourceLeadingRegisteredTracker).isSameAs(this);
            if (!sourceDirtyTracker.isTrackingChanges()) {
                throw new IllegalStateException(
                        "Not tracking changes right now, thus events should not be able to arrive!");
            }
            sourceDirtyTracker.directMarkDirty(true, evt.getPropertyName());
            for (final IDirtyTrackerListener listener : sourceDirtyTracker.getListeners()) {
                listener.propertyChange(evt);
            }
        }

        @Override
        protected boolean shouldIgnoreEvent(final PropertyChangeEvent evt) {
            final Object source = evt.getSource();
            if (source instanceof AValueObject) {
                final AValueObject cSource = (AValueObject) source;
                final DirtyTracker sourceDirtyTracker = cSource.dirtyTracker();
                final TrackingChangesPropagatingRecursivePersistentPropertyChangeListener sourceLeadingRegisteredTracker = sourceDirtyTracker
                        .getOrUpdateLeadingRegisteredTracker(this);
                if (sourceLeadingRegisteredTracker == this) {
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onListenerAdded(final ARecursivePersistentPropertyChangeListener listener) {
            final APropertyChangeSupported source = listener.getSource();
            if (source instanceof AValueObject) {
                final AValueObject cSource = (AValueObject) source;
                final DirtyTracker sourceDirtyTracker = cSource.dirtyTracker();
                sourceDirtyTracker.addRegisteredTracker(this, listener.getSourceBeanPath());
            }
        }

        @Override
        protected void onListenerRemoved(final ARecursivePersistentPropertyChangeListener listener) {
            final APropertyChangeSupported source = listener.getSource();
            if (source instanceof AValueObject) {
                final AValueObject cSource = (AValueObject) source;
                final DirtyTracker sourceDirtyTracker = cSource.dirtyTracker();
                sourceDirtyTracker.removeRegisteredTracker(this);
            }
        }

        public boolean onManualMarkClean(final String... beanPathPrefixes) {
            final boolean changed = directMarkClean(false, beanPathPrefixes);
            //we need to propagate upwards again since there might be a distance between this parent and the root parent.
            childrenMarkClean(beanPathPrefixes);
            return changed;
        }

        public boolean onManualMarkDirty(final String... beanPathPrefixes) {
            final boolean changed = directMarkDirty(false, beanPathPrefixes);
            //we need to propagate upwards again since there might be a distance between this parent and the root parent.
            childrenMarkDirty(beanPathPrefixes);
            return changed;
        }
    }

    private synchronized void removeRegisteredTracker(
            final TrackingChangesPropagatingRecursivePersistentPropertyChangeListener tracker) {
        final Map<TrackingChangesPropagatingRecursivePersistentPropertyChangeListener, String> registeredTrackers = getRegisteredTrackers();
        Assertions.assertThat(registeredTrackers.remove(tracker)).isNotNull();
        removeLeadingRegisteredTrackerIfMatching(tracker);
        if (registeredTrackers.isEmpty() && isTrackingChangesDirectly()) {
            addDirectTracker();
        }
    }

    private synchronized void removeLeadingRegisteredTrackerIfMatching(
            final TrackingChangesPropagatingRecursivePersistentPropertyChangeListener tracker) {
        if (leadingRegisteredTrackerRef != null && leadingRegisteredTrackerRef.get() == tracker) {
            leadingRegisteredTrackerRef = null;
        }
    }

    private synchronized void addRegisteredTracker(
            final TrackingChangesPropagatingRecursivePersistentPropertyChangeListener tracker,
            final String sourceBeanPath) {
        Assertions.assertThat(getRegisteredTrackers().put(tracker, sourceBeanPath)).isNull();
        Assertions.assertThat(getOrUpdateLeadingRegisteredTracker(tracker)).isNotNull();
    }

    private synchronized TrackingChangesPropagatingRecursivePersistentPropertyChangeListener getOrUpdateLeadingRegisteredTracker(
            final TrackingChangesPropagatingRecursivePersistentPropertyChangeListener tracker) {
        if (leadingRegisteredTrackerRef == null || leadingRegisteredTrackerRef.get() == null) {
            leadingRegisteredTrackerRef = new WeakReference<TrackingChangesPropagatingRecursivePersistentPropertyChangeListener>(
                    tracker);
        }
        return leadingRegisteredTrackerRef.get();
    }

}
