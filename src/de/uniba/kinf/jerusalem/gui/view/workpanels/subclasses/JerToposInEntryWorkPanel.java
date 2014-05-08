package de.uniba.kinf.jerusalem.gui.view.workpanels.subclasses;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.vividsolutions.jts.io.ParseException;

import de.uniba.kinf.jerusalem.gui.helper.JerResourceBundleAccessor;
import de.uniba.kinf.jerusalem.gui.model.subclasses.JerEntryModel;
import de.uniba.kinf.jerusalem.gui.model.subclasses.JerPlaceModel;
import de.uniba.kinf.jerusalem.gui.model.subclasses.JerToposInEntryModel;
import de.uniba.kinf.jerusalem.gui.model.subclasses.JerToposModel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.JerWorkPanel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.JerComboBox;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.subclasses.JerIntNumberTextField;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.subclasses.JerStringTextField;

/**
 * Provides {@link JerWorkPanel} for topos in entry entity.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerToposInEntryWorkPanel extends JerWorkPanel {

        private JerStringTextField additionalInfoF;
        private JerComboBox assoPlaceCB;
        private JButton editPlaceBtn;
        private JerComboBox entryCB;
        private JerStringTextField notesF;
        private JerIntNumberTextField numberF;
        private final JerWorkPanel self;
        private JerStringTextField toposNameF;
        private JerComboBox toposNameIDCB;
        private JerStringTextField toposSitF;
        private JerStringTextField traditionsF;

        public JerToposInEntryWorkPanel(final JerToposInEntryModel model) {
                super(model);
                self = this;
        }

        @Override
        protected void addAdditionalCBListener() {

                toposNameIDCB.addItemListener(new ItemListener() {

                        @Override
                        public void itemStateChanged(final ItemEvent e) {
                                if (e.getStateChange() == ItemEvent.SELECTED
                                                && toposNameIDCB.getSelectedItem() != null) {
                                        try {
                                                getModel().checkForAssociatedPlaces(
                                                                toposNameIDCB.getColumnTitle(),
                                                                toposNameIDCB.getSelectedJerComboItem()
                                                                                .getId());
                                                assoPlaceCB.setEnabled(true);
                                                assoPlaceCB.setCBoxValues(getModel()
                                                                .getAllPlaceForToposID(
                                                                                toposNameIDCB.getSelectedJerComboItem()
                                                                                                .getId()));
                                        } catch (final ParseException e1) {
                                                getModel().getJerInfoMsgHandler()
                                                                .showMsg(e1.getLocalizedMessage());
                                        }
                                }
                                if (e.getStateChange() == ItemEvent.DESELECTED) {
                                        try {
                                                getModel().checkForAssociatedPlaces(
                                                                toposNameIDCB.getColumnTitle(),
                                                                -1);
                                                assoPlaceCB.setEnabled(false);
                                        } catch (final ParseException e1) {
                                                getModel().getJerInfoMsgHandler()
                                                                .showMsg(e1.getLocalizedMessage());
                                        }
                                }
                        }
                });

                editPlaceBtn.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(final ActionEvent e) {

                                int toposID = -1;
                                if (toposNameIDCB.getSelectedIndex() != -1) {
                                        toposID = toposNameIDCB
                                                        .getSelectedJerComboItem()
                                                        .getId();
                                }
                                if (assoPlaceCB.getSelectedIndex() == -1) {
                                        // -1 signals that new topos in entry
                                        // has to be created
                                        getModel().openTab(self, "PLACE_ID",
                                                        -1, "TOPOS_ID", toposID);
                                } else {
                                        getModel().openTab(
                                                        self,
                                                        "PLACE_ID",
                                                        assoPlaceCB.getSelectedJerComboItem()
                                                                        .getId(),
                                                        "TOPOS_ID", toposID);
                                }

                        }
                });
        }

        @Override
        protected void addEntries() {
                addEntry(entryCB);
                addEntry(toposNameIDCB);
                addEntry(toposNameF);
                addEntry(toposSitF);
                addEntry(traditionsF);
                addEntry(additionalInfoF);
                addEntry(numberF);
                addEntry(notesF);
                final JPanel p = new JPanel();
                p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
                p.add(assoPlaceCB);
                p.add(editPlaceBtn);
                addEntry(p);
        }

        @Override
        protected void createTextFields() {
                jm.getPreparer().add(
                                entryCB = new JerComboBox(this, "ENTRY_ID",
                                                false));
                jm.getPreparer().add(
                                toposNameIDCB = new JerComboBox(this,
                                                "TOPOS_ID", false));
                jm.getPreparer().add(
                                toposNameF = new JerStringTextField(this,
                                                "topos_name_in_entry",
                                                LimitedText));
                jm.getPreparer()
                                .add(toposSitF = new JerStringTextField(this,
                                                "topos_situation", LimitedText));
                jm.getPreparer().add(
                                traditionsF = new JerStringTextField(this,
                                                "traditions", LimitedText));
                jm.getPreparer().add(
                                additionalInfoF = new JerStringTextField(this,
                                                "additional_information",
                                                LimitedText));
                jm.getPreparer()
                                .add(numberF = new JerIntNumberTextField(this,
                                                "number",
                                                LimitedNumbersTo6DigitsAndNull));
                jm.getPreparer().add(
                                notesF = new JerStringTextField(this, "notes",
                                                LimitedText));
                jm.getPreparer().add(
                                assoPlaceCB = new JerComboBox(this, "PLACE_ID",
                                                true));
                assoPlaceCB.setEnabled(false);
                defaultCB.setEnabled(false);
                entryCB.setEnabled(false);
                entryCB.setMinusOneItemIsAllowed();
                editPlaceBtn = new JButton(
                                JerResourceBundleAccessor
                                                .get("editPlaceBtn_tiewp"));
        }

        @Override
        protected void disableAdditionalMainBtns() {
                editPlaceBtn.setEnabled(false);
        }

        @Override
        protected void enableAdditionalMainBtns() {
                editPlaceBtn.setEnabled(true);
        }

        @Override
        public final void updateInternalBoxes() {
                if (jm.getSelectedModelFromMainModel() == null
                                || jm.getSelectedModelFromMainModel() instanceof JerEntryModel) {
                        entryCB.setCBoxValues(getModel().getAllEntries());
                }
                if (getModel().getSelectedModelFromMainModel() == null
                                || getModel().getSelectedModelFromMainModel() instanceof JerToposModel) {
                        toposNameIDCB.setCBoxValues(getModel().getAllTopoi());
                }
                if (getModel().getSelectedModelFromMainModel() == null
                                || getModel().getSelectedModelFromMainModel() instanceof JerPlaceModel
                                || getModel().getSelectedModelFromMainModel() instanceof JerToposInEntryModel) {
                        if (toposNameIDCB.getSelectedIndex() != -1) {
                                assoPlaceCB.setCBoxValues(getModel()
                                                .getAllPlaceForToposID(
                                                                toposNameIDCB.getSelectedJerComboItem()
                                                                                .getId()));
                        }
                }
        }
}
