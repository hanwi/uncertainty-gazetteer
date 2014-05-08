package de.uniba.kinf.jerusalem.gui.view.workpanels.subclasses;

import de.uniba.kinf.jerusalem.gui.model.subclasses.JerAuthorModel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.JerWorkPanel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.subclasses.JerIntNumberTextField;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.subclasses.JerStringTextField;

/**
 * Provides {@link JerWorkPanel} for author entity.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerAuthorWorkPanel extends JerWorkPanel {

        private JerIntNumberTextField beginF;
        private JerIntNumberTextField endF;
        private JerStringTextField nameF;
        private JerStringTextField notesF;
        private JerStringTextField originF;
        private JerStringTextField religionF;

        public JerAuthorWorkPanel(final JerAuthorModel authorModel) {
                super(authorModel);
        }

        @Override
        protected final void addEntries() {
                addEntry(nameF);
                addEntry(originF);
                addEntry(religionF);
                addEntry(beginF);
                addEntry(endF);
                addEntry(notesF);
        }

        @Override
        protected final void createTextFields() {
                jm.getPreparer().add(
                                nameF = new JerStringTextField(this, "name",
                                                LimitedText));
                jm.getPreparer().add(
                                originF = new JerStringTextField(this,
                                                "origin", LimitedText));
                jm.getPreparer().add(
                                religionF = new JerStringTextField(this,
                                                "religious_denomination",
                                                LimitedText));
                jm.getPreparer().add(
                                beginF = new JerIntNumberTextField(this,
                                                "begin_year", MakeNullable));
                jm.getPreparer().add(
                                endF = new JerIntNumberTextField(this,
                                                "end_year", MakeNullable));
                jm.getPreparer().add(
                                notesF = new JerStringTextField(this, "notes",
                                                LimitedText));
        }

}
