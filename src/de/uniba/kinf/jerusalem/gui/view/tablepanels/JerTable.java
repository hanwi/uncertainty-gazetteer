package de.uniba.kinf.jerusalem.gui.view.tablepanels;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

import de.uniba.kinf.jerusalem.Main;
import de.uniba.kinf.jerusalem.gui.model.JerTableModel;

/**
 * {@link JTable} with regex filter which can be dynamically set.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerTable extends JTable {

        // disables cell outline
        static class BorderLessTableCellRenderer extends
                        DefaultTableCellRenderer {

                private static final long serialVersionUID = 1L;

                @Override
                public Component getTableCellRendererComponent(
                                final JTable table, final Object value,
                                final boolean isSelected,
                                final boolean hasFocus, final int row,
                                final int col) {

                        final boolean showFocusedCellBorder = false;
                        final Component c = super
                                        .getTableCellRendererComponent(table,
                                                        value, isSelected,
                                                        showFocusedCellBorder,
                                                        row, col);
                        return c;
                }
        }

        /**
     * 
     */
        private static final long serialVersionUID = -936419421830116204L;
        private final TableRowSorter<JerTableModel> rowSorter;

        private final JerTableModel tableModel;

        public JerTable(final JerTableModel model) {
                super(model);
                tableModel = model;
                setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                final ListSelectionModel selectionModel1 = getSelectionModel();

                setSelectionBackground(Main.DEFCOLORPROBLEM);
                setSelectionForeground(Color.BLACK);
                selectionModel1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                setDefaultRenderer(Object.class,
                                new BorderLessTableCellRenderer());

                selectionModel1.addListSelectionListener(new ListSelectionListener() {

                        @Override
                        public void valueChanged(final ListSelectionEvent lse) {
                                final int selectedRowView = getSelectedRow();
                                int selectedRowModel = selectedRowView;
                                if (selectedRowModel != -1) {
                                        selectedRowModel = convertRowIndexToModel(selectedRowView);
                                }
                                if (!lse.getValueIsAdjusting()) {
                                        tableModel.propagateSelectedRowModel(selectedRowModel);
                                }

                        }
                });
                rowSorter = new TableRowSorter<>(tableModel);
                setRowSorter(rowSorter);
        }

        @Override
        public void changeSelection(final int rowIndex, final int columnIndex,
                        final boolean toggle, final boolean extend) {
                super.changeSelection(rowIndex, columnIndex, true, false);
        }

        public void setNewRegexFilter(final String str, final int columnNumber) {
                final RowFilter<JerTableModel, Object> rf = RowFilter
                                .regexFilter(str, columnNumber);
                rowSorter.setRowFilter(rf);
        }

}
