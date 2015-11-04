package de.invesdwin.util.time.duration;

import java.util.List;

public interface IDurationAggregate {

    IDurationAggregate reverse();

    Duration sum();

    /**
     * x_quer = (x_1 + x_2 + ... + x_n) / n
     * 
     * @see <a href="http://de.wikipedia.org/wiki/Arithmetisches_Mittel">Source</a>
     */
    Duration avg();

    Duration max();

    Duration min();

    List<? extends Duration> values();

}
