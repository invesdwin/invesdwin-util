package de.invesdwin.util.swing.listener;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.annotation.concurrent.Immutable;

@Immutable
public class FocusListenerSupport implements FocusListener {

    @Override
    public void focusGained(final FocusEvent e) {}

    @Override
    public void focusLost(final FocusEvent e) {}

}
