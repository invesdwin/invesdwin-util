package de.invesdwin.util.math.stream.doubl.correlation;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum CorrelationType {
    UpToUpAndDownToDown("U2U-D2D"),
    UpToDownAndDownToUp("U2D-D2U");

    private String text;

    CorrelationType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}