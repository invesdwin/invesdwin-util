package de.invesdwin.util.swing.text;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JTextField;
import javax.swing.UIManager;

import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.swing.Components;

@NotThreadSafe
public class KeyGrabberTextField extends JTextField implements FocusListener, KeyListener, MouseListener {

    public static final boolean DEFAULT_SINGLE_KEY_ENABLED = true;
    public static final String PROP_HOTKEY = "hotkey";

    private static final String ENTER_HOTKEY_TEXT = "Enter hotkey";
    private static final String DISABLED_TEXT = "Disabled";

    private final boolean singleKeyEnabled;
    private int key;
    private int modifiers;
    private boolean editing;
    private boolean editable;
    private final boolean initialized;

    public KeyGrabberTextField() {
        this(DEFAULT_SINGLE_KEY_ENABLED);
    }

    public KeyGrabberTextField(final boolean singleKeyEnabled) {
        this.singleKeyEnabled = singleKeyEnabled;

        addFocusListener(this);
        addKeyListener(this);
        addMouseListener(this);

        super.setEditable(false);
        updateOnEditing(false);
        setFocusable(false);

        initialized = true;
    }

    private void printText() {
        if (editing) {
            super.setText(ENTER_HOTKEY_TEXT);
        } else {
            if (key != KeyEvent.VK_UNDEFINED) {
                String text = KeyEvent.getModifiersExText(modifiers);
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
        if (editing) {
            //keep caret where it is when popup menu is shown
            updateOnEditing(false);
        }
        setFocusable(false);
    }

    @Override
    public void keyTyped(final KeyEvent e) {
        e.consume();
    }

    @Override
    public void keyReleased(final KeyEvent e) {
        e.consume();
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        e.consume();
        if (!editing || !editable || !isEnabled()) {
            return;
        }
        if (e.getModifiersEx() == KeyEvent.VK_UNDEFINED && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            //escape cancels and keeps old hotkey
            updateOnEditing(false);
        } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            //ctrl+escape disables the hotkey
            setHotkey(KeyEvent.VK_UNDEFINED, KeyEvent.VK_UNDEFINED);
            updateOnEditing(false);
        } else if (isKeyCodeNoModifier(e) && (e.getModifiersEx() != KeyEvent.VK_UNDEFINED || singleKeyEnabled)) {
            setHotkey(e.getKeyCode(), e.getModifiersEx());
            updateOnEditing(false);
        }
    }

    private void updateOnEditing(final boolean newEditing) {
        //don't show caret, needs to be updated multiple times
        getCaret().setVisible(false);

        if (!editable || !isEnabled()) {
            Components.setBackground(this, UIManager.getColor("TextField.inactiveBackground"));
            Components.setForeground(this, UIManager.getColor("TextField.inactiveForeground"));
            editing = false;
            printText();
            return;
        }
        editing = newEditing;
        Components.setBackground(this, UIManager.getColor("TextField.background"));
        Components.setForeground(this, UIManager.getColor("TextField.foreground"));
        if (newEditing) {
            printText();
            select(0, Integer.MAX_VALUE);
        } else {
            printText();
        }
    }

    private boolean isKeyCodeNoModifier(final KeyEvent e) {
        return e.getKeyCode() != KeyEvent.VK_SHIFT && e.getKeyCode() != KeyEvent.VK_CONTROL
                && e.getKeyCode() != KeyEvent.VK_META && e.getKeyCode() != KeyEvent.VK_ALT
                && e.getKeyCode() != KeyEvent.VK_ALT_GRAPH;
    }

    @Override
    public String getText() {
        return Hotkey.encode(key, modifiers);
    }

    @Override
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
    public void mousePressed(final MouseEvent e) {
        if (editing) {
            e.consume();
        }
        setFocusable(true);
        requestFocus();
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        if (editing) {
            e.consume();
            //restore full selection highlighting
            select(0, Integer.MAX_VALUE);
        }
    }

    @Override
    public void setEnabled(final boolean enabled) {
        final boolean oldEnabled = isEnabled();
        if (oldEnabled != enabled) {
            updateOnEditing(editing);
        }
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        if (!editable || !isEnabled()) {
            return;
        }
        if (editing) {
            e.consume();
            if (e.getButton() == MouseEvent.BUTTON2 && e.getClickCount() == 1) {
                //middle mouse removes the hotkey
                setHotkey(KeyEvent.VK_UNDEFINED, KeyEvent.VK_UNDEFINED);
                updateOnEditing(false);
            } else if (e.getButton() == MouseEvent.BUTTON1) {
                //disable editing when clicked again
                updateOnEditing(false);
            }
        } else {
            if (!contains(e.getX(), e.getY())) {
                return;
            }
            if (Strings.isNotBlank(getSelectedText())) {
                return;
            }
            if (e.getClickCount() != 1) {
                return;
            }
            if (e.getButton() == MouseEvent.BUTTON1) {
                //start editing when clicked into
                updateOnEditing(true);
            }
        }
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        //noop
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        //noop
    }

    @Override
    public void focusGained(final FocusEvent e) {
        //noop
    }

    @Override
    public void setEditable(final boolean editable) {
        if (!initialized) {
            return;
        }
        final boolean oldEditable = this.editable;
        this.editable = editable;
        if (oldEditable != editable) {
            updateOnEditing(editing);
        }
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

}