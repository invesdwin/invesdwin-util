package de.invesdwin.util.swing.text;

import java.awt.event.KeyEvent;

import javax.annotation.concurrent.Immutable;
import javax.swing.KeyStroke;

import de.invesdwin.norva.beanpath.annotation.BeanPathEndPoint;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.string.Strings;

@BeanPathEndPoint
@Immutable
public final class Hotkey {

    private static final String SEPARATOR = " ";
    private final int key;
    private final int modifiers;

    public Hotkey(final int key, final int modifiers) {
        this.key = key;

        if (isDefined()) {
            this.modifiers = modifiers;
        } else {
            this.modifiers = KeyEvent.VK_UNDEFINED;
        }
    }

    public int getKey() {
        return key;
    }

    public int getModifiers() {
        return modifiers;
    }

    @Override
    public String toString() {
        return encode(key, modifiers);
    }

    public static String encode(final int key, final int modifiers) {
        return key + SEPARATOR + modifiers;
    }

    public static String encode(final Hotkey hotkey) {
        return Strings.asString(hotkey);
    }

    public static Hotkey decode(final String encoded) {
        if (encoded == null) {
            return null;
        }

        final String[] split = encoded.split(SEPARATOR);
        if (split.length != 2) {
            return null;
        }

        if (!Strings.isInteger(split[0]) || !Strings.isInteger(split[1])) {
            return null;
        }

        return new Hotkey(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    public boolean isDefined() {
        return key != KeyEvent.VK_UNDEFINED;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Hotkey) {
            final Hotkey hotkey = (Hotkey) obj;
            return this.key == hotkey.key && this.modifiers == hotkey.modifiers;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(Hotkey.class, this.key, this.modifiers);
    }

    public KeyStroke toKeyStroke() {
        if (!isDefined()) {
            return null;
        }

        return KeyStroke.getKeyStroke(key, modifiers);
    }
}
