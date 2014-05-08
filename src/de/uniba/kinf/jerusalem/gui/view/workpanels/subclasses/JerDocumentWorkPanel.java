package de.uniba.kinf.jerusalem.gui.view.workpanels.subclasses;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

import de.uniba.kinf.jerusalem.gui.model.subclasses.JerAuthorModel;
import de.uniba.kinf.jerusalem.gui.model.subclasses.JerDocumentModel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.JerWorkPanel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.JerComboBox;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.subclasses.JerIntNumberTextField;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.subclasses.JerStringTextField;

/**
 * Provides {@link JerWorkPanel} for document entity.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerDocumentWorkPanel extends JerWorkPanel {

        private JerComboBox authorCB;
        private JerIntNumberTextField beginF;
        private JerIntNumberTextField endF;
        private JerStringTextField genreF;
        private JerStringTextField innercountF;
        private JerStringTextField notesF;
        private JerStringTextField oceditionF;
        private JerStringTextField ocenglishF;
        private JerStringTextField ocgermanF;
        private JerStringTextField ocotherF;
        private JerStringTextField propsF;
        private JerStringTextField titleF;
        private JerIntNumberTextField trustF;

        public JerDocumentWorkPanel(final JerDocumentModel documentModel) {
                super(documentModel);
        }

        @Override
        protected final void addAdditionalCBListener() {
                authorCB.addItemListener(new ItemListener() {

                        @Override
                        public void itemStateChanged(final ItemEvent e) {

                                if (beginF.getText().isEmpty()
                                                && endF.getText().isEmpty()) {
                                        if (e.getStateChange() == ItemEvent.SELECTED) {
                                                if (authorCB.getSelectedItem() != null) {
                                                        final HashMap<String, Object> values = getModel()
                                                                        .getTimeDataForID(
                                                                                        authorCB.getSelectedJerComboItem()
                                                                                                        .getId());
                                                        beginF.displayInfo(values);
                                                        endF.displayInfo(values);
                                                }
                                        } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                                                beginF.clear();
                                                endF.clear();
                                        }
                                }
                        }
                });
        }

        @Override
        protected void addEntries() {
                addEntry(authorCB);
                addEntry(titleF);
                addEntry(beginF);
                addEntry(endF);
                addEntry(innercountF);
                addEntry(oceditionF);
                addEntry(ocgermanF);
                addEntry(ocenglishF);
                addEntry(ocotherF);
                addEntry(genreF);
                addEntry(propsF);
                addEntry(trustF);
                addEntry(notesF);
        }

        @Override
        protected void createTextFields() {
                jm.getPreparer().add(
                                authorCB = new JerComboBox(this, "AUTHOR_ID",
                                                true));
                jm.getPreparer().add(
                                titleF = new JerStringTextField(this, "title",
                                                LimitedText));
                jm.getPreparer().add(
                                beginF = new JerIntNumberTextField(this,
                                                "begin_year", MakeNullable));
                jm.getPreparer().add(
                                endF = new JerIntNumberTextField(this,
                                                "end_year", MakeNullable));
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
                                genreF = new JerStringTextField(this, "genre",
                                                LimitedText));
                jm.getPreparer().add(
                                propsF = new JerStringTextField(this,
                                                "properties", LimitedText));
                jm.getPreparer().add(
                                notesF = new JerStringTextField(this, "notes",
                                                LimitedText));
                jm.getPreparer().add(
                                trustF = new JerIntNumberTextField(this,
                                                "trust", LimitedNumbers1To5));
        }

        @Override
        public void updateInternalBoxes() {
                if ((getModel().getSelectedModelFromMainModel() == null)
                                || (getModel().getSelectedModelFromMainModel() instanceof JerAuthorModel)) {
                        authorCB.setCBoxValues(getModel().getAllAuthors());
                }
        }

}
