package de.invesdwin.util.swing.button;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ButtonUI;

import de.invesdwin.util.swing.listener.MouseListenerSupport;
import de.invesdwin.util.swing.listener.MouseMotionListenerSupport;

/**
 * Adapted from: https://github.com/akuhtz/jsplitbutton/blob/master/src/org/gpl/JSplitButton/JSplitButton.java
 */
@NotThreadSafe
public class JSplitButton extends JButton {

    private int separatorSpacing = 4;
    private int splitWidth = 22;
    private int arrowSize = 8;
    private boolean onSplit;
    private Rectangle splitRectangle;
    private JPopupMenu popupMenu;
    private boolean alwaysDropDown;
    private Image arrowImage;
    private Image disabledArrowImage;
    private final List<ActionListener> actionListeners = new ArrayList<>();

    /**
     * Creates a button with no set text or icon.
     */
    public JSplitButton() {
        this(null, null);
        setUI(new ButtonUI() {
        });
    }

    /**
     * Creates a button with text.
     *
     * @param text
     *            the text of the button
     */
    public JSplitButton(final String text) {
        this(text, null);
    }

    /**
     * Creates a button with an icon.
     *
     * @param icon
     *            the Icon image to display on the button
     */
    public JSplitButton(final Icon icon) {
        this(null, icon);
    }

    /**
     * Creates a button with initial text and an icon.
     *
     * @param text
     *            the text of the button
     * @param icon
     *            the Icon image to display on the button
     */
    public JSplitButton(final String text, final Icon icon) {
        super(text, icon);
        setBorder(BorderFactory.createCompoundBorder(getBorder(), new EmptyBorder(0, 4, 0, splitWidth - 2)));
        addMouseMotionListener(new MouseMotionListenerSupport() {
            @Override
            public void mouseMoved(final MouseEvent e) {
                if (splitRectangle.contains(e.getPoint())) {
                    onSplit = true;
                } else {
                    onSplit = false;
                }
                repaint(splitRectangle);
            }
        });
        addMouseListener(new MouseListenerSupport() {
            @Override
            public void mouseExited(final MouseEvent e) {
                onSplit = false;
                repaint(splitRectangle);
            }
        });
        super.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (popupMenu == null) {
                    fireButtonClicked(e);
                } else if (alwaysDropDown) {
                    popupMenu.show(JSplitButton.this, getWidth() - (int) popupMenu.getPreferredSize().getWidth(),
                            getHeight());
                    fireButtonClicked(e);
                } else if (onSplit) {
                    popupMenu.show(JSplitButton.this, getWidth() - (int) popupMenu.getPreferredSize().getWidth(),
                            getHeight());
                } else {
                    fireButtonClicked(e);
                }
            }

        });
    }

    @Override
    public void addActionListener(final ActionListener l) {
        actionListeners.add(l);
    }

    @Override
    public void removeActionListener(final ActionListener l) {
        actionListeners.remove(l);
    }

    @Override
    public ActionListener[] getActionListeners() {
        return actionListeners.toArray(new ActionListener[actionListeners.size()]);
    }

    /**
     * Returns the JPopupMenu if set, null otherwise.
     * 
     * @return JPopupMenu
     */
    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    /**
     * Sets the JPopupMenu to be displayed, when the split part of the button is clicked.
     * 
     * @param popupMenu
     */
    public void setPopupMenu(final JPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
        arrowImage = null; //to repaint the arrow image
    }

    /**
     * Returns the separatorSpacing. Separator spacing is the space above and below the separator( the line drawn when
     * you hover your mouse over the split part of the button).
     * 
     * @return separatorSpacingimage = null; //to repaint the image with the new size
     */
    public int getSeparatorSpacing() {
        return separatorSpacing;
    }

    /**
     * Sets the separatorSpacing.Separator spacing is the space above and below the separator( the line drawn when you
     * hover your mouse over the split part of the button).
     * 
     * @param separatorSpacing
     */
    public void setSeparatorSpacing(final int separatorSpacing) {
        this.separatorSpacing = separatorSpacing;
    }

    /**
     * Show the dropdown menu, if attached, even if the button part is clicked.
     * 
     * @return true if alwaysDropdown, false otherwise.
     */
    public boolean isAlwaysDropDown() {
        return alwaysDropDown;
    }

    /**
     * Show the dropdown menu, if attached, even if the button part is clicked.
     * 
     * @param alwaysDropDown
     *            true to show the attached dropdown even if the button part is clicked, false otherwise
     */
    public void setAlwaysDropDown(final boolean alwaysDropDown) {
        this.alwaysDropDown = alwaysDropDown;
    }

    /**
     * Splitwidth is the width of the split part of the button.
     * 
     * @return splitWidth
     */
    public int getSplitWidth() {
        return splitWidth;
    }

    /**
     * Splitwidth is the width of the split part of the button.
     * 
     * @param splitWidth
     */
    public void setSplitWidth(final int splitWidth) {
        this.splitWidth = splitWidth;
    }

    /**
     * gets the size of the arrow.
     * 
     * @return size of the arrow
     */
    public int getArrowSize() {
        return arrowSize;
    }

    /**
     * sets the size of the arrow
     * 
     * @param arrowSize
     */
    public void setArrowSize(final int arrowSize) {
        this.arrowSize = arrowSize;
        arrowImage = null; //to repaint the image with the new size
        disabledArrowImage = null;
    }

    /**
     * Gets the image to be drawn in the split part. If no is set, a new image is created with the triangle.
     * 
     * @return image
     */
    public Image getArrowImage() {
        if (arrowImage != null) {
            return arrowImage;
        } else {
            final Color color = popupMenu != null ? UIManager.getColor("Button.foreground")
                    : UIManager.getColor("ComboBox.disabledForeground");
            arrowImage = newArrowImage(color);
            return arrowImage;
        }
    }

    protected Image newArrowImage(final Color color) {
        Graphics2D g = null;
        BufferedImage img = new BufferedImage(arrowSize, arrowSize, BufferedImage.TYPE_INT_RGB);
        g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.setColor(color);
        //this creates a triangle facing right >
        g.fillPolygon(new int[] { 0, 0, arrowSize / 2 }, new int[] { 0, arrowSize, arrowSize / 2 }, 3);
        g.dispose();
        //rotate it to face downwards
        img = rotate(img, 90);
        final BufferedImage dimg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        g = dimg.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(img, null, 0, 0);
        g.dispose();
        for (int i = 0; i < dimg.getHeight(); i++) {
            for (int j = 0; j < dimg.getWidth(); j++) {
                if (dimg.getRGB(j, i) == Color.WHITE.getRGB()) {
                    dimg.setRGB(j, i, 0x8F1C1C);
                }
            }
        }
        return Toolkit.getDefaultToolkit().createImage(dimg.getSource());
    }

    /**
     * Sets the image to draw instead of the triangle.
     * 
     * @param image
     */
    public void setArrowImage(final Image image) {
        this.arrowImage = image;
    }

    /**
     * Gets the disabled image to be drawn in the split part. If no is set, a new image is created with the triangle.
     * 
     * @return image
     */
    public Image getDisabledArrowImage() {
        if (disabledArrowImage != null) {
            return disabledArrowImage;
        } else {
            final Color color = UIManager.getColor("ComboBox.disabledForeground");
            disabledArrowImage = newArrowImage(color);
            return disabledArrowImage;
        }
    }

    public void setDisabledArrowImage(final Image image) {
        this.disabledArrowImage = image;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Color oldColor = g.getColor();
        splitRectangle = new Rectangle(getWidth() - splitWidth, 0, splitWidth, getHeight());
        g.translate(splitRectangle.x, splitRectangle.y);
        final int mh = getHeight() / 2;
        final int mw = splitWidth / 2;
        g.drawImage((isEnabled() ? getArrowImage() : getDisabledArrowImage()), mw - arrowSize / 2,
                mh + 2 - arrowSize / 2, null);
        //        if (onSplit && !alwaysDropDown && popupMenu != null) {
        g.setColor(UIManager.getColor("Separator.background"));
        g.drawLine(1, separatorSpacing + 2, 1, getHeight() - separatorSpacing - 2);
        g.setColor(UIManager.getColor("Separator.foreground"));
        g.drawLine(2, separatorSpacing + 2, 2, getHeight() - separatorSpacing - 2);
        //        }
        g.setColor(oldColor);
        g.translate(-splitRectangle.x, -splitRectangle.y);
    }

    private BufferedImage rotate(final BufferedImage img, final int angle) {
        final int w = img.getWidth();
        final int h = img.getHeight();
        final BufferedImage dimg = new BufferedImage(w, h, img.getType());
        final Graphics2D g = dimg.createGraphics();
        g.rotate(Math.toRadians(angle), w / 2, h / 2);
        g.drawImage(img, null, 0, 0);
        return dimg;
    }

    private void fireButtonClicked(final ActionEvent event) {
        for (final ActionListener l : getActionListeners()) {
            l.actionPerformed(event);
        }
    }

}