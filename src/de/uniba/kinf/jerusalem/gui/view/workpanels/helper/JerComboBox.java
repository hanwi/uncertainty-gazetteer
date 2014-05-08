package de.uniba.kinf.jerusalem.gui.view.workpanels.helper;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;

import javax.swing.JComboBox;

import de.uniba.kinf.jerusalem.Main;
import de.uniba.kinf.jerusalem.gui.helper.JerResourceBundleAccessor;
import de.uniba.kinf.jerusalem.gui.view.workpanels.JerWorkPanel;

/**
 * This class represents a {@link JComboBox} which fulfills the JerFieldInfoIF.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerComboBox extends JComboBox<JerItem> implements JerFieldInfoIF {

        /**
     * 
     */
        private static final long serialVersionUID = -6531198201395276436L;
        private boolean isValidContent;
        private boolean minusOneAllowed;
        private final String name;
        private final boolean nIsAllowed;
        private final JerWorkPanel workPanel;

        public JerComboBox(final JerWorkPanel jwp, final String str,
                        final boolean nullIsAllowed) {
                workPanel = jwp;
                name = str;
                nIsAllowed = nullIsAllowed;
                if (nIsAllowed) {
                        isValidContent = true;
                } else {
                        isValidContent = false;
                }
                minusOneAllowed = false;
                if (name.equals(workPanel.getModel().getIdName())) {
                        minusOneAllowed = true;
                }

                addItemListener(new ItemListener() {

                        @Override
                        public void itemStateChanged(final ItemEvent e) {
                                if (e.getStateChange() == ItemEvent.SELECTED
                                                || e.getStateChange() == ItemEvent.DESELECTED) {
                                        if (getSelectedIndex() == -1
                                                        && nIsAllowed
                                                        || getSelectedIndex() != -1) {
                                                setIsValid(true);
                                        } else {
                                                setIsValid(false);
                                        }
                                }
                        }
                });

                final Dimension d = getPreferredSize();
                setMaximumSize(new Dimension(Short.MAX_VALUE, d.height));

        }

        @Override
        public void clear() {
                if (nIsAllowed) {
                        setSelectedIndex(-1);
                }
        }

        @SuppressWarnings("boxing")
        @Override
        public void displayInfo(final HashMap<String, Object> values) {
                int id = -1;
                if (values.get(getColumnTitle()) != null) {
                        id = (Integer) values.get(getColumnTitle());
                }
                setID(id);
        }

        @Override
        public void enableFunctionality(final boolean b) {
                setEnabled(b);
        }

        @Override
        public void focusOnField() {
                requestFocus();
        }

        @Override
        public String getColumnTitle() {
                return name;
        }

        @Override
        public int getID() {
                return getSelectedJerComboItem().getId();
        }

        public JerItem getSelectedJerComboItem() {
                return (JerItem) getSelectedItem();
        }

        @Override
        public boolean isFieldValid() {
                return isValidContent;
        }

        @SuppressWarnings("unchecked")
        public void setCBoxValues(final List<Object> colLi) {
                removeAllItems();
                if (minusOneAllowed) {
                        addItem(new JerItem(-1,
                                        JerResourceBundleAccessor.get("new")));
                }
                if (nIsAllowed) {
                        addItem(null);
                }
                for (int i = 0; i < colLi.size(); i++) {
                        final List<Object> rowLi = (List<Object>) colLi.get(i);
                        final JerItem jci = new JerItem((Integer) rowLi.get(0),
                                        rowLi.get(1));
                        addItem(jci);
                }
        }

        @Override
        public void setID(final int val) {
                for (int i = 0; i < getItemCount(); i++) {
                        if (getItemAt(i) != null) {
                                final JerItem jci = getItemAt(i);
                                if (jci.getId() == val) {
                                        setSelectedIndex(i);
                                        break;
                                }
                        }
                }
        }

        @Override
        public final void setIsValid(final boolean b) {
                isValidContent = b;
                if (!isValidContent) {
                        setBackground(Main.DEFCOLORPROBLEM);
                } else {
                        setBackground(Main.DEFCOLOROK);
                }
                workPanel.actualizeStatus();
        }

        public void setMinusOneItemIsAllowed() {
                minusOneAllowed = true;
        }

        @Override
        public PreparedStatement setPrpStmt(final PreparedStatement ps,
                        final int pos) throws SQLException {
                if (nIsAllowed && getSelectedItem() == null) {
                        ps.setNull(pos, Types.INTEGER);
                } else {
                        ps.setInt(pos, getSelectedJerComboItem().getId());
                }
                return ps;
        }

        @Override
        public boolean stringIsValidEntry(final String str) {
                return getSelectedIndex() == -1 && !nIsAllowed;
        }

}
