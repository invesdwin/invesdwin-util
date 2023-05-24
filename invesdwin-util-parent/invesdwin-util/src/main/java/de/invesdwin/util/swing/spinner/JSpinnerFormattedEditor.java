package de.invesdwin.util.swing.spinner;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextField;
import javax.swing.text.DefaultFormatterFactory;

@NotThreadSafe
public class JSpinnerFormattedEditor extends DefaultEditor {

    public JSpinnerFormattedEditor(final JSpinner spinner, final AbstractFormatter formatter) {
        super(spinner);
        final DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);
        final JFormattedTextField ftf = getTextField();
        ftf.setEditable(true);
        ftf.setFormatterFactory(factory);
        ftf.setHorizontalAlignment(JTextField.RIGHT);
    }

}
