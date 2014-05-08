package de.uniba.kinf.jerusalem.gui.helper.subclasses;

import de.uniba.kinf.jerusalem.gui.helper.JerMsgType;
import de.uniba.kinf.jerusalem.gui.helper.JerObserverMsg;

/**
 * {@link JerObserverMsg} class that indicates: show tab with indicated IDName.
 * 
 * @author Hanno Wierichs
 * 
 */
public final class JerShowTabObserverMsg extends JerObserverMsg {

        private final String idName;

        public JerShowTabObserverMsg(final String iDName) {
                super(JerMsgType.SHOWTAB);
                this.idName = iDName;
        }

        public String getIdName() {
                return idName;
        }

}
