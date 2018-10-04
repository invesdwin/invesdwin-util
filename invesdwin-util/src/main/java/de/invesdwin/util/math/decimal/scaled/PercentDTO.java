package de.invesdwin.util.math.decimal.scaled;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.math.decimal.Decimal;

@NotThreadSafe
public class PercentDTO implements IPercentData, ISerializableValueObject {

    private double rate = Double.NaN;

    public PercentDTO(final double rate) {
        this.rate = rate;
    }

    public PercentDTO() {}

    @Override
    public double getRate() {
        return rate;
    }

    public void setRate(final double rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return String.valueOf(new Decimal(rate));
    }

}
