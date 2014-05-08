package de.uniba.kinf.jerusalem.gui.view.workpanels;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import de.uniba.kinf.jerusalem.gui.helper.JerMsgType;
import de.uniba.kinf.jerusalem.gui.helper.JerObserverMsg;
import de.uniba.kinf.jerusalem.gui.helper.JerResourceBundleAccessor;
import de.uniba.kinf.jerusalem.gui.helper.subclasses.JerShowTabObserverMsg;
import de.uniba.kinf.jerusalem.gui.helper.subclasses.JerTabObserverMsg;
import de.uniba.kinf.jerusalem.gui.model.JerMainModel;
import de.uniba.kinf.jerusalem.gui.model.JerModel;
import de.uniba.kinf.jerusalem.gui.model.helper.JerSelectorIF;
import de.uniba.kinf.jerusalem.gui.model.subclasses.JerPlaceModel;
import de.uniba.kinf.jerusalem.gui.view.JerMainView;

/**
 * Adapted {@link JTabbedPane} to correspond to selected item in
 * {@link JerMainModel}.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerTabbedPane extends JTabbedPane implements Observer {

        /**
     * 
     */
        private static final long serialVersionUID = 90558139160637773L;
        private final List<JerWorkPanel> wpLi;
        private final JerTabbedPane self;

        public JerTabbedPane(final JerMainModel mainModel,
                        final List<JerWorkPanel> jerTabs) {
                setMinimumSize(new Dimension(1, 1));
                setPreferredSize(new Dimension(350, 300));
                setFocusCycleRoot(true);
                wpLi = jerTabs;
                self = this;
                setDefTabs(mainModel.getProps());
                addFocusListener(new FocusListener() {

                        @Override
                        public void focusLost(FocusEvent e) {
                        }

                        @Override
                        public void focusGained(FocusEvent e) {
                                JerModel m = wpLi.get(self.getSelectedIndex()).jm;
                                if (!m.getIdName().equals("TOPOS_IN_ENTRY_ID")) {
                                        mainModel.setModelAndIDAndNotify(
                                                        wpLi.get(self.getSelectedIndex()).jm,
                                                        -1);
                                }
                        }
                });
                setSelectedIndex(0);
                // Dimension d = getPreferredSize();
                // setMaximumSize(d);
                JerMainView.addAccelerator(this, mainModel.getProps(),
                                "changeTab");
        }

        public void setCorrespondingTab(final String idName) {
                if (idName.equals("AUTHOR_ID")) {
                        setSelectedIndex(0);
                } else if (idName.equals("DOCUMENT_ID")) {
                        setSelectedIndex(1);
                } else if (idName.equals("ENTRY_ID")) {
                        setSelectedIndex(2);
                } else if (idName.equals("PLACE_ID")) {
                        setSelectedIndex(4);
                } else if (idName.equals("TOPOS_ID")) {
                        setSelectedIndex(5);
                } else if (idName.equals("TOPOS_IN_ENTRY_ID")) {
                        setSelectedIndex(3);
                }
        }

        private void setDefTabs(Properties props) {
                removeAll();
                for (int i = 0; i < wpLi.size(); i++) {
                        final JerWorkPanel jt = wpLi.get(i);
                        addTab(JerResourceBundleAccessor.get(jt.getClass()
                                        .getSimpleName().toLowerCase()),
                                        jt.getBasePanel());
                }
        }

        public void setAccelerators(Properties props) {
                removeAll();
                for (int i = 0; i < wpLi.size(); i++) {
                        final JerWorkPanel jt = wpLi.get(i);
                        JComponent c = jt.getBasePanel();
                        addTab(JerResourceBundleAccessor.get(jt.getClass()
                                        .getSimpleName().toLowerCase()), c);
                }
        }

        @Override
        public void update(final Observable o, final Object arg) {
                final JerObserverMsg msg = (JerObserverMsg) arg;
                final JerSelectorIF s = (JerSelectorIF) o;

                if (msg.getType() == JerMsgType.SELECTION) {
                        setCorrespondingTab(s.getSelectedModel().getIdName());
                }
                if (msg.getType() == JerMsgType.GEOMETRY
                                && s.getSelectedModel() instanceof JerPlaceModel) {
                        setCorrespondingTab("PLACE_ID");
                }
                if (msg.getType() == JerMsgType.TAB) {
                        final JerTabObserverMsg m = (JerTabObserverMsg) msg;
                        setCorrespondingTab(m.getDestIDName());
                }
                if (msg.getType() == JerMsgType.SHOWTAB) {
                        final JerShowTabObserverMsg m = (JerShowTabObserverMsg) msg;
                        setCorrespondingTab(m.getIdName());
                }
        }

}
