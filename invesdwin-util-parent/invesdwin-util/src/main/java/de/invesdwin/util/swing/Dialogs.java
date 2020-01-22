package de.invesdwin.util.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;

import javax.annotation.concurrent.Immutable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorChooserComponentFactory;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

import de.invesdwin.util.concurrent.reference.MutableReference;
import de.invesdwin.util.lang.Reflections;
import de.invesdwin.util.lang.uri.URIs;
import de.invesdwin.util.swing.listener.IColorChooserListener;

@Immutable
public final class Dialogs extends javax.swing.JOptionPane {

    private static final KeyStroke WINDOW_CLOSING_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    private static final String ESCAPE_STROKE_KEY = Dialogs.class.getName() + ":WINDOW_CLOSING";

    private static final String EYE_DROPPER_COLOR_CHOOSER_PANEL_CLASS = "org.jdesktop.swingx.color.EyeDropperColorChooserPanel";

    private static AComponentVisitor dialogVisitor;

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory.getXLogger(Dialogs.class);

    private Dialogs() {}

    public static void setDialogVisitor(final AComponentVisitor dialogVisitor) {
        Dialogs.dialogVisitor = dialogVisitor;
    }

    public static AComponentVisitor getDialogVisitor() {
        return dialogVisitor;
    }

    public static String showInputDialog(final Object message) {
        return javax.swing.JOptionPane.showInputDialog(standardizeMessage(message));
    }

    public static String showInputDialog(final Object message, final Object initialSelectionValue) {
        return javax.swing.JOptionPane.showInputDialog(standardizeMessage(message), initialSelectionValue);
    }

    public static String showInputDialog(final Component parentComponent, final Object message) {
        return javax.swing.JOptionPane.showInputDialog(parentComponent, standardizeMessage(message));
    }

    public static String showInputDialog(final Component parentComponent, final Object message,
            final Object initialSelectionValue) {
        return javax.swing.JOptionPane.showInputDialog(parentComponent, standardizeMessage(message),
                initialSelectionValue);
    }

    public static String showInputDialog(final Component parentComponent, final Object message, final String title,
            final int messageType) {
        return javax.swing.JOptionPane.showInputDialog(parentComponent, standardizeMessage(message), title,
                messageType);
    }

    public static Object showInputDialog(final Component parentComponent, final Object message, final String title,
            final int messageType, final Icon icon, final Object[] selectionValues,
            final Object initialSelectionValue) {
        return javax.swing.JOptionPane.showInputDialog(parentComponent, standardizeMessage(message), title, messageType,
                icon, selectionValues, initialSelectionValue);
    }

    public static void showMessageDialog(final Component parentComponent, final Object message) {
        javax.swing.JOptionPane.showMessageDialog(parentComponent, standardizeMessage(message));
    }

    public static void showMessageDialog(final Component parentComponent, final Object message, final String title,
            final int messageType) {
        javax.swing.JOptionPane.showMessageDialog(parentComponent, standardizeMessage(message), title, messageType);
    }

    public static void showMessageDialog(final Component parentComponent, final Object message, final String title,
            final int messageType, final Icon icon) {
        javax.swing.JOptionPane.showMessageDialog(parentComponent, standardizeMessage(message), title, messageType,
                icon);
    }

    public static int showConfirmDialog(final Component parentComponent, final Object message) {
        return javax.swing.JOptionPane.showConfirmDialog(parentComponent, standardizeMessage(message));
    }

    public static int showConfirmDialog(final Component parentComponent, final Object message, final String title,
            final int optionType) {
        return javax.swing.JOptionPane.showConfirmDialog(parentComponent, standardizeMessage(message), title,
                optionType);
    }

    public static int showConfirmDialog(final Component parentComponent, final Object message, final String title,
            final int optionType, final int messageType) {
        return javax.swing.JOptionPane.showConfirmDialog(parentComponent, standardizeMessage(message), title,
                optionType, messageType);
    }

    public static int showConfirmDialog(final Component parentComponent, final Object message, final String title,
            final int optionType, final int messageType, final Icon icon) {
        return javax.swing.JOptionPane.showConfirmDialog(parentComponent, standardizeMessage(message), title,
                optionType, messageType, icon);
    }

    //CHECKSTYLE:OFF
    public static int showOptionDialog(final Component parentComponent, final Object message, final String title,
            final int optionType, final int messageType, final Icon icon, final Object[] options,
            final Object initialValue) {
        //CHECKSTYLE:ON
        return javax.swing.JOptionPane.showOptionDialog(parentComponent, standardizeMessage(message), title, optionType,
                messageType, icon, options, initialValue);
    }

    public static void showInternalMessageDialog(final Component parentComponent, final Object message) {
        javax.swing.JOptionPane.showInternalMessageDialog(parentComponent, standardizeMessage(message));
    }

    public static void showInternalMessageDialog(final Component parentComponent, final Object message,
            final String title, final int messageType) {
        javax.swing.JOptionPane.showInternalMessageDialog(parentComponent, standardizeMessage(message), title,
                messageType);
    }

    public static void showInternalMessageDialog(final Component parentComponent, final Object message,
            final String title, final int messageType, final Icon icon) {
        javax.swing.JOptionPane.showInternalMessageDialog(parentComponent, standardizeMessage(message), title,
                messageType, icon);
    }

    public static int showInternalConfirmDialog(final Component parentComponent, final Object message) {
        return javax.swing.JOptionPane.showInternalConfirmDialog(parentComponent, standardizeMessage(message));
    }

    public static int showInternalConfirmDialog(final Component parentComponent, final Object message,
            final String title, final int optionType) {
        return javax.swing.JOptionPane.showInternalConfirmDialog(parentComponent, standardizeMessage(message), title,
                optionType);
    }

    public static int showInternalConfirmDialog(final Component parentComponent, final Object message,
            final String title, final int optionType, final int messageType) {
        return javax.swing.JOptionPane.showInternalConfirmDialog(parentComponent, standardizeMessage(message), title,
                optionType, messageType);
    }

    public static int showInternalConfirmDialog(final Component parentComponent, final Object message,
            final String title, final int optionType, final int messageType, final Icon icon) {
        return javax.swing.JOptionPane.showInternalConfirmDialog(parentComponent, standardizeMessage(message), title,
                optionType, messageType, icon);
    }

    //CHECKSTYLE:OFF
    public static int showInternalOptionDialog(final Component parentComponent, final Object message,
            final String title, final int optionType, final int messageType, final Icon icon, final Object[] options,
            final Object initialValue) {
        //CHECKSTYLE:ON
        return javax.swing.JOptionPane.showInternalOptionDialog(parentComponent, standardizeMessage(message), title,
                optionType, messageType, icon, options, initialValue);
    }

    public static String showInternalInputDialog(final Component parentComponent, final Object message) {
        return javax.swing.JOptionPane.showInternalInputDialog(parentComponent, standardizeMessage(message));
    }

    public static String showInternalInputDialog(final Component parentComponent, final Object message,
            final String title, final int messageType) {
        return javax.swing.JOptionPane.showInternalInputDialog(parentComponent, standardizeMessage(message), title,
                messageType);
    }

    public static Object showInternalInputDialog(final Component parentComponent, final Object message,
            final String title, final int messageType, final Icon icon, final Object[] selectionValues,
            final Object initialSelectionValue) {
        return javax.swing.JOptionPane.showInternalInputDialog(parentComponent, standardizeMessage(message), title,
                messageType, icon, selectionValues, initialSelectionValue);
    }

    /**
     * So that links can be clicked on properly.
     */
    private static Object standardizeMessage(final Object message) {
        Object ret = message;
        if (message instanceof CharSequence) {
            final String messageString = message.toString();
            if (messageString.contains("<html>")) {
                final JEditorPane messagePane = newHtmlMessagePane();
                messagePane.setText(messageString);
                ret = messagePane;
            }
        }

        if (ret instanceof Component && dialogVisitor != null) {
            final Component component = (Component) ret;
            dialogVisitor.visitAll(component);
        }

        return ret;
    }

    public static JEditorPane newHtmlMessagePane() {
        return newHtmlMessagePane(new JEditorPane());
    }

    public static JEditorPane newHtmlMessagePane(final JEditorPane editor) {
        editor.setContentType("text/html");
        editor.setEditable(false);
        editor.setOpaque(false);
        editor.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(final HyperlinkEvent e) {
                if (e.getEventType() == EventType.ACTIVATED) {
                    try {
                        final URL url = e.getURL();
                        final String file;
                        if (url != null) {
                            file = url.getFile();
                        } else {
                            file = null;
                        }
                        if (file != null) {
                            if (!new File(URIs.decode(file)).exists()) {
                                Dialogs.showMessageDialog(Dialogs.getRootFrame(),
                                        "<html><b>File does not exist:</b><br>" + file, "File not found",
                                        Dialogs.ERROR_MESSAGE);
                                return;
                            }
                        }
                        if (url != null) {
                            final URI uri = URIs.asUri(url);
                            Desktop.getDesktop().browse(uri);
                        } else {
                            LOG.error("Invalid URL: %s", e.getDescription());
                        }
                    } catch (final Throwable e1) {
                        throw new RuntimeException(e1);
                    }
                }
            }
        });
        editor.setDisabledTextColor(editor.getForeground());
        return editor;
    }

    public static Color showColorChooserDialog(final Component component, final String name, final Color initialColor) {
        return showColorChooserDialog(component, name, initialColor, true);
    }

    public static Color showColorChooserDialog(final Component component, final String name, final Color initialColor,
            final boolean transparency) {
        return showColorChooserDialog(component, name, initialColor, transparency, null);
    }

    public static Color showColorChooserDialog(final Component component, final String name, final Color initialColor,
            final boolean transparency, final IColorChooserListener listener) {
        final JColorChooser pane = new JColorChooser(initialColor != null ? initialColor : Color.white);
        if (listener != null) {
            pane.getSelectionModel().addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(final ChangeEvent e) {
                    listener.change(initialColor, pane.getColor());
                }
            });
        }

        pane.setChooserPanels(ColorChooserComponentFactory.getDefaultChooserPanels());
        if (Reflections.classExists(EYE_DROPPER_COLOR_CHOOSER_PANEL_CLASS)) {
            final Class<Object> eyeDropperPanelClass = Reflections.classForName(EYE_DROPPER_COLOR_CHOOSER_PANEL_CLASS);
            try {
                final AbstractColorChooserPanel eyeDropperPanel = (AbstractColorChooserPanel) eyeDropperPanelClass
                        .getDeclaredConstructor()
                        .newInstance();
                pane.addChooserPanel(eyeDropperPanel);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                throw new RuntimeException(e);
            }
        }
        final Method setColorTransparencySelectionEnabledMethod = Reflections
                .findMethod(AbstractColorChooserPanel.class, "setColorTransparencySelectionEnabled");
        for (final AbstractColorChooserPanel ccPanel : pane.getChooserPanels()) {
            if (setColorTransparencySelectionEnabledMethod != null) {
                try {
                    setColorTransparencySelectionEnabledMethod.invoke(ccPanel, transparency);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        final MutableReference<Color> selectedColor = new MutableReference<>();
        selectedColor.set(initialColor);
        final ActionListener okListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                final Color color = pane.getColor();
                if (color != null) {
                    selectedColor.set(color);
                }
                listener.ok(initialColor, color);
            }
        };
        final ActionListener cancelListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (listener != null) {
                    listener.cancel(initialColor, pane.getColor());
                }
            }
        };
        final JDialog dialog = JColorChooser.createDialog(component, name, true, pane, okListener, cancelListener);
        installEscapeCloseOperation(dialog);
        dialog.setVisible(true);
        return selectedColor.get();
    }

    /**
     * https://stackoverflow.com/questions/642925/swing-how-do-i-close-a-dialog-when-the-esc-key-is-pressed
     */
    public static void installEscapeCloseOperation(final JDialog dialog) {
        final Action dispatchClosing = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
            }
        };
        final JRootPane root = dialog.getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(WINDOW_CLOSING_STROKE, ESCAPE_STROKE_KEY);
        root.getActionMap().put(ESCAPE_STROKE_KEY, dispatchClosing);
    }

}
