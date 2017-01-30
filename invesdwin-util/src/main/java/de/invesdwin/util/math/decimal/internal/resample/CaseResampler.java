package de.invesdwin.util.math.decimal.internal.resample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.math3.random.RandomAdaptor;
import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.internal.DecimalAggregate;

@ThreadSafe
public class CaseResampler<E extends ADecimal<E>> implements IDecimalResampler<E> {

    private final List<E> sample;

    public CaseResampler(final DecimalAggregate<E> parent) {
        this.sample = parent.values();
    }

    @Override
    public Iterator<E> resample(final RandomGenerator random) {
        final List<E> sampleCopy = new ArrayList<E>(sample);
        Collections.shuffle(sampleCopy, new RandomAdaptor(random));
        return sampleCopy.iterator();
    }

}
