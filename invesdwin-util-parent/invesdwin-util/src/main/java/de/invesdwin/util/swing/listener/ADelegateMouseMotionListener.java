package de.invesdwin.util.swing.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ADelegateMouseMotionListener implements MouseMotionListener {

    private MouseMotionListener delegate;

    @Override
    public void mouseDragged(final MouseEvent e) {
        getDelegate().mouseDragged(e);
    }

    private MouseMotionListener getDelegate() {
        if (delegate == null) {
            delegate = newDelegate();
        }
        return delegate;
    }

    protected abstract MouseMotionListener newDelegate();

    @Override
    public void mouseMoved(final MouseEvent e) {
        getDelegate().mouseMoved(e);
    }

}
