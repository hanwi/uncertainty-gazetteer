package de.uniba.kinf.jerusalem.gui.view.tablepanels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import de.uniba.kinf.jerusalem.gui.helper.JerMsgType;
import de.uniba.kinf.jerusalem.gui.helper.JerObserverMsg;
import de.uniba.kinf.jerusalem.gui.helper.JerResourceBundleAccessor;
import de.uniba.kinf.jerusalem.gui.model.JerTableModel;

/**
 * This class provides layout for {@link JerTable}. It triggers update in
 * JerTable in case table column visibility changes.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerTableContainer extends Observable implements Observer {

        private static final Dimension MINIMUMSIZE = new Dimension(10, 10);
        private static final int MINWIDTH = 0;
        private static final int PREFWIDTH = 5;
        private final JPanel jPanel;
        private final JerTable table;
        private final JerTableModel tableModel;

        public JerTableContainer(final JerTableModel jerTableModel,
                        final Properties properties) {
                tableModel = jerTableModel;
                final JerTableContainer self = this;
                jPanel = new JPanel();
                table = new JerTable(tableModel);
                tableModel.setTable(table);

                final JScrollPane tableScrollPane = new JScrollPane(table);
                final JMenuBar menuBar = new JMenuBar();
                final JButton showDialogBtn = new JButton(
                                JerResourceBundleAccessor.get("displayadjust"));

                jPanel.setLayout(new BorderLayout());
                jPanel.setMinimumSize(MINIMUMSIZE);

                menuBar.add(Box.createHorizontalStrut(5));
                menuBar.add(new JLabel(JerResourceBundleAccessor.get(tableModel
                                .getTableName())));
                menuBar.add(Box.createHorizontalStrut(5));
                menuBar.add(showDialogBtn);

                showDialogBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent event) {
                                new JerSelectionDialog(tableModel
                                                .getTableColumnList(), self,
                                                properties);
                        }
                });
                jPanel.add(menuBar, BorderLayout.NORTH);
                jPanel.add(tableScrollPane, BorderLayout.CENTER);

                adjustViewAccordingToColumnStatus(
                                jerTableModel.getTableColumnList(), properties);
                jPanel.setVisible(true);
        }

        private void adjustViewAccordingToColumnStatus(
                        final List<JerTableColumnProps> tableColumnPropsList,
                        final Properties properties) {
                for (final JerTableColumnProps tableColumnProps : tableColumnPropsList) {
                        int selected = 1;
                        final String propname = getJTable().getModel()
                                        .getClass().getSimpleName()
                                        + "_" + tableColumnProps.getIdent();
                        final String propsIsSelected = properties
                                        .getProperty(propname);
                        if (propsIsSelected != null) {
                                selected = Integer.parseInt(propsIsSelected);
                        }
                        if (selected == 1) {
                                tableColumnProps.setVisible(true);
                        } else {
                                tableColumnProps.setVisible(false);
                        }
                }
                updateTableView();
        }

        private TableColumn getColumn(final String ident) {
                final int value = tableModel.findColumn(ident);
                final int secondValue = table.convertColumnIndexToView(value);
                return table.getColumnModel().getColumn(secondValue);
        }

        public JPanel getJPanel() {
                return jPanel;
        }

        public JTable getJTable() {
                return table;
        }

        @Override
        public void update(final Observable arg0, final Object arg1) {
                final JerObserverMsg msg = (JerObserverMsg) arg1;
                if (msg.getType() == JerMsgType.TABLE_COLUMNS) {
                        updateTableView();
                }
        }

        private void updateTableView() {
                for (final JerTableColumnProps tableColumnProps : tableModel
                                .getTableColumnList()) {
                        final TableColumn column = getColumn(JerResourceBundleAccessor
                                        .get(tableColumnProps.getIdent()));
                        if (tableColumnProps.isVisible() && column != null) {
                                column.setMinWidth(PREFWIDTH);
                                column.setMaxWidth(Integer.MAX_VALUE);
                                column.setPreferredWidth(PREFWIDTH);
                        } else if (!tableColumnProps.isVisible()
                                        && column != null) {
                                column.setMinWidth(MINWIDTH);
                                column.setMaxWidth(MINWIDTH);
                        }
                }
                table.revalidate();
        }

}
