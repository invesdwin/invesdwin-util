package de.invesdwin.util.swing;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

import javax.annotation.concurrent.Immutable;
import javax.swing.ImageIcon;

@Immutable
public final class HiDPI {

    private static double scaleFactor = determineScaleFactor();
    private static float scaleFactorFloat = (float) scaleFactor;

    private HiDPI() {}

    private static double determineScaleFactor() {
        if (GraphicsEnvironment.isHeadless()) {
            return 1D;
        }
        final double trueHorizontalLines = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        //we scale based on 1920x1080 (1k), since that is where normally the scaling comes into play with 2.0 for 4k
        final double scaledHorizontalLines = 1080;
        final double dpiScaleFactor = trueHorizontalLines / scaledHorizontalLines;
        return dpiScaleFactor;
    }

    public static void setScaleFactor(final double scaleFactor) {
        HiDPI.scaleFactor = scaleFactor;
        HiDPI.scaleFactorFloat = (float) scaleFactor;
    }

    public static double getScaleFactor() {
        return scaleFactor;
    }

    public static float getScaleFactorFloat() {
        return scaleFactorFloat;
    }

    public static Font scale(final Font font) {
        return Fonts.resizeFont(font, scale(font.getSize()));
    }

    public static Font descale(final Font font) {
        return Fonts.resizeFont(font, descale(font.getSize()));
    }

    public static int scale(final int size) {
        return (int) Math.ceil(size * scaleFactor);
    }

    public static int descale(final int size) {
        return (int) Math.ceil(size / scaleFactor);
    }

    public static double scale(final double size) {
        return size * scaleFactor;
    }

    public static double descale(final double size) {
        return size / scaleFactor;
    }

    public static float scale(final float size) {
        return size * scaleFactorFloat;
    }

    public static float descale(final float size) {
        return size / scaleFactorFloat;
    }

    public static Dimension scale(final Dimension dimension) {
        return new Dimension(scale(dimension.width), scale(dimension.height));
    }

    public static Dimension descale(final Dimension dimension) {
        return new Dimension(descale(dimension.width), descale(dimension.height));
    }

    public static ImageIcon scale(final ImageIcon icon) {
        return new ImageIcon(icon.getImage()
                .getScaledInstance(scale(icon.getIconWidth()), scale(icon.getIconHeight()),
                        java.awt.Image.SCALE_SMOOTH));
    }

    public static ImageIcon descale(final ImageIcon icon) {
        return new ImageIcon(icon.getImage()
                .getScaledInstance(descale(icon.getIconWidth()), descale(icon.getIconHeight()),
                        java.awt.Image.SCALE_SMOOTH));
    }

}
