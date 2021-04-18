package de.invesdwin.util.math.stream.doubl.error;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.stream.doubl.DoubleStreamMedian;
import de.invesdwin.util.math.stream.doubl.IDoubleDoubleStreamAlgorithm;

/**
 * Median Absolute Deviation: MAD=median(|e−median(e)|).
 * 
 * https://datascience.stackexchange.com/questions/42760/mad-vs-rmse-vs-mae-vs-msle-vs-r%C2%B2-when-to-use-which
 * 
 * https://github.com/elastic/elasticsearch/pull/34482
 * 
 * https://github.com/tdunning/t-digest
 *
 */
@NotThreadSafe
public class DoubleStreamMedianAbsoluteDeviation implements IDoubleDoubleStreamAlgorithm {

    private final DoubleStreamMedian median = new DoubleStreamMedian();
    private final DoubleStreamMedian mad = new DoubleStreamMedian();

    @Override
    public double process(final double actual, final double prediction) {
        final double error = DoubleStreamError.error(actual, prediction);
        median.process(error);
        final double medianE = median.getMedian();
        mad.process(Doubles.abs(error - medianE));
        return Double.NaN;
    }

    public double getMedianAbsoluteDeviation() {
        return mad.getMedian();
    }

}
