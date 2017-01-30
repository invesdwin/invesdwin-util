package de.invesdwin.util.math.decimal.internal.resample;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.decimal.internal.DecimalAggregate;

@ThreadSafe
public class CaseReplacementResampler<E extends ADecimal<E>> implements IDecimalResampler<E> {

    private final List<? extends E> sample;
    private final E converter;

    public CaseReplacementResampler(final DecimalAggregate<E> parent) {
        this.sample = parent.values();
        this.converter = parent.getConverter();
    }

    @Override
    public IDecimalAggregate<E> resample(final RandomGenerator random) {
        final int size = sample.size();
        final List<E> resample = new ArrayList<E>(size);
        for (int resampleIdx = 0; resampleIdx < size; resampleIdx++) {
            final int sourceIdx = random.nextInt(size);
            resample.add(sample.get(sourceIdx));
        }
        return new DecimalAggregate<E>(resample, converter);
    }

}
