package de.invesdwin.util.math.expression;

import de.invesdwin.util.time.fdate.FDate;

public interface IPreviousKeyFunction {

    FDate getPreviousKey(FDate key, int index);

}
