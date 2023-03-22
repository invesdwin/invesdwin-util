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

    //Used for resizing TableColumns
    private Integer[] columnWidths;
    private Boolean[] setMaxWidth;

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
        maybeResetResizeFields(table, row, column, component);

        if (component instanceof JLabel) {
            final JLabel lbl = (JLabel) component;
            resizeLabelColumn(table, row, column, lbl);
        } else if (component instanceof JTextField) {
            final JTextField txt = (JTextField) component;
            resizeTextFieldColumn(table, row, column, txt);
        } else {
            resizeOtherColumn(table, row, column, component);
        }

        maybeApplyColumnWidths(table, row, column);
    }

    protected void resizeLabelColumn(final JTable table, final int row, final int column, final JLabel lbl) {
        lbl.setHorizontalAlignment(JLabel.RIGHT);
        final String text = lbl.getText();
        resizeTextColumn(table, row, column, lbl, text);
    }

    protected void resizeTextFieldColumn(final JTable table, final int row, final int column, final JTextField txt) {
        txt.setHorizontalAlignment(JLabel.RIGHT);
        final String text = txt.getText();
        resizeTextColumn(table, row, column, txt, text);
    }

    protected void resizeTextColumn(final JTable table, final int row, final int column, final JComponent component,
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

        if (this.columnWidths != null && (this.columnWidths[column] == null || this.columnWidths[column] < textWidth)) {
            this.columnWidths[column] = textWidth;
        }
        this.setMaxWidth[column] = false;
    }

    protected void resizeOtherColumn(final JTable table, final int row, final int column, final Component component) {
        int minWidth = component.getMinimumSize().width;
        if (component instanceof JCheckBox) {
            minWidth *= 2;
        }
        if (this.columnWidths[column] == null || this.columnWidths[column] < minWidth) {
            this.columnWidths[column] = minWidth;
        }
        this.setMaxWidth[column] = true;
    }

    /**
     * Applies the saved ColumnWidths after the last column of the last row has been processed. Processing always
     * happens in order from first row/column to last row/column.
     */
    private void maybeApplyColumnWidths(final JTable table, final int row, final int column) {
        //The NullCheck on columnWidth here is because when you mouse-hover over a row in the PositionManager
        //or you click on one we restart the whole "go over each cell routine" from that row on (not from row 0)
        //--> so a reset could have happened before we got to the last column of the last row.
        if (row == table.getRowCount() - 1 && column == table.getColumnCount() - 1 && this.columnWidths != null) {
            for (int i = 0; i < table.getColumnCount(); i++) {
                final TableColumn tableColumn = table.getColumnModel().getColumn(i);
                Components.setMinWidth(tableColumn, this.columnWidths[i]);
                Components.setPreferredWidth(tableColumn, this.columnWidths[i]);
                if (Boolean.TRUE.equals(this.setMaxWidth[i])) {
                    Components.setMaxWidth(tableColumn, this.columnWidths[i]);
                }
            }
            this.columnWidths = null;
        }
    }

    /**
     * Resets the fields used for TableColumn-Resizing when we start processing at the first column of the first row.
     */
    private void maybeResetResizeFields(final JTable table, final int row, final int column,
            final Component component) {
        if (this.setMaxWidth == null) {
            this.setMaxWidth = new Boolean[table.getColumnCount()];
        }
        if (row == 0 && column == 0) {
            this.columnWidths = new Integer[table.getColumnCount()];
            //Prefill with header minWidth
            for (int i = 0; i < columnWidths.length; i++) {
                final AffineTransform affinetransform = new AffineTransform();
                final FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
                final String plainText = HtmlToPlainText.htmlToPlainText(table.getColumnName(i));
                final String longestLine = Strings.extractLongestLine(plainText);
                final int textWidth = (int) (component.getFont().getStringBounds(longestLine, frc).getWidth())
                        + HiDPI.scale(10);
                columnWidths[i] = textWidth;
            }
        }
    }
}
