package de.invesdwin.util.swing.table;

import java.awt.Dimension;
import java.util.Vector;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import de.invesdwin.util.swing.HiDPI;

/**
 * https://bugs.openjdk.java.net/browse/JDK-4127936
 */
@NotThreadSafe
public class JTableWithHorizontalScroll extends JTable {

    public JTableWithHorizontalScroll() {
        this(null, null, null);
    }

    public JTableWithHorizontalScroll(final TableModel dm) {
        this(dm, null, null);
    }

    public JTableWithHorizontalScroll(final TableModel dm, final TableColumnModel cm) {
        this(dm, cm, null);
    }

    public JTableWithHorizontalScroll(final int numRows, final int numColumns) {
        this(new DefaultTableModel(numRows, numColumns));
    }

    public JTableWithHorizontalScroll(final Vector<? extends Vector> rowData, final Vector<?> columnNames) {
        this(new DefaultTableModel(rowData, columnNames));
    }

    public JTableWithHorizontalScroll(final Object[][] rowData, final Object[] columnNames) {
        this(new AbstractTableModel() {
            @Override
            public String getColumnName(final int column) {
                return columnNames[column].toString();
            }

            @Override
            public int getRowCount() {
                return rowData.length;
            }

            @Override
            public int getColumnCount() {
                return columnNames.length;
            }

            @Override
            public Object getValueAt(final int row, final int col) {
                return rowData[row][col];
            }

            @Override
            public boolean isCellEditable(final int row, final int column) {
                return true;
            }

            @Override
            public void setValueAt(final Object value, final int row, final int col) {
                rowData[row][col] = value;
                fireTableCellUpdated(row, col);
            }
        });
    }

    public JTableWithHorizontalScroll(final TableModel dm, final TableColumnModel cm, final ListSelectionModel sm) {
        super(dm, cm, sm);
        setRowHeight(HiDPI.scale(getRowHeight()));
    }

    // when the viewport shrinks below the preferred size, stop tracking the viewport width
    @Override
    public boolean getScrollableTracksViewportWidth() {
        if (autoResizeMode != AUTO_RESIZE_OFF) {
            if (getParent() instanceof JViewport) {
                return (((JViewport) getParent()).getWidth() > getPreferredSize().width);
            }
        }
        return false;
    }

    // when the viewport shrinks below the preferred size, return the minimum size
    // so that scrollbars will be shown
    @Override
    public Dimension getPreferredSize() {
        if (getParent() instanceof JViewport) {
            if (((JViewport) getParent()).getWidth() < super.getPreferredSize().width) {
                return getMinimumSize();
            }
        }

        return super.getPreferredSize();
    }

}
