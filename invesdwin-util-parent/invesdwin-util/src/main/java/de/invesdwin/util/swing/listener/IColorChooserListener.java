package de.invesdwin.util.swing.listener;

import java.awt.Color;

public interface IColorChooserListener {

    void change(Color initialColor, Color newColor);

    void ok(Color initialColor, Color acceptedColor);

    void cancel(Color initialColor, Color cancelledColor);

}
