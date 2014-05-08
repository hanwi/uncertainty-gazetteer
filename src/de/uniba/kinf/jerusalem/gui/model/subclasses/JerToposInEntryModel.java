package de.uniba.kinf.jerusalem.gui.model.subclasses;

import java.sql.SQLException;
import java.util.List;

import de.uniba.kinf.jerusalem.gui.model.JerMainModel;
import de.uniba.kinf.jerusalem.gui.model.JerModel;

/**
 * Model for topos in entry entity.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerToposInEntryModel extends JerModel {

        public JerToposInEntryModel(final JerMainModel mainModel1,
                        final String name) throws SQLException {
                super(mainModel1, name, "TOPOS_IN_ENTRY_ID");
        }

        @Override
        public List<Object> getDefaultAll() {
                return getAllTopoiInEntry();
        }

        @Override
        protected String getSQLStrForIDs(final JerModel jm, final int selectedID) {
                // default select all ids
                String sql = "SELECT TOPOS_IN_ENTRY_ID FROM JERUSALEM.TOPOS_IN_ENTRY";
                if (selectedID != -1) {
                        if (jm instanceof JerAuthorModel) {
                                sql = "SELECT TOPOS_IN_ENTRY_ID FROM (SELECT TOPOS_IN_ENTRY_ID, ENTRY_ID FROM JERUSALEM.TOPOS_IN_ENTRY)TAB1 "
                                                + "INNER JOIN "
                                                + "(SELECT ENTRY_ID FROM (SELECT ENTRY_ID, DOCUMENT_ID FROM JERUSALEM.ENTRIES)TAB2 "
                                                + "INNER JOIN "
                                                + "(SELECT DOCUMENT_ID FROM JERUSALEM.DOCUMENTS WHERE AUTHOR_ID = "
                                                + selectedID
                                                + ")TAB3 ON TAB2.DOCUMENT_ID=TAB3.DOCUMENT_ID)TAB4 ON TAB1.ENTRY_ID = TAB4.ENTRY_ID";

                        }
                        if (jm instanceof JerDocumentModel) {
                                sql = "SELECT TOPOS_IN_ENTRY_ID FROM (SELECT TOPOS_IN_ENTRY_ID, ENTRY_ID FROM JERUSALEM.TOPOS_IN_ENTRY)TAB1 INNER JOIN (SELECT ENTRY_ID FROM JERUSALEM.ENTRIES WHERE DOCUMENT_ID = "
                                                + selectedID
                                                + ")TAB2 ON TAB1.ENTRY_ID = TAB2.ENTRY_ID";
                        }
                        if (jm instanceof JerEntryModel) {
                                sql = "SELECT TOPOS_IN_ENTRY_ID FROM JERUSALEM.TOPOS_IN_ENTRY WHERE ENTRY_ID  = "
                                                + selectedID;
                        }
                        if (jm instanceof JerPlaceModel) {
                                sql = "SELECT TOPOS_IN_ENTRY_ID FROM JERUSALEM.TOPOS_IN_ENTRY WHERE PLACE_ID = "
                                                + selectedID;
                        }
                        if (jm instanceof JerToposModel) {
                                sql = "SELECT TOPOS_IN_ENTRY_ID FROM JERUSALEM.TOPOS_IN_ENTRY WHERE TOPOS_ID = "
                                                + selectedID;
                        }
                }
                return sql;
        }

}
