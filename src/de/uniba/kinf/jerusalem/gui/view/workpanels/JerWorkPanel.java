package de.uniba.kinf.jerusalem.gui.view.workpanels;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import de.uniba.kinf.jerusalem.gui.helper.JerLogger;
import de.uniba.kinf.jerusalem.gui.helper.JerMsgType;
import de.uniba.kinf.jerusalem.gui.helper.JerObserverMsg;
import de.uniba.kinf.jerusalem.gui.helper.JerResourceBundleAccessor;
import de.uniba.kinf.jerusalem.gui.helper.subclasses.JerTabObserverMsg;
import de.uniba.kinf.jerusalem.gui.model.JerModel;
import de.uniba.kinf.jerusalem.gui.view.JerMainView;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.JerComboBox;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.JerFieldInfoIF;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.JerTextField;

/**
 * Provides layout and shared functionality for {@link JerWorkPanel} subclasses.
 * Defines regex for {@link JerTextField}.
 * 
 * @author Hanno Wierichs
 * 
 */
public abstract class JerWorkPanel implements Observer {
        public static final String LimitedNumbers = "^(\\d{0,4}\\.?\\d{0,})$";
        public static final String LimitedNumbers1To5 = "[1,2,3,4,5]{0,1}";
        public static final String LimitedNumbersTo6DigitsAndNull = "^(null|\\d{0,6})$";
        public static final String LimitedText = "^(null|.{0,1000})$";
        public static final String LimitedTextTo32000 = "^(null|.{0,32000})$";
        public static final Logger LOGGER = JerLogger.getLogger();
        public static final String MakeNullable = "^(null|[0]{1}|-{0,1}[0-9]{0,4})$";
        private final JPanel basePanel;
        protected JerComboBox defaultCB;
        private final JButton deleteBtn;
        protected final JerModel jm;
        private final JButton nextBtn;
        private final JButton previousBtn;
        private final JButton resetBtn;
        private final JButton saveBtn;

        public JerWorkPanel(final JerModel model) {
                jm = model;
                basePanel = new JPanel();
                basePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.Y_AXIS));

                jm.getPreparer().add(
                                defaultCB = new JerComboBox(this, model
                                                .getIdName(), false));
                createTextFields();
                addEntry(defaultCB);
                addEntries();

                final JPanel btnPanel = new JPanel();
                btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
                btnPanel.add(saveBtn = new JButton(JerResourceBundleAccessor
                                .get("save")));
                btnPanel.add(previousBtn = new JButton("<"));
                btnPanel.add(nextBtn = new JButton(">"));
                btnPanel.add(resetBtn = new JButton(JerResourceBundleAccessor
                                .get("reset")));
                btnPanel.add(deleteBtn = new JButton(JerResourceBundleAccessor
                                .get("delete")));
                basePanel.add(btnPanel);

                setDefaultCBListener();
                addAdditionalCBListener();
                addListenerToBtns();
                Properties props = model.getProps();
                JerMainView.addAccelerator(saveBtn, props, "save_btn_workpanel");
                JerMainView.addAccelerator(previousBtn, props,
                                "previous_btn_workpanel");
                JerMainView.addAccelerator(nextBtn, props, "next_btn_workpanel");
                JerMainView.addAccelerator(resetBtn, props,
                                "reset_btn_workpanel");
                JerMainView.addAccelerator(deleteBtn, props,
                                "delete_btn_workpanel");

                // display values at startup
                setFieldValues(new HashMap<String, Object>());

                basePanel.setVisible(true);
        }

        public final void actualizeStatus() {
                for (final JerFieldInfoIF jfi : jm.getPreparer().getFieldLi()) {
                        if (!jfi.isFieldValid()) {
                                disableMainBtns();
                                disableSecondaryBtns();
                                return;
                        }
                }
                enableMainBtsn();
        }

        protected void addAdditionalCBListener() {
        }

        protected abstract void addEntries();

        protected void addEntry(final Component component) {
                basePanel.add(component);
        }

        private void addListenerToBtns() {

                saveBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                                getModel().save();
                        }
                });

                previousBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                                getModel().previous();
                        }
                });

                nextBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                                getModel().next();
                        }
                });

                resetBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                                fillInValues(new JerObserverMsg(
                                                JerMsgType.SELECTION));
                        }
                });

                deleteBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                                getModel().del();
                        }
                });
        }

        protected final void clearFields() {
                for (final JerFieldInfoIF jfi : jm.getPreparer().getFieldLi()) {
                        jfi.clear();
                }
                disableSecondaryBtns();
        }

        protected abstract void createTextFields();

        protected void disableAdditionalMainBtns() {
        }

        protected final void disableMainBtns() {
                disableAdditionalMainBtns();
                saveBtn.setEnabled(false);
                previousBtn.setEnabled(false);
                nextBtn.setEnabled(false);
        }

        protected final void disableSecondaryBtns() {
                resetBtn.setEnabled(false);
                deleteBtn.setEnabled(false);
        }

        protected void enableAdditionalMainBtns() {
        }

        protected final void enableMainBtsn() {
                enableAdditionalMainBtns();
                saveBtn.setEnabled(true);
                if (defaultCB.getSelectedJerComboItem().getId() != -1) {
                        previousBtn.setEnabled(true);
                        nextBtn.setEnabled(true);
                } else {
                        previousBtn.setEnabled(false);
                        nextBtn.setEnabled(false);
                }
        }

        private void enableSecondaryBtns() {
                resetBtn.setEnabled(true);
                deleteBtn.setEnabled(true);
        }

        protected void fillInGeomInfo(final JerObserverMsg msg) {
        }

        protected void fillInValues(final JerObserverMsg msg) {
                if (msg.getType() == JerMsgType.DB_UPDATE) {
                        updateInternalComponents();
                }
                if (msg.getType() == JerMsgType.TAB) {
                        setVal((JerTabObserverMsg) msg);
                }
                if (msg.getType() == JerMsgType.GEOMETRY) {
                        fillInGeomInfo(msg);
                }
                if (msg.getType() == JerMsgType.SELECTION
                                && getModel().getSelectedModelFromMainModel() != null
                                && getModel().getSelectedModelFromMainModel()
                                                .equals(getModel())) {
                        setFieldValues(getModel()
                                        .getAllDataForID(
                                                        getModel().getSelectedIDFromMainModel()));
                }
        }

        public JComponent getBasePanel() {
                return basePanel;
        }

        public List<Object> getDefAllForMulti() {
                return new ArrayList<Object>();
        }

        public JerModel getModel() {
                return jm;
        }

        public List<Object> getMultiVals() {
                return new ArrayList<Object>();
        }

        protected final void setDefaultCBListener() {
                defaultCB.addItemListener(new ItemListener() {

                        @Override
                        public void itemStateChanged(final ItemEvent e) {

                                if (e.getStateChange() == ItemEvent.SELECTED
                                                && defaultCB.getSelectedItem() != null) {
                                        int val = defaultCB
                                                        .getSelectedJerComboItem()
                                                        .getId();
                                        final HashMap<String, Object> hm = getModel()
                                                        .getAllDataForID(val);
                                        if (hm.isEmpty()) {
                                                clearFields();
                                                if (defaultCB.getItemCount() > 1) {
                                                        getModel().setModelAndID(
                                                                        val);
                                                }
                                        } else {
                                                setFieldValues(hm);
                                                enableSecondaryBtns();
                                                getModel().setModelAndID(val);
                                        }
                                }
                        }
                });
        }

        protected final void setFieldValues(final HashMap<String, Object> values) {
                for (final JerFieldInfoIF jfi : jm.getPreparer().getFieldLi()) {
                        jfi.displayInfo(values);
                }
                setStaticInfo();
                actualizeStatus();
                setFocusToFirstField();
        }

        protected void setFocusToFirstField() {
                defaultCB.focusOnField();
        }

        protected void setStaticInfo() {
        }

        protected void setVal(final JerTabObserverMsg m) {
                if (m.getDestIDName().equals(getModel().getIdName())) {
                        getModel().setOpenTabObsMsg(m);
                        if (m.getDestID() != -1) {
                                getModel().setModelAndID(m.getDestID());
                        } else {
                                defaultCB.setID(m.getDestID());
                                for (final JerFieldInfoIF jfi : jm
                                                .getPreparer().getFieldLi()) {
                                        jfi.clear();
                                        if (jfi.getColumnTitle()
                                                        .equals(m.getInitiatingModel()
                                                                        .getIdName())) {
                                                jfi.setID(m.getInitiatingVal());
                                        }
                                        if (jfi.getColumnTitle()
                                                        .equals(m.getNameAdditionalFieldInfo())) {
                                                jfi.setID(m.getAdditionalIDtoBeSet());
                                        }
                                }
                                setStaticInfo();
                                actualizeStatus();
                        }
                }
        }

        @Override
        public void update(final Observable arg0, final Object arg1) {
                fillInValues((JerObserverMsg) arg1);
        }

        public void updateInternalBoxes() {
        }

        public void updateInternalComponents() {
                // == null to ensure displaying values at startup in cbbox
                if (jm.getSelectedModelFromMainModel() == null
                                || jm.getSelectedModelFromMainModel()
                                                .equals(jm)) {
                        defaultCB.setCBoxValues(getModel().getDefaultAll());
                }
                updateInternalBoxes();
        }

}
