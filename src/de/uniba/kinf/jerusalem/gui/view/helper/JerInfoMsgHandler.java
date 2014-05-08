package de.uniba.kinf.jerusalem.gui.view.helper;

import java.awt.Color;

import javax.swing.text.JTextComponent;

import de.uniba.kinf.jerusalem.Main;
import de.uniba.kinf.jerusalem.gui.view.JerMainView;

/**
 * Displays given message in text area.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerInfoMsgHandler {

        private JTextComponent jta = null;

        public JerInfoMsgHandler() {
        }

        public void setMsgBoxToNormal() {
                if (jta != null) {
                        jta.setText(jta.getText());
                        jta.setBackground(Color.WHITE);
                }
        }

        public void setTextComponent(final JTextComponent jtc) {
                jta = jtc;
                jta.setText(JerMainView.getTextualMenuShortCutKeyMask()
                                + System.getProperty("line.separator"));
        }

        public void showMsg(final String msg) {
                if (jta != null) {
                        jta.setBackground(Main.DEFCOLORPROBLEM);
                        jta.setText(jta.getText() + msg
                                        + System.getProperty("line.separator"));
                }
        }
}
