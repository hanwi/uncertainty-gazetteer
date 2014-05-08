package de.uniba.kinf.jerusalem.gui.view.map;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Properties;

import javax.swing.JComponent;

/**
 * Displays map image from {@link JerMapComponent}.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerMapLayer extends JComponent {
        /**
     * 
     */
        private static final long serialVersionUID = 2429715220326912801L;
        private final BufferedImage img;

        public JerMapLayer(final JerMapComponent jmc) {

                img = jmc.getImg();
                final Properties props = jmc.getProps();

                int topleftX = 0;
                int topleftY = 0;
                int width = 0;
                int height = 0;

                final String nameDefMap = props.getProperty("defaultmap");
                final String topleftXValue = props.getProperty("map_"
                                + nameDefMap + "_topleftX");
                final String topleftYValue = props.getProperty("map_"
                                + nameDefMap + "_topleftY");
                final String widthValue = props.getProperty("map_" + nameDefMap
                                + "_width");
                final String heightValue = props.getProperty("map_"
                                + nameDefMap + "_height");

                if (topleftXValue != null) {
                        topleftX = Integer.parseInt(topleftXValue);
                }
                if (topleftYValue != null) {
                        topleftY = Integer.parseInt(topleftYValue);
                }
                if (widthValue != null) {
                        width = Integer.parseInt(widthValue);
                }
                if (heightValue != null) {
                        height = Integer.parseInt(heightValue);
                }

                setBounds(topleftX, topleftY, width, height);
        }

        @Override
        public Dimension getPreferredSize() {
                return new Dimension(img.getWidth(), img.getHeight());
        }

        @Override
        protected void paintComponent(final Graphics g) {
                final Graphics2D g2 = (Graphics2D) g;
                super.paintComponent(g2);
                g2.drawImage(img, 0, 0, null);
        }

}
