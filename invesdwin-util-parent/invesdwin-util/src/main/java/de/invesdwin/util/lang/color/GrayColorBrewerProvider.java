package de.invesdwin.util.lang.color;

import javax.annotation.concurrent.NotThreadSafe;

import org.jcolorbrewer.ColorBrewer;

@NotThreadSafe
public class GrayColorBrewerProvider extends ColorBrewerProvider {

    @Override
    protected ColorBrewer[] newColorPalettes() {
        return new ColorBrewer[] { ColorBrewer.Greys };
    }

}
