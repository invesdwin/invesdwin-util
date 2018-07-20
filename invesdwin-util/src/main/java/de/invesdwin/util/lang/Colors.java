package de.invesdwin.util.lang;

import java.awt.Color;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Colors {

    private Colors() {}

    public static String toHexHtml(final Color color) {
        return "#" + toHex(color);
    }

    public static String toHex(final Color color) {
        return Strings.leftPad(Integer.toHexString(color.getRGB() & 0xffffff), 6, "0");
    }

}
