package de.invesdwin.util.swing.listener;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.annotation.concurrent.Immutable;

@Immutable
public class WindowListenerSupport implements WindowListener {

    @Override
    public void windowOpened(final WindowEvent e) {}

    @Override
    public void windowClosing(final WindowEvent e) {}

    @Override
    public void windowClosed(final WindowEvent e) {}

    @Override
    public void windowIconified(final WindowEvent e) {}

    @Override
    public void windowDeiconified(final WindowEvent e) {}

    @Override
    public void windowActivated(final WindowEvent e) {}

    @Override
    public void windowDeactivated(final WindowEvent e) {}

}
