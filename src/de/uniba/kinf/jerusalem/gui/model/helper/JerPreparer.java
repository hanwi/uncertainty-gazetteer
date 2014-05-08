package de.uniba.kinf.jerusalem.gui.model.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.JerFieldInfoIF;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.JerMultiFieldInfoIF;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.JerMultiItem;

/**
 * Provides delete, insert, update statements for database interaction..
 * 
 * @author Hanno Wierichs
 * 
 */
/**
 * @author pc
 * 
 */
public class JerPreparer {

        private final List<JerFieldInfoIF> fieldLi;
        private final String idName;
        private final List<JerMultiFieldInfoIF> multiLi;
        private final String sqlTable;

        public JerPreparer(final String sqlTabl, final String idNam) {
                sqlTable = sqlTabl;
                idName = idNam;
                fieldLi = new ArrayList<>();
                multiLi = new ArrayList<>();
        }

        public void add(final JerFieldInfoIF fi) {
                fieldLi.add(fi);
                multiLi.clear();
                for (int i = 0; i < fieldLi.size(); i++) {
                        if (fieldLi.get(i) instanceof JerMultiFieldInfoIF) {
                                final JerMultiFieldInfoIF jmf = (JerMultiFieldInfoIF) fieldLi
                                                .get(i);
                                multiLi.add(jmf);
                        }
                }
        }

        public List<PreparedStatement> createDelStmts(final Connection conn)
                        throws SQLException {
                final List<PreparedStatement> li = new ArrayList<PreparedStatement>();
                final int val = getCurrentIDVal();

                if (!multiLi.isEmpty()) {
                        li.add(getDelStmtMulti(conn, val));
                }

                final String sqlDelete = "DELETE FROM " + sqlTable + " WHERE "
                                + idName + " = ?";
                final PreparedStatement psJm = conn.prepareStatement(sqlDelete);
                psJm.setInt(1, val);
                li.add(psJm);
                return li;
        }

        public PreparedStatement createInsertStmt(final Connection conn)
                        throws SQLException {
                final String sqlInsert = "INSERT INTO " + sqlTable + "("
                                + getColumnTitles() + ") VALUES ("
                                + getQuestionMarks() + ")";
                final PreparedStatement psJm = conn.prepareStatement(sqlInsert);
                setJerModelPrepStmt(psJm);
                return psJm;
        }

        public List<PreparedStatement> createUpdateStmts(final Connection conn)
                        throws SQLException {
                final List<PreparedStatement> li = new ArrayList<PreparedStatement>();
                final int val = getCurrentIDVal();

                final String sqlUpdate = "UPDATE " + sqlTable + " SET "
                                + getColumnNamesAndQuestionMarks() + " WHERE "
                                + idName + " = ?";
                final PreparedStatement psJm = conn.prepareStatement(sqlUpdate);
                final int number = setJerModelPrepStmt(psJm);
                final int positionInPrepStmt = fieldLi.size() - number;
                psJm.setInt(positionInPrepStmt, val);
                li.add(psJm);
                li.addAll(getPrepStmtsFromMultiFields(conn, val));
                return li;
        }

        private String getColumnNamesAndQuestionMarks() {
                final StringBuffer colNamesQMs = new StringBuffer();
                for (int i = 0; i < fieldLi.size(); i++) {
                        final JerFieldInfoIF jfi = fieldLi.get(i);
                        final String columnTitle = jfi.getColumnTitle();
                        if (!columnTitle.equals(idName)
                                        && !(jfi instanceof JerMultiFieldInfoIF)) {
                                colNamesQMs.append("," + columnTitle + "= ?");
                        }
                }
                colNamesQMs.deleteCharAt(0);
                return colNamesQMs.toString();
        }

        private String getColumnTitles() {
                final StringBuffer colTitles = new StringBuffer();
                for (int i = 0; i < fieldLi.size(); i++) {
                        final JerFieldInfoIF jfi = fieldLi.get(i);
                        final String columnTitle = jfi.getColumnTitle();
                        if (!columnTitle.equals(idName)
                                        && !(jfi instanceof JerMultiFieldInfoIF)) {
                                colTitles.append("," + columnTitle);
                        }
                }
                colTitles.deleteCharAt(0);
                return colTitles.toString();
        }

        /**
         * @return -1 if no id.
         */
        public int getCurrentIDVal() {
                int id = -1;
                for (int i = 0; i < fieldLi.size(); i++) {
                        if (fieldLi.get(i).getColumnTitle().equals(idName)) {
                                id = fieldLi.get(i).getID();
                                break;
                        }
                }
                return id;
        }

        private PreparedStatement getDelStmtMulti(final Connection conn,
                        final int val) throws SQLException {
                final PreparedStatement psJ = conn
                                .prepareStatement("DELETE FROM JERUSALEM.PLACETOPOS WHERE "
                                                + idName + " = ?");
                psJ.setInt(1, val);
                return psJ;
        }

        public List<JerFieldInfoIF> getFieldLi() {
                return fieldLi;
        }

        private List<JerMultiItem> getMultiItems() throws SQLException {
                final List<JerMultiItem> multiItems = new ArrayList<>();
                for (final JerMultiFieldInfoIF jmf : multiLi) {
                        multiItems.addAll(jmf.getAllMultiItems());
                }
                return multiItems;
        }

        public List<PreparedStatement> getPrepStmtsFromMultiFields(
                        final Connection conn, final int val)
                        throws SQLException {
                final List<PreparedStatement> li = new ArrayList<>();
                if (!multiLi.isEmpty()) {
                        li.add(getDelStmtMulti(conn, val));
                        for (final JerMultiItem jmi : getMultiItems()) {
                                final PreparedStatement psJm = conn
                                                .prepareStatement("INSERT INTO JERUSALEM.PLACETOPOS (PLACE_ID, TOPOS_ID, BEGIN_YEAR, END_YEAR) VALUES (?,?,?,?)");
                                if (idName.equals("PLACE_ID")) {
                                        psJm.setInt(1, val);
                                        psJm.setInt(2, jmi
                                                        .getIDSelectedJerItem());
                                } else {
                                        psJm.setInt(1, jmi
                                                        .getIDSelectedJerItem());
                                        psJm.setInt(2, val);
                                }
                                if (jmi.getBeginF().getText().isEmpty()) {
                                        psJm.setNull(3, Types.INTEGER);
                                } else {
                                        psJm.setInt(3, Integer.parseInt(jmi
                                                        .getBeginF().getText()));
                                }
                                if (jmi.getEndF().getText().isEmpty()) {
                                        psJm.setNull(4, Types.INTEGER);
                                } else {
                                        psJm.setInt(4, Integer.parseInt(jmi
                                                        .getEndF().getText()));
                                }
                                li.add(psJm);
                        }
                }
                return li;
        }

        private String getQuestionMarks() {
                final StringBuffer questionMarks = new StringBuffer();
                for (int i = 0; i < fieldLi.size(); i++) {
                        final JerFieldInfoIF jfi = fieldLi.get(i);
                        final String columnTitle = jfi.getColumnTitle();
                        if (!columnTitle.equals(idName)
                                        && !(jfi instanceof JerMultiFieldInfoIF)) {
                                questionMarks.append(",?");
                        }
                }
                questionMarks.deleteCharAt(0);
                return questionMarks.toString();
        }

        private int setJerModelPrepStmt(final PreparedStatement ps)
                        throws SQLException {
                // indicates number of multifieldinfoif-items in jerfieldinfoif
                // list
                int number = 0;
                for (int i = 0; i < fieldLi.size(); i++) {
                        if (!fieldLi.get(i).getColumnTitle().equals(idName)
                                        && !(fieldLi.get(i) instanceof JerMultiFieldInfoIF)) {
                                fieldLi.get(i).setPrpStmt(ps, i);
                        }
                        if (fieldLi.get(i) instanceof JerMultiFieldInfoIF) {
                                number += 1;
                        }
                }
                return number;
        }

}
