package de.invesdwin.util.math.decimal.scaled;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.math.decimal.Decimal;

@NotThreadSafe
public class PercentDTO implements IPercentData, ISerializableValueObject {

    private Decimal rate;

    public PercentDTO(final Decimal rate) {
        this.rate = rate;
    }

    public PercentDTO() {}

    @Override
    public Decimal getRate() {
        return rate;
    }

    public void setRate(final Decimal rate) {
        this.rate = rate;
    }

}
