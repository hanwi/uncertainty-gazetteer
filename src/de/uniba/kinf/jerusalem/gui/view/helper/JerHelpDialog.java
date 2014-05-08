package de.uniba.kinf.jerusalem.gui.view.helper;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import de.uniba.kinf.jerusalem.gui.helper.JerResourceBundleAccessor;

/**
 * Represents help dialog for program.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerHelpDialog extends JDialog {

        private static final Dimension JTASIZE = new Dimension(250, 50);
        /**
     * 
     */
        private static final long serialVersionUID = -652106125107628531L;

        public JerHelpDialog() {
                setModal(false);
                setTitle(JerResourceBundleAccessor.get("manual"));
                setResizable(true);

                final JPanel diaPanel = new JPanel();
                diaPanel.setLayout(new GridLayout(1, 1));

                final JTextArea jta = new JTextArea();
                jta.setSize(JTASIZE);
                jta.setLineWrap(false);
                jta.setFocusable(false);
                jta.setEditable(false);

                final String str = "@"
                                + JerResourceBundleAccessor.get("map")
                                + System.lineSeparator()
                                + System.lineSeparator()
                                + JerResourceBundleAccessor
                                                .get("manual_description_place_main_location")
                                + System.lineSeparator()
                                + JerResourceBundleAccessor
                                                .get("manual_description_additional_location")
                                + System.lineSeparator()
                                + System.lineSeparator()
                                + JerResourceBundleAccessor.get("manual_mouse")
                                + " : "
                                + System.lineSeparator()
                                + JerResourceBundleAccessor
                                                .get("manual_mouse_no_key")
                                + System.lineSeparator()
                                + JerResourceBundleAccessor
                                                .get("manual_mouse_shift_key")
                                + System.lineSeparator()
                                + JerResourceBundleAccessor
                                                .get("manual_mouse_shift_ctrl_key")
                                + System.lineSeparator()
                                + JerResourceBundleAccessor
                                                .get("manual_mouse_alt_key")
                                + System.lineSeparator()
                                + JerResourceBundleAccessor
                                                .get("manual_mouse_shift_alt_key")
                                + System.lineSeparator()
                                + JerResourceBundleAccessor
                                                .get("manual_mouse_alt_ctrl_key")
                                + System.lineSeparator()
                                + System.lineSeparator()
                                + JerResourceBundleAccessor
                                                .get("manual_confirm_action_buttons");
                jta.setText(str);
                diaPanel.add(jta);

                setContentPane(diaPanel);
                pack();
                setVisible(true);
        }
}
