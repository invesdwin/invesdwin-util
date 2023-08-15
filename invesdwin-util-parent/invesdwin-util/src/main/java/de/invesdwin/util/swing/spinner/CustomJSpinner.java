package de.invesdwin.util.swing.spinner;

import java.awt.event.MouseWheelListener;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JSpinner;

/**
 * Adds mouseWheelListener-support (to scroll through prev-/next-values) for a JSpinner by default.
 *
 * @author matze
 *
 */
@NotThreadSafe
public class CustomJSpinner extends JSpinner {

    private final MouseWheelListener mouseWheelListener = event -> {
        final Object value = event.getWheelRotation() < 0 ? getNextValue() : getPreviousValue();
        if (value != null) {
            setValue(value);
        }
    };

    public CustomJSpinner() {
        super();
        addMouseWheelListener(mouseWheelListener);
    }
}
