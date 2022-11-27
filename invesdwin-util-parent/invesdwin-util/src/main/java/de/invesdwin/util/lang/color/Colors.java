package de.invesdwin.util.lang.color;

import java.awt.Color;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;

@Immutable
public final class Colors {

    public static final Color INVISIBLE_COLOR = new Color(0, 0, 0, 0);

    public static final int MIN_ALPHA = 0;
    public static final int MAX_ALPHA = 255;

    private Colors() {
    }

    public static String toHexHtml(final Color color) {
        return "#" + toHex(color);
    }

    public static String toHex(final Color color) {
        return Strings.leftPad(Integer.toHexString(color.getRGB() & 0xffffff), 6, "0");
    }

    public static Color fromHex(final String hex) {
        return Color.decode(Strings.putPrefix(hex, "#"));
    }

    /**
     * Returns a value between 0 and 100
     */
    public static Percent getTransparency(final Color color) {
        final int alpha = getAlpha(color);
        return alphaToTransparency(alpha);
    }

    public static int getAlpha(final Color color) {
        return color.getAlpha();
    }

    /**
     * Returns a value between 0 and 100
     */
    public static Percent alphaToTransparency(final double alpha) {
        final double percent = 1D - (alpha / MAX_ALPHA);
        return new Percent(percent, PercentScale.RATE);
    }

    public static int transparencyToAlpha(final Percent transparency) {
        return (int) ((1D - transparency.getRate()) * MAX_ALPHA);
    }

    public static Color modifyTransparencyBy(final Color color, final Percent modifyPercent) {
        final int alpha = modifyTransparencyBy(getAlpha(color), modifyPercent);
        return setAlpha(color, alpha);
    }

    public static int modifyTransparencyBy(final int alpha, final Percent modifyPercent) {
        final int modified = (int) (alpha + modifyPercent.getRate() * alpha);
        return Integers.between(modified, MIN_ALPHA, MAX_ALPHA);
    }

    public static Color modifyAlphaBy(final Color color, final int modifyAlpha) {
        final int alpha = modifyAlphaBy(getAlpha(color), modifyAlpha);
        return setAlpha(color, alpha);
    }

    public static int modifyAlphaBy(final int alpha, final int modifyAlpha) {
        final int modified = alpha + modifyAlpha;
        return Integers.between(modified, MIN_ALPHA, MAX_ALPHA);
    }

    public static Color setTransparency(final Color color, final Percent transparency) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), transparencyToAlpha(transparency));
    }

    public static Color setAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static Color getContrastColor(final Color color) {
        return getContrastColor(color, Color.black, Color.white);
    }

    /**
     * https://stackoverflow.com/questions/4672271/reverse-opposing-colors
     */
    public static Color getContrastColor(final Color color, final Color dark, final Color bright) {
        return isBrightColor(color) ? dark : bright;
    }

    public static boolean isBrightColor(final Color color) {
        final double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return y >= 128;
    }

    public static boolean isDarkColor(final Color color) {
        return !isBrightColor(color);
    }

}
