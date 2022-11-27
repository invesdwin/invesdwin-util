package de.invesdwin.util.lang.color;

import java.awt.Color;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class LightColorBrewerProvider extends ColorBrewerProvider {

    @Override
    public Color getNextColor() {
        while (true) {
            final Color color = super.getNextColor();
            if (Colors.isBrightColor(color)) {
                return color;
            }
        }
    }

}
