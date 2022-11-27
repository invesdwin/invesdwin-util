package de.invesdwin.util.lang.color;

import java.awt.Color;

import javax.annotation.concurrent.NotThreadSafe;

import org.jcolorbrewer.ColorBrewer;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.iterable.buffer.BufferingIterator;

@NotThreadSafe
public class ColorBrewerProvider implements IColorProvider {

    private final BufferingIterator<Color> allColors = new BufferingIterator<Color>();

    @Override
    public Color getNextColor() {
        if (allColors.isEmpty()) {
            final ColorBrewer[] palettes = newColorPalettes();
            for (final ColorBrewer palette : palettes) {
                final Color[] colors = palette.getColorPalette(getColorCountPerPalette());
                allColors.addAll(Arrays.asList(colors));
            }
        }
        return allColors.next();
    }

    protected ColorBrewer[] newColorPalettes() {
        return ColorBrewer.getQualitativeColorPalettes(false);
    }

    protected int getColorCountPerPalette() {
        return 12;
    }

}
