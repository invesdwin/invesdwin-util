package de.invesdwin.util.swing.listener;

import java.awt.Color;

import javax.annotation.concurrent.Immutable;

@Immutable
public class ColorChooserListenerSupport implements IColorChooserListener {

    @Override
    public void change(final Color initialColor, final Color newColor) {}

    @Override
    public void ok(final Color initialColor, final Color acceptedColor) {}

    @Override
    public void cancel(final Color initialColor, final Color cancelledColor) {}

}
