package de.invesdwin.util.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.annotation.concurrent.Immutable;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.table.TableColumn;

import de.invesdwin.util.lang.Objects;

@Immutable
public final class Components {

    private Components() {}

    public static void setForeground(final Component component, final Color foregroundColor) {
        if (!foregroundColor.equals(component.getForeground())) {
            component.setForeground(foregroundColor);
        }
    }

    public static void setBackground(final Component component, final Color backgroundColor) {
        if (!backgroundColor.equals(component.getBackground())) {
            component.setBackground(backgroundColor);
        }
    }

    public static void setRowHeight(final JTable table, final int row, final int height) {
        if (height != table.getRowHeight(row)) {
            table.setRowHeight(row, height);
        }
    }

    public static void setMinWidth(final TableColumn column, final int minWidth) {
        if (minWidth != column.getMinWidth()) {
            column.setMinWidth(minWidth);
        }
    }

    public static void setMaxWidth(final TableColumn column, final int maxWidth) {
        if (maxWidth != column.getMaxWidth()) {
            column.setMaxWidth(maxWidth);
        }
    }

    public static void setPreferredWidth(final TableColumn column, final int preferredWidth) {
        if (preferredWidth != column.getPreferredWidth()) {
            column.setPreferredWidth(preferredWidth);
        }
    }

    public static boolean isMouseOverComponent(final Component component) {
        if (!component.isShowing()) {
            return false;
        } else {
            final Point locationOnScreen = MouseInfo.getPointerInfo().getLocation();
            final Point locationOnComponent = new Point(locationOnScreen);
            SwingUtilities.convertPointFromScreen(locationOnComponent, component);
            return component.contains(locationOnComponent);
        }
    }

    public static void setToolTipText(final JComponent component, final String text) {
        if (!Objects.equals(component.getToolTipText(), text)) {
            component.setToolTipText(text);
            updateToolTip(component);
        }
    }

    /**
     * https://stackoverflow.com/questions/12822819/dynamically-update-tooltip-currently-displayed
     */
    public static void updateToolTip(final Component component) {
        if (component.isShowing()) {
            final Point locationOnScreen = MouseInfo.getPointerInfo().getLocation();
            final Point locationOnComponent = new Point(locationOnScreen);
            SwingUtilities.convertPointFromScreen(locationOnComponent, component);
            if (component.contains(locationOnComponent)) {
                ToolTipManager.sharedInstance()
                        .mouseMoved(new MouseEvent(component, -1, System.currentTimeMillis(), 0, locationOnComponent.x,
                                locationOnComponent.y, locationOnScreen.x, locationOnScreen.y, 0, false, 0));
            }
        }
    }

    public static void setEnabled(final Component component, final boolean enabled) {
        if (component.isEnabled() != enabled) {
            component.setEnabled(enabled);
        }
    }

    public static void setVisible(final Component component, final boolean visible) {
        if (component.isVisible() != visible) {
            component.setVisible(visible);
        }
    }

    public static void setBorder(final JComponent component, final Border border) {
        if (component.getBorder() != border) {
            component.setBorder(border);
        }
    }

}
