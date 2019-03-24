package de.invesdwin.util.swing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import de.invesdwin.util.swing.listener.IColorChooserListener;

@NotThreadSafe
public class ColorChooserButtonActionListener implements ActionListener, IColorChooserListener {

    private final JButton button;
    private Color current;

    public ColorChooserButtonActionListener(final JButton button, final Color initialColor) {
        this.button = button;
        setSelectedColor(initialColor);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        Dialogs.showColorChooserDialog(button, getChooserDialogTitle(), current, isTransparencyEnabled(), this);
    }

    protected String getChooserDialogTitle() {
        return "Choose Color";
    }

    protected boolean isTransparencyEnabled() {
        return true;
    }

    public Color getSelectedColor() {
        return current;
    }

    public void setSelectedColor(final Color newColor) {
        if (newColor == null) {
            return;
        }

        current = newColor;
        button.setIcon(createIcon(current, 16, 16));
        button.repaint();
    }

    public static ImageIcon createIcon(final Color main, final int width, final int height) {
        final BufferedImage image = new BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics = image.createGraphics();
        graphics.setColor(main);
        graphics.fillRect(0, 0, width, height);
        graphics.setXORMode(Color.DARK_GRAY);
        graphics.drawRect(0, 0, width - 1, height - 1);
        image.flush();
        final ImageIcon icon = new ImageIcon(image);
        return icon;
    }

    @Override
    public void change(final Color initialColor, final Color newColor) {
        setSelectedColor(newColor);
    }

    @Override
    public void ok(final Color initialColor, final Color acceptedColor) {
        setSelectedColor(acceptedColor);
    }

    @Override
    public void cancel(final Color initialColor, final Color cancelledColor) {
        setSelectedColor(initialColor);
    }
}