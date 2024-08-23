package de.invesdwin.util.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.Field;

import javax.annotation.concurrent.Immutable;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.table.TableColumn;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.swing.listener.ADelegateMouseMotionListener;
import de.invesdwin.util.swing.text.Hotkey;
import de.invesdwin.util.swing.text.KeyGrabberTextField;
import de.invesdwin.util.swing.text.ToolTipFormatter;

@SuppressWarnings("restriction")
@Immutable
public final class Components {

    private static ToolTipFormatter defaultToolTipFormatter = new ToolTipFormatter();

    private static final long COMPONENT_MINSIZE_FIELD_OFFSET;
    private static final long COMPONENT_MINSIZESET_FIELD_OFFSET;

    static {
        final Field minSizeField = Reflections.findField(Component.class, "minSize");
        COMPONENT_MINSIZE_FIELD_OFFSET = Reflections.getUnsafe().objectFieldOffset(minSizeField);

        final Field minSizeSetField = Reflections.findField(Component.class, "minSizeSet");
        COMPONENT_MINSIZESET_FIELD_OFFSET = Reflections.getUnsafe().objectFieldOffset(minSizeSetField);
    }

    private Components() {}

    public static void setDefaultToolTipFormatter(final ToolTipFormatter defaultToolTipFormatter) {
        Components.defaultToolTipFormatter = defaultToolTipFormatter;
    }

    public static ToolTipFormatter getDefaultToolTipFormatter() {
        return defaultToolTipFormatter;
    }

    public static void setForeground(final Component component, final Color foregroundColor) {
        if (!Objects.equals(foregroundColor, component.getForeground())) {
            component.setForeground(foregroundColor);
        }
    }

    public static void setBackground(final Component component, final Color backgroundColor) {
        if (!Objects.equals(backgroundColor, component.getBackground())) {
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

    public static void setToolTipText(final JComponent component, final String text, final boolean update) {
        setToolTipText(component, text, update, defaultToolTipFormatter);
    }

    public static void setToolTipText(final JComponent component, final String text, final boolean update,
            final ToolTipFormatter formatter) {
        final JComponent tooltipComponent = getTooltipComponent(component);
        final String htmlText;
        if (text != null) {
            final String htmlTag = "<html>";
            if (htmlTag.equalsIgnoreCase(text.trim())) {
                htmlText = null;
            } else {
                final String replaced = text.replace("\n", "<br>");
                final String formatted;
                if (formatter != null) {
                    formatted = formatter.format(replaced);
                } else {
                    formatted = replaced;
                }
                htmlText = Strings.putPrefix(formatted, htmlTag);
            }
        } else {
            htmlText = text;
        }
        if (!Objects.equals(tooltipComponent.getToolTipText(), htmlText)) {
            tooltipComponent.setToolTipText(htmlText);
            if (update) {
                updateToolTip(tooltipComponent);
            }
        }
    }

    public static JComponent getTooltipComponent(final JComponent component) {
        if (component instanceof JSpinner) {
            final JSpinner cComponent = (JSpinner) component;
            final JComponent editor = cComponent.getEditor();
            if (editor instanceof DefaultEditor) {
                final DefaultEditor cEditor = (DefaultEditor) editor;
                return cEditor.getTextField();
            }
        }
        return component;
    }

    public static void updateToolTip(final JComponent component) {
        triggerMouseMoved(component, new ADelegateMouseMotionListener() {
            @Override
            protected MouseMotionListener newDelegate() {
                return ToolTipManager.sharedInstance();
            }
        });
    }

    /**
     * Sets the focus on a (JSpinner-)TextField and selects the full-text inside of it. Just focuses the component if no
     * text is available to be selected.
     */
    public static void requestFocusAndSelectAll(final JComponent component) {
        final JComponent tooltipComponent = getTooltipComponent(component);
        tooltipComponent.requestFocusInWindow();
        if (tooltipComponent instanceof JTextComponent) {
            final JTextComponent textComponent = (JTextComponent) tooltipComponent;
            //No idea why but .selectAll() is not working properly unless i call this (otherwise useless) setText(...) line here.
            //google spat this 19 year old thread out: https://coderanch.com/t/335499/java/selectAll-JTextField
            textComponent.setText(textComponent.getText());
            textComponent.selectAll();
        }
    }

    /**
     * https://stackoverflow.com/questions/12822819/dynamically-update-tooltip-currently-displayed
     */
    public static void triggerMouseMoved(final JComponent component, final MouseMotionListener listener) {
        if (isShowingAndWindowIsActive(component)) {
            final PointerInfo pointerInfo = MouseInfo.getPointerInfo();
            if (pointerInfo == null) {
                return;
            }
            final Point locationOnScreen = pointerInfo.getLocation();
            final Point locationOnComponent = new Point(locationOnScreen);
            SwingUtilities.convertPointFromScreen(locationOnComponent, component);
            if (component.contains(locationOnComponent)) {
                listener.mouseMoved(new MouseEvent(component, -1, System.currentTimeMillis(), 0, locationOnComponent.x,
                        locationOnComponent.y, locationOnScreen.x, locationOnScreen.y, 0, false, 0));
            }
        }
    }

    public static boolean isShowingAndWindowIsActive(final Component component) {
        if (component.isShowing()) {
            return isWindowActive(component);
        } else {
            return false;
        }
    }

    public static boolean isWindowActive(final Component component) {
        final Window window = SwingUtilities.getWindowAncestor(component);
        return window.isActive();
    }

    public static boolean isMouseOverComponent(final Component component) {
        final Point locationOnComponent = getMouseLocationOnComponent(component);
        return locationOnComponent != null;
    }

    public static boolean isActiveMouseOverComponent(final Component component) {
        final Point locationOnComponent = getActiveMouseLocationOnComponent(component);
        return locationOnComponent != null;
    }

    public static Point getMouseLocationOnComponent(final Component component) {
        if (!component.isShowing()) {
            return null;
        }
        return internalGetMouseLocationOnComponent(component);
    }

    public static Point getActiveMouseLocationOnComponent(final Component component) {
        if (isShowingAndWindowIsActive(component)) {
            return null;
        }
        return internalGetMouseLocationOnComponent(component);
    }

    private static Point internalGetMouseLocationOnComponent(final Component component) {
        final PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        if (pointerInfo == null) {
            return null;
        }
        final Point locationOnScreen = pointerInfo.getLocation();
        final Point locationOnComponent = new Point(locationOnScreen);
        SwingUtilities.convertPointFromScreen(locationOnComponent, component);
        if (component.contains(locationOnComponent)) {
            return locationOnComponent;
        } else {
            return null;
        }
    }

    /**
     * https://stackoverflow.com/questions/6473464/force-a-java-tooltip-to-appear
     */
    public static void showTooltipNow(final Component component) {
        final ToolTipManager ttm = ToolTipManager.sharedInstance();
        final int oldDelay = ttm.getInitialDelay();
        ttm.setInitialDelay(0);

        final Point mousePoint = getActiveMouseLocationOnComponent(component);
        final int id = -1;
        final long when = System.currentTimeMillis();
        final int modifiers = 0;
        final int x;
        final int y;
        final int clickCount = 0;
        final boolean popupTrigger = false;
        if (mousePoint != null) {
            x = mousePoint.x;
            y = mousePoint.y;
        } else {
            x = 0;
            y = 0;
        }
        final MouseEvent event = new MouseEvent(component, id, when, modifiers, x, y, clickCount, popupTrigger);
        ttm.mouseMoved(event);
        //CHECKSTYLE:OFF
        SwingUtilities.invokeLater(new Runnable() {
            //CHECKSTYLE:ON
            @Override
            public void run() {
                ttm.setInitialDelay(oldDelay);
            }
        });
    }

    public static void showTooltipWithoutDelay(final Component component) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(final MouseEvent e) {
                Components.showTooltipNow(component);
            }
        });
    }

    public static void setEnabled(final Component component, final boolean enabled) {
        if (component.isEnabled() != enabled) {
            component.setEnabled(enabled);
        }
    }

    public static void setEditable(final JTextComponent component, final boolean editable) {
        if (component.isEditable() != editable) {
            component.setEditable(editable);
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

    public static void setText(final JComponent component, final String text) {
        if (component instanceof JTextComponent) {
            final JTextComponent cComponent = (JTextComponent) component;
            setText(cComponent, text);
        } else if (component instanceof JLabel) {
            final JLabel cComponent = (JLabel) component;
            setText(cComponent, text);
        } else if (component instanceof AbstractButton) {
            final AbstractButton cComponent = (AbstractButton) component;
            setText(cComponent, text);
        }
    }

    public static void setText(final AbstractButton component, final String text) {
        if (!Objects.equals(text, component.getText())) {
            component.setText(text);
        }
    }

    public static void setText(final JLabel component, final String text) {
        if (!Objects.equals(text, component.getText())) {
            component.setText(text);
        }
    }

    public static void setText(final JTextComponent component, final String text) {
        if (!Objects.equals(text, component.getText())) {
            component.setText(text);
        }
    }

    public static void setValue(final JSpinner component, final Object value) {
        if (!Objects.equals(value, component.getValue())) {
            component.setValue(value);
        }
    }

    public static void setHotkey(final KeyGrabberTextField component, final Hotkey hotkey) {
        if (!Objects.equals(hotkey, component.getHotkey())) {
            component.setHotkey(hotkey);
        }
    }

    public static void packHeight(final Window window) {
        if (window != null) {
            synchronized (window) {
                final Dimension minimumSizeBefore = window.getMinimumSize();
                window.setMinimumSize(new Dimension(window.getWidth(), 1));
                window.pack();
                window.setMinimumSize(minimumSizeBefore);
            }
        }
    }

    /**
     * https://stackoverflow.com/questions/2039373/maintaing-jtextarea-scroll-position
     */
    public static void disableCaretScrolling(final JTextComponent component) {
        final DefaultCaret caret = (DefaultCaret) component.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
    }

    public static void setMinimumSizeWithoutNotify(final Component component, final Dimension minimumSize) {
        Reflections.getUnsafe().putObject(component, COMPONENT_MINSIZE_FIELD_OFFSET, minimumSize);
        Reflections.getUnsafe().putBoolean(component, COMPONENT_MINSIZESET_FIELD_OFFSET, minimumSize != null);
    }

}
