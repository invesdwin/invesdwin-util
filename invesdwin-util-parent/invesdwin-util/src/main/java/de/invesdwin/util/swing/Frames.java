package de.invesdwin.util.swing;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Integers;

@Immutable
public final class Frames {

    private Frames() {
    }

    public static Dimension getMaxFrameSize(final Window window) {
        final GraphicsConfiguration config = window.getGraphicsConfiguration();
        final Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(config);
        final int left = screenInsets.left;
        final int right = screenInsets.right;
        final int top = screenInsets.top;
        final int bottom = screenInsets.bottom;

        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int maxWidth = screenSize.width - left - right;
        final int maxHeight = screenSize.height - top - bottom;
        final Dimension maxSize = new Dimension(maxWidth, maxHeight);
        return maxSize;
    }

    /**
     * To set the size to the maximum seems to be the only reliable way to maximize a window
     * 
     * http://stackoverflow.com/questions/479523/java-swing-maximize-window
     * 
     * JFrame.MAXIMIZED_BOTH seems not to work properly.
     */
    public static void setInitialFrameSize(final Window window, final Dimension size) {
        final Dimension maxSize = getMaxFrameSize(window);
        if (size == null) {
            window.setSize(maxSize);
            window.setLocation(0, 0);
        } else {
            window.setSize(Integers.min(size.width, maxSize.width), Integers.min(size.height, maxSize.height));
            window.setLocationRelativeTo(null);
        }
    }

}
