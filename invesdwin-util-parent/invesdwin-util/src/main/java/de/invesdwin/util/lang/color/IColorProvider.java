package de.invesdwin.util.lang.color;

import java.awt.Color;

@FunctionalInterface
public interface IColorProvider {

    Color getNextColor();

}
