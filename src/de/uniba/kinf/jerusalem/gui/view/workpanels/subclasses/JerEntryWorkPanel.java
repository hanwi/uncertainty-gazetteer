package de.uniba.kinf.jerusalem.gui.view.workpanels.subclasses;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import de.uniba.kinf.jerusalem.gui.helper.JerResourceBundleAccessor;
import de.uniba.kinf.jerusalem.gui.model.subclasses.JerDocumentModel;
import de.uniba.kinf.jerusalem.gui.model.subclasses.JerEntryModel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.JerWorkPanel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.JerComboBox;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.subclasses.JerStringTextField;

/**
 * Provides {@link JerWorkPanel} for entry entity.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerEntryWorkPanel extends JerWorkPanel {
        private JButton addTIEBtn;
        private JerComboBox documentCB;
        private JerStringTextField innercountF;
        private JerStringTextField notesF;
        private JerStringTextField oceditionF;
        private JerStringTextField ocenglishF;
        private JerStringTextField ocgermanF;
        private JerStringTextField ocotherF;
        private final JerWorkPanel self;

        public JerEntryWorkPanel(final JerEntryModel entryModel) {
                super(entryModel);
                self = this;
        }

        @Override
        protected void addEntries() {
                addEntry(documentCB);
                addEntry(innercountF);
                addEntry(oceditionF);
                addEntry(ocgermanF);
                addEntry(ocenglishF);
                addEntry(ocotherF);
                addEntry(notesF);
                addEntry(addTIEBtn);
        }

        @Override
        protected void createTextFields() {
                addTIEBtn = new JButton(
                                JerResourceBundleAccessor
                                                .get("jerworkpanel_addtiebtn"));
                addTIEBtn.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(final ActionEvent e) {
                                // -1 signals that new topos in entry has to be
                                // created, second
                                // -1 signals that no id has to be set in dest
                                // tab
                                getModel().openTab(self, "TOPOS_IN_ENTRY_ID",
                                                -1, "", -1);
                        }
                });
                jm.getPreparer().add(
                                documentCB = new JerComboBox(this,
                                                "DOCUMENT_ID", false));
                jm.getPreparer().add(
                                innercountF = new JerStringTextField(this,
                                                "inner_count", LimitedText));
                jm.getPreparer().add(
                                oceditionF = new JerStringTextField(this,
                                                "outer_count_edition",
                                                LimitedText));
                jm.getPreparer().add(
                                ocgermanF = new JerStringTextField(this,
                                                "outer_count_german",
                                                LimitedText));
                jm.getPreparer().add(
                                ocenglishF = new JerStringTextField(this,
                                                "outer_count_english",
                                                LimitedText));
                jm.getPreparer().add(
                                ocotherF = new JerStringTextField(this,
                                                "outer_count_other",
                                                LimitedText));
                jm.getPreparer().add(
                                notesF = new JerStringTextField(this, "notes",
                                                LimitedText));

        }

        @Override
        public void updateInternalBoxes() {
                if (getModel().getSelectedModelFromMainModel() == null
                                || getModel().getSelectedModelFromMainModel() instanceof JerDocumentModel) {
                        documentCB.setCBoxValues(getModel().getAllDocuments());
                }
        }

}
