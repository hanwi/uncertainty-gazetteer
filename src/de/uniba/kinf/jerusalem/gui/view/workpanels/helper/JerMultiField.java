package de.uniba.kinf.jerusalem.gui.view.workpanels.helper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import de.uniba.kinf.jerusalem.gui.helper.JerResourceBundleAccessor;
import de.uniba.kinf.jerusalem.gui.view.workpanels.JerWorkPanel;

/**
 * Container class for multiple {@link JerMultiItem}.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerMultiField extends JComponent implements JerMultiFieldInfoIF {

        /**
     * 
     */
        private static final long serialVersionUID = 3622140995114346680L;
        private final JButton btnAdd;
        private final List<JerMultiItem> multiItemList;
        private final JPanel multiItemsPanel;
        private final String name;
        private final JerWorkPanel parentWP;

        public JerMultiField(final JerWorkPanel jwp, final String str) {
                final JerMultiField self = this;
                parentWP = jwp;
                name = str;
                multiItemList = new ArrayList<JerMultiItem>();

                multiItemsPanel = new JPanel();
                multiItemsPanel.setBorder(BorderFactory
                                .createLineBorder(Color.BLACK));
                multiItemsPanel.setLayout(new BoxLayout(multiItemsPanel,
                                BoxLayout.Y_AXIS));

                btnAdd = new JButton(JerResourceBundleAccessor.get("add_"
                                + name));
                btnAdd.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(final ActionEvent arg0) {
                                addJMI(new JerMultiItem(self));
                        }

                });
                multiItemsPanel.add(btnAdd);
                setLayout(new GridLayout(1, 1));
                add(multiItemsPanel);

        }

        private void addJMI(JerMultiItem item) {
                List<Object> li = parentWP.getDefAllForMulti();
                if (!li.isEmpty()) {
                        item.setCBoxValues(li);
                        multiItemList.add(item);
                        multiItemsPanel.add(item);
                        resize();
                }
        }

        @Override
        public void clear() {
                for (final JerMultiItem jmi : multiItemList) {
                        multiItemsPanel.remove(jmi);
                }
                multiItemList.clear();
                resize();
        }

        @SuppressWarnings("unchecked")
        @Override
        public void displayInfo(final HashMap<String, Object> values) {
                final List<Object> multiValLi = parentWP.getMultiVals();
                clear();
                for (int i = 0; i < multiValLi.size(); i++) {
                        final List<Object> rowLi = (List<Object>) multiValLi
                                        .get(i);
                        final int val = (Integer) rowLi.get(0);
                        final JerMultiItem j = new JerMultiItem(this);
                        addJMI(j);
                        final JComboBox<JerItem> cb = j.getCbbox();
                        for (int k = 0; k < cb.getItemCount(); k++) {
                                if (cb.getItemAt(k) != null) {
                                        final JerItem jci = cb.getItemAt(k);
                                        if (jci.getId() == val) {
                                                cb.setSelectedIndex(k);
                                                break;
                                        }
                                }
                        }
                        final String beginTxt = rowLi.get(2) + "";
                        if (!beginTxt.equals("null")) {
                                j.getBeginF().setText(beginTxt);
                        }
                        final String endTxt = rowLi.get(3) + "";
                        if (!endTxt.equals("null")) {
                                j.getEndF().setText(endTxt);
                        }
                }
        }

        @Override
        public void enableFunctionality(final boolean b) {
                multiItemsPanel.setEnabled(b);
        }

        @Override
        public void focusOnField() {
                multiItemsPanel.requestFocus();
        }

        @Override
        public List<JerMultiItem> getAllMultiItems() {
                return multiItemList;
        }

        @Override
        public String getColumnTitle() {
                return name;
        }

        @Override
        public int getID() {
                return -1;
        }

        protected JerWorkPanel getWorkPanel() {
                return parentWP;
        }

        @Override
        public boolean isFieldValid() {
                return true;
        }

        protected void removeMultiItem(final JerMultiItem mi) {
                multiItemsPanel.remove(mi);
                multiItemList.remove(mi);
                resize();
        }

        private void resize() {
                final Dimension d = getPreferredSize();
                setMaximumSize(new Dimension(Short.MAX_VALUE, d.height));
                parentWP.getBasePanel().revalidate();
                // parentWP.getBasePanel().repaint();
        }

        @Override
        public void setID(final int nVal) {
        }

        @Override
        public void setIsValid(final boolean b) {
        }

        @Override
        public PreparedStatement setPrpStmt(final PreparedStatement ps,
                        final int pos) throws SQLException {
                return null;
        }

        public void setValues(final List<Object> allVals) {
                for (final JerMultiItem jmi : multiItemList) {
                        jmi.setCBoxValues(allVals);
                }
        }

        @Override
        public boolean stringIsValidEntry(final String str) {
                return true;
        }

}
