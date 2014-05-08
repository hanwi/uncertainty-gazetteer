package de.uniba.kinf.jerusalem.gui.view.workpanels.subclasses;

import java.util.HashMap;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTWriter;

import de.uniba.kinf.jerusalem.gui.helper.JerMsgType;
import de.uniba.kinf.jerusalem.gui.helper.JerObserverMsg;
import de.uniba.kinf.jerusalem.gui.helper.JerPlace;
import de.uniba.kinf.jerusalem.gui.helper.subclasses.JerGeoObserverMsg;
import de.uniba.kinf.jerusalem.gui.model.subclasses.JerEntryModel;
import de.uniba.kinf.jerusalem.gui.model.subclasses.JerPlaceModel;
import de.uniba.kinf.jerusalem.gui.model.subclasses.JerToposInEntryModel;
import de.uniba.kinf.jerusalem.gui.model.subclasses.JerToposModel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.JerWorkPanel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.JerMultiField;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.subclasses.JerDoubleNumberTextField;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.subclasses.JerIntNumberTextField;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.subclasses.JerStringTextField;

/**
 * Provides {@link JerWorkPanel} for place entity.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerPlaceWorkPanel extends JerWorkPanel {

        private JerIntNumberTextField beginF;
        private JerIntNumberTextField endF;
        private JerDoubleNumberTextField locEastF;
        private JerDoubleNumberTextField locNorthF;
        private JerStringTextField nameF;
        private JerStringTextField notesF;
        private JerStringTextField sFF;
        private JerStringTextField sridF;
        private JerStringTextField tavoF;
        private JerMultiField toposMultiField;

        public JerPlaceWorkPanel(final JerPlaceModel placeModel) {
                super(placeModel);
        }

        @Override
        protected void addEntries() {
                addEntry(nameF);
                addEntry(toposMultiField);
                addEntry(beginF);
                addEntry(endF);
                addEntry(locEastF);
                addEntry(locNorthF);
                addEntry(tavoF);
                addEntry(notesF);
                addEntry(sFF);
                addEntry(sridF);
        }

        @Override
        protected final void createTextFields() {
                jm.getPreparer().add(
                                nameF = new JerStringTextField(this, "name",
                                                LimitedText));
                jm.getPreparer().add(
                                beginF = new JerIntNumberTextField(this,
                                                "begin_year", MakeNullable));
                jm.getPreparer().add(
                                endF = new JerIntNumberTextField(this,
                                                "end_year", MakeNullable));
                jm.getPreparer().add(
                                locEastF = new JerDoubleNumberTextField(this,
                                                "location_easting",
                                                LimitedNumbers));
                jm.getPreparer().add(
                                locNorthF = new JerDoubleNumberTextField(this,
                                                "location_northing",
                                                LimitedNumbers));
                jm.getPreparer().add(
                                tavoF = new JerStringTextField(this, "tavo_bb",
                                                LimitedText));
                jm.getPreparer().add(
                                notesF = new JerStringTextField(this, "notes",
                                                LimitedText));
                jm.getPreparer().add(
                                sFF = new JerStringTextField(this,
                                                "simple_feature",
                                                LimitedTextTo32000));
                jm.getPreparer().add(
                                sridF = new JerStringTextField(this, "srid",
                                                LimitedText));
                // toposMultiField-element must be last to be added due to
                // JerPreparer.setJerModelPrepStmt()
                jm.getPreparer().add(
                                toposMultiField = new JerMultiField(this,
                                                "TOPOS_ID"));
                // to avoid uncomfortable width for workpanel
                sFF.setVisible(false);
                // not necessary for typical end user
                sridF.setVisible(false);
        }

        @Override
        protected void fillInGeomInfo(final JerObserverMsg msg) {
                if (msg.getType() == JerMsgType.GEOMETRY) {
                        final HashMap<String, Object> values = new HashMap<>();
                        final JerGeoObserverMsg m = (JerGeoObserverMsg) msg;

                        if (m.getPlaceLi().size() == 1
                                        && (getModel().getSelectedIDFromMainModel() == -1
                                                        || getModel().getSelectedModelFromMainModel() instanceof JerPlaceModel
                                                        || getModel().getSelectedModelFromMainModel() instanceof JerToposInEntryModel || getModel()
                                                        .getSelectedModelFromMainModel() instanceof JerEntryModel)) {
                                final JerPlace place = m.getPlaceLi().get(0);
                                values.put(getModel().getIdName(), defaultCB
                                                .getSelectedJerComboItem()
                                                .getId());
                                final Geometry g = place.getMainLoc();
                                values.put("LOCATION_EASTING",
                                                g.getCoordinate().x);
                                values.put("LOCATION_NORTHING",
                                                g.getCoordinate().y);
                                if (place.hasAdditionalInstances()) {
                                        final WKTWriter writer = new WKTWriter();
                                        final String str = writer.write(place
                                                        .getAdditionalInst());
                                        values.put("SIMPLE_FEATURE", str);
                                }
                        }
                        setFieldValues(values);
                }
        }

        @Override
        public List<Object> getDefAllForMulti() {
                return getModel().getAllTopoi();
        }

        @Override
        public List<Object> getMultiVals() {
                return getModel().getAllToposForPlaceID();
        }

        @Override
        protected void setStaticInfo() {
                sridF.setText(getModel().getSRID());
        }

        @Override
        public void updateInternalBoxes() {
                if (getModel().getSelectedModelFromMainModel() == null
                                || getModel().getSelectedModelFromMainModel() instanceof JerToposModel) {
                        toposMultiField.setValues(getModel().getAllTopoi());
                }
                setStaticInfo();
        }

}
