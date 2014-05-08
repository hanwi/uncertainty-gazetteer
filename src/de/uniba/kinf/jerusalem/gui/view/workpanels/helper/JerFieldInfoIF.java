package de.uniba.kinf.jerusalem.gui.view.workpanels.helper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Specifies key methods of every field element used in JerWorkpanels.
 * 
 * @author Hanno Wierichs
 * 
 */
public interface JerFieldInfoIF {

        void clear();

        void displayInfo(HashMap<String, Object> values);

        void enableFunctionality(boolean b);

        void focusOnField();

        String getColumnTitle();

        int getID();

        boolean isFieldValid();

        void setID(int nVal);

        void setIsValid(boolean b);

        PreparedStatement setPrpStmt(PreparedStatement ps, int pos)
                        throws SQLException;

        boolean stringIsValidEntry(String str);

}
