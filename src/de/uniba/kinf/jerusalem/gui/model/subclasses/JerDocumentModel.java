package de.uniba.kinf.jerusalem.gui.model.subclasses;

import java.sql.SQLException;
import java.util.List;

import de.uniba.kinf.jerusalem.gui.model.JerMainModel;
import de.uniba.kinf.jerusalem.gui.model.JerModel;

/**
 * Model for document entity.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerDocumentModel extends JerModel {
        public JerDocumentModel(final JerMainModel mainModel1,
                        final String tableName1) throws SQLException {
                super(mainModel1, tableName1, "DOCUMENT_ID");
        }

        @Override
        public List<Object> getDefaultAll() {
                return getAllDocuments();
        }

        @Override
        public String getSQLStrForIDs(final JerModel jm, final int selectedID) {
                // default select all ids
                String sql = "SELECT DOCUMENT_ID FROM JERUSALEM.DOCUMENTS";
                if (selectedID != -1) {
                        if (jm instanceof JerAuthorModel) {
                                sql = "SELECT DOCUMENT_ID FROM JERUSALEM.DOCUMENTS WHERE AUTHOR_ID = "
                                                + selectedID;
                        }
                        if (jm instanceof JerEntryModel) {
                                sql = "SELECT DOCUMENT_ID FROM (SELECT DOCUMENT_ID, ENTRY_ID FROM JERUSALEM.ENTRIES)TAB1 INNER JOIN (SELECT ENTRY_ID FROM JERUSALEM.ENTRIES WHERE ENTRY_ID = "
                                                + selectedID
                                                + ")TAB2 ON TAB1.ENTRY_ID=TAB2.ENTRY_ID";
                        }
                        if (jm instanceof JerPlaceModel) {
                                sql = "SELECT DOCUMENT_ID FROM (SELECT DOCUMENT_ID, ENTRY_ID FROM JERUSALEM.ENTRIES)TAB1 INNER JOIN (SELECT ENTRY_ID FROM JERUSALEM.TOPOS_IN_ENTRY WHERE PLACE_ID = "
                                                + selectedID
                                                + ")TAB2 ON TAB1.ENTRY_ID=TAB2.ENTRY_ID";
                        }
                        if (jm instanceof JerToposModel) {
                                sql = "SELECT DOCUMENT_ID FROM (SELECT DOCUMENT_ID, ENTRY_ID FROM JERUSALEM.ENTRIES)TAB1 INNER JOIN (SELECT ENTRY_ID FROM JERUSALEM.TOPOS_IN_ENTRY WHERE TOPOS_ID = "
                                                + selectedID
                                                + ")TAB2 ON TAB1.ENTRY_ID=TAB2.ENTRY_ID";
                        }
                        if (jm instanceof JerToposInEntryModel) {
                                sql = "SELECT DOCUMENT_ID FROM (SELECT DOCUMENT_ID, ENTRY_ID FROM JERUSALEM.ENTRIES)TAB1 INNER JOIN (SELECT ENTRY_ID FROM JERUSALEM.TOPOS_IN_ENTRY WHERE TOPOS_IN_ENTRY_ID = "
                                                + selectedID
                                                + ")TAB2 ON TAB1.ENTRY_ID=TAB2.ENTRY_ID";
                        }
                }
                return sql;
        }

}
