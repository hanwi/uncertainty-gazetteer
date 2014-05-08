package de.uniba.kinf.jerusalem.gui.view.workpanels.helper;

import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

import de.uniba.kinf.jerusalem.Main;
import de.uniba.kinf.jerusalem.gui.helper.JerResourceBundleAccessor;
import de.uniba.kinf.jerusalem.gui.view.workpanels.JerWorkPanel;

class JerDocumentFilter extends DocumentFilter {

        private final JerFieldInfoIF jfi;

        public JerDocumentFilter(final JerFieldInfoIF jfi1) {
                this.jfi = jfi1;
        }

        @Override
        public void insertString(final FilterBypass fb, final int offs,
                        final String str, final AttributeSet a)
                        throws BadLocationException {
                final Document d = fb.getDocument();
                final String startStr = d.getText(0, offs);
                final String endStr = d.getText(offs, d.getLength() - offs);

                final String completeStr = startStr + str + endStr;

                final boolean ve = jfi.stringIsValidEntry(completeStr);

                if (ve) {
                        fb.insertString(offs, str, a);
                        jfi.setIsValid(ve);
                }
        }

        @Override
        public void remove(final FilterBypass fb, final int offset,
                        final int length) throws BadLocationException {
                fb.remove(offset, length);

                final Document d = fb.getDocument();
                final String entire = d.getText(0, d.getLength());
                final String after = d.getText(offset + length, d.getLength());

                final String all = entire + after;

                final boolean ve = jfi.stringIsValidEntry(all);
                if (ve) {
                        jfi.setIsValid(true);
                } else {
                        jfi.setIsValid(false);
                }

        }

        @Override
        public void replace(final FilterBypass fb, final int offset,
                        final int length, final String text,
                        final AttributeSet attrs) throws BadLocationException {
                final Document d = fb.getDocument();
                final StringBuilder stb = new StringBuilder(d.getText(0,
                                d.getLength()));
                stb.replace(offset, offset + length, text);

                final boolean ve = jfi.stringIsValidEntry(stb.toString());

                if (ve) {
                        fb.replace(offset, length, text, attrs);
                        jfi.setIsValid(ve);
                }
        }
}

/**
 * Provides basic functionality for all {@link JTextField} of
 * {@link JerWorkPanel}.
 * 
 * @author Hanno Wierichs
 * 
 */
public abstract class JerTextField extends JComponent implements JerFieldInfoIF {

        private static final long serialVersionUID = 8311913900933551839L;
        private boolean isValidContent;
        protected final String name;
        private final String regex;
        protected final JTextField tf;

        private final JerWorkPanel workPanel;

        public JerTextField(final JerWorkPanel jwp, final String name1,
                        final String regexPattern) {
                workPanel = jwp;
                name = name1.toUpperCase();
                regex = regexPattern;

                final PlainDocument pd = new PlainDocument();
                pd.setDocumentFilter(new JerDocumentFilter(this));

                tf = new JTextField();
                tf.setDocument(pd);

                tf.setBorder(javax.swing.BorderFactory.createEmptyBorder());
                final JLabel label = new JLabel();
                label.setText(" " + JerResourceBundleAccessor.get(name) + ": ");

                setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

                add(label);
                add(tf);
                final Dimension d = getPreferredSize();
                setMaximumSize(new Dimension(Short.MAX_VALUE, d.height));
        }

        @Override
        public void clear() {
                tf.setText("");
        }

        @Override
        public void displayInfo(final HashMap<String, Object> values) {
                final String content = "" + values.get(name);
                if (stringIsValidEntry(content)) {
                        setIsValid(true);
                        // to avoid displaying "null"
                        if (!content.equals("null")) {
                                tf.setText(content);
                        }
                } else {
                        setIsValid(false);
                }
        }

        @Override
        public void enableFunctionality(final boolean b) {
                setEnabled(b);
        }

        @Override
        public void focusOnField() {
                requestFocus();
        }

        @Override
        public String getColumnTitle() {
                return name;
        }

        @Override
        public int getID() {
                return -1;
        }

        public String getText() {
                return tf.getText();
        }

        @Override
        public boolean isFieldValid() {
                return isValidContent;
        }

        @Override
        public void setID(final int i) {
        }

        @Override
        public void setIsValid(final boolean valid) {
                isValidContent = valid;
                if (isValidContent) {
                        setBackground(Main.DEFCOLOROK);
                } else {
                        setBackground(Main.DEFCOLORPROBLEM);
                }
                workPanel.actualizeStatus();
        }

        public void setText(final String t) {
                tf.setText(t);
        }

        @Override
        public boolean stringIsValidEntry(final String str) {
                return str.matches(regex);
        }

}
