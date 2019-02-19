package de.invesdwin.util.swing.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.annotation.concurrent.Immutable;

@Immutable
public class MouseListenerSupport implements MouseListener {

    @Override
    public void mouseClicked(final MouseEvent e) {}

    @Override
    public void mousePressed(final MouseEvent e) {}

    @Override
    public void mouseReleased(final MouseEvent e) {}

    @Override
    public void mouseEntered(final MouseEvent e) {}

    @Override
    public void mouseExited(final MouseEvent e) {}

}
