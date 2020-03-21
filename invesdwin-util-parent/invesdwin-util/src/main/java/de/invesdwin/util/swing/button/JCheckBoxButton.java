package de.invesdwin.util.swing.button;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

@NotThreadSafe
public class JCheckBoxButton extends JPanel {

    private ActionListener action;
    private final JCheckBox checkbox;
    private final JLabel label;

    public JCheckBoxButton(final String text) {
        super(new FlowLayout(FlowLayout.CENTER, 0, 0));
        add(Box.createHorizontalStrut(5));
        this.checkbox = new JCheckBox();
        add(checkbox);
        this.label = new JLabel(text);
        add(label);
        add(Box.createHorizontalStrut(5));
        //        setBorder(BorderFactory.createRaisedBevelBorder());
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(final MouseEvent me) {
                setBorder(BorderFactory.createEtchedBorder());
            }

            @Override
            public void mouseReleased(final MouseEvent me) {
                setBorder(BorderFactory.createRaisedBevelBorder());
            }

            @Override
            public void mouseClicked(final MouseEvent me) {
                if (action != null) {
                    action.actionPerformed(new ActionEvent(JCheckBoxButton.this, me.getID(), "action", me.getWhen(),
                            me.getModifiersEx()));
                }
            }
        });

        final String pp = "Button" + ".";
        LookAndFeel.installColorsAndFont(this, pp + "background", pp + "foreground", pp + "font");
        LookAndFeel.installBorder(this, pp + "border");

        final Object rollover = UIManager.get(pp + "rollover");
        if (rollover != null) {
            LookAndFeel.installProperty(this, "rolloverEnabled", rollover);
        }
    }

    public JCheckBox getCheckbox() {
        return checkbox;
    }

    public JLabel getLabel() {
        return label;
    }

    public void setAction(final ActionListener action) {
        this.action = action;
    }

    public ActionListener getAction() {
        return action;
    }
}
