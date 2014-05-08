package de.uniba.kinf.jerusalem.gui.helper.subclasses;

import java.util.ArrayList;
import java.util.List;

import de.uniba.kinf.jerusalem.gui.helper.JerMsgType;
import de.uniba.kinf.jerusalem.gui.helper.JerObserverMsg;
import de.uniba.kinf.jerusalem.gui.helper.JerPlace;

/**
 * {@link JerObserverMsg} class that transports {@link JerPlace}.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerGeoObserverMsg extends JerObserverMsg {

        private final List<JerPlace> placeLi;

        public JerGeoObserverMsg() {
                super(JerMsgType.GEOMETRY);
                placeLi = new ArrayList<>();
        }

        public void addPlaceToLi(final JerPlace jp) {
                getPlaceLi().add(jp);
        }

        public List<JerPlace> getPlaceLi() {
                return placeLi;
        }

}
