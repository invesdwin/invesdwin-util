package de.invesdwin.util.math.stream.doubl.correlation;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;

/**
 * Single Pass Calculation:
 * 
 * r = ((n*sum(x*y)) - (sum(x)*sum(y))) / (sqrt(n*sum(x^2)-(sum(x))^2) * sqrt(n*sum(y^2)-(sum(y))^2))
 * 
 * <a href= "http://stackoverflow.com/questions/8370857/efficient-algorithm-to-calculate-correlation-between-two-arrays"
 * >Source</a>
 * <a href="http://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient#Mathematical_properties" >Single
 * -Pass</a>
 */
@NotThreadSafe
public class DoubleStreamCorrelation implements ICorrelation {

    private double n = 0D;
    private double sumXxY = 0D;
    private double sumX = 0D;
    private double sumY = 0D;
    private double sumXquadr = 0D;
    private double sumYquadr = 0D;

    public void process(final double x, final double y) {
        n++;
        sumXxY += x * y;
        sumX += x;
        sumY += y;
        sumXquadr += Doubles.square(x);
        sumYquadr += Doubles.square(y);
    }

    @Override
    public Percent getCorrelation() {
        //((n*sum(x*y)) - (sum(x)*sum(y)))
        final double s1 = n * sumXxY;
        final double s2 = sumX * sumY;
        final double dividend = s1 - s2;
        //(sqrt(n*sum(x^2)-(sum(x))^2) * sqrt(n*sum(y^2)-(sum(y))^2))
        final double s3 = Doubles.sqrt(Doubles.abs(n * sumXquadr - Doubles.square(sumX)));
        final double s4 = Doubles.sqrt(Doubles.abs(n * sumYquadr - Doubles.square(sumY)));
        final double divisor = s3 * s4;
        //r = ((n*sum(x*y)) - (sum(x)*sum(y))) / (sqrt(n*sum(x^2)-(sum(x))^2) * sqrt(n*sum(y^2)-(sum(y))^2))
        final double r = Doubles.divide(dividend, divisor);
        return new Percent(r, PercentScale.RATE);
    }

}
