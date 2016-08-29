package de.invesdwin.util.math.decimal.config;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class AInterpolationConfig<E> {

    private boolean punishEdges = false;
    private boolean higherIsBetter = true;

    public boolean isPunishEdges() {
        return punishEdges;
    }

    public boolean isHigherIsBetter() {
        return higherIsBetter;
    }

    public E withPunishEdges(final boolean punishEdges, final boolean higherIsBetter) {
        this.punishEdges = punishEdges;
        this.higherIsBetter = higherIsBetter;
        return (E) this;
    }

}
