package de.invesdwin.util.math.stream.doubl.error;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.stream.doubl.IDoubleDoubleStreamAlgorithm;

/**
 * https://www.geeksforgeeks.org/program-mean-absolute-deviation/
 * 
 * https://stackoverflow.com/questions/3903538/online-algorithm-for-calculating-absolute-deviation
 *
 */
@NotThreadSafe
public class DoubleStreamMeanAbsoluteDeviation implements IDoubleDoubleStreamAlgorithm {

    private double m2;
    private double mean;
    private int count;

    @Override
    public double process(final double actual, final double prediction) {
        //        M2=0
        //        mean=0
        //        out <- numeric(n)
        //        for(i in 1:n) {
        count++;
        final double error = DoubleStreamError.error(actual, prediction);
        //            delta <- x[i] - mean
        final double delta = error - mean;
        //            mean <- mean + delta/i
        mean = mean + delta / count;
        //            M2 = M2 + sqrt(delta*(x[i] - mean))
        m2 = m2 + Doubles.sqrt(delta * (error - mean));
        //            out[i] <- M2/i
        //        }
        return Double.NaN;
    }

    public double getMeanAbsoluteDeviation() {
        return m2 / count;
    }

}
