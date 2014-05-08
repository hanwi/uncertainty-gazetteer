package de.uniba.kinf.jerusalem.gui.view.workpanels.helper.subclasses;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import javax.swing.JTextField;

import de.uniba.kinf.jerusalem.gui.view.workpanels.JerWorkPanel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.JerTextField;

/**
 * Only for {@link JTextField} of type integer.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerIntNumberTextField extends JerTextField {

        /**
     * 
     */
        private static final long serialVersionUID = 4766453728736404841L;

        public JerIntNumberTextField(final JerWorkPanel jwp,
                        final String name1, final String regexPattern) {
                super(jwp, name1, regexPattern);
        }

        @Override
        public PreparedStatement setPrpStmt(final PreparedStatement ps,
                        final int pos) throws SQLException {
                if (getText().isEmpty()) {
                        ps.setNull(pos, Types.INTEGER);
                } else {
                        ps.setInt(pos, Integer.parseInt(getText()));
                }
                return ps;
        }

}
