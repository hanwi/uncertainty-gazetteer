package de.uniba.kinf.jerusalem.gui.view.workpanels.helper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.text.DefaultFormatter;

/**
 * Represents an entity of table jerusalem.placetopos.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerMultiItem extends JComponent {

        /**
     * 
     */
        private static final long serialVersionUID = 4574762945652307126L;
        private final JFormattedTextField beginF;
        private final JButton btnDel;
        private final JComboBox<JerItem> cbbox;
        private final JFormattedTextField endF;

        public JerMultiItem(final JerMultiField parent) {

                final RegexPatternFormatter makeNullableFormatter = new RegexPatternFormatter(
                                Pattern.compile("^(null|[0]{1}|-{0,1}[0-9]{0,4})$",
                                                Pattern.CASE_INSENSITIVE));
                makeNullableFormatter.setAllowsInvalid(false);
                beginF = new JFormattedTextField(makeNullableFormatter);
                beginF.setToolTipText("begin");
                endF = new JFormattedTextField(makeNullableFormatter);
                endF.setToolTipText("end");
                btnDel = new JButton("-");

                setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
                add(btnDel);
                add(cbbox = new JComboBox<>());
                add(beginF);
                add(endF);

                final JerMultiItem self = this;
                btnDel.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                                parent.removeMultiItem(self);
                        }
                });
        }

        public JTextField getBeginF() {
                return beginF;
        }

        public JComboBox<JerItem> getCbbox() {
                return cbbox;
        }

        public JTextField getEndF() {
                return endF;
        }

        public int getIDSelectedJerItem() {
                final JerItem j = (JerItem) cbbox.getSelectedItem();
                return j.getId();
        }

        @SuppressWarnings("unchecked")
        public void setCBoxValues(final List<Object> colLi) {
                cbbox.removeAllItems();
                for (int i = 0; i < colLi.size(); i++) {
                        final List<Object> rowLi = (List<Object>) colLi.get(i);
                        final JerItem jci = new JerItem((Integer) rowLi.get(0),
                                        rowLi.get(1));
                        cbbox.addItem(jci);
                }
        }

}

class RegexPatternFormatter extends DefaultFormatter {

        /**
     * 
     */
        private static final long serialVersionUID = -930495715032221676L;
        private final Matcher matcher;

        public RegexPatternFormatter(final Pattern regex) {
                setOverwriteMode(false);
                matcher = regex.matcher("");
        }

        @Override
        public Object stringToValue(final String string)
                        throws java.text.ParseException {
                if (string == null) {
                        return null;
                }
                matcher.reset(string);
                if (!matcher.matches()) {
                        throw new java.text.ParseException("", 0);
                }
                return super.stringToValue(string);
        }
}
