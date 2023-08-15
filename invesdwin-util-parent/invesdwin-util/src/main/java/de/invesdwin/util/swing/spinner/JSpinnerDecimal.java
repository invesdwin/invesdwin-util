package de.invesdwin.util.swing.spinner;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class JSpinnerDecimal extends CustomJSpinner {

    public JSpinnerDecimal() {
        final SpinnerDecimalModel model = newModel();
        setModel(model);
        final SpinnerDecimalEditor editor = newEditor();
        setEditor(editor);
    }

    protected SpinnerDecimalModel newModel() {
        return SpinnerDecimalModel.newDecimalModel();
    }

    protected SpinnerDecimalEditor newEditor() {
        return SpinnerDecimalEditor.newDecimalEditor(this);
    }

}
