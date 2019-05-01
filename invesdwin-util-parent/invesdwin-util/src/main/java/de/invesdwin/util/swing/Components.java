package de.invesdwin.util.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;

import javax.annotation.concurrent.Immutable;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

@Immutable
public final class Components {

    private Components() {}

    public static void updateForeground(final Component component, final Color foregroundColor) {
        if (!foregroundColor.equals(component.getForeground())) {
            component.setForeground(foregroundColor);
        }
    }

    public static void updateBackground(final Component component, final Color backgroundColor) {
        if (!backgroundColor.equals(component.getBackground())) {
            component.setBackground(backgroundColor);
        }
    }

    public static void updateRowHeight(final JTable table, final int row, final int height) {
        if (height != table.getRowHeight(row)) {
            table.setRowHeight(row, height);
        }
    }

    public static void updateMinWidth(final TableColumn column, final int minWidth) {
        if (minWidth != column.getMinWidth()) {
            column.setMinWidth(minWidth);
        }
    }

    public static void updateMaxWidth(final TableColumn column, final int maxWidth) {
        if (maxWidth != column.getMaxWidth()) {
            column.setMaxWidth(maxWidth);
        }
    }

    public static void updatePreferredWidth(final TableColumn column, final int preferredWidth) {
        if (preferredWidth != column.getPreferredWidth()) {
            column.setPreferredWidth(preferredWidth);
        }
    }

    public static boolean isMouseOverComponent(final Component component) {
        if (!component.isShowing()) {
            return false;
        } else {
            final Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
            final Point componentLocation = component.getLocationOnScreen();
            return mouseLocation.x >= componentLocation.x
                    && mouseLocation.x <= componentLocation.x + component.getWidth()
                    && mouseLocation.y >= componentLocation.y
                    && mouseLocation.y <= componentLocation.y + component.getHeight();
        }
    }

}
