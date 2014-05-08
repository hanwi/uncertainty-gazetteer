package de.uniba.kinf.jerusalem.gui.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import de.uniba.kinf.jerusalem.Main;
import de.uniba.kinf.jerusalem.gui.helper.DBBackupRunner;
import de.uniba.kinf.jerusalem.gui.helper.JerHistObj;
import de.uniba.kinf.jerusalem.gui.helper.JerHistRef;
import de.uniba.kinf.jerusalem.gui.helper.JerLogger;
import de.uniba.kinf.jerusalem.gui.helper.JerMsgType;
import de.uniba.kinf.jerusalem.gui.helper.JerObserverMsg;
import de.uniba.kinf.jerusalem.gui.helper.JerPlace;
import de.uniba.kinf.jerusalem.gui.helper.JerResourceBundleAccessor;
import de.uniba.kinf.jerusalem.gui.helper.subclasses.JerGeoObserverMsg;
import de.uniba.kinf.jerusalem.gui.helper.subclasses.JerTabObserverMsg;
import de.uniba.kinf.jerusalem.gui.model.helper.JerSelectorIF;
import de.uniba.kinf.jerusalem.gui.model.subclasses.JerPlaceModel;
import de.uniba.kinf.jerusalem.gui.view.JerAnalysisDialog;
import de.uniba.kinf.jerusalem.gui.view.helper.JerInfoMsgHandler;

/**
 * Provides methods for database interaction (read, write, export data).
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerMainModel extends Observable implements JerSelectorIF {
        private static final Logger LOGGER = JerLogger.getLogger();
        private static String SRID;
        private Connection conn;
        private String dbLocation;
        private final JerHistRef jerHistRef;
        private final JerInfoMsgHandler jerInfoMsgHandler;
        private JerPlaceModel pm = null;
        private final Properties properties;
        private String schemaName;
        // no selection at startup
        private int selectedID = -1;
        private JerModel selectedModel;
        private String sqlScriptDelimiter;
        private String sqlScriptLocation;
        private List<String> tablesList;

        public JerMainModel(final Properties props) throws IOException,
                        SQLException {
                jerHistRef = new JerHistRef();
                properties = props;
                jerInfoMsgHandler = new JerInfoMsgHandler();
                setSystemAndDBValues();
                setUpListOfTables();
                connectToDBAndEnsureAllTablesExist();
        }

        public void checkSelectedPlaces() throws ParseException {
                checkAssociatedPlaces(selectedModel, selectedModel.getIdName(),
                                selectedID);
        }

        @SuppressWarnings("unchecked")
        public void checkAssociatedPlaces(final JerModel jerModel,
                        final String modelIDName, final int val)
                        throws ParseException {

                // default show all places
                String sql = JerModel.sqlAllPlacesAndInformation;

                if (val != -1) {
                        if (modelIDName.equalsIgnoreCase("AUTHOR_ID")) {
                                sql = "SELECT LOCATION_EASTING, LOCATION_NORTHING, SIMPLE_FEATURE FROM (SELECT LOCATION_EASTING, LOCATION_NORTHING, SIMPLE_FEATURE,  PLACE_ID FROM JERUSALEM.PLACES)TAB5 INNER JOIN (SELECT PLACE_ID FROM (SELECT PLACE_ID, ENTRY_ID FROM JERUSALEM.TOPOS_IN_ENTRY)TAB3 INNER JOIN (SELECT ENTRY_ID FROM(SELECT ENTRY_ID, DOCUMENT_ID FROM JERUSALEM.ENTRIES)TAB1 INNER JOIN (SELECT DOCUMENT_ID FROM JERUSALEM.DOCUMENTS WHERE AUTHOR_ID = "
                                                + val
                                                + ")TAB2 ON TAB1.DOCUMENT_ID = TAB2.DOCUMENT_ID)TAB4 ON TAB3.ENTRY_ID=TAB4.ENTRY_ID)TAB6 ON TAB5.PLACE_ID=TAB6.PLACE_ID";
                        }
                        if (modelIDName.equalsIgnoreCase("DOCUMENT_ID")) {
                                sql = "SELECT NUMBER, LOCATION_EASTING, LOCATION_NORTHING, SIMPLE_FEATURE FROM (SELECT LOCATION_EASTING, LOCATION_NORTHING, SIMPLE_FEATURE, PLACE_ID FROM JERUSALEM.PLACES)TAB3 INNER JOIN (SELECT PLACE_ID, NUMBER FROM (SELECT PLACE_ID, ENTRY_ID, NUMBER FROM JERUSALEM.TOPOS_IN_ENTRY)TAB1 INNER JOIN (SELECT ENTRY_ID FROM JERUSALEM.ENTRIES WHERE DOCUMENT_ID = "
                                                + val
                                                + ")TAB2 ON TAB1.ENTRY_ID = TAB2.ENTRY_ID)TAB4 ON TAB3.PLACE_ID=TAB4.PLACE_ID ORDER BY NUMBER";
                        }
                        if (modelIDName.equalsIgnoreCase("ENTRY_ID")) {
                                sql = "SELECT LOCATION_EASTING, LOCATION_NORTHING, SIMPLE_FEATURE FROM (SELECT PLACE_ID FROM JERUSALEM.TOPOS_IN_ENTRY WHERE ENTRY_ID = "
                                                + val
                                                + ")TAB1 INNER JOIN (SELECT PLACE_ID, LOCATION_EASTING, LOCATION_NORTHING,  SIMPLE_FEATURE FROM JERUSALEM.PLACES)TAB2 ON TAB1.PLACE_ID=TAB2.PLACE_ID";
                        }
                        if (modelIDName.equalsIgnoreCase("PLACE_ID")) {
                                sql = "SELECT LOCATION_EASTING, LOCATION_NORTHING, SIMPLE_FEATURE FROM JERUSALEM.PLACES WHERE PLACE_ID = "
                                                + val;
                        }
                        if (modelIDName.equalsIgnoreCase("TOPOS_ID")) {
                                sql = "SELECT LOCATION_EASTING, LOCATION_NORTHING, SIMPLE_FEATURE FROM(SELECT LOCATION_EASTING, LOCATION_NORTHING, SIMPLE_FEATURE, PLACE_ID FROM JERUSALEM.PLACES)TAB1 INNER JOIN (SELECT PLACE_ID FROM JERUSALEM.PLACETOPOS WHERE TOPOS_ID = "
                                                + val
                                                + ")TAB2 ON TAB1.PLACE_ID=TAB2.PLACE_ID";
                        }
                        if (modelIDName.equalsIgnoreCase("TOPOS_IN_ENTRY_ID")) {
                                sql = "SELECT  LOCATION_EASTING, LOCATION_NORTHING, SIMPLE_FEATURE FROM (SELECT PLACE_ID FROM JERUSALEM.TOPOS_IN_ENTRY WHERE TOPOS_IN_ENTRY_ID = "
                                                + val
                                                + ")TAB1 INNER JOIN (SELECT PLACE_ID, LOCATION_EASTING, LOCATION_NORTHING,  SIMPLE_FEATURE FROM JERUSALEM.PLACES)TAB2 ON TAB1.PLACE_ID=TAB2.PLACE_ID";
                        }
                }

                final List<Object> pathObjLi = readDB(sql);
                final JerGeoObserverMsg geoMsg = new JerGeoObserverMsg();
                final GeometryFactory gf = new GeometryFactory();

                for (final Object o : pathObjLi) {
                        final List<Object> objLi = (List<Object>) o;

                        if (objLi.size() == 2 && objLi.get(0) != null
                                        && objLi.get(1) != null) {
                                final Point p = gf.createPoint(new Coordinate(
                                                (double) objLi.get(0),
                                                (double) objLi.get(1)));
                                final JerPlace place = new JerPlace(p);
                                geoMsg.addPlaceToLi(place);
                        }

                        if (modelIDName.equalsIgnoreCase("DOCUMENT_ID")
                                        && val != -1 && objLi.size() == 3
                                        && objLi.get(1) != null
                                        && objLi.get(2) != null) {
                                final Point p = gf.createPoint(new Coordinate(
                                                (double) objLi.get(1),
                                                (double) objLi.get(2)));
                                final JerPlace place = new JerPlace(p);
                                if (objLi.get(0) != null) {
                                        place.setNumber((int) objLi.get(0));
                                }
                                geoMsg.addPlaceToLi(place);
                        }

                        if (objLi.size() == 3 && objLi.get(0) != null
                                        && objLi.get(1) != null) {
                                final Point p = gf.createPoint(new Coordinate(
                                                (double) objLi.get(0),
                                                (double) objLi.get(1)));
                                final JerPlace place = new JerPlace(p);
                                if (objLi.get(2) != null) {
                                        final WKTReader wktr = new WKTReader(gf);
                                        final Geometry additional = wktr
                                                        .read((String) objLi
                                                                        .get(2));
                                        place.setAdditionalInstances(additional);
                                }
                                geoMsg.addPlaceToLi(place);
                        }

                        if (objLi.size() == 4 && objLi.get(1) != null
                                        && objLi.get(2) != null) {
                                final Point p = gf.createPoint(new Coordinate(
                                                (double) objLi.get(1),
                                                (double) objLi.get(2)));
                                final JerPlace place = new JerPlace(p);
                                if (objLi.get(0) != null) {
                                        place.setNumber((int) objLi.get(0));
                                }
                                if (objLi.get(3) != null) {
                                        final WKTReader wktr = new WKTReader(gf);
                                        final Geometry additional = wktr
                                                        .read((String) objLi
                                                                        .get(3));
                                        place.setAdditionalInstances(additional);
                                }
                                geoMsg.addPlaceToLi(place);
                        }
                }
                informAboutCoordinates(geoMsg);
        }

        private void connectToDBAndEnsureAllTablesExist() throws IOException,
                        SQLException {
                conn = DriverManager.getConnection("jdbc:derby:" + dbLocation
                                + ";create=true");
                LOGGER.info("connected to DB # DB-LOCATION: " + dbLocation
                                + " # SCHEMA-NAME: " + schemaName);
                final DatabaseMetaData dbm = conn.getMetaData();
                int value = 0;
                try (ResultSet rs = dbm.getTables(null, "JERUSALEM", "%", null)) {
                        while (rs.next()) {
                                for (final String str : tablesList) {
                                        if (str.toUpperCase().equals(
                                                        rs.getString(3))) {
                                                value++;
                                        }
                                }
                        }
                }
                if (value != tablesList.size()) {
                        createTables(conn, parseSQLFile(sqlScriptLocation));
                }
                new DBBackupRunner(this).start();

        }

        @SuppressWarnings("rawtypes")
        private Class convert(final int str) {
                Class b = Object.class;
                if (str == Types.INTEGER) {
                        b = Integer.class;
                }
                if (str == Types.VARCHAR || str == Types.LONGVARCHAR) {
                        b = String.class;
                }
                if (str == Types.DOUBLE) {
                        b = Double.class;
                }
                return b;
        }

        private void createTables(final Connection connection,
                        final List<String> stmtList) throws SQLException {
                connection.setAutoCommit(false);
                try (Statement stmt = connection.createStatement()) {
                        for (final String str : stmtList) {
                                stmt.execute(str);
                        }
                        connection.commit();
                        LOGGER.info("schema and tables created");
                } catch (final SQLException e) {
                        connection.rollback();
                        connection.setAutoCommit(true);
                        throw e;
                } finally {
                        connection.setAutoCommit(true);
                }
        }

        public void delete(final JerModel jerModel) {
                writeDB(jerModel, false, false, true);
        }

        public void exportAllData(final String location) {
                final java.text.SimpleDateFormat todaysDate = new java.text.SimpleDateFormat(
                                "yyyy-MM-dd");
                final String backupFile = location
                                + "_"
                                + todaysDate.format((java.util.Calendar
                                                .getInstance()).getTime());

                try (CallableStatement cs = conn
                                .prepareCall("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)")) {

                        cs.setString(1, schemaName);
                        cs.setString(2, "AUTHORS");
                        cs.setString(3, backupFile + "_TABLE_AUTHORS.csv");
                        cs.setString(4, ",");
                        cs.setString(5, null);
                        cs.setString(6, "UTF-8");
                        cs.execute();

                        cs.setString(2, "DOCUMENTS");
                        cs.setString(3, backupFile + "_TABLE_DOCUMENTS.csv");
                        cs.execute();

                        cs.setString(2, "ENTRIES");
                        cs.setString(3, backupFile + "_TABLE_ENTRIES.csv");
                        cs.execute();

                        cs.setString(2, "TOPOS_IN_ENTRY");
                        cs.setString(3, backupFile
                                        + "_TABLE_TOPOS_IN_ENTRY.csv");
                        cs.execute();

                        cs.setString(2, "TOPOI");
                        cs.setString(3, backupFile + "_TABLE_TOPOI.csv");
                        cs.execute();

                        cs.setString(2, "PLACES");
                        cs.setString(3, backupFile + "_TABLE_PLACES.csv");
                        cs.execute();

                        cs.setString(2, "PLACETOPOS");
                        cs.setString(3, backupFile + "_TABLE_PLACETOPOS.csv");
                        cs.execute();

                        getJerInfoMsgHandler()
                                        .showMsg(JerResourceBundleAccessor
                                                        .get("successful_dataexport"));

                } catch (final SQLException e) {
                        getJerInfoMsgHandler().showMsg(e.getLocalizedMessage());
                }
        }

        public List<String> fetchColumnNames(final JerModel jm)
                        throws SQLException {
                final List<String> strList = new ArrayList<>();
                try (PreparedStatement stmt = conn.prepareStatement(jm
                                .getSqlAllData());
                                ResultSet rs = stmt.executeQuery()) {
                        final ResultSetMetaData rsmd = rs.getMetaData();
                        final int columnCount = rsmd.getColumnCount();
                        for (int i = 1; i <= columnCount; i++) {
                                final String str = rsmd.getColumnName(i);
                                strList.add(str);
                        }
                }
                return strList;
        }

        @SuppressWarnings("unchecked")
        public JerPlace getAdditionalInstancesForSpecificPlace(
                        final int selectedID2) throws ParseException {
                final GeometryFactory gf = new GeometryFactory();
                final JerGeoObserverMsg geoMsg = new JerGeoObserverMsg();
                final List<Object> pathObjLi = readDB(selectedModel
                                .getSqlAdditionalInstancesForSpecificPlace(selectedID2));

                for (final Object o : pathObjLi) {
                        final List<Object> objLi = (List<Object>) o;

                        if (objLi.size() == 2 && objLi.get(0) != null
                                        && objLi.get(1) != null) {
                                final Point p = gf.createPoint(new Coordinate(
                                                (double) objLi.get(0),
                                                (double) objLi.get(1)));
                                final JerPlace place = new JerPlace(p);
                                geoMsg.addPlaceToLi(place);
                        }

                        if (objLi.size() == 3 && objLi.get(0) != null
                                        && objLi.get(1) != null) {
                                final Point p = gf.createPoint(new Coordinate(
                                                (double) objLi.get(0),
                                                (double) objLi.get(1)));
                                final JerPlace place = new JerPlace(p);
                                if (objLi.get(2) != null) {
                                        final WKTReader wktr = new WKTReader(gf);
                                        final Geometry additional = wktr
                                                        .read((String) objLi
                                                                        .get(2));
                                        place.setAdditionalInstances(additional);
                                }
                                geoMsg.addPlaceToLi(place);
                        }

                        if (objLi.size() == 4 && objLi.get(1) != null
                                        && objLi.get(2) != null) {
                                final Point p = gf.createPoint(new Coordinate(
                                                (double) objLi.get(1),
                                                (double) objLi.get(2)));
                                final JerPlace place = new JerPlace(p);
                                if (objLi.get(0) != null) {
                                        place.setNumber((int) objLi.get(0));
                                }
                                if (objLi.get(3) != null) {
                                        final WKTReader wktr = new WKTReader(gf);
                                        final Geometry additional = wktr
                                                        .read((String) objLi
                                                                        .get(3));
                                        place.setAdditionalInstances(additional);
                                }
                                geoMsg.addPlaceToLi(place);
                        }
                }
                return geoMsg.getPlaceLi().get(0);
        }

        public List<Object> getAllAuthors() {
                return readDB(JerModel.sqlAllAuthors);
        }

        public List<Object> getAllData(final JerModel jm) {
                return readDB(jm.getSqlAllData());
        }

        @SuppressWarnings("unchecked")
        public HashMap<String, Object> getAllDataForID(final JerModel jm,
                        final int id) {
                final String str = jm.getSqlAllDataForID(id);
                final List<Object> colLi = readDB(str);
                final HashMap<String, Object> hm = new HashMap<>();
                if (colLi.size() == 1) {
                        final List<Object> rowLi = (List<Object>) colLi.get(0);
                        for (int i = 0; i < jm.getDataColumns().length; i++) {
                                hm.put(jm.getDataColumns()[i], rowLi.get(i));
                        }
                }
                return hm;
        }

        public List<Object> getAllDocuments() {
                return readDB(JerModel.sqlAllDocuments);
        }

        public List<Object> getAllEntries() {
                return readDB(JerModel.sqlAllEntries);
        }

        public List<Object> getAllOrigin() {
                return readDB(JerModel.sqlAllDistinctOrigin);
        }

        public List<Object> getAllPlaceForToposID(final JerModel jm,
                        final int id) {
                return readDB(jm.getSqlAllPlacesForToposID(id));
        }

        public List<Object> getAllPlaces() {
                return readDB(JerModel.sqlAllPlaces);
        }

        public List<Object> getAllReligion() {
                return readDB(JerModel.sqlAllDistinctReligion);
        }

        public List<Object> getAllTopoi() {
                return readDB(JerModel.sqlAllTopoi);
        }

        public List<Object> getAllTopoiInEntry() {
                return readDB(JerModel.sqlAllTopoiInEntry);
        }

        public List<Object> getAllToposForPlaceID(final JerModel jm,
                        final int id) {
                return readDB(jm.getSqlAllTopoiForPlaceID(id));
        }

        @SuppressWarnings("unchecked")
        public Set<Integer> getAssociatedIDs(final JerModel jm) {
                final String sql = jm
                                .getSQLStrForIDs(selectedModel, selectedID);
                final List<Object> objLi = readDB(sql);
                final Set<Integer> associatedIDs = new HashSet<>();
                for (int i = 0; i < objLi.size(); i++) {
                        final List<Object> valueList = (List<Object>) objLi
                                        .get(i);
                        associatedIDs.add((Integer) valueList.get(0));
                }
                return associatedIDs;
        }

        public Connection getConnection() {
                return conn;
        }

        public List<Object> getDataForAnalysis(
                        final JerAnalysisDialog jerAnalysisDialog) {
                return readDB(jerAnalysisDialog.getSqlDataForAnalysis());
        }

        public JerInfoMsgHandler getJerInfoMsgHandler() {
                return jerInfoMsgHandler;
        }

        @SuppressWarnings("unchecked")
        public int getMaxIDValueForTable(final JerModel jerModel) {
                final List<Object> rowLi = (List<Object>) readDB(
                                jerModel.getSqlMaxID()).get(0);
                return (int) rowLi.get(0);
        }

        // @SuppressWarnings("unchecked")
        // public HashMap<String, Object> getMinBeginValueForTable() {
        // final String str1 =
        // "(select min(begin_year) as a from jerusalem.authors) union (select min(begin_year) as a from jerusalem.documents) union(select min(begin_year) as a from jerusalem.places) union (select min(begin_year) as a from jerusalem.placetopos) order by a asc";
        // final String str2 =
        // "(select max(end_year) as b from jerusalem.authors) union (select max(end_year) as b from jerusalem.documents) union(select max(end_year) as b from jerusalem.places) union (select max(end_year) as b from jerusalem.placetopos) order by b desc";
        // final List<Object> colLi1 = readDB(str1);
        // final List<Object> colLi2 = readDB(str2);
        // final HashMap<String, Object> hm = new HashMap<>();
        // if (!colLi1.isEmpty()) {
        // final List<Object> rowLi1 = (List<Object>) colLi1
        // .get(0);
        // hm.put("MIN_BEGIN_YEAR", rowLi1.get(0));
        // }
        // if (!colLi2.isEmpty()) {
        // final List<Object> rowLi2 = (List<Object>) colLi2
        // .get(0);
        // hm.put("MAX_END_YEAR", rowLi2.get(0));
        // }
        // return hm;
        // }

        public Properties getProps() {
                return properties;
        }

        public String getSchemaName() {
                return schemaName;
        }

        @Override
        public int getSelectedID() {
                return selectedID;
        }

        @Override
        public JerModel getSelectedModel() {
                return selectedModel;
        }

        public String getSRID() {
                return SRID;
        }

        @SuppressWarnings("unchecked")
        public HashMap<String, Object> getTimeDataForID(final JerModel jm,
                        final int id) {
                final String str = jm.getSqlTimeDataForAuthorID(id);
                final List<Object> colLi = readDB(str);
                final HashMap<String, Object> hm = new HashMap<>();
                if (colLi.size() == 1) {
                        final List<Object> rowLi = (List<Object>) colLi.get(0);
                        hm.put("BEGIN_YEAR", rowLi.get(0));
                        hm.put("END_YEAR", rowLi.get(1));
                }
                return hm;
        }

        @SuppressWarnings("rawtypes")
        public Class[] getTypes(final JerModel jm) throws SQLException {
                final List<Class> classTypeList = new ArrayList<>();
                try (PreparedStatement stmt = conn.prepareStatement(jm
                                .getSqlAllData());
                                ResultSet rs = stmt.executeQuery()) {
                        final ResultSetMetaData rsmd = rs.getMetaData();
                        final int columnCount = rsmd.getColumnCount();
                        for (int i = 1; i <= columnCount; i++) {
                                final int str = rsmd.getColumnType(i);
                                classTypeList.add(convert(str));
                        }
                }
                return classTypeList.toArray(new Class[0]);
        }

        public void informAboutCoordinates(final JerGeoObserverMsg geoMsg) {
                setChanged();
                notifyObservers(geoMsg);
        }

        public void insert(final JerModel jerModel) {
                writeDB(jerModel, true, false, false);
        }

        public void makeUpdate(final JerModel jerModel) {
                writeDB(jerModel, false, true, false);
        }

        public void openTab(final JerModel jerModel, final int initiatingVal,
                        final String idNameModelDestTab, final int idDestTab,
                        final String nameAdditionalFieldInfo,
                        final int additionalIDtoBeSet) {
                setChanged();
                notifyObservers(new JerTabObserverMsg(jerModel, initiatingVal,
                                idNameModelDestTab, idDestTab,
                                nameAdditionalFieldInfo, additionalIDtoBeSet));
        }

        private List<String> parseSQLFile(final String fileLocation)
                        throws IOException {
                List<String> stmtList = new ArrayList<>();
                final StringBuffer sb = new StringBuffer();
                try (BufferedReader in = Files.newBufferedReader(
                                Paths.get(fileLocation),
                                Charset.defaultCharset())) {
                        String str = "";
                        while ((str = in.readLine()) != null) {
                                sb.append(str);
                        }
                }
                final String entireSQLFile = sb.toString();
                stmtList = Arrays.asList(entireSQLFile
                                .split(sqlScriptDelimiter));
                return stmtList;
        }

        private List<Object> readDB(final String sql) {
                final List<Object> columnList = new ArrayList<>();
                try {
                        conn.setAutoCommit(false);
                        try (PreparedStatement stmt = conn
                                        .prepareStatement(sql);
                                        ResultSet rs = stmt.executeQuery()) {
                                while (rs.next()) {
                                        final int columnCount = rs
                                                        .getMetaData()
                                                        .getColumnCount();
                                        final List<Object> rowList = new ArrayList<>(
                                                        columnCount);
                                        for (int i = 1; i <= columnCount; i++) {
                                                rowList.add(rs.getObject(i));
                                        }
                                        columnList.add(rowList);
                                }
                                conn.commit();
                        } catch (final SQLException e) {
                                conn.rollback();
                                throw e;
                        } finally {
                                conn.setAutoCommit(true);
                        }
                } catch (final SQLException e1) {
                        jerInfoMsgHandler.showMsg(e1.getLocalizedMessage());
                }
                return columnList;
        }

        public void reloadMapWithAllFeatures() {
                if (selectedModel != null) {
                        setModelAndID(selectedModel, -1);
                }
        }

        private void setHistObj(final JerHistObj jho) {
                if (jho == null) {
                        jerInfoMsgHandler.showMsg(JerResourceBundleAccessor
                                        .get("mainModel_noHistObj"));
                } else {
                        setModelAndIDAndNotify(jho.getModel(), jho.getId());
                }
        }

        public void setModelAndIDAndNotify(final JerModel jm, final int selID) {
                selectedModel = jm;
                selectedID = selID;
                setChanged();
                notifyObservers(new JerObserverMsg(JerMsgType.SELECTION));
                jerInfoMsgHandler.setMsgBoxToNormal();
        }

        protected void setModelAndID(final JerModel jm, final int selID) {
                if (selectedModel != null) {
                        if (jm.equals(selectedModel)
                                        && selID == this.selectedID) {
                                return;
                        }
                        if (!jm.equals(selectedModel) && selID == -1) {
                                return;
                        }
                }
                jerHistRef.add(new JerHistObj(jm, selID));
                setModelAndIDAndNotify(jm, selID);
        }

        public void setNextModelID() {
                setHistObj(jerHistRef.getNextHistObj());
        }

        public void setPlaceModel(final JerPlaceModel pm2) {
                pm = pm2;
        }

        @SuppressWarnings("unchecked")
        public void setPlaceModelAndID(final Coordinate coordinate) {
                final List<Object> colLi = readDB(selectedModel
                                .getSqlSelectPlaceByCoordinate(coordinate));
                int val = -1;
                if (colLi.size() == 1 && colLi.get(0) != null) {
                        final List<Object> rowLi = (List<Object>) colLi.get(0);
                        if (rowLi.size() == 1 && rowLi.get(0) != null) {
                                val = (int) rowLi.get(0);
                        }
                }
                if (val != -1 && pm != null) {
                        setModelAndID(pm, val);
                }
        }

        public void setPreviousModelID() {
                setHistObj(jerHistRef.getPrevHistObj());
        }

        private void setSystemAndDBValues() {
                System.setProperty("file.encoding",
                                properties.getProperty("jvm_encoding"));
                schemaName = properties.getProperty("DB_schema");
                sqlScriptDelimiter = properties
                                .getProperty("DB_SQL_file_delimiter");
                dbLocation = properties.getProperty("DB_absolute_path");
                sqlScriptLocation = properties
                                .getProperty("DB_SQL_file_absolute_path");
                SRID = properties.getProperty("srid");

                if (!Main.DEBUG) {
                        System.setProperty(
                                        properties.getProperty("derby_system_home"),
                                        properties.getProperty("derby_system_home_path"));
                        System.setProperty(
                                        properties.getProperty("derby_infolog_append"),
                                        properties.getProperty("derby_infolog_append_value"));
                        System.setProperty(
                                        properties.getProperty("derby_stream_error_file"),
                                        properties.getProperty("derby_stream_error_file_path"));
                        System.setProperty(
                                        properties.getProperty("derby_locks_waitTimeout"),
                                        properties.getProperty("derby_locks_waitTimeout_value"));
                        System.setProperty(
                                        properties.getProperty("derby_locks_deadlockTimeout"),
                                        properties.getProperty("derby_locks_deadlockTimeout_value"));
                        LOGGER.info("SYSTEM_PROPERTIES VIA SYSTEM.GETPROPERTIES().TOSTRING()  "
                                        + System.getProperties().toString());
                }
        }

        // list of tables to be used in program; has to be edited manually !
        private void setUpListOfTables() {
                tablesList = new ArrayList<>();
                tablesList.add(properties.getProperty("DB_table_authors"));
                tablesList.add(properties.getProperty("DB_table_documents"));
                tablesList.add(properties.getProperty("DB_table_entries"));
                tablesList.add(properties.getProperty("DB_table_topoi"));
                tablesList.add(properties
                                .getProperty("DB_table_topos_in_entry"));
                tablesList.add(properties.getProperty("DB_table_places"));
                tablesList.add(properties.getProperty("DB_table_placetopos"));
        }

        public void shutdownDB() throws SQLException {
                DriverManager.getConnection("jdbc:derby:;shutdown=true");
        }

        private void writeDB(final JerModel jerModel, final boolean isInsert,
                        final boolean isUpdate, final boolean isDelete) {
                try {
                        try {
                                conn.setAutoCommit(false);
                                if (isInsert) {
                                        try (PreparedStatement psJm = jerModel
                                                        .getPreparer()
                                                        .createInsertStmt(conn)) {
                                                psJm.executeUpdate();
                                        }
                                        conn.commit();
                                        for (final PreparedStatement stmt : jerModel
                                                        .getPreparer()
                                                        .getPrepStmtsFromMultiFields(
                                                                        conn,
                                                                        getMaxIDValueForTable(jerModel))) {
                                                try (PreparedStatement psJm = stmt) {
                                                        psJm.executeUpdate();
                                                }
                                        }
                                } else if (isUpdate) {
                                        for (final PreparedStatement stmt : jerModel
                                                        .getPreparer()
                                                        .createUpdateStmts(conn)) {
                                                try (PreparedStatement psJm = stmt) {
                                                        psJm.executeUpdate();
                                                }
                                        }
                                } else if (isDelete) {
                                        for (final PreparedStatement stmt : jerModel
                                                        .getPreparer()
                                                        .createDelStmts(conn)) {
                                                try (PreparedStatement psJm = stmt) {
                                                        psJm.executeUpdate();
                                                }
                                        }
                                }
                                conn.commit();
                                setChanged();
                                notifyObservers(new JerObserverMsg(
                                                JerMsgType.DB_UPDATE));
                        } catch (SQLException | NumberFormatException nfe) {
                                conn.rollback();
                                conn.setAutoCommit(true);
                                throw nfe;
                        } finally {
                                conn.setAutoCommit(true);
                        }
                } catch (final SQLException e1) {
                        LOGGER.severe(e1.getMessage());
                        setModelAndID(jerModel, -1);
                        jerInfoMsgHandler.showMsg("@" + jerModel.getIdName()
                                        + ": " + e1.getLocalizedMessage());
                }
        }

}
