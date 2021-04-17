package de.invesdwin.util.math.stream.doubl.error;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.stream.doubl.IDoubleDoubleStreamAlgorithm;

/**
 * https://datascience.stackexchange.com/questions/42760/mad-vs-rmse-vs-mae-vs-msle-vs-r%C2%B2-when-to-use-which
 * 
 * Mean Error: ME=mean(e)
 */
@Immutable
public final class DoubleStreamError implements IDoubleDoubleStreamAlgorithm {

    public static final DoubleStreamError INSTANCE = new DoubleStreamError();

    private DoubleStreamError() {
    }

    @Override
    public double process(final double prediction, final double actual) {
        return error(prediction, actual);
    }

    public static double error(final double prediction, final double actual) {
        return prediction - actual;
    }

}
