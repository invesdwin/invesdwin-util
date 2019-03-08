package de.invesdwin.util.bean;

import java.beans.PropertyChangeEvent;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DirtyTrackerListenerSupport implements IDirtyTrackerListener {

    @Override
    public void onDirty(final String beanPath) {}

    @Override
    public void onClean(final String beanPath) {}

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {}

}
