package de.uniba.kinf.jerusalem.gui.model.subclasses;

import java.sql.SQLException;
import java.util.List;

import de.uniba.kinf.jerusalem.gui.model.JerMainModel;
import de.uniba.kinf.jerusalem.gui.model.JerModel;

/**
 * Model for place entity.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerPlaceModel extends JerModel {

        public JerPlaceModel(final JerMainModel mainModel1,
                        final String tableName1) throws SQLException {
                super(mainModel1, tableName1, "PLACE_ID");
        }

        @Override
        public List<Object> getDefaultAll() {
                return getAllPlaces();
        }

        @Override
        public String getSQLStrForIDs(final JerModel jm, final int selectedID) {
                // default select all ids
                String sql = "SELECT PLACE_ID FROM JERUSALEM.PLACES";
                if (selectedID != -1) {
                        if (jm instanceof JerAuthorModel) {
                                sql = "SELECT PLACE_ID FROM (SELECT PLACE_ID, ENTRY_ID FROM JERUSALEM.TOPOS_IN_ENTRY)TAB3 INNER JOIN (SELECT ENTRY_ID FROM(SELECT ENTRY_ID, DOCUMENT_ID FROM JERUSALEM.ENTRIES)TAB1 INNER JOIN (SELECT DOCUMENT_ID FROM JERUSALEM.DOCUMENTS WHERE AUTHOR_ID = "
                                                + selectedID
                                                + ")TAB2 ON TAB1.DOCUMENT_ID = TAB2.DOCUMENT_ID)TAB4 ON TAB3.ENTRY_ID=TAB4.ENTRY_ID";
                        }
                        if (jm instanceof JerDocumentModel) {
                                sql = "SELECT PLACE_ID FROM (SELECT PLACE_ID, ENTRY_ID FROM JERUSALEM.TOPOS_IN_ENTRY)TAB1 INNER JOIN (SELECT ENTRY_ID FROM JERUSALEM.ENTRIES WHERE DOCUMENT_ID = "
                                                + selectedID
                                                + ")TAB2 ON TAB1.ENTRY_ID = TAB2.ENTRY_ID";
                        }
                        if (jm instanceof JerEntryModel) {
                                sql = "SELECT PLACE_ID FROM JERUSALEM.TOPOS_IN_ENTRY WHERE ENTRY_ID  = "
                                                + selectedID;
                        }
                        if (jm instanceof JerToposModel) {
                                sql = "SELECT PLACE_ID FROM JERUSALEM.PLACETOPOS WHERE TOPOS_ID = "
                                                + selectedID;
                        }
                        if (jm instanceof JerToposInEntryModel) {
                                sql = "SELECT PLACE_ID FROM JERUSALEM.TOPOS_IN_ENTRY WHERE TOPOS_IN_ENTRY_ID = "
                                                + selectedID;
                        }
                }
                return sql;
        }
}
