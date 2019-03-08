package de.invesdwin.util.swing.listener;

import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import javax.annotation.concurrent.Immutable;

@Immutable
public class ContainerListenerSupport implements ContainerListener {

    @Override
    public void componentAdded(final ContainerEvent e) {}

    @Override
    public void componentRemoved(final ContainerEvent e) {}

}
