package de.uniba.kinf.jerusalem.gui.view;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.JTextComponent;

import de.uniba.kinf.jerusalem.Main;
import de.uniba.kinf.jerusalem.gui.controller.JerMainController;
import de.uniba.kinf.jerusalem.gui.helper.JerLogger;
import de.uniba.kinf.jerusalem.gui.helper.JerResourceBundleAccessor;
import de.uniba.kinf.jerusalem.gui.view.helper.JerHelpDialog;
import de.uniba.kinf.jerusalem.gui.view.helper.JerPropChangeListener;
import de.uniba.kinf.jerusalem.gui.view.map.JerMapComponent;
import de.uniba.kinf.jerusalem.gui.view.tablepanels.JerTableContainer;
import de.uniba.kinf.jerusalem.gui.view.workpanels.JerTabbedPane;

/**
 * Sets main frame and inner frames. Positions frames according to properties
 * file.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerMainView extends JFrame {

        private static final int DIVIDERSIZE = 8;
        /**
     * 
     */
        private static final long serialVersionUID = 7025269030047675599L;

        private final JMenuItem analysisItem;
        private final JButton backBtn;
        private final JMenuItem exitItem;
        private final JMenuItem exportDataItem;
        private final JButton forwardBtn;
        private final JMenuItem helpItem;
        private final JTextArea jta;
        private final JerMainController mainController;
        private final JFrame self;

        public JerMainView(final JerMainController mainCtrl,
                        final Properties props, final JerTabbedPane tabbedPane,
                        final JerMapComponent jMComp,
                        final List<JerTableContainer> tableContainerList)
                        throws IOException, ClassNotFoundException,
                        InstantiationException, IllegalAccessException,
                        UnsupportedLookAndFeelException {

                setTitle("Jerusalem - "
                                + JerResourceBundleAccessor.get("database"));
                setResizable(true);
                setExtendedState(MAXIMIZED_BOTH);
                try {
                        for (final LookAndFeelInfo info : UIManager
                                        .getInstalledLookAndFeels()) {
                                if ("Nimbus".equals(info.getName())) {
                                        UIManager.setLookAndFeel(info
                                                        .getClassName());
                                        break;
                                }
                        }
                } catch (final Exception e) {
                        JerLogger.getLogger().info(e.getLocalizedMessage());
                        UIManager.setLookAndFeel(UIManager
                                        .getSystemLookAndFeelClassName());
                }

                self = this;
                mainController = mainCtrl;

                jta = new JTextArea(1, 1);

                jta.setLineWrap(true);
                jta.setFocusable(false);

                final JMenu fileMenu = new JMenu(
                                JerResourceBundleAccessor.get("file"));
                fileMenu.add(helpItem = new JMenuItem(JerResourceBundleAccessor
                                .get("manual")));
                fileMenu.add(analysisItem = new JMenuItem(
                                JerResourceBundleAccessor.get("analysis")));
                fileMenu.add(exportDataItem = new JMenuItem(
                                JerResourceBundleAccessor.get("export")));
                fileMenu.add(exitItem = new JMenuItem(JerResourceBundleAccessor
                                .get("exit")));

                final JMenuBar mainMenuBar = new JMenuBar();
                mainMenuBar.add(Box.createHorizontalStrut(5));
                mainMenuBar.add(fileMenu);
                mainMenuBar.add(backBtn = new JButton(JerResourceBundleAccessor
                                .get("back")));
                addAccelerator(backBtn, props, "backInHistory");
                mainMenuBar.add(forwardBtn = new JButton(
                                JerResourceBundleAccessor.get("forward")));
                addAccelerator(forwardBtn, props, "forwardInHistory");

                mainMenuBar.add(new JScrollPane(jta));
                setJMenuBar(mainMenuBar);

                final JPanel contentPane = new JPanel();
                contentPane.setLayout(new BoxLayout(contentPane,
                                BoxLayout.X_AXIS));

                final JSplitPane splitPaneVertical = new JSplitPane(
                                JSplitPane.VERTICAL_SPLIT, true);

                final JSplitPane splitPaneHorizontal = new JSplitPane(
                                JSplitPane.HORIZONTAL_SPLIT, true);

                JComponent componentTableContainers = tableContainerList
                                .remove(0).getJPanel();
                int count = 0;
                for (final JerTableContainer tableContainer : tableContainerList) {
                        count++;
                        final JSplitPane jsp1 = new JSplitPane(
                                        JSplitPane.HORIZONTAL_SPLIT, true,
                                        componentTableContainers,
                                        tableContainer.getJPanel());
                        int rpos = -1;
                        final String propname = "splitpane" + count;
                        final String srpos = props.getProperty(propname);
                        if (srpos != null) {
                                rpos = Integer.parseInt(srpos);
                        }
                        jsp1.setDividerSize(DIVIDERSIZE);
                        jsp1.setDividerLocation(rpos);
                        jsp1.setOneTouchExpandable(true);
                        jsp1.addPropertyChangeListener(
                                        JSplitPane.DIVIDER_LOCATION_PROPERTY,
                                        new JerPropChangeListener(props,
                                                        propname));
                        componentTableContainers = jsp1;
                }

                final JScrollPane scrPaneCompound = new JScrollPane(tabbedPane);
                scrPaneCompound.getVerticalScrollBar().setUnitIncrement(
                                Main.DEFUNITINCREMENT);
                scrPaneCompound.getHorizontalScrollBar().setUnitIncrement(
                                Main.DEFUNITINCREMENT);

                splitPaneHorizontal.setLeftComponent(componentTableContainers);
                splitPaneHorizontal.setRightComponent(scrPaneCompound);
                adjustSplitPane(splitPaneHorizontal, props,
                                "splitpaneHorizontal");

                splitPaneVertical.setLeftComponent(splitPaneHorizontal);
                splitPaneVertical.setRightComponent(jMComp);
                adjustSplitPane(splitPaneVertical, props, "splitpaneVertical");

                addEventListener();
                contentPane.add(splitPaneVertical);
                setContentPane(contentPane);

                // because some components are created before application's
                // look and feel is set
                SwingUtilities.updateComponentTreeUI(contentPane);
                setVisible(true);
        }

        private void addEventListener() {
                addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(final WindowEvent arg0) {
                                mainController.exit();
                        }
                });

                exitItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(final ActionEvent arg0) {
                                mainController.exit();
                        }
                });

                helpItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(final ActionEvent e) {
                                new JerHelpDialog();
                        }
                });

                analysisItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(final ActionEvent e) {
                                new JerAnalysisDialog(mainController
                                                .getMainModel());
                        }
                });

                backBtn.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(final ActionEvent e) {
                                mainController.setPreviousModelID();
                        }
                });

                forwardBtn.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(final ActionEvent e) {
                                mainController.setNextModelID();
                        }
                });

                exportDataItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(final ActionEvent e) {
                                final JFileChooser fileChooser = new JFileChooser(
                                                new File("."));
                                fileChooser.setFileFilter(new FileNameExtensionFilter(
                                                "CSV", "csv"));
                                final int option = fileChooser
                                                .showSaveDialog(self);
                                if (option == JFileChooser.APPROVE_OPTION) {
                                        mainController.export(fileChooser
                                                        .getSelectedFile()
                                                        .getAbsolutePath());
                                }
                        }
                });
        }

        private void adjustSplitPane(final JSplitPane splitPane,
                        final Properties props, final String string) {
                splitPane.setDividerSize(DIVIDERSIZE);
                splitPane.setOneTouchExpandable(true);
                splitPane.addPropertyChangeListener(
                                JSplitPane.DIVIDER_LOCATION_PROPERTY,
                                new JerPropChangeListener(props, string));
                int positionSplitPane = -1;
                final String srpos = props.getProperty(string);
                if (srpos != null) {
                        positionSplitPane = Integer.parseInt(srpos);
                }
                splitPane.setDividerLocation(positionSplitPane);
        }

        public JTextComponent getInfoArea() {
                return jta;
        }

        public static void addAccelerator(final JComponent c, Properties props,
                        final String property) {
                int val = Integer.parseInt(props.getProperty("keyEvent_"
                                + property));
                KeyStroke k = KeyStroke.getKeyStroke(val, Toolkit
                                .getDefaultToolkit().getMenuShortcutKeyMask());
                c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(k, "act");
                Action act = new AbstractAction() {
                        /**
                         * 
                         */
                        private static final long serialVersionUID = -6134818442304310701L;

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                if (c instanceof JButton) {
                                        ((JButton) c).doClick();
                                }
                                if (c instanceof JTabbedPane) {
                                        c.requestFocus();
                                }
                        }
                };
                c.getActionMap().put("act", act);
        }

        public static String getTextualMenuShortCutKeyMask() {
                String str = "";
                int k = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
                if (k == 2) {
                        str = "modifier key: CTRL";
                }
                if (k == 4) {
                        str = "modifier key: META";
                }
                if (k == 8) {
                        str = "modifier key: ALT";
                }
                return str;
        }

}
