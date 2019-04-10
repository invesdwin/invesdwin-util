package de.invesdwin.util.swing.spinner;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JSpinner;

@NotThreadSafe
public class JSpinnerDecimal extends JSpinner {

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
