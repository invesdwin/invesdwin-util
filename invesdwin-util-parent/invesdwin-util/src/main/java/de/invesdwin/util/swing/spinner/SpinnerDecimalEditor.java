package de.invesdwin.util.swing.spinner;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import de.invesdwin.util.math.decimal.Decimal;

@NotThreadSafe
public class SpinnerDecimalEditor extends DefaultEditor {

    public static final String DECIMAL_FORMAT = Decimal.newDefaultDecimalFormat(Decimal.DEFAULT_ROUNDING_SCALE);
    public static final String INTEGER_FORMAT = Decimal.newDefaultDecimalFormat(0);

    private final NumberEditorFormatter formatter;

    public SpinnerDecimalEditor(final JSpinner spinner) {
        this(spinner, DECIMAL_FORMAT);
    }

    public SpinnerDecimalEditor(final JSpinner spinner, final String decimalFormatPattern) {
        this(spinner, new DecimalFormat(decimalFormatPattern, Decimal.DEFAULT_DECIMAL_FORMAT_SYMBOLS));
    }

    public SpinnerDecimalEditor(final JSpinner spinner, final DecimalFormat format) {
        super(spinner);
        if (!(spinner.getModel() instanceof SpinnerDecimalModel)) {
            throw new IllegalArgumentException("model not a " + SpinnerDecimalModel.class.getSimpleName());
        }

        final SpinnerDecimalModel model = (SpinnerDecimalModel) spinner.getModel();
        this.formatter = new NumberEditorFormatter(model, format);
        final DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);
        final JFormattedTextField ftf = getTextField();
        ftf.setEditable(true);
        ftf.setFormatterFactory(factory);
        ftf.setHorizontalAlignment(JTextField.RIGHT);

        /*
         * TBD - initializing the column width of the text field is imprecise and doing it here is tricky because the
         * developer may configure the formatter later.
         */
        try {
            final String maxString = formatter.valueToString(model.getMinimum());
            final String minString = formatter.valueToString(model.getMaximum());
            ftf.setColumns(Math.max(maxString.length(), minString.length()));
        } catch (final ParseException e) {
            // TBD should throw a chained error here
        }

    }

    public NumberEditorFormatter getFormatter() {
        return formatter;
    }

    public DecimalFormat getFormat() {
        return (DecimalFormat) ((NumberFormatter) (getTextField().getFormatter())).getFormat();
    }

    public SpinnerNumberModel getModel() {
        return (SpinnerNumberModel) (getSpinner().getModel());
    }

    public static class NumberEditorFormatter extends NumberFormatter {
        private final SpinnerNumberModel model;
        private boolean installing;

        NumberEditorFormatter(final SpinnerNumberModel model, final NumberFormat format) {
            super(format);
            this.model = model;
            setValueClass(model.getValue().getClass());
        }

        public boolean isInstalling() {
            return installing;
        }

        @Override
        public void install(final JFormattedTextField ftf) {
            installing = true;
            try {
                super.install(ftf);
            } finally {
                installing = false;
            }
        }

        @Override
        public void setMinimum(final Comparable min) {
            model.setMinimum(min);
        }

        @Override
        public Comparable getMinimum() {
            return model.getMinimum();
        }

        @Override
        public void setMaximum(final Comparable max) {
            model.setMaximum(max);
        }

        @Override
        public Comparable getMaximum() {
            return model.getMaximum();
        }
    }

    public static SpinnerDecimalEditor newDecimalEditor(final JSpinner spinner, final boolean integral) {
        if (integral) {
            return newIntegerEditor(spinner);
        } else {
            return newDecimalEditor(spinner);
        }
    }

    public static SpinnerDecimalEditor newDecimalEditor(final JSpinner spinner) {
        return new SpinnerDecimalEditor(spinner);
    }

    public static SpinnerDecimalEditor newIntegerEditor(final JSpinner spinner) {
        return new SpinnerDecimalEditor(spinner, INTEGER_FORMAT);
    }
}