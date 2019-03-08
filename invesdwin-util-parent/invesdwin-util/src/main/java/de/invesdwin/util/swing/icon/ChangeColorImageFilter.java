package de.invesdwin.util.swing.icon;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.ImageIcon;

@NotThreadSafe
public class ChangeColorImageFilter extends RGBImageFilter {
    private final int red, green, blue;

    public ChangeColorImageFilter(final Color color) {
        red = color.getRed();
        green = color.getGreen();
        blue = color.getBlue();
        canFilterIndexColorModel = true;
    }

    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        final Color color = new Color(rgb, true);
        final int a = color.getAlpha();
        return a << 24 | red << 16 | green << 8 | blue;
    }

    public static Image apply(final Image image, final Color color) {
        final ChangeColorImageFilter filter = new ChangeColorImageFilter(color);
        final ImageProducer prod = new FilteredImageSource(image.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(prod);
    }

    public static ImageIcon apply(final ImageIcon imageIcon, final Color color) {
        return new ImageIcon(apply(imageIcon.getImage(), color));
    }
}
