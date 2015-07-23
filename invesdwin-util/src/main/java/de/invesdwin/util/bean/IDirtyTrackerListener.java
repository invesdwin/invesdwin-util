package de.invesdwin.util.bean;

import java.beans.PropertyChangeListener;
import java.io.Serializable;

/**
 * Also implements PropertyChangeListener in order to track changes also from child beans via bean paths. The
 * PropertyChangeListener receives events for any call to a setter, even though the values are still the same.
 * 
 * @author subes
 * 
 */
public interface IDirtyTrackerListener extends Serializable, PropertyChangeListener {

    void onDirty(String beanPath);

    void onClean(String beanPath);

}
