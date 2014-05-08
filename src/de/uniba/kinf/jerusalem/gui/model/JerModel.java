package de.uniba.kinf.jerusalem.gui.model;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.io.ParseException;

import de.uniba.kinf.jerusalem.gui.helper.subclasses.JerTabObserverMsg;
import de.uniba.kinf.jerusalem.gui.model.helper.JerPreparer;
import de.uniba.kinf.jerusalem.gui.model.helper.JerSequencerIF;
import de.uniba.kinf.jerusalem.gui.view.helper.JerInfoMsgHandler;
import de.uniba.kinf.jerusalem.gui.view.workpanels.JerWorkPanel;

/**
 * Provides basic functionality and methods for interaction with
 * {@link JerMainModel} for all entity specific subclasses of {@link JerModel}.
 * 
 * 
 * @author Hanno Wierichs
 * 
 */
public abstract class JerModel {

        protected static String sqlAllAuthors = "SELECT AUTHOR_ID, NAME FROM JERUSALEM.AUTHORS ORDER BY NAME";
        protected static String sqlAllDistinctOrigin = "SELECT  DISTINCT ORIGIN FROM JERUSALEM.AUTHORS";
        protected static String sqlAllDistinctReligion = "SELECT DISTINCT RELIGIOUS_DENOMINATION FROM JERUSALEM.AUTHORS";
        protected static String sqlAllDocuments = "SELECT DOCUMENT_ID, TITLE FROM JERUSALEM.DOCUMENTS ORDER BY TITLE";
        protected static String sqlAllEntries = "SELECT ENTRY_ID, ENTRY_ID FROM JERUSALEM.ENTRIES ORDER BY ENTRY_ID";
        protected static String sqlAllPlaces = "SELECT PLACE_ID, NAME FROM JERUSALEM.PLACES ORDER BY NAME";
        protected static String sqlAllPlacesAndInformation = "SELECT LOCATION_EASTING, LOCATION_NORTHING, SIMPLE_FEATURE FROM JERUSALEM.PLACES";
        protected static String sqlAllTopoi = "SELECT TOPOS_ID, NAME FROM JERUSALEM.TOPOI ORDER BY NAME";
        protected static String sqlAllTopoiInEntry = "SELECT TOPOS_IN_ENTRY_ID, TOPOS_IN_ENTRY_ID FROM JERUSALEM.TOPOS_IN_ENTRY ORDER BY TOPOS_NAME_IN_ENTRY";

        private String[] dataColumns;
        private final String idName;
        private final JerMainModel mainModel;
        private JerTabObserverMsg openTabObsMsg = null;
        private final JerPreparer preparer;
        private final String schemaName;
        private JerSequencerIF sequencer;
        private String sqlAllData;
        private String sqlMaxID;
        private String sqlTable;
        private final String tableName;

        public JerModel(final JerMainModel mainMod, final String name,
                        final String iDName) throws SQLException {
                mainModel = mainMod;
                schemaName = mainModel.getSchemaName();
                tableName = name;
                defineSQLStrsFetchColumnNames();
                preparer = new JerPreparer(sqlTable, iDName);
                idName = iDName;
        }

        protected void addSequencer(final JerSequencerIF sequencer2) {
                sequencer = sequencer2;
        }

        public void checkForAssociatedPlaces(final String modelIDName,
                        final int val) throws ParseException {
                mainModel.checkAssociatedPlaces(this, modelIDName, val);
        }

        protected void defineSQLStrsFetchColumnNames() throws SQLException {
                sqlTable = schemaName + "." + tableName;
                sqlAllData = "SELECT * FROM " + sqlTable;
                dataColumns = mainModel.fetchColumnNames(this).toArray(
                                new String[0]);
                sqlMaxID = "SELECT MAX(" + dataColumns[0] + ") FROM "
                                + sqlTable;
        }

        public void del() {
                final int val = getSequencer().prev(preparer.getCurrentIDVal());
                mainModel.delete(this);
                mainModel.setModelAndID(this, val);
        }

        public List<Object> getAllAuthors() {
                return mainModel.getAllAuthors();
        }

        public List<Object> getAllData() {
                return mainModel.getAllData(this);
        }

        public HashMap<String, Object> getAllDataForID(final int id) {
                return mainModel.getAllDataForID(this, id);
        }

        public List<Object> getAllDocuments() {
                return mainModel.getAllDocuments();
        }

        public List<Object> getAllEntries() {
                return mainModel.getAllEntries();
        }

        public List<Object> getAllPlaceForToposID(final int id) {
                return mainModel.getAllPlaceForToposID(this, id);
        }

        public List<Object> getAllPlaces() {
                return mainModel.getAllPlaces();
        }

        public List<Object> getAllTopoi() {
                return mainModel.getAllTopoi();
        }

        public List<Object> getAllTopoiInEntry() {
                return mainModel.getAllTopoiInEntry();
        }

        public List<Object> getAllToposForPlaceID() {
                return mainModel.getAllToposForPlaceID(this,
                                getSelectedIDFromMainModel());
        }

        public Set<Integer> getAssociatedIDs() {
                return mainModel.getAssociatedIDs(this);
        }

        public String[] getDataColumns() {
                return dataColumns;
        }

        public abstract List<Object> getDefaultAll();

        public String getIdName() {
                return idName;
        }

        public JerInfoMsgHandler getJerInfoMsgHandler() {
                return mainModel.getJerInfoMsgHandler();
        }

        public JerPreparer getPreparer() {
                return preparer;
        }

        public Properties getProps() {
                return mainModel.getProps();
        }

        public int getSelectedIDFromMainModel() {
                return mainModel.getSelectedID();
        }

        public JerModel getSelectedModelFromMainModel() {
                return mainModel.getSelectedModel();
        }

        public JerSequencerIF getSequencer() {
                return sequencer;
        }

        public String getSqlAdditionalInstancesForSpecificPlace(
                        final int selectedID2) {
                return "SELECT PLACE_ID, LOCATION_EASTING, LOCATION_NORTHING, SIMPLE_FEATURE FROM JERUSALEM.PLACES WHERE PLACE_ID= "
                                + selectedID2;
        }

        public String getSqlAllData() {
                return sqlAllData;
        }

        public String getSqlAllDataForID(final int id) {
                return sqlAllData + " WHERE " + dataColumns[0] + " = " + id;
        }

        public String getSqlAllPlacesForToposID(final int id) {
                return "SELECT P_ID, NAME, BEGIN_YEAR, END_YEAR FROM (SELECT PLACE_ID AS P_ID, NAME FROM JERUSALEM.PLACES)TAB1 INNER JOIN (SELECT PLACE_ID, BEGIN_YEAR, END_YEAR FROM JERUSALEM.PLACETOPOS WHERE TOPOS_ID = "
                                + id + ")TAB2 ON TAB1.P_ID=TAB2.PLACE_ID";
        }

        public String getSqlAllTopoiForPlaceID(final int id) {
                return "SELECT T_ID, NAME, BEGIN_YEAR, END_YEAR FROM (SELECT TOPOS_ID AS T_ID, NAME FROM JERUSALEM.TOPOI)TAB1 INNER JOIN (SELECT TOPOS_ID, BEGIN_YEAR, END_YEAR FROM JERUSALEM.PLACETOPOS WHERE PLACE_ID = "
                                + id + ")TAB2 ON TAB1.T_ID=TAB2.TOPOS_ID";
        }

        public String getSqlMaxID() {
                return sqlMaxID;
        }

        public String getSqlSelectPlaceByCoordinate(final Coordinate coordinate) {
                return "SELECT PLACE_ID FROM JERUSALEM.PLACES WHERE LOCATION_EASTING ="
                                + coordinate.x
                                + " AND LOCATION_NORTHING = "
                                + coordinate.y;
        }

        protected abstract String getSQLStrForIDs(JerModel jm, int selectedID);

        public String getSqlTimeDataForAuthorID(final int id) {
                return "SELECT BEGIN_YEAR, END_YEAR FROM JERUSALEM.AUTHORS WHERE AUTHOR_ID = "
                                + id;
        }

        public final String getSRID() {
                return mainModel.getSRID();
        }

        public String getTableName() {
                return tableName.toUpperCase();
        }

        public HashMap<String, Object> getTimeDataForID(final int id) {
                return mainModel.getTimeDataForID(this, id);
        }

        @SuppressWarnings("rawtypes")
        public Class[] getTypes() throws SQLException {
                return mainModel.getTypes(this);
        }

        public void next() {
                final int val = getSequencer().next(preparer.getCurrentIDVal());
                mainModel.makeUpdate(this);
                mainModel.setModelAndID(this, val);
        }

        public void openTab(final JerWorkPanel wp,
                        final String idNameModelDestTab, final int idDestTab,
                        final String nameAdditionalFieldInfo,
                        final int additionalIDtoBeSet) {
                final int initiatingVal = save();
                mainModel.openTab(this, initiatingVal, idNameModelDestTab,
                                idDestTab, nameAdditionalFieldInfo,
                                additionalIDtoBeSet);
        }

        public void previous() {
                final int val = getSequencer().prev(preparer.getCurrentIDVal());
                mainModel.makeUpdate(this);
                mainModel.setModelAndID(this, val);
        }

        public final int save() {
                int val = preparer.getCurrentIDVal();
                if (openTabObsMsg != null) {
                        final JerModel startModel = openTabObsMsg
                                        .getInitiatingModel();
                        final int startVal = openTabObsMsg.getInitiatingVal();
                        openTabObsMsg = null;
                        mainModel.insert(this);
                        mainModel.setModelAndID(startModel, startVal);
                } else {
                        if (val == -1) {
                                // insert new entity
                                mainModel.insert(this);
                                mainModel.setModelAndID(this, val);
                                // openTab() needs max value for model/table
                                val = mainModel.getMaxIDValueForTable(this);
                        } else {
                                // update entity
                                mainModel.makeUpdate(this);
                                mainModel.setModelAndID(this, val);
                        }

                }
                return val;

        }

        public void setModelAndID(final int id) {
                mainModel.setModelAndID(this, id);
        }

        public void setOpenTabObsMsg(final JerTabObserverMsg msg) {
                this.openTabObsMsg = msg;
        }

}
