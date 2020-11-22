package de.invesdwin.util.math.stream.decimal;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.stream.IStreamAlgorithm;
import de.invesdwin.util.math.stream.doubl.DoubleStreamProduct;

@NotThreadSafe
public class DecimalStreamProduct<E extends ADecimal<E>>
        implements IStreamAlgorithm<E, Void>, ISerializableValueObject {

    private final DoubleStreamProduct delegate = new DoubleStreamProduct() {
        @Override
        protected double getValueAdjustmentAddition() {
            final E adj = DecimalStreamProduct.this.getValueAdjustmentAddition();
            if (adj == null) {
                return Double.NaN;
            } else {
                return adj.getDefaultValue();
            }
        }
    };

    private final E converter;
    private E product;

    public DecimalStreamProduct(final E converter) {
        this.converter = converter;
    }

    protected E getValueAdjustmentAddition() {
        return null;
    }

    @Override
    public Void process(final E value) {
        delegate.process(value.getDefaultValue());
        product = null;
        return null;
    }

    public E getProduct() {
        if (product == null) {
            product = converter.fromDefaultValue(delegate.getProduct());
        }
        return product;
    }

}
