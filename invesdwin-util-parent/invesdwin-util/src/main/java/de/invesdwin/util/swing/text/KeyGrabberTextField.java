package de.invesdwin.util.swing.text;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JTextField;
import javax.swing.UIManager;

@NotThreadSafe
public class KeyGrabberTextField extends JTextField implements FocusListener, KeyListener {

    public static final String ENTER_HOTKEY_TEXT = "Enter hotkey";
    public static final String DISABLED_TEXT = "Disabled";
    public static final String PROP_KEY = "key";
    public static final String PROP_MODIFIERS = "modifiers";
    private int key = 0;
    private int modifiers = 0;
    private boolean singleKeyEnabled = false;
    private boolean editing = false;

    public KeyGrabberTextField() {
        super();
    }

    public KeyGrabberTextField(final boolean enableSingleKey) {
        super();
        addFocusListener(this);
        addKeyListener(this);
        singleKeyEnabled = enableSingleKey;
    }

    private void printText() {
        if (editing) {
            setText(ENTER_HOTKEY_TEXT);
        } else {
            if (key != KeyEvent.VK_UNDEFINED) {
                String text = KeyEvent.getKeyModifiersText(modifiers);
                if (text.length() > 0) {
                    text += "+";
                }
                text += KeyEvent.getKeyText(key);
                setText(text);
            } else {
                setText(DISABLED_TEXT);
            }
        }
    }

    @Override
    public void focusGained(final FocusEvent e) {
        updateOnFocus(true);
    }

    @Override
    public void focusLost(final FocusEvent e) {
        updateOnFocus(false);
    }

    @Override
    public void keyTyped(final KeyEvent e) {
        e.consume();
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        if (e.getModifiers() == 0 && e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            setKey(KeyEvent.VK_UNDEFINED);
            setModifiers(0);
            updateOnFocus(false);
        } else {
            if (isKeyCodeNoModifier(e) && (e.getModifiers() != 0 || singleKeyEnabled)) {
                setKey(e.getKeyCode());
                setModifiers(e.getModifiers());
                updateOnFocus(false);
            }
        }
        e.consume();
    }

    private void updateOnFocus(final boolean focus) {
        editing = focus;
        if (focus) {
            setBackground((Color) UIManager.get("List.selectionBackground"));
            setForeground((Color) UIManager.get("List.selectionForeground"));
            getCaret().setSelectionVisible(false);
            getCaret().setVisible(false);
            printText();
        } else {
            setBackground((Color) UIManager.get("TextArea.background"));
            setForeground((Color) UIManager.get("TextArea.foreground"));
            printText();
        }
    }

    private boolean isKeyCodeNoModifier(final KeyEvent e) {
        return e.getKeyCode() != KeyEvent.VK_SHIFT && e.getKeyCode() != KeyEvent.VK_CONTROL
                && e.getKeyCode() != KeyEvent.VK_META && e.getKeyCode() != KeyEvent.VK_ALT
                && e.getKeyCode() != KeyEvent.VK_ALT_GRAPH;
    }

    @Override
    public void keyReleased(final KeyEvent e) {
        e.consume();
    }

    public int getKey() {
        return key;
    }

    public int getModifiers() {
        return modifiers;
    }

    public void setKey(final int key) {
        final int oldKey = this.key;
        this.key = key;
        firePropertyChange(PROP_KEY, oldKey, key);
        printText();
    }

    public void setModifiers(final int modifiers) {
        final int oldModifiers = this.modifiers;
        this.modifiers = modifiers;
        firePropertyChange(PROP_MODIFIERS, oldModifiers, modifiers);
        printText();
    }
}