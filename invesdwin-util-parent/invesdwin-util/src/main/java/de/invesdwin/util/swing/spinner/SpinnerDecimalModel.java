package de.invesdwin.util.swing.spinner;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.SpinnerNumberModel;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.decimal.Decimal;

@NotThreadSafe
public class SpinnerDecimalModel extends SpinnerNumberModel {

    protected Decimal stepSize = Decimal.ONE;
    protected Decimal stepRatio = Decimal.ONE;
    protected Decimal value;
    protected Decimal lastValidValue;
    protected Decimal minimum;
    protected Decimal maximum;
    protected ISpinnerDecimalModelValidator valueValidator;
    protected boolean stateChangeEventFiring = false;

    public SpinnerDecimalModel() {
        this(Decimal.ZERO);
    }

    public SpinnerDecimalModel(final Decimal value) {
        super(value, null, null, Decimal.ONE);

        this.value = value;
        this.lastValidValue = value;

        autoAdjustStepSize(value, value);
    }

    @Override
    public void setMinimum(final Comparable min) {
        setMinimum(Decimal.valueOf((Number) min));
    }

    public void setMinimum(final Decimal min) {
        if (!Objects.equals(min, minimum)) {
            minimum = min;
            if ((lastValidValue == null) || (min.compareTo(lastValidValue) > 0)) {
                final boolean wasValid = isCurrentValueValid();
                lastValidValue = min;
                if (wasValid != isCurrentValueValid()) {
                    fireStateChanged();
                }
            } else if ((lastValidValue == null) || (!lastValidValue.equals(value))) {
                lastValidValue = value;
                fireStateChanged();
            }
        }
    }

    @Override
    public Decimal getMinimum() {
        return minimum;
    }

    @Override
    public void setMaximum(final Comparable max) {
        setMaximum(Decimal.valueOf((Number) max));
    }

    public void setMaximum(final Decimal max) {
        if (!Objects.equals(max, maximum)) {
            maximum = max;
            if ((lastValidValue == null) || (max.compareTo(lastValidValue) < 0)) {
                final boolean wasValid = isCurrentValueValid();
                lastValidValue = max;
                if (wasValid != isCurrentValueValid()) {
                    fireStateChanged();
                }
            } else if (!lastValidValue.equals(value)) {
                lastValidValue = value;
                fireStateChanged();
            }
        }
    }

    @Override
    public Decimal getMaximum() {
        return maximum;
    }

    @Override
    public void setStepSize(final Number stepSize) {
        if (stepSize == null) {
            throw new IllegalArgumentException("Cannot be null");
        }

        this.stepSize = Decimal.valueOf(stepSize);
    }

    @Override
    public Decimal getStepSize() {
        return stepSize;
    }

    public void setStepRatio(final Number stepRatio) {
        this.stepRatio = Decimal.valueOf(stepRatio);
        autoAdjustStepSize(value, value);
    }

    public Decimal getStepRatio() {
        return stepRatio;
    }

    @Override
    public Decimal getNextValue() {
        return incrementValue();
    }

    @Override
    public Decimal getPreviousValue() {
        return decrementValue();
    }

    @Override
    public Decimal getNumber() {
        return value;
    }

    @Override
    public Decimal getValue() {
        return value;
    }

    @Override
    public void setValue(final Object value) {
        setValue(Decimal.valueOf((Number) value));
    }

    public void setValue(final Decimal newValue) {
        if (stateChangeEventFiring) {
            return;
        }
        if (this.value == null || newValue == null || (!newValue.equals(this.value))) {
            boolean valid = true;
            if (((maximum != null) && (maximum.compareTo(newValue) < 0))
                    || ((minimum != null) && (minimum.compareTo(newValue) > 0))) {
                valid = false;
            }

            if ((valueValidator != null) && (!valueValidator.isValidValue(newValue))) {
                valid = false;
            }

            if (valid) {
                autoAdjustStepSize(this.lastValidValue, newValue);
            }

            this.value = newValue;
            if (valid) {
                lastValidValue = this.value;
            }

            stateChangeEventFiring = true;
            try {
                fireStateChanged();
            } finally {
                stateChangeEventFiring = false;
            }
        }
    }

    public Decimal getLastValidValue() {
        return lastValidValue;
    }

    protected Decimal incrementValue() {
        if (lastValidValue == null) {
            return null;
        }
        final Decimal result = lastValidValue.add(stepSize).round();

        if ((maximum != null) && (maximum.compareTo(result) < 0)) {
            return maximum;
        }
        if ((minimum != null) && (minimum.compareTo(result) > 0)) {
            return minimum;
        }
        return result;
    }

    protected Decimal decrementValue() {
        if (lastValidValue == null) {
            return null;
        }
        final Decimal result = lastValidValue.subtract(stepSize).round();

        if ((maximum != null) && (maximum.compareTo(result) < 0)) {
            return maximum;
        }
        if ((minimum != null) && (minimum.compareTo(result) > 0)) {
            return minimum;
        }
        return result;
    }

    public boolean isCurrentValueValid() {
        return (value != null) && (lastValidValue != null) && (value.equals(lastValidValue));
    }

    protected void autoAdjustStepSize(final Decimal prevValue, final Decimal newValue) {
        //only adjust when this was not an increment/decrement step
        if (stepRatio != null && newValue.compareTo(prevValue.subtract(stepSize)) != 0
                && newValue.compareTo(prevValue.add(stepSize)) != 0) {
            Decimal stepSize = stepRatio.scaleByPowerOfTen(-Decimal.valueOf(newValue).getDecimalDigits()).round();
            if (stepSize.isZero()) {
                stepSize = stepRatio;
            }
            setStepSize(stepSize);
        }
    }

    public static SpinnerDecimalModel newModel(final boolean integral) {
        if (integral) {
            return newIntegerModel();
        } else {
            return newDecimalModel();
        }
    }

    public static SpinnerDecimalModel newDecimalModel() {
        return new SpinnerDecimalModel();
    }

    public static SpinnerDecimalModel newIntegerModel() {
        final SpinnerDecimalModel model = new SpinnerDecimalModel();
        model.setStepRatio(null);
        return model;
    }

}