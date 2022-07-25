package de.invesdwin.util.swing.button;

import java.awt.FlowLayout;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

@NotThreadSafe
public class JCheckBoxButton extends JButton {

    public static final String CHECKBOX_NAME_SUFFIX = "Enabled";
    private final JCheckBox checkbox;
    private final JLabel label;

    public JCheckBoxButton(final String text) {
        final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        panel.setOpaque(false);
        this.checkbox = new JCheckBox();
        checkbox.setBorder(BorderFactory.createEmptyBorder());
        panel.add(checkbox);
        panel.add(Box.createHorizontalStrut(5));
        this.label = new JLabel(text);
        panel.add(label);
        add(panel);
    }

    @Override
    public void setName(final String name) {
        super.setName(name);
        checkbox.setName(name + CHECKBOX_NAME_SUFFIX);
    }

    @Override
    public void setText(final String text) {
        if (label != null) {
            label.setText(text);
        }
    }

    /**
     * Use getTextLabel().getText() instead
     */
    @Deprecated
    @Override
    public String getText() {
        return null;
    }

    public JLabel getTextLabel() {
        return label;
    }

    public JCheckBox getCheckbox() {
        return checkbox;
    }

}
