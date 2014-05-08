package de.uniba.kinf.jerusalem.gui.view.workpanels.subclasses;

import java.util.List;

import de.uniba.kinf.jerusalem.gui.model.subclasses.JerPlaceModel;
import de.uniba.kinf.jerusalem.gui.model.subclasses.JerToposModel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.JerWorkPanel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.JerMultiField;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.JerTextField;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.subclasses.JerStringTextField;

/**
 * Provides {@link JerWorkPanel} for topos entity.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerToposWorkPanel extends JerWorkPanel {

        private JerTextField nameF;
        private JerTextField notesF;
        private JerMultiField placeMultiField;

        public JerToposWorkPanel(final JerToposModel tm) {
                super(tm);
        }

        @Override
        protected final void addEntries() {
                addEntry(nameF);
                addEntry(placeMultiField);
                addEntry(notesF);
        }

        @Override
        protected void createTextFields() {
                jm.getPreparer().add(
                                nameF = new JerStringTextField(this, "name",
                                                LimitedText));
                jm.getPreparer().add(
                                notesF = new JerStringTextField(this, "notes",
                                                LimitedText));
                // placeMultiField-element must be last to be added due to
                // JerPreparer.setJerModelPrepStmt()
                jm.getPreparer().add(
                                placeMultiField = new JerMultiField(this,
                                                "PLACE_ID"));
        }

        @Override
        public List<Object> getDefAllForMulti() {
                return getModel().getAllPlaces();
        }

        @Override
        public List<Object> getMultiVals() {
                return getModel().getAllPlaceForToposID(
                                getModel().getSelectedIDFromMainModel());
        }

        @Override
        public void updateInternalBoxes() {
                if (getModel().getSelectedModelFromMainModel() == null
                                || getModel().getSelectedModelFromMainModel() instanceof JerPlaceModel) {
                        placeMultiField.setValues(getDefAllForMulti());
                }
        }
}
