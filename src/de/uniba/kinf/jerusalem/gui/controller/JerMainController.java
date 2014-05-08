package de.uniba.kinf.jerusalem.gui.controller;

import java.awt.geom.NoninvertibleTransformException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.UnsupportedLookAndFeelException;

import de.uniba.kinf.jerusalem.gui.helper.JerLogger;
import de.uniba.kinf.jerusalem.gui.helper.JerPropsHelper;
import de.uniba.kinf.jerusalem.gui.helper.JerResourceBundleAccessor;
import de.uniba.kinf.jerusalem.gui.model.JerMainModel;
import de.uniba.kinf.jerusalem.gui.model.JerModel;
import de.uniba.kinf.jerusalem.gui.model.JerTableModel;
import de.uniba.kinf.jerusalem.gui.model.subclasses.JerAuthorModel;
import de.uniba.kinf.jerusalem.gui.model.subclasses.JerDocumentModel;
import de.uniba.kinf.jerusalem.gui.model.subclasses.JerEntryModel;
import de.uniba.kinf.jerusalem.gui.model.subclasses.JerPlaceModel;
import de.uniba.kinf.jerusalem.gui.model.subclasses.JerToposInEntryModel;
import de.uniba.kinf.jerusalem.gui.model.subclasses.JerToposModel;
import de.uniba.kinf.jerusalem.gui.view.JerMainView;
import de.uniba.kinf.jerusalem.gui.view.map.JerMapComponent;
import de.uniba.kinf.jerusalem.gui.view.tablepanels.JerTableContainer;
import de.uniba.kinf.jerusalem.gui.view.workpanels.JerTabbedPane;
import de.uniba.kinf.jerusalem.gui.view.workpanels.JerWorkPanel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.subclasses.JerAuthorWorkPanel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.subclasses.JerDocumentWorkPanel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.subclasses.JerEntryWorkPanel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.subclasses.JerPlaceWorkPanel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.subclasses.JerToposInEntryWorkPanel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.subclasses.JerToposWorkPanel;

/**
 * Creates {@link JerMainModel}, {@link JerMapComponent}, {@link JerMainView},
 * and all: {@link JerModel}, {@link JerTableModel}, {@link JerWorkPanel},
 * {@link JerTableContainer} ; establishes observer relation: JerTableModels,
 * JerWorkPanels, JerMapComponent, JerTabbedPane as observer of JerMainModel.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerMainController {

        private static final Logger LOGGER = JerLogger.getLogger();
        private final JerMainModel mainModel;
        private final JerPropsHelper propsHelper;

        public JerMainController(final JerPropsHelper jph) throws SQLException,
                        ParseException, IOException, ClassNotFoundException,
                        InstantiationException, IllegalAccessException,
                        UnsupportedLookAndFeelException,
                        NoninvertibleTransformException {

                propsHelper = jph;
                final Properties properties = propsHelper.getProperties();
                new JerResourceBundleAccessor(properties);

                mainModel = new JerMainModel(properties);

                final JerAuthorModel am = new JerAuthorModel(mainModel,
                                properties.getProperty("DB_table_authors"));
                final JerDocumentModel dm = new JerDocumentModel(mainModel,
                                properties.getProperty("DB_table_documents"));
                final JerEntryModel em = new JerEntryModel(mainModel,
                                properties.getProperty("DB_table_entries"));
                final JerToposInEntryModel tiem = new JerToposInEntryModel(
                                mainModel,
                                properties.getProperty("DB_table_topos_in_entry"));
                final JerPlaceModel pm = new JerPlaceModel(mainModel,
                                properties.getProperty("DB_table_places"));
                final JerToposModel tm = new JerToposModel(mainModel,
                                properties.getProperty("DB_table_topoi"));

                final List<JerModel> jerModels = new ArrayList<>();
                jerModels.add(am);
                jerModels.add(dm);
                jerModels.add(em);
                jerModels.add(tiem);
                jerModels.add(pm);
                jerModels.add(tm);

                final List<JerTableModel> tableModels = new ArrayList<>(
                                jerModels.size());
                tableModels.add(new JerTableModel(am));
                tableModels.add(new JerTableModel(dm));
                tableModels.add(new JerTableModel(em));
                tableModels.add(new JerTableModel(tiem));
                tableModels.add(new JerTableModel(pm));
                tableModels.add(new JerTableModel(tm));

                // order of workpanels is important for
                // JerMainView.addWorkPanels()
                final List<JerWorkPanel> workpanels = new ArrayList<>(
                                jerModels.size());
                workpanels.add(new JerAuthorWorkPanel(am));
                workpanels.add(new JerDocumentWorkPanel(dm));
                workpanels.add(new JerEntryWorkPanel(em));
                workpanels.add(new JerToposInEntryWorkPanel(tiem));
                workpanels.add(new JerPlaceWorkPanel(pm));
                workpanels.add(new JerToposWorkPanel(tm));

                for (final JerTableModel tableModel : tableModels) {
                        mainModel.addObserver(tableModel);
                }
                for (final JerWorkPanel wp : workpanels) {
                        mainModel.addObserver(wp);
                        // ensure that internal components(comboboxes) get
                        // current db status
                        // at startup
                        wp.updateInternalComponents();
                }

                final List<JerTableContainer> tableContainerList = new ArrayList<>(
                                jerModels.size());
                for (final JerTableModel tableModel : tableModels) {
                        final JerTableContainer tc = new JerTableContainer(
                                        tableModel, properties);
                        tableContainerList.add(tc);
                }

                final JerMapComponent mapComponent = new JerMapComponent(
                                mainModel, properties);
                final JerTabbedPane tabbedPane = new JerTabbedPane(mainModel,
                                workpanels);

                mainModel.addObserver(mapComponent);
                mainModel.addObserver(tabbedPane);

                final JerMainView mainView = new JerMainView(this, properties,
                                tabbedPane, mapComponent, tableContainerList);

                mainModel.setPlaceModel(pm);
                mainModel.getJerInfoMsgHandler().setTextComponent(
                                mainView.getInfoArea());
                mainModel.reloadMapWithAllFeatures();
                dm.setModelAndID(-1);
        }

        /**
         * Write current properties into properties file before exiting.
         */
        public final void exit() {
                try {
                        propsHelper.saveProperties();
                        mainModel.shutdownDB();
                        LOGGER.info("System exit !");
                        System.exit(0);
                } catch (final IOException e1) {
                        LOGGER.severe("cannot store properties file "
                                        + e1.getMessage());
                        System.exit(-1);
                } catch (final SQLException e) {
                        if ("XJ015".equals(e.getSQLState())) {
                                // ignore error according to derby
                                // documentation: error signals
                                // clean shutdown of database
                                LOGGER.info("Derby shut down normally");
                                LOGGER.info("System exit !");
                                System.exit(0);
                        } else {
                                LOGGER.info("Cannot shutdown Derby database. SQL state: "
                                                + e.getSQLState());
                                System.exit(-1);
                        }
                }
        }

        /**
         * Triggers exportAllData in JerMainModel.
         * 
         * @param location
         *                to export to
         */
        public final void export(final String location) {
                mainModel.exportAllData(location);
        }

        public JerMainModel getMainModel() {
                return mainModel;
        }

        /**
         * set next model in mainmodel.
         */
        public void setNextModelID() {
                mainModel.setNextModelID();
        }

        /**
         * set previous model in mainmodel.
         */
        public void setPreviousModelID() {
                mainModel.setPreviousModelID();
        }

}
