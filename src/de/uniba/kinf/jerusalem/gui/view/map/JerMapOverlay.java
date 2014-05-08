package de.uniba.kinf.jerusalem.gui.view.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.geotools.geometry.jts.LiteShape;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import de.uniba.kinf.jerusalem.gui.helper.JerPlace;
import de.uniba.kinf.jerusalem.gui.helper.subclasses.JerGeoObserverMsg;

/**
 * Draws information from a {@link JerGeoObserverMsg} using
 * {@link AffineTransform} provided by {@link JerWorldPix_PixWorldConverter}.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerMapOverlay extends JComponent {
        private static final long serialVersionUID = 635793184372247932L;
        private static final double TOLERANCE = 0.02;
        private final JerWorldPix_PixWorldConverter converter;
        private final JerGeoObserverMsg msg;
        private final JerMapComponent parent;

        @SuppressWarnings("boxing")
        public JerMapOverlay(final JerMapComponent jerMapComponent,
                        final JerGeoObserverMsg geoMsg) {
                parent = jerMapComponent;
                final Dimension d = parent.getPane().getPreferredSize();
                setBounds(0, 0, d.width, d.height);
                msg = geoMsg;
                converter = jerMapComponent.getConverter();
        }

        private void drawObj(final Graphics2D g2) {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,
                                BasicStroke.JOIN_ROUND));

                final GeometryFactory gf = new GeometryFactory();

                for (final JerPlace p : msg.getPlaceLi()) {
                        makeThePainting(g2, gf, TOLERANCE + 0.03, p
                                        .getMainLoc().getCoordinates(), true);
                        if (p.hasAdditionalInstances()) {
                                makeThePainting(g2, gf, TOLERANCE, p
                                                .getAdditionalInst()
                                                .getCoordinates(), false);
                        }
                        if (p.hasNumber()) {
                                makeTheNumberPainting(g2, p);
                        }
                }
        }

        private void makeTheNumberPainting(final Graphics2D g2, final JerPlace p) {
                final Point2D worldPoint = new Point2D.Double(p.getMainLoc()
                                .getCoordinate().x, p.getMainLoc()
                                .getCoordinate().y);
                final Point2D pixelPoint = new Point2D.Double();
                converter.getWorld_To_Pixel().transform(worldPoint, pixelPoint);
                g2.drawString(p.getNumber() + "", (int) pixelPoint.getX(),
                                (int) pixelPoint.getY());
        }

        private void makeThePainting(final Graphics2D g2,
                        final GeometryFactory gf, final double tol,
                        final Coordinate[] a, final boolean isMainLocation) {
                for (final Coordinate c : a) {
                        final List<Coordinate> abc = new ArrayList<>();
                        abc.add(new Coordinate(c.x - tol, c.y));
                        abc.add(new Coordinate(c.x, c.y + tol));
                        abc.add(new Coordinate(c.x + tol, c.y));
                        abc.add(new Coordinate(c.x, c.y - tol));
                        abc.add(new Coordinate(c.x - tol, c.y));
                        final LineString lineStri = gf.createLineString(abc
                                        .toArray(new Coordinate[0]));
                        final LiteShape ls = new LiteShape(lineStri,
                                        converter.getWorld_To_Pixel(), false);
                        g2.draw(ls);
                        if (isMainLocation) {
                                g2.setColor(Color.RED);
                        } else {
                                g2.setColor(Color.GREEN);
                        }
                        g2.fill(ls);
                        g2.setColor(Color.BLACK);
                }
        }

        @Override
        protected void paintComponent(final Graphics g) {
                final Graphics2D g2 = (Graphics2D) g;
                super.paintComponent(g2);
                drawObj(g2);
        }

}
