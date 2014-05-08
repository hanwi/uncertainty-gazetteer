package de.uniba.kinf.jerusalem.gui.view.map;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.operation.transform.AffineTransform2D;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.io.ParseException;

import de.uniba.kinf.jerusalem.Main;
import de.uniba.kinf.jerusalem.gui.helper.JerMsgType;
import de.uniba.kinf.jerusalem.gui.helper.JerObserverMsg;
import de.uniba.kinf.jerusalem.gui.helper.JerPlace;
import de.uniba.kinf.jerusalem.gui.helper.JerResourceBundleAccessor;
import de.uniba.kinf.jerusalem.gui.helper.subclasses.JerGeoObserverMsg;
import de.uniba.kinf.jerusalem.gui.model.JerMainModel;

/**
 * Provides layout for {@link JerMapLayer}, {@link JerMapOverlay}. Has mouse
 * listener to register actions on pane.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerMapComponent extends JLayeredPane implements Observer {

        /**
     * 
     */
        private static final long serialVersionUID = -3868360058490120417L;
        private final JerWorldPix_PixWorldConverter converter;
        private List<Coordinate> coordinateLi;
        private JerGeoObserverMsg geoMsg;
        private BufferedImage img;
        private JerMapOverlay jmo;
        private JLayeredPane layeredMapPane;
        private final JerMainModel mainModel;
        private JerMapLayer mapLayer;
        private final Properties properties;
        private JScrollPane scrPaneMap;
        private JCheckBox showFeaturesChckBox;
        private final double tolerance = 0.05;
        private JToolBar toolBar;
        private double xMinusToleranceTarget;
        private double xPlusToleranceTarget;
        private double yMinusToleranceTarget;
        private double yPlusToleranceTarget;

        public JerMapComponent(final JerMainModel mainMod,
                        final Properties props) throws IOException,
                        NoninvertibleTransformException {
                mainModel = mainMod;
                properties = props;
                converter = new JerWorldPix_PixWorldConverter(props);
                coordinateLi = new ArrayList<>();
                createToolBar();
                createLayeredMapPane(props);
                add(toolBar, JLayeredPane.PALETTE_LAYER);
                add(scrPaneMap, JLayeredPane.FRAME_CONTENT_LAYER);
                adjustViewPort();
                repaint();
        }

        private void adjustViewPort() {
                final JViewport vp = scrPaneMap.getViewport();
                final Point p = new Point(0, 0);
                final String xValueName = "viewPort_x_coordinate";
                final String yValueName = "viewPort_y_coordinate";
                final String sXValue = properties.getProperty(xValueName);
                final String sYValue = properties.getProperty(yValueName);
                if (sXValue != null && sYValue != null) {
                        p.setLocation(Integer.parseInt(sXValue),
                                        Integer.parseInt(sYValue));
                }
                vp.setViewPosition(p);
                scrPaneMap.setWheelScrollingEnabled(true);
                scrPaneMap.getVerticalScrollBar().setUnitIncrement(
                                Main.DEFUNITINCREMENT);
                scrPaneMap.getHorizontalScrollBar().setUnitIncrement(
                                Main.DEFUNITINCREMENT);
                scrPaneMap.getViewport().addChangeListener(
                                new JerChangeListener(vp, properties,
                                                xValueName, yValueName));
        }

        private MouseListener createAnonymMouseListener() {
                return new MouseListener() {

                        private boolean isInToleranceArea(
                                        final Coordinate mouseCoordinateInWorldUnits) {
                                return xMinusToleranceTarget <= mouseCoordinateInWorldUnits.x
                                                && mouseCoordinateInWorldUnits.x <= xPlusToleranceTarget
                                                && yMinusToleranceTarget <= mouseCoordinateInWorldUnits.y
                                                && mouseCoordinateInWorldUnits.y <= yPlusToleranceTarget;
                        }

                        private void makeAction(
                                        final MouseEvent e,
                                        final Coordinate mouseCoordinateInWorldUnits,
                                        final GeometryFactory s) {

                                final boolean isDel = e.isShiftDown();

                                if (geoMsg != null) {
                                        for (int i = 0; i < geoMsg.getPlaceLi()
                                                        .size(); i++) {
                                                final JerPlace p = geoMsg
                                                                .getPlaceLi()
                                                                .get(i);
                                                final Coordinate c = p
                                                                .getMainLoc()
                                                                .getCoordinate();
                                                setSupportingValsAccordingToCoordinate(c);
                                                if (isInToleranceArea(mouseCoordinateInWorldUnits)) {
                                                        if (isDel) {
                                                                geoMsg.getPlaceLi()
                                                                                .set(i,
                                                                                                null);
                                                                mainModel.informAboutCoordinates(geoMsg);
                                                        } else {
                                                                mainModel.setPlaceModelAndID(c);
                                                        }
                                                        return;
                                                }
                                                if (p.hasAdditionalInstances()
                                                                && isDel) {
                                                        final Coordinate[] start = p
                                                                        .getAdditionalInst()
                                                                        .getCoordinates();
                                                        final List<Coordinate> dest = new ArrayList<>();
                                                        for (final Coordinate coord : start) {
                                                                setSupportingValsAccordingToCoordinate(coord);
                                                                if (!isInToleranceArea(mouseCoordinateInWorldUnits)) {
                                                                        dest.add(coord);
                                                                }
                                                        }
                                                        final MultiPoint np = s
                                                                        .createMultiPoint(dest
                                                                                        .toArray(new Coordinate[0]));
                                                        p.setAdditionalInstances(np);
                                                        mainModel.informAboutCoordinates(geoMsg);
                                                        return;
                                                }
                                        }
                                }
                        }

                        private void makeUpdateMainLoc(
                                        final Coordinate mouseCoordinateInWorldUnits,
                                        final GeometryFactory s)
                                        throws ParseException {
                                final JerGeoObserverMsg msg = new JerGeoObserverMsg();
                                final List<Coordinate> dest = new ArrayList<>();
                                final JerPlace place = new JerPlace(
                                                s.createPoint(mouseCoordinateInWorldUnits));
                                for (final Coordinate c2 : coordinateLi) {
                                        dest.add(c2);
                                }

                                if (mainModel.getSelectedID() != -1) {
                                        final JerPlace p = mainModel
                                                        .getAdditionalInstancesForSpecificPlace(mainModel
                                                                        .getSelectedID());
                                        if (p.hasAdditionalInstances()) {
                                                for (final Coordinate c : p
                                                                .getAdditionalInst()
                                                                .getCoordinates()) {
                                                        dest.add(c);
                                                }
                                        }
                                }

                                coordinateLi = new ArrayList<>();

                                if (dest.size() > 0) {
                                        final MultiPoint np = s
                                                        .createMultiPoint(dest
                                                                        .toArray(new Coordinate[0]));
                                        place.setAdditionalInstances(np);
                                }
                                msg.addPlaceToLi(place);
                                mainModel.informAboutCoordinates(msg);
                        }

                        @Override
                        public void mouseClicked(final MouseEvent e) {
                        }

                        @Override
                        public void mouseEntered(final MouseEvent e) {
                        }

                        @Override
                        public void mouseExited(final MouseEvent e) {
                        }

                        @Override
                        public void mousePressed(final MouseEvent e) {
                                reactToMouseEvent(e);
                        }

                        @Override
                        public void mouseReleased(final MouseEvent e) {
                        }

                        private void reactToMouseEvent(final MouseEvent e) {

                                try {
                                        final MathTransform mathTransform = new AffineTransform2D(
                                                        getConverter().getPixel_To_World());
                                        final Coordinate mouseCoordinateInWorldUnits = JTS
                                                        .transform(new Coordinate(
                                                                        e.getX(),
                                                                        e.getY()),
                                                                        null,
                                                                        mathTransform);

                                        final GeometryFactory gf = new GeometryFactory();

                                        boolean jerCondition1 = e.isShiftDown();
                                        boolean jerCondition2 = e.isAltDown();
                                        boolean jerCondition3 = e
                                                        .isControlDown();

                                        // select place
                                        if (!jerCondition1 && !jerCondition2
                                                        && !jerCondition3) {
                                                makeAction(e,
                                                                mouseCoordinateInWorldUnits,
                                                                gf);
                                        }

                                        // if place selected in table: move main
                                        // location place, else: create new
                                        // place
                                        if (jerCondition1 && !jerCondition2
                                                        && !jerCondition3) {
                                                makeUpdateMainLoc(
                                                                mouseCoordinateInWorldUnits,
                                                                gf);
                                        }

                                        // add additional instances to selected
                                        // place
                                        if (jerCondition1 && !jerCondition2
                                                        && jerCondition3) {
                                                coordinateLi.add(mouseCoordinateInWorldUnits);
                                        }

                                        // reload map
                                        if (!jerCondition1 && jerCondition2
                                                        && !jerCondition3) {
                                                mainModel.reloadMapWithAllFeatures();
                                        }

                                        // delete additional instances of place
                                        if (jerCondition1 && jerCondition2
                                                        && !jerCondition3) {
                                                makeAction(e,
                                                                mouseCoordinateInWorldUnits,
                                                                gf);
                                        }

                                        // show mouse coordinates
                                        if (!jerCondition1 && jerCondition2
                                                        && jerCondition3) {
                                                mainModel.getJerInfoMsgHandler()
                                                                .showMsg(JerResourceBundleAccessor
                                                                                .get("pixelPosition_mouse_mapComponent")
                                                                                + e.getX()
                                                                                + ","
                                                                                + e.getY());
                                        }

                                } catch (final TransformException
                                                | ParseException e1) {
                                        mainModel.getJerInfoMsgHandler()
                                                        .showMsg(e1.getLocalizedMessage());
                                }
                        }

                        private void setSupportingValsAccordingToCoordinate(
                                        final Coordinate c) {
                                xPlusToleranceTarget = c.x + tolerance;
                                xMinusToleranceTarget = c.x - tolerance;
                                yPlusToleranceTarget = c.y + tolerance;
                                yMinusToleranceTarget = c.y - tolerance;
                        }
                };
        }

        private void createLayeredMapPane(final Properties props)
                        throws IOException {
                layeredMapPane = new JLayeredPane();

                scrPaneMap = new JScrollPane(layeredMapPane);

                final String nameDefMap = props.getProperty("defaultmap");
                img = ImageIO.read(new File(props.getProperty(nameDefMap
                                + "_absolute_path")));
                layeredMapPane.setPreferredSize(new Dimension(img.getWidth(),
                                img.getHeight()));

                mapLayer = new JerMapLayer(this);
                layeredMapPane.add(mapLayer, JLayeredPane.DEFAULT_LAYER);

                layeredMapPane.addMouseListener(createAnonymMouseListener());

        }

        private void createToolBar() {
                toolBar = new JToolBar();
                toolBar.setOpaque(false);
                toolBar.setFloatable(false);

                showFeaturesChckBox = new JCheckBox(
                                JerResourceBundleAccessor
                                                .get("show_information"));
                showFeaturesChckBox.setSelected(true);
                showFeaturesChckBox.setOpaque(false);
                showFeaturesChckBox.addItemListener(new ItemListener() {

                        @Override
                        public void itemStateChanged(final ItemEvent e) {
                                if (e.getStateChange() == ItemEvent.DESELECTED
                                                && jmo != null) {
                                        layeredMapPane.remove(jmo);
                                        geoMsg = null;
                                        repaint();
                                }
                                if (e.getStateChange() == ItemEvent.SELECTED) {
                                        try {
                                                mainModel.checkSelectedPlaces();
                                        } catch (final ParseException e1) {
                                                mainModel.getJerInfoMsgHandler()
                                                                .showMsg(e1.getLocalizedMessage());
                                        }
                                }
                        }
                });
                toolBar.add(showFeaturesChckBox);
        }

        public JerWorldPix_PixWorldConverter getConverter() {
                return converter;
        }

        public BufferedImage getImg() {
                return img;
        }

        public Component getPane() {
                return layeredMapPane;
        }

        public Properties getProps() {
                return properties;
        }

        @Override
        public void repaint() {
                final Dimension td = toolBar.getPreferredSize();
                scrPaneMap.setBounds(0, 0, getSize().width, getSize().height);
                toolBar.setBounds(30, 10, td.width, td.height);
                super.repaint();
        }

        @Override
        public void update(final Observable arg0, final Object arg1) {
                final JerObserverMsg msg = (JerObserverMsg) arg1;

                if (msg.getType() == JerMsgType.GEOMETRY
                                && showFeaturesChckBox.isSelected()) {
                        geoMsg = (JerGeoObserverMsg) msg;
                        if (jmo != null) {
                                layeredMapPane.remove(jmo);
                        }
                        jmo = new JerMapOverlay(this, geoMsg);

                        layeredMapPane.add(jmo, JLayeredPane.PALETTE_LAYER);

                }
                if ((msg.getType() == JerMsgType.SELECTION || msg.getType() == JerMsgType.DB_UPDATE)
                                && (showFeaturesChckBox.isSelected())) {
                        try {
                                mainModel.checkSelectedPlaces();
                        } catch (final ParseException e) {
                                mainModel.getJerInfoMsgHandler().showMsg(
                                                e.getLocalizedMessage());
                        }
                }
        }
}
