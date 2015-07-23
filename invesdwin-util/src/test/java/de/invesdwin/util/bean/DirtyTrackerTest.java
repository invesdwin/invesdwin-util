package de.invesdwin.util.bean;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class DirtyTrackerTest {

    @Test
    public void testIsTrackingChangesDirectlyCascade() {
        final OuterVo outer = newOuterVo();

        //first on directly
        outer.dirtyTracker().startTrackingChangesDirectly();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChangesDirectly()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChangesDirectly()).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChangesDirectly()).isFalse();
        Assertions.assertThat(outer.getPropertyChangeListeners()).hasSize(1);
        Assertions.assertThat(outer.getInner().getPropertyChangeListeners()).hasSize(1);
        Assertions.assertThat(outer.getInner().getInner().getPropertyChangeListeners()).hasSize(1);

        //second on directly
        outer.getInner().dirtyTracker().startTrackingChangesDirectly();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChangesDirectly()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChangesDirectly()).isTrue();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChangesDirectly()).isFalse();
        Assertions.assertThat(outer.getPropertyChangeListeners()).hasSize(1);
        Assertions.assertThat(outer.getInner().getPropertyChangeListeners()).hasSize(2);
        Assertions.assertThat(outer.getInner().getInner().getPropertyChangeListeners()).hasSize(2);

        //third on directly
        outer.getInner().getInner().dirtyTracker().startTrackingChangesDirectly();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChangesDirectly()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChangesDirectly()).isTrue();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChangesDirectly()).isTrue();
        Assertions.assertThat(outer.getPropertyChangeListeners()).hasSize(1);
        Assertions.assertThat(outer.getInner().getPropertyChangeListeners()).hasSize(2);
        Assertions.assertThat(outer.getInner().getInner().getPropertyChangeListeners()).hasSize(3);

        //first off directly
        outer.dirtyTracker().stopTrackingChangesDirectly();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChanges()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChangesDirectly()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChangesDirectly()).isTrue();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChangesDirectly()).isTrue();
        Assertions.assertThat(outer.getPropertyChangeListeners()).hasSize(0);
        Assertions.assertThat(outer.getInner().getPropertyChangeListeners()).hasSize(1);
        Assertions.assertThat(outer.getInner().getInner().getPropertyChangeListeners()).hasSize(2);

        //second off directly
        outer.getInner().dirtyTracker().stopTrackingChangesDirectly();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChanges()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChanges()).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChangesDirectly()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChangesDirectly()).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChangesDirectly()).isTrue();
        Assertions.assertThat(outer.getPropertyChangeListeners()).hasSize(0);
        Assertions.assertThat(outer.getInner().getPropertyChangeListeners()).hasSize(0);
        Assertions.assertThat(outer.getInner().getInner().getPropertyChangeListeners()).hasSize(1);

        //third off directly
        outer.getInner().getInner().dirtyTracker().stopTrackingChangesDirectly();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChanges()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChanges()).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChanges()).isFalse();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChangesDirectly()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChangesDirectly()).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChangesDirectly()).isFalse();
        Assertions.assertThat(outer.getPropertyChangeListeners()).hasSize(0);
        Assertions.assertThat(outer.getInner().getPropertyChangeListeners()).hasSize(0);
        Assertions.assertThat(outer.getInner().getInner().getPropertyChangeListeners()).hasSize(0);
    }

    @Test
    public void testFirstStaysOffIfSecondIsTracking() {
        final OuterVo outer = newOuterVo();

        //second on directly
        outer.getInner().dirtyTracker().startTrackingChangesDirectly();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChanges()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChangesDirectly()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChangesDirectly()).isTrue();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChangesDirectly()).isFalse();

        //second off directly
        outer.getInner().dirtyTracker().stopTrackingChangesDirectly();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChanges()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChanges()).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChanges()).isFalse();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChangesDirectly()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChangesDirectly()).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChangesDirectly()).isFalse();
    }

    @Test
    public void testManualMarkingWithoutTracking() {
        final OuterVo outer = newOuterVo();

        //add listener
        final List<String> expectedDirtyEventsOnSecondLevel = new ArrayList<String>();
        final List<String> expectedCleanEventsOnSecondLevel = new ArrayList<String>();
        final List<String> expectedPropertyChangeEventsOnSecondLevel = new ArrayList<String>();
        addListenerWithAssertions(outer, expectedDirtyEventsOnSecondLevel, expectedCleanEventsOnSecondLevel,
                expectedPropertyChangeEventsOnSecondLevel);

        //all clean
        Assertions.assertThat(outer.dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner")).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner.value")).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();

        //second dirty
        expectedDirtyEventsOnSecondLevel.add("inner");
        expectedDirtyEventsOnSecondLevel.add("value");
        expectedDirtyEventsOnSecondLevel.add("inner.value");
        Assertions.assertThat(outer.getInner().dirtyTracker().markDirty()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner")).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner.value")).isTrue();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();

        //third clean
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().markClean()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner")).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner.value")).isTrue();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();

        //second clean
        expectedCleanEventsOnSecondLevel.add("inner");
        expectedCleanEventsOnSecondLevel.add("value");
        expectedCleanEventsOnSecondLevel.add("inner.value");
        Assertions.assertThat(outer.getInner().dirtyTracker().markClean()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner")).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner.value")).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();
    }

    private void addListenerWithAssertions(final OuterVo outer, final List<String> expectedDirtyEventsOnSecondLevel,
            final List<String> expectedCleanEventsOnSecondLevel,
            final List<String> expectedPropertyChangeEventsOnSecondLevel) {
        outer.getInner().dirtyTracker().getListeners().add(new IDirtyTrackerListener() {
            private int countPropertyChangeEvents = 0;
            private int countOnDirtyEvents = 0;
            private int countOnCleanEvents = 0;

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                countPropertyChangeEvents++;
                try {
                    Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel.remove(evt.getPropertyName()))
                    .isTrue();
                } catch (final Throwable t) {
                    throw new RuntimeException(
                            countPropertyChangeEvents + ". propertyChange: " + evt.getPropertyName(), t);
                }
            }

            @Override
            public void onDirty(final String beanPath) {
                countOnDirtyEvents++;
                try {
                    Assertions.assertThat(expectedDirtyEventsOnSecondLevel.remove(beanPath)).isTrue();
                } catch (final Throwable t) {
                    throw new RuntimeException(countOnDirtyEvents + ". onDirty: " + beanPath, t);
                }
            }

            @Override
            public void onClean(final String beanPath) {
                countOnCleanEvents++;
                try {
                    Assertions.assertThat(expectedCleanEventsOnSecondLevel.remove(beanPath)).isTrue();
                } catch (final Throwable t) {
                    throw new RuntimeException(countOnCleanEvents + ". onClean: " + beanPath, t);
                }
            }
        });
    }

    @Test
    public void testManualMarkingWithTracking() {
        final OuterVo outer = newOuterVo();

        //add listener
        final List<String> expectedDirtyEventsOnSecondLevel = new ArrayList<String>();
        final List<String> expectedCleanEventsOnSecondLevel = new ArrayList<String>();
        final List<String> expectedPropertyChangeEventsOnSecondLevel = new ArrayList<String>();
        addListenerWithAssertions(outer, expectedDirtyEventsOnSecondLevel, expectedCleanEventsOnSecondLevel,
                expectedPropertyChangeEventsOnSecondLevel);

        //first on directly
        outer.dirtyTracker().startTrackingChangesDirectly();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChangesDirectly()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChangesDirectly()).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChangesDirectly()).isFalse();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();

        //all clean
        Assertions.assertThat(outer.dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner")).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner.value")).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();

        //second dirty
        expectedDirtyEventsOnSecondLevel.add("inner");
        expectedDirtyEventsOnSecondLevel.add("value");
        expectedDirtyEventsOnSecondLevel.add("inner.value");
        Assertions.assertThat(outer.getInner().dirtyTracker().markDirty()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner")).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner.value")).isTrue();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();

        //third clean
        expectedCleanEventsOnSecondLevel.add("inner.value");
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().markClean()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner")).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner.value")).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();

        //second clean
        expectedCleanEventsOnSecondLevel.add("inner");
        expectedCleanEventsOnSecondLevel.add("value");
        Assertions.assertThat(outer.getInner().dirtyTracker().markClean()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner")).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner.value")).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();
    }

    @Test
    public void testAutomaticMarkingWithoutTracking() {
        final OuterVo outer = newOuterVo();

        //add listener
        final List<String> expectedDirtyEventsOnSecondLevel = new ArrayList<String>();
        final List<String> expectedCleanEventsOnSecondLevel = new ArrayList<String>();
        final List<String> expectedPropertyChangeEventsOnSecondLevel = new ArrayList<String>();
        addListenerWithAssertions(outer, expectedDirtyEventsOnSecondLevel, expectedCleanEventsOnSecondLevel,
                expectedPropertyChangeEventsOnSecondLevel);

        //all clean
        Assertions.assertThat(outer.dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner")).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner.value")).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();

        //second dirty
        outer.getInner().setValue(Integer.MAX_VALUE);
        Assertions.assertThat(outer.dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner")).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner.value")).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();

        //third dirty
        outer.getInner().getInner().setValue(String.valueOf(Integer.MAX_VALUE));
        Assertions.assertThat(outer.dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner")).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner.value")).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();
    }

    @Test
    public void testAutomaticMarkingWithTracking() {
        final OuterVo outer = newOuterVo();

        //add listener
        final List<String> expectedDirtyEventsOnSecondLevel = new ArrayList<String>();
        final List<String> expectedCleanEventsOnSecondLevel = new ArrayList<String>();
        final List<String> expectedPropertyChangeEventsOnSecondLevel = new ArrayList<String>();
        addListenerWithAssertions(outer, expectedDirtyEventsOnSecondLevel, expectedCleanEventsOnSecondLevel,
                expectedPropertyChangeEventsOnSecondLevel);

        //first on directly
        outer.dirtyTracker().startTrackingChangesDirectly();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChangesDirectly()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChangesDirectly()).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChangesDirectly()).isFalse();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();

        //all clean
        Assertions.assertThat(outer.dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner")).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner.value")).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();

        //second dirty
        expectedDirtyEventsOnSecondLevel.add("value");
        expectedPropertyChangeEventsOnSecondLevel.add("value");
        outer.getInner().setValue(Integer.MAX_VALUE);
        Assertions.assertThat(outer.dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isDirty("inner")).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isDirty("inner.value")).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner")).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner.value")).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();

        //third dirty
        expectedDirtyEventsOnSecondLevel.add("inner.value");
        expectedPropertyChangeEventsOnSecondLevel.add("inner.value");
        outer.getInner().getInner().setValue(String.valueOf(Integer.MAX_VALUE));
        Assertions.assertThat(outer.dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isDirty("inner")).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isDirty("inner.value")).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner")).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner.value")).isTrue();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();

    }

    @Test
    public void testAutomaticMarkingWithTrackingAndChangingValueObjects() {
        final OuterVo outer = newOuterVo();

        //add listener
        final List<String> expectedDirtyEventsOnSecondLevel = new ArrayList<String>();
        final List<String> expectedCleanEventsOnSecondLevel = new ArrayList<String>();
        final List<String> expectedPropertyChangeEventsOnSecondLevel = new ArrayList<String>();
        addListenerWithAssertions(outer, expectedDirtyEventsOnSecondLevel, expectedCleanEventsOnSecondLevel,
                expectedPropertyChangeEventsOnSecondLevel);

        //first on directly
        outer.dirtyTracker().startTrackingChangesDirectly();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChanges()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChangesDirectly()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChangesDirectly()).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChangesDirectly()).isFalse();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();

        //all clean
        Assertions.assertThat(outer.dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner")).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner.value")).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();

        //second dirty
        expectedDirtyEventsOnSecondLevel.add("value");
        expectedPropertyChangeEventsOnSecondLevel.add("value");
        outer.getInner().setValue(Integer.MAX_VALUE);
        Assertions.assertThat(outer.dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isDirty("inner")).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isDirty("inner.value")).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner")).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner.value")).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();

        //third new value object
        expectedDirtyEventsOnSecondLevel.add("inner");
        expectedDirtyEventsOnSecondLevel.add("inner.value");
        expectedPropertyChangeEventsOnSecondLevel.add("inner");
        outer.getInner().setInner(new InnerInnerVO());
        Assertions.assertThat(outer.dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isDirty("inner")).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isDirty("inner.value")).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner")).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner.value")).isTrue();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();

        //third dirty
        expectedPropertyChangeEventsOnSecondLevel.add("inner.value");
        outer.getInner().getInner().setValue(String.valueOf(Integer.MAX_VALUE));
        Assertions.assertThat(outer.dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isDirty("inner")).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isDirty("inner.value")).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner")).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner.value")).isTrue();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();

        //third clean
        expectedCleanEventsOnSecondLevel.add("inner.value");
        outer.getInner().getInner().dirtyTracker().markClean();
        Assertions.assertThat(outer.dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isDirty("inner")).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isDirty("inner.value")).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner")).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner.value")).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();

        //third same value set thus unchanged
        outer.getInner().getInner().setValue(String.valueOf(Integer.MAX_VALUE));
        Assertions.assertThat(outer.dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isDirty("inner")).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isDirty("inner.value")).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner")).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner.value")).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isDirty()).isFalse();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();

        //third dirty
        expectedPropertyChangeEventsOnSecondLevel.add("inner.value");
        expectedDirtyEventsOnSecondLevel.add("inner.value");
        outer.getInner().getInner().setValue(null);
        Assertions.assertThat(outer.dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isDirty("inner")).isTrue();
        Assertions.assertThat(outer.dirtyTracker().isDirty("inner.value")).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner")).isTrue();
        Assertions.assertThat(outer.getInner().dirtyTracker().isDirty("inner.value")).isTrue();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isDirty()).isTrue();
        Assertions.assertThat(expectedDirtyEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedCleanEventsOnSecondLevel).isEmpty();
        Assertions.assertThat(expectedPropertyChangeEventsOnSecondLevel).isEmpty();
    }

    private OuterVo newOuterVo() {
        final OuterVo outer = new OuterVo();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChanges()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChanges()).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChanges()).isFalse();
        Assertions.assertThat(outer.dirtyTracker().isTrackingChangesDirectly()).isFalse();
        Assertions.assertThat(outer.getInner().dirtyTracker().isTrackingChangesDirectly()).isFalse();
        Assertions.assertThat(outer.getInner().getInner().dirtyTracker().isTrackingChangesDirectly()).isFalse();
        return outer;
    }

    //TOOD: maybe also test some of the healing mechanisms and those healing mechanisms that are not working

    public static class OuterVo extends AValueObject {
        private static final long serialVersionUID = 1L;
        private InnerVO inner = new InnerVO();
        private Integer otherValue;

        public InnerVO getInner() {
            return inner;
        }

        public void setInner(final InnerVO inner) {
            final InnerVO oldValue = this.inner;
            this.inner = inner;
            firePropertyChange("inner", oldValue, inner);
        }

        public Integer getOtherValue() {
            return otherValue;
        }

        public void setOtherValue(final Integer otherValue) {
            final Integer oldValue = this.otherValue;
            this.otherValue = otherValue;
            firePropertyChange("otherValue", oldValue, otherValue);

        }

    }

    public static class InnerVO extends AValueObject {
        private static final long serialVersionUID = 1L;

        private Integer value;
        private InnerInnerVO inner = new InnerInnerVO();

        public Integer getValue() {
            return value;
        }

        public void setValue(final Integer value) {
            final Integer oldValue = this.value;
            this.value = value;
            firePropertyChange("value", oldValue, value);

        }

        public InnerInnerVO getInner() {
            return inner;
        }

        public void setInner(final InnerInnerVO inner) {
            final InnerInnerVO oldValue = this.inner;
            this.inner = inner;
            firePropertyChange("inner", oldValue, inner);
        }
    }

    public static class InnerInnerVO extends AValueObject {
        private static final long serialVersionUID = 1L;

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(final String value) {
            final String oldValue = this.value;
            this.value = value;
            firePropertyChange("value", oldValue, value);
        }
    }

}
