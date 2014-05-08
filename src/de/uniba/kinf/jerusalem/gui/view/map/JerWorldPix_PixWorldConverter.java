package de.uniba.kinf.jerusalem.gui.view.map;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.Properties;

/**
 * Provides {@link AffineTransform} which can be used to convert from pixel to
 * world and from world to pixel coordinates.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerWorldPix_PixWorldConverter {

        private double delta_Pixel_per_1_realWorldUnit_H_Y;
        private double delta_Pixel_per_1_realWorldUnit_W_X;
        private double delta_realWordUnit_per_1_screenPixel_W_X;
        private double delta_realWorldUnit_per_1_screenPixel_H_Y;
        private AffineTransform pixel2world;
        private double realWorldUnit_0_H_Y;
        private double realWorldUnit_0_W_X;
        private AffineTransform world2pixel;

        public JerWorldPix_PixWorldConverter(final Properties properties)
                        throws NoninvertibleTransformException {
                final String nameDefMap = properties.getProperty("defaultmap");

                final String x1Value = properties.getProperty("map_"
                                + nameDefMap + "_x1");
                final String y1Value = properties.getProperty("map_"
                                + nameDefMap + "_y1");
                final String x2Value = properties.getProperty("map_"
                                + nameDefMap + "_x2");
                final String y2Value = properties.getProperty("map_"
                                + nameDefMap + "_y2");
                final String w1Value = properties.getProperty("map_"
                                + nameDefMap + "_w1");
                final String h1Value = properties.getProperty("map_"
                                + nameDefMap + "_h1");
                final String w2Value = properties.getProperty("map_"
                                + nameDefMap + "_w2");
                final String h2Value = properties.getProperty("map_"
                                + nameDefMap + "_h2");

                final int x1 = Integer.parseInt(x1Value);
                final int y1 = Integer.parseInt(y1Value);
                final int x2 = Integer.parseInt(x2Value);
                final int y2 = Integer.parseInt(y2Value);
                final double w1 = Double.parseDouble(w1Value);
                final double h1 = Double.parseDouble(h1Value);
                final double w2 = Double.parseDouble(w2Value);
                final double h2 = Double.parseDouble(h2Value);

                define_realWorld_per_1_pixel(x1, y1, x2, y2, w1, h1, w2, h2);
                define_pixel_per_1_realworld(x1, y1, x2, y2, w1, h1, w2, h2);
                define_realWorld_at_screen_x_y_0_0(x1, y1, w1, h1);
                define_World_To_Pixel();
        }

        private void define_pixel_per_1_realworld(final int x1, final int y1,
                        final int x2, final int y2, final double w1,
                        final double h1, final double w2, final double h2) {
                delta_Pixel_per_1_realWorldUnit_W_X = (x2 - x1) / (w2 - w1);
                delta_Pixel_per_1_realWorldUnit_H_Y = (y2 - y1) / (h2 - h1);
        }

        private void define_realWorld_at_screen_x_y_0_0(final int x1,
                        final int y1, final double w1, final double h1) {
                realWorldUnit_0_W_X = w1 - x1
                                * delta_realWordUnit_per_1_screenPixel_W_X;
                realWorldUnit_0_H_Y = h1 - y1
                                * delta_realWorldUnit_per_1_screenPixel_H_Y;
        }

        private void define_realWorld_per_1_pixel(final int x1, final int y1,
                        final int x2, final int y2, final double w1,
                        final double h1, final double w2, final double h2) {
                delta_realWordUnit_per_1_screenPixel_W_X = (w2 - w1)
                                / (x2 - x1);
                delta_realWorldUnit_per_1_screenPixel_H_Y = (h2 - h1)
                                / (y2 - y1);
        }

        private void define_World_To_Pixel()
                        throws NoninvertibleTransformException {
                final AffineTransform translate_World_To_Pixel = AffineTransform
                                .getTranslateInstance(-realWorldUnit_0_W_X,
                                                -realWorldUnit_0_H_Y);
                final AffineTransform scale_World_To_Pixel = AffineTransform
                                .getScaleInstance(
                                                delta_Pixel_per_1_realWorldUnit_W_X,
                                                delta_Pixel_per_1_realWorldUnit_H_Y);
                world2pixel = new AffineTransform(scale_World_To_Pixel);
                world2pixel.concatenate(translate_World_To_Pixel);
                pixel2world = world2pixel.createInverse();

        }

        public AffineTransform getPixel_To_World() {
                return pixel2world;
        }

        public AffineTransform getWorld_To_Pixel() {
                return world2pixel;
        }

}
