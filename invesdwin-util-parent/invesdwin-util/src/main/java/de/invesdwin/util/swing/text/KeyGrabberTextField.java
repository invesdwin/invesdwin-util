package de.invesdwin.util.swing.text;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.BeanProperty;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JTextField;
import javax.swing.UIManager;

@NotThreadSafe
public class KeyGrabberTextField extends JTextField implements FocusListener, KeyListener, MouseListener {

    public static final String ENTER_HOTKEY_TEXT = "Enter hotkey";
    public static final String DISABLED_TEXT = "Disabled";
    public static final String PROP_HOTKEY = "hotkey";

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
        addMouseListener(this);
        super.setEditable(false);
        updateOnFocus(false);
        setFocusable(false);

        singleKeyEnabled = enableSingleKey;
    }

    private void printText() {
        if (editing) {
            super.setText(ENTER_HOTKEY_TEXT);
        } else {
            if (key != KeyEvent.VK_UNDEFINED) {
                String text = KeyEvent.getKeyModifiersText(modifiers);
                if (text.length() > 0) {
                    text += "+";
                }
                text += KeyEvent.getKeyText(key);
                super.setText(text);
            } else {
                super.setText(DISABLED_TEXT);
            }
        }
    }

    @Override
    public void focusLost(final FocusEvent e) {
        updateOnFocus(false);
        setFocusable(false);
    }

    @Override
    public void keyTyped(final KeyEvent e) {
        e.consume();
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        if (!editing) {
            e.consume();
            return;
        }

        if (e.getModifiers() == KeyEvent.VK_UNDEFINED && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            setHotkey(KeyEvent.VK_UNDEFINED, KeyEvent.VK_UNDEFINED);
            updateOnFocus(false);
        } else {
            if (isKeyCodeNoModifier(e) && (e.getModifiers() != KeyEvent.VK_UNDEFINED || singleKeyEnabled)) {
                setHotkey(e.getKeyCode(), e.getModifiers());
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

    @Override
    public String getText() {
        return Hotkey.encode(key, modifiers);
    }

    @Override
    @BeanProperty(bound = false, description = "the text of this component")
    public void setText(final String t) {
        setHotkey(Hotkey.decode(t));
    }

    public Hotkey getHotkey() {
        return new Hotkey(this.key, this.modifiers);
    }

    public void setHotkey(final int key, final int modifieres) {
        setHotkey(new Hotkey(key, modifieres));
    }

    public void setHotkey(final Hotkey hotkey) {
        final Hotkey oldHotkey = getHotkey();

        this.key = hotkey.getKey();
        this.modifiers = hotkey.getModifiers();

        printText();
        firePropertyChange(PROP_HOTKEY, oldHotkey, hotkey);
    }

    @Override
    public void mouseClicked(final MouseEvent e) {}

    @Override
    public void mousePressed(final MouseEvent e) {}

    @Override
    public void mouseReleased(final MouseEvent e) {
        setFocusable(true);
        requestFocus();
        updateOnFocus(true);
    }

    @Override
    public void mouseEntered(final MouseEvent e) {}

    @Override
    public void mouseExited(final MouseEvent e) {}

    @Override
    public void focusGained(final FocusEvent e) {}

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void setEditable(final boolean b) {}
}