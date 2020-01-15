package de.invesdwin.util.swing;

import java.awt.Component;
import java.awt.Font;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Fonts {

    private Fonts() {}

    public static Font resizeFont(final Font font, final int size) {
        if (font.getSize() != size) {
            return font.deriveFont((float) size);
        } else {
            return font;
        }
    }

    public static void resizeFont(final Component component, final int size) {
        if (component.getFont().getSize() != size) {
            component.setFont(resizeFont(component.getFont(), size));
        }
    }

}
