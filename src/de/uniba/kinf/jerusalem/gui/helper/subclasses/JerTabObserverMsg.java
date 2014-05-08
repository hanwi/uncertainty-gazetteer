package de.uniba.kinf.jerusalem.gui.helper.subclasses;

import de.uniba.kinf.jerusalem.gui.helper.JerMsgType;
import de.uniba.kinf.jerusalem.gui.helper.JerObserverMsg;
import de.uniba.kinf.jerusalem.gui.model.JerModel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.JerWorkPanel;

/**
 * {@link JerObserverMsg} class to switch between {@link JerWorkPanel} and
 * provide additional information.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerTabObserverMsg extends JerObserverMsg {

        @Override
        public String toString() {
                return "JerTabObserverMsg [additionalIDtoBeSet="
                                + additionalIDtoBeSet + ", destID=" + destID
                                + ", destIDName=" + destIDName
                                + ", initiatingModel=" + initiatingModel
                                + ", initiatingVal=" + initiatingVal
                                + ", nameAdditionalFieldInfo="
                                + nameAdditionalFieldInfo + "]";
        }

        private final int additionalIDtoBeSet;
        private final int destID;
        private final String destIDName;
        private final JerModel initiatingModel;
        private final int initiatingVal;
        private final String nameAdditionalFieldInfo;

        public JerTabObserverMsg(final JerModel initialModel,
                        final int initialVal, final String idNameModelDestTab,
                        final int idDestTab,
                        final String nameAdditionlFieldInfo,
                        final int additionlIDtoBeSet) {
                super(JerMsgType.TAB);
                initiatingModel = initialModel;
                initiatingVal = initialVal;
                destIDName = idNameModelDestTab;
                destID = idDestTab;
                nameAdditionalFieldInfo = nameAdditionlFieldInfo;
                additionalIDtoBeSet = additionlIDtoBeSet;
        }

        public int getAdditionalIDtoBeSet() {
                return additionalIDtoBeSet;
        }

        public int getDestID() {
                return destID;
        }

        public String getDestIDName() {
                return destIDName;
        }

        public JerModel getInitiatingModel() {
                return initiatingModel;
        }

        public int getInitiatingVal() {
                return initiatingVal;
        }

        public String getNameAdditionalFieldInfo() {
                return nameAdditionalFieldInfo;
        }

}
