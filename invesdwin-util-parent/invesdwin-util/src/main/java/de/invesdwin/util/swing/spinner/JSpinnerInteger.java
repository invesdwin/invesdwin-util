package de.invesdwin.util.swing.spinner;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class JSpinnerInteger extends JSpinnerDecimal {

    @Override
    protected SpinnerDecimalModel newModel() {
        return SpinnerDecimalModel.newIntegerModel();
    }

    @Override
    protected SpinnerDecimalEditor newEditor() {
        return SpinnerDecimalEditor.newIntegerEditor(this);
    }

}
