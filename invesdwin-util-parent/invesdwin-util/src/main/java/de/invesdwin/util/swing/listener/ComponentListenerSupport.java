package de.invesdwin.util.swing.listener;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.annotation.concurrent.Immutable;

@Immutable
public class ComponentListenerSupport implements ComponentListener {

    @Override
    public void componentResized(final ComponentEvent e) {}

    @Override
    public void componentMoved(final ComponentEvent e) {}

    @Override
    public void componentShown(final ComponentEvent e) {}

    @Override
    public void componentHidden(final ComponentEvent e) {}

}
