package de.invesdwin.util.swing.listener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.annotation.concurrent.Immutable;

@Immutable
public class KeyListenerSupport implements KeyListener {

    @Override
    public void keyTyped(final KeyEvent e) {}

    @Override
    public void keyPressed(final KeyEvent e) {}

    @Override
    public void keyReleased(final KeyEvent e) {}

}
