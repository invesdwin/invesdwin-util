package de.invesdwin.util.swing.table;

import java.awt.Dimension;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JTable;
import javax.swing.JViewport;

/**
 * https://bugs.openjdk.java.net/browse/JDK-4127936
 */
@NotThreadSafe
public class JTableWithHorizontalScroll extends JTable {

    // when the viewport shrinks below the preferred size, stop tracking the viewport width
    @Override
    public boolean getScrollableTracksViewportWidth() {
        if (autoResizeMode != AUTO_RESIZE_OFF) {
            if (getParent() instanceof JViewport) {
                return (((JViewport) getParent()).getWidth() > getPreferredSize().width);
            }
        }
        return false;
    }

    // when the viewport shrinks below the preferred size, return the minimum size
    // so that scrollbars will be shown
    @Override
    public Dimension getPreferredSize() {
        if (getParent() instanceof JViewport) {
            if (((JViewport) getParent()).getWidth() < super.getPreferredSize().width) {
                return getMinimumSize();
            }
        }

        return super.getPreferredSize();
    }

}
