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

    /**
     * Edges will only get punished when the values size is >= 5, or else the data is insufficient to tell that the
     * edges are actually worse.
     */
    public E withPunishEdges(final boolean punishEdges, final boolean higherIsBetter) {
        this.punishEdges = punishEdges;
        this.higherIsBetter = higherIsBetter;
        return (E) this;
    }

}
