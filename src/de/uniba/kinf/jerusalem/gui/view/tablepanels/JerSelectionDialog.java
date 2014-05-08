package de.uniba.kinf.jerusalem.gui.view.tablepanels;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Observable;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import de.uniba.kinf.jerusalem.gui.helper.JerMsgType;
import de.uniba.kinf.jerusalem.gui.helper.JerObserverMsg;
import de.uniba.kinf.jerusalem.gui.helper.JerResourceBundleAccessor;
import de.uniba.kinf.jerusalem.gui.view.helper.JerPropChangeListener;

/**
 * Provides frame and actions to select columns in {@link JerTable}.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerSelectionDialog extends Observable implements ItemListener {
        private final JPanel contentPane;
        private final JDialog dialog;
        private final JerTableContainer parent;
        private final List<JerTableColumnProps> tableColumnPropsList;

        public JerSelectionDialog(final List<JerTableColumnProps> colList,
                        final JerTableContainer tableContainer,
                        final Properties properties) {
                dialog = new JDialog();
                parent = tableContainer;
                tableColumnPropsList = colList;
                addObserver(tableContainer);
                dialog.setModal(true);
                dialog.setTitle(JerResourceBundleAccessor
                                .get(tableColumnPropsList.get(0).getModel()
                                                .getTableName()));
                dialog.setResizable(true);
                dialog.setLocationRelativeTo(parent.getJPanel());
                contentPane = new JPanel();
                contentPane.setLayout(new GridLayout(tableColumnPropsList
                                .size() + 1, 1));
                dialog.setContentPane(contentPane);
                enterColumnStatus(properties);
                dialog.pack();
                dialog.setVisible(true);
        }

        private void enterColumnStatus(final Properties properties) {
                for (final JerTableColumnProps tableColumnProps : tableColumnPropsList) {
                        final JerCheckBox jerCheckBox = new JerCheckBox(
                                        JerResourceBundleAccessor.get(tableColumnProps
                                                        .getIdent()),
                                        tableColumnProps.isVisible());
                        jerCheckBox.addItemListener(this);
                        int selected = 1;
                        final String propname = parent.getJTable().getModel()
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
                        jerCheckBox.addPropertyChangeListener(new JerPropChangeListener(
                                        properties, propname));
                        contentPane.add(jerCheckBox);
                }
        }

        @Override
        public void itemStateChanged(final ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                        makeAction(e, 0, 1, true);
                }
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                        makeAction(e, 1, 0, false);
                }
        }

        private void makeAction(final ItemEvent e, final int val0,
                        final int val1, final boolean setVis) {
                final JCheckBox checkBox = (JCheckBox) e.getItemSelectable();
                for (final JerTableColumnProps columnTable : tableColumnPropsList) {
                        if (checkBox.getText()
                                        .equals(JerResourceBundleAccessor.get(columnTable
                                                        .getIdent()))) {
                                columnTable.setVisible(setVis);
                                checkBox.firePropertyChange(parent.getJTable()
                                                .getModel().getClass()
                                                .getSimpleName()
                                                + "_" + columnTable.getIdent(),
                                                val0, val1);
                                setChanged();
                                notifyObservers(new JerObserverMsg(
                                                JerMsgType.TABLE_COLUMNS));
                        }
                }
        }

}
