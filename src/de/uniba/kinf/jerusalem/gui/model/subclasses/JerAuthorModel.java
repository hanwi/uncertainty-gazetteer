package de.uniba.kinf.jerusalem.gui.model.subclasses;

import java.sql.SQLException;
import java.util.List;

import de.uniba.kinf.jerusalem.gui.model.JerMainModel;
import de.uniba.kinf.jerusalem.gui.model.JerModel;

/**
 * Model for author entity.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerAuthorModel extends JerModel {

        public JerAuthorModel(final JerMainModel mainModel1,
                        final String tableName1) throws SQLException {
                super(mainModel1, tableName1, "AUTHOR_ID");
        }

        @Override
        public List<Object> getDefaultAll() {
                return getAllAuthors();
        }

        @Override
        public String getSQLStrForIDs(final JerModel jm, final int selectedID) {
                // default select all ids
                String sql = "SELECT AUTHOR_ID FROM JERUSALEM.AUTHORS";
                if (selectedID != -1) {
                        if (jm instanceof JerDocumentModel) {
                                sql = "SELECT AUTHOR_ID FROM JERUSALEM.DOCUMENTS WHERE DOCUMENT_ID = "
                                                + selectedID;
                        }
                        if (jm instanceof JerEntryModel) {
                                sql = "SELECT AUTHOR_ID FROM (SELECT AUTHOR_ID, DOCUMENT_ID FROM JERUSALEM.DOCUMENTS)TAB1 INNER JOIN (SELECT DOCUMENT_ID FROM JERUSALEM.ENTRIES WHERE ENTRY_ID = "
                                                + selectedID
                                                + ")TAB2 ON TAB1.DOCUMENT_ID=TAB2.DOCUMENT_ID";
                        }
                        if (jm instanceof JerPlaceModel) {
                                sql = "SELECT AUTHOR_ID FROM (SELECT AUTHOR_ID, DOCUMENT_ID FROM JERUSALEM.DOCUMENTS)TAB3 INNER JOIN (SELECT DOCUMENT_ID FROM(SELECT ENTRY_ID, DOCUMENT_ID FROM JERUSALEM.ENTRIES)TAB1 INNER JOIN (SELECT ENTRY_ID FROM JERUSALEM.TOPOS_IN_ENTRY WHERE PLACE_ID = "
                                                + selectedID
                                                + ")TAB2 ON TAB1.ENTRY_ID=TAB2.ENTRY_ID)TAB4 ON TAB3.DOCUMENT_ID=TAB4.DOCUMENT_ID";
                        }
                        if (jm instanceof JerToposModel) {
                                sql = "SELECT AUTHOR_ID FROM (SELECT AUTHOR_ID, DOCUMENT_ID FROM JERUSALEM.DOCUMENTS)TAB3 INNER JOIN (SELECT DOCUMENT_ID FROM(SELECT ENTRY_ID, DOCUMENT_ID FROM JERUSALEM.ENTRIES)TAB1 INNER JOIN (SELECT ENTRY_ID FROM JERUSALEM.TOPOS_IN_ENTRY WHERE TOPOS_ID = "
                                                + selectedID
                                                + ")TAB2 ON TAB1.ENTRY_ID=TAB2.ENTRY_ID)TAB4 ON TAB3.DOCUMENT_ID=TAB4.DOCUMENT_ID";
                        }
                        if (jm instanceof JerToposInEntryModel) {
                                sql = "SELECT AUTHOR_ID FROM (SELECT AUTHOR_ID, DOCUMENT_ID FROM JERUSALEM.DOCUMENTS)TAB3 INNER JOIN (SELECT DOCUMENT_ID FROM(SELECT ENTRY_ID, DOCUMENT_ID FROM JERUSALEM.ENTRIES)TAB1 INNER JOIN (SELECT ENTRY_ID FROM JERUSALEM.TOPOS_IN_ENTRY WHERE TOPOS_IN_ENTRY_ID = "
                                                + selectedID
                                                + ")TAB2 ON TAB1.ENTRY_ID=TAB2.ENTRY_ID)TAB4 ON TAB3.DOCUMENT_ID=TAB4.DOCUMENT_ID";
                        }
                }
                return sql;
        }

}
