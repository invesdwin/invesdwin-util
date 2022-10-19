package de.invesdwin.util.swing.table;

import java.awt.Component;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.lang.string.description.HtmlToPlainText;
import de.invesdwin.util.swing.Components;
import de.invesdwin.util.swing.HiDPI;

@NotThreadSafe
public class ResizingDelegateTableCellRenderer implements TableCellRenderer {

    public static final JLabel EMPTY_LABEL_COMPONENT = new DefaultTableCellRenderer();

    protected final TableCellRenderer delegate;

    public ResizingDelegateTableCellRenderer(final TableCellRenderer delegate) {
        this.delegate = delegate;
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
            final boolean hasFocus, final int row, final int column) {
        final Component component = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                column);
        if (component instanceof JProgressBar) {
            return component;
        }
        resizeColumn(table, isSelected, row, column, component);
        return component;
    }

    protected void resizeColumn(final JTable table, final boolean isSelected, final int row, final int column,
            final Component component) {
        if (component instanceof JLabel) {
            final JLabel lbl = (JLabel) component;
            resizeLabelColumn(table, column, lbl);
        } else if (component instanceof JTextField) {
            final JTextField txt = (JTextField) component;
            resizeTextFieldColumn(table, column, txt);
        } else {
            resizeOtherColumn(table, column, component);
        }
    }

    protected void resizeLabelColumn(final JTable table, final int column, final JLabel lbl) {
        lbl.setHorizontalAlignment(JLabel.RIGHT);
        final String text = lbl.getText();
        resizeTextColumn(table, column, lbl, text);
    }

    protected void resizeTextFieldColumn(final JTable table, final int column, final JTextField txt) {
        txt.setHorizontalAlignment(JLabel.RIGHT);
        final String text = txt.getText();
        resizeTextColumn(table, column, txt, text);
    }

    protected void resizeTextColumn(final JTable table, final int column, final JComponent component,
            final String text) {
        if (Strings.isBlank(text)) {
            return;
        }
        final AffineTransform affinetransform = new AffineTransform();
        final FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
        final String plainText = HtmlToPlainText.htmlToPlainText(text);
        final String longestLine = Strings.extractLongestLine(plainText);
        final int textWidth = (int) (component.getFont().getStringBounds(longestLine, frc).getWidth())
                + HiDPI.scale(10);
        final TableColumn c = table.getColumnModel().getColumn(column);
        Components.setMinWidth(c, Math.max(c.getMinWidth(), textWidth));
        Components.setPreferredWidth(c, Math.max(c.getPreferredWidth(), textWidth));
    }

    protected void resizeOtherColumn(final JTable table, final int column, final Component component) {
        final TableColumn c = table.getColumnModel().getColumn(column);
        int minWidth = component.getMinimumSize().width;
        if (component instanceof JCheckBox) {
            minWidth *= 2;
        }
        Components.setMinWidth(c, minWidth);
        Components.setPreferredWidth(c, minWidth);
        Components.setMaxWidth(c, minWidth);
    }
}
