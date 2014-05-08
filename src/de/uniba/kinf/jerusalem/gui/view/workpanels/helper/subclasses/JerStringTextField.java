package de.uniba.kinf.jerusalem.gui.view.workpanels.helper.subclasses;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JTextField;

import de.uniba.kinf.jerusalem.gui.view.workpanels.JerWorkPanel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.JerTextField;

/**
 * Only for {@link JTextField} of type String.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerStringTextField extends JerTextField {

        /**
     * 
     */
        private static final long serialVersionUID = -2698045900499747532L;

        public JerStringTextField(final JerWorkPanel jwp, final String name1,
                        final String regexPattern) {
                super(jwp, name1, regexPattern);
        }

        @Override
        public PreparedStatement setPrpStmt(final PreparedStatement ps,
                        final int pos) throws SQLException {
                ps.setString(pos, getText());
                return ps;
        }

}
