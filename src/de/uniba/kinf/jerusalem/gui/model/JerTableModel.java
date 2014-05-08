package de.uniba.kinf.jerusalem.gui.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import de.uniba.kinf.jerusalem.gui.helper.JerMsgType;
import de.uniba.kinf.jerusalem.gui.helper.JerObserverMsg;
import de.uniba.kinf.jerusalem.gui.helper.JerResourceBundleAccessor;
import de.uniba.kinf.jerusalem.gui.model.helper.JerSequencerIF;
import de.uniba.kinf.jerusalem.gui.view.tablepanels.JerTable;
import de.uniba.kinf.jerusalem.gui.view.tablepanels.JerTableColumnProps;

/**
 * TableModel for {@link JerTable}. Retrieves data according to current
 * selection in {@link JerMainModel}.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerTableModel extends AbstractTableModel implements Observer,
                JerSequencerIF {

        /**
     * 
     */
        private static final long serialVersionUID = 4887160563344806096L;
        private List<Object> data;
        private Set<Integer> ids;
        private final JerModel jerModel;
        private JerTable jerTable;
        private List<JerTableColumnProps> tableColumnProps;
        @SuppressWarnings("rawtypes")
        private final Class[] types;

        public JerTableModel(final JerModel model) throws SQLException {
                ids = new HashSet<>();
                jerModel = model;
                jerModel.addSequencer(this);
                data = jerModel.getAllData();
                fetchColumnNames();
                types = jerModel.getTypes();
        }

        // returns -1 if no row selected
        @SuppressWarnings("boxing")
        private int destillateIDFromGivenRow(final int rowValue) {
                int idValue;
                if (rowValue != -1) {
                        idValue = (int) getValueAt(
                                        rowValue,
                                        findColumn(JerResourceBundleAccessor.get(jerModel
                                                        .getDataColumns()[0])));
                } else {
                        idValue = rowValue;
                }
                return idValue;
        }

        void fetchColumnNames() {
                tableColumnProps = new ArrayList<>();
                for (final String str : jerModel.getDataColumns()) {
                        tableColumnProps.add(new JerTableColumnProps(str, true,
                                        this));
                }
        }

        @Override
        public int findColumn(final String columnName) {
                return super.findColumn(columnName);
        }

        // returns -1 if id not found
        @SuppressWarnings("boxing")
        private int findRowInModelForGivenID(final int id) {
                int row = -1;
                for (int i = 0; i < data.size(); i++) {
                        final int a = (int) getValueAt(
                                        i,
                                        findColumn(JerResourceBundleAccessor.get(jerModel
                                                        .getDataColumns()[0])));
                        if (a == id) {
                                row = i;
                                break;
                        }
                }
                return row;
        }

        @Override
        public Class<?> getColumnClass(final int columnIndex) {
                return types[columnIndex];
        }

        @Override
        public int getColumnCount() {
                return tableColumnProps.size();
        }

        @Override
        public String getColumnName(final int arg0) {
                return JerResourceBundleAccessor.get(tableColumnProps.get(arg0)
                                .getIdent());
        }

        public JerTable getJerTable() {
                return this.jerTable;
        }

        @Override
        public int getRowCount() {
                return data.size();
        }

        public List<JerTableColumnProps> getTableColumnList() {
                return tableColumnProps;
        }

        public String getTableName() {
                return jerModel.getTableName();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
                final List<Object> list = (ArrayList<Object>) data
                                .get(rowIndex);
                final Object value = list.get(columnIndex);
                return value;
        }

        @Override
        public boolean isCellEditable(final int arg0, final int arg1) {
                return false;
        }

        /**
         * @return -1 if id not found
         * @param id
         *                to be used to get next value to
         * 
         **/
        @SuppressWarnings("boxing")
        @Override
        public int next(final int id) {
                final int aRow = findRowInModelForGivenID(id);
                if (aRow + 1 < data.size() && aRow != -1) {
                        return (int) getValueAt(
                                        aRow + 1,
                                        findColumn(JerResourceBundleAccessor.get(jerModel
                                                        .getDataColumns()[0])));
                }
                return -1;
        }

        /**
         * @return -1 if id not found
         * @param id
         *                to be used to get previous value to
         **/
        @SuppressWarnings("boxing")
        @Override
        public int prev(final int id) {
                final int aRow = findRowInModelForGivenID(id);
                if (aRow - 1 >= 0 && aRow != -1) {
                        return (int) getValueAt(
                                        aRow - 1,
                                        findColumn(JerResourceBundleAccessor.get(jerModel
                                                        .getDataColumns()[0])));
                }
                return -1;
        }

        public void propagateSelectedRowModel(final int rowValue) {
                final int idValue = destillateIDFromGivenRow(rowValue);
                jerModel.setModelAndID(idValue);
        }

        private void refreshData() {
                tableColumnProps.clear();
                data.clear();
                data = jerModel.getAllData();
                fetchColumnNames();
                fireTableDataChanged();
        }

        @Override
        public void setFocus() {
                final int value = findRowInModelForGivenID(1);
                if (value != -1) {
                        final int rowValueView = jerTable
                                        .convertRowIndexToView(value);
                        jerTable.clearSelection();
                        jerTable.changeSelection(rowValueView, 0, false, false);
                }
        }

        public void setTable(final JerTable jt) {
                jerTable = jt;
        }

        @Override
        public void update(final Observable arg0, final Object arg1) {
                final JerObserverMsg msg = (JerObserverMsg) arg1;
                if (msg.getType() == JerMsgType.DB_UPDATE) {
                        refreshData();
                        updateID();
                }
                if (msg.getType() == JerMsgType.SELECTION) {
                        updateID();
                }
        }

        public void updateID() {
                ids = jerModel.getAssociatedIDs();
                final StringBuffer strBuf = new StringBuffer();

                switch (ids.size()) {
                case 0:
                        // -2 signals no corresponding data
                        strBuf.append("-2");
                        break;
                case 1:
                        strBuf.append('^');
                        for (final Integer i : ids) {
                                if (i != null) {
                                        strBuf.append(i.toString());
                                }
                        }
                        strBuf.append('$');
                        break;
                default:
                        for (final Integer i : ids) {
                                if (i != null) {
                                        strBuf.append("|" + i.toString());
                                }
                        }
                        strBuf.deleteCharAt(0);
                        break;
                }
                strBuf.trimToSize();
                if (jerModel.getSelectedModelFromMainModel() != null
                                && jerModel.getSelectedModelFromMainModel()
                                                .equals(jerModel)
                                && jerModel.getSelectedIDFromMainModel() != -1) {
                        final int rowValueModel = findRowInModelForGivenID(jerModel
                                        .getSelectedIDFromMainModel());
                        int rowValueView = -1;
                        if (rowValueModel != -1) {
                                rowValueView = jerTable
                                                .convertRowIndexToView(rowValueModel);
                        }
                        final int selectedRow = jerTable.getSelectedRow();
                        if (selectedRow != rowValueView) {
                                jerTable.clearSelection();
                                jerTable.changeSelection(rowValueView, 0,
                                                false, false);
                        }
                } else {
                        jerTable.clearSelection();
                        // columnNumber indicates column on which to apply the
                        // regexFilter
                        jerTable.setNewRegexFilter(strBuf.toString(), 0);
                }
        }
}
