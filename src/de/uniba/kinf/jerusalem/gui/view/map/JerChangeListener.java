package de.uniba.kinf.jerusalem.gui.view.map;

import java.awt.Point;
import java.util.Properties;

import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Enables storing of viewport position of {@link JerMapComponent}.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerChangeListener implements ChangeListener {

        private final Properties properties;
        private final JViewport vp;
        private final String xValue;
        private final String yValue;

        public JerChangeListener(final JViewport vp1,
                        final Properties properties1, final String xValue1,
                        final String yValue1) {
                this.xValue = xValue1;
                this.yValue = yValue1;
                this.properties = properties1;
                this.vp = vp1;
        }

        @Override
        public void stateChanged(final ChangeEvent arg0) {
                final Point p = vp.getViewPosition();
                properties.setProperty(xValue, "" + p.x);
                properties.setProperty(yValue, "" + p.y);
        }

}
