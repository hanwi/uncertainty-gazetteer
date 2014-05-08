package de.uniba.kinf.jerusalem.gui.model.subclasses;

import java.sql.SQLException;
import java.util.List;

import de.uniba.kinf.jerusalem.gui.model.JerMainModel;
import de.uniba.kinf.jerusalem.gui.model.JerModel;

/**
 * Model for entry entity.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerEntryModel extends JerModel {

        public JerEntryModel(final JerMainModel mainModel1,
                        final String tableName1) throws SQLException {
                super(mainModel1, tableName1, "ENTRY_ID");
        }

        @Override
        public List<Object> getDefaultAll() {
                return getAllEntries();
        }

        @Override
        public String getSQLStrForIDs(final JerModel jm, final int selectedID) {
                // default select all ids
                String sql = "SELECT ENTRY_ID FROM JERUSALEM.ENTRIES";
                if (selectedID != -1) {
                        if (jm instanceof JerAuthorModel) {
                                sql = "SELECT ENTRY_ID FROM (SELECT ENTRY_ID, DOCUMENT_ID FROM JERUSALEM.ENTRIES)TAB1 INNER JOIN (SELECT DOCUMENT_ID FROM JERUSALEM.DOCUMENTS WHERE AUTHOR_ID = "
                                                + selectedID
                                                + ")TAB2 ON TAB1.DOCUMENT_ID=TAB2.DOCUMENT_ID";
                        }
                        if (jm instanceof JerDocumentModel) {
                                sql = "SELECT ENTRY_ID FROM JERUSALEM.ENTRIES WHERE DOCUMENT_ID = "
                                                + selectedID;
                        }
                        if (jm instanceof JerPlaceModel) {
                                sql = "SELECT ENTRY_ID FROM JERUSALEM.TOPOS_IN_ENTRY WHERE PLACE_ID = "
                                                + selectedID;
                        }
                        if (jm instanceof JerToposModel) {
                                sql = "SELECT ENTRY_ID FROM JERUSALEM.TOPOS_IN_ENTRY WHERE TOPOS_ID = "
                                                + selectedID;
                        }
                        if (jm instanceof JerToposInEntryModel) {
                                sql = "SELECT ENTRY_ID FROM JERUSALEM.TOPOS_IN_ENTRY WHERE TOPOS_IN_ENTRY_ID = "
                                                + selectedID;
                        }
                }
                return sql;
        }
}
