package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.variable.Constant;
import de.invesdwin.util.math.expression.variable.IVariable;

@Immutable
public final class Variables {

    public static final IVariable PI = new Constant("pi", Math.PI);

    public static final IVariable EULER = new Constant("euler", Math.E);

    private Variables() {}

}
