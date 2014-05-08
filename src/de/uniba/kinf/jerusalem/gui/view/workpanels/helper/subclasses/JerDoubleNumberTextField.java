package de.uniba.kinf.jerusalem.gui.view.workpanels.helper.subclasses;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import de.uniba.kinf.jerusalem.gui.view.workpanels.JerWorkPanel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.JerTextField;

/**
 * Only for JTextField} of type double.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerDoubleNumberTextField extends JerTextField {

        /**
     * 
     */
        private static final long serialVersionUID = -3436700352620075856L;

        public JerDoubleNumberTextField(final JerWorkPanel jwp,
                        final String name1, final String regexPattern) {
                super(jwp, name1, regexPattern);
        }

        @Override
        public PreparedStatement setPrpStmt(final PreparedStatement ps,
                        final int pos) throws SQLException {
                if (getText().isEmpty()) {
                        ps.setNull(pos, Types.DOUBLE);
                } else {
                        ps.setDouble(pos, Double.parseDouble(getText()));
                }
                return ps;
        }

        // not compatible with Mac OS
        // @Override
        // public void displayInfo(final HashMap<String, Object> values) {
        // final String content = "" + values.get(name);
        // if (stringIsValidEntry(content)) {
        // setIsValid(true);
        // // to avoid displaying "null"
        // if (!content.equals("null")) {
        // DecimalFormat df = new DecimalFormat("####.##");
        // tf.setText(df.format(Double
        // .parseDouble(content)));
        // }
        // } else {
        // setIsValid(false);
        // }
        // }

}
