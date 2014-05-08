package de.uniba.kinf.jerusalem.gui.view.tablepanels;

import javax.swing.JCheckBox;

/**
 * Dummy class to avoid that values in {@link JerSelectionDialog}
 * .itemStateChanged() are overwritten with JComponent-specific (and therefore
 * Look&Feel dependent) values.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerCheckBox extends JCheckBox {

        /**
     * 
     */
        private static final long serialVersionUID = -8675746333179663156L;

        public JerCheckBox(final String text, final boolean selected) {
                super(text, selected);
        }

        @Override
        public void firePropertyChange(final String propertyName,
                        final byte oldValue, final byte newValue) {
        }

        @Override
        public void firePropertyChange(final String propertyName,
                        final double oldValue, final double newValue) {
        }

        @Override
        public void firePropertyChange(final String propertyName,
                        final float oldValue, final float newValue) {
        }

        @Override
        public void firePropertyChange(final String propertyName,
                        final long oldValue, final long newValue) {
        }

        @Override
        protected void firePropertyChange(final String propertyName,
                        final Object oldValue, final Object newValue) {
        }

}
