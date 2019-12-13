package de.invesdwin.util.swing;

import java.awt.event.MouseEvent;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JComponent;

import de.invesdwin.util.swing.listener.MouseListenerSupport;

@NotThreadSafe
public final class MouseEnteredListener extends MouseListenerSupport {

    public static final String CLIENTPROP_MOUSE_ENTERED_LISTENER_INSTANCE = "MOUSE_ENTERED_LISTENER_INSTANCE";

    private boolean mouseEntered;

    private MouseEnteredListener() {}

    @Override
    public void mouseEntered(final MouseEvent e) {
        mouseEntered = true;
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        mouseEntered = false;
    }

    public boolean isMouseEntered() {
        return mouseEntered;
    }

    public static MouseEnteredListener get(final JComponent component) {
        final MouseEnteredListener listener = (MouseEnteredListener) component
                .getClientProperty(CLIENTPROP_MOUSE_ENTERED_LISTENER_INSTANCE);
        if (listener != null) {
            return listener;
        }
        final MouseEnteredListener newListener = new MouseEnteredListener();
        component.addMouseListener(newListener);
        component.putClientProperty(CLIENTPROP_MOUSE_ENTERED_LISTENER_INSTANCE, newListener);
        return newListener;
    }
}
