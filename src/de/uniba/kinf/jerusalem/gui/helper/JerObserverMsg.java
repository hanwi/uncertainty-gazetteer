package de.uniba.kinf.jerusalem.gui.helper;

/**
 * General ObserverMsg. See gui.helper.subclasses.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerObserverMsg {

        private final JerMsgType type;

        public JerObserverMsg(final JerMsgType t) {
                type = t;
        }

        public final JerMsgType getType() {
                return type;
        }

}
