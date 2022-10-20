package de.invesdwin.util.swing.table;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.table.TableModel;
import javax.swing.table.TableStringConverter;

// CHECKSTYLE:OFF
@NotThreadSafe
public class ComparableTableRowSorter<M extends TableModel> extends ComparableDefaultRowSorter<M, Integer> {
    private static final Comparator<?> COMPARABLE_COMPARATOR = new ComparableComparator();

    private M tableModel;

    private TableStringConverter stringConverter;

    public ComparableTableRowSorter() {
        this(null);
    }

    public ComparableTableRowSorter(final M model) {
        setModel(model);
    }

    public void setModel(final M model) {
        tableModel = model;
        setModelWrapper(new TableRowSorterModelWrapper());
    }

    public void setStringConverter(final TableStringConverter stringConverter) {
        this.stringConverter = stringConverter;
    }

    public TableStringConverter getStringConverter() {
        return stringConverter;
    }

    @Override
    public Comparator<?> getComparator(final int column) {
        final Comparator<?> comparator = super.getComparator(column);
        if (comparator != null) {
            return comparator;
        }
        final Class<?> columnClass = getModel().getColumnClass(column);
        if (columnClass == String.class) {
            return Collator.getInstance();
        }
        if (Comparable.class.isAssignableFrom(columnClass)) {
            return COMPARABLE_COMPARATOR;
        }
        return Collator.getInstance();
    }

    @Override
    protected boolean useToString(final int column) {
        final Comparator<?> comparator = super.getComparator(column);
        if (comparator != null) {
            return false;
        }
        final Class<?> columnClass = getModel().getColumnClass(column);
        if (columnClass == String.class) {
            return false;
        }
        if (Comparable.class.isAssignableFrom(columnClass)) {
            return false;
        }
        return true;
    }

    private class TableRowSorterModelWrapper extends ModelWrapper<M, Integer> {
        @Override
        public M getModel() {
            return tableModel;
        }

        @Override
        public int getColumnCount() {
            return (tableModel == null) ? 0 : tableModel.getColumnCount();
        }

        @Override
        public int getRowCount() {
            return (tableModel == null) ? 0 : tableModel.getRowCount();
        }

        @Override
        public Object getValueAt(final int row, final int column) {
            return tableModel.getValueAt(row, column);
        }

        @Override
        public String getStringValueAt(final int row, final int column) {
            final TableStringConverter converter = getStringConverter();
            if (converter != null) {
                // Use the converter
                final String value = converter.toString(tableModel, row, column);
                if (value != null) {
                    return value;
                }
                return "";
            }

            // No converter, use getValueAt followed by toString
            final Object o = getValueAt(row, column);
            if (o == null) {
                return "";
            }
            final String string = o.toString();
            if (string == null) {
                return "";
            }
            return string;
        }

        @Override
        public Integer getIdentifier(final int index) {
            return index;
        }
    }

    private static class ComparableComparator implements Comparator<Object>, Serializable {
        @Override
        @SuppressWarnings({ "unchecked", "rawtypes" })
        public int compare(final Object o1, final Object o2) {
            try {
                return ((Comparable) o1).compareTo(o2);
            } catch (final Throwable t) {
                //e.g. when comparing PCT money to real currency
                return -1;
            }
        }
    }
}
