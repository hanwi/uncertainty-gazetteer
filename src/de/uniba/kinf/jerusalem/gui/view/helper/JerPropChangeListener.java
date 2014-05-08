package de.uniba.kinf.jerusalem.gui.view.helper;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Properties;

/**
 * Saves property events into properties file.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerPropChangeListener implements PropertyChangeListener {
        private final Properties props;
        private final String prpname;

        public JerPropChangeListener(final Properties properties,
                        final String propname) {
                this.prpname = propname;
                this.props = properties;
        }

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
                props.setProperty(prpname, "" + evt.getNewValue());
        }
}
