package de.invesdwin.util.math.stream.decimal;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;
import de.invesdwin.util.math.stream.IStreamAlgorithm;
import de.invesdwin.util.math.stream.doubl.DoubleStreamPerformanceFromHPRs;

@NotThreadSafe
public class DecimalStreamPerformanceFromHPRs implements IStreamAlgorithm<Percent, Void>, ISerializableValueObject {

    private final DoubleStreamPerformanceFromHPRs delegate = new DoubleStreamPerformanceFromHPRs() {
        @Override
        public double getInitialPerformance() {
            return DecimalStreamPerformanceFromHPRs.this.getInitialPerformance().getRate();
        }
    };
    private Percent performance;

    @Override
    public Void process(final Percent holdingPeriodReturn) {
        //improve accuracy by using log sum instead of multiplication directly for the HPRs
        delegate.process(holdingPeriodReturn.getRate());
        performance = null;
        return null;
    }

    public Percent getPerformance() {
        if (performance == null) {
            performance = new Percent(delegate.getPerformance(), PercentScale.RATE);
        }
        return performance;
    }

    public Percent getInitialPerformance() {
        return Percent.ONE_HUNDRED_PERCENT;
    }

}
