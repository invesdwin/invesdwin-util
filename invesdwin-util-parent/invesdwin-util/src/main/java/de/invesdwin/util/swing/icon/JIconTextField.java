package de.invesdwin.util.swing.icon;

import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * https://gmigdos.wordpress.com/2010/03/30/java-a-custom-jtextfield-for-searching/
 */
public class JIconTextField extends JTextField {

    private Icon icon;
    private final Insets dummyInsets;

    public JIconTextField() {
        super();
        this.icon = null;

        final Border border = UIManager.getBorder("TextField.border");
        final JTextField dummy = new JTextField();
        this.dummyInsets = border.getBorderInsets(dummy);
    }

    public void setIcon(final Icon icon) {
        this.icon = icon;
    }

    public Icon getIcon() {
        return this.icon;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        int textX = 2;

        if (this.icon != null) {
            final int iconWidth = icon.getIconWidth();
            final int iconHeight = icon.getIconHeight();
            final int x = dummyInsets.left + 5;//this is our icon's x
            textX = x + iconWidth + 2; //this is the x where text should start
            final int y = (this.getHeight() - iconHeight) / 2;
            icon.paintIcon(this, g, x, y);
        }

        setMargin(new Insets(2, textX, 2, 2));

    }

}