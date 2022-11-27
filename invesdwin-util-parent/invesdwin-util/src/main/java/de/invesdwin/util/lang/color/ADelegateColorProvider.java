package de.invesdwin.util.lang.color;

import java.awt.Color;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class ADelegateColorProvider implements IColorProvider {

    @Override
    public Color getNextColor() {
        return getDelegate().getNextColor();
    }

    protected abstract IColorProvider getDelegate();

}
