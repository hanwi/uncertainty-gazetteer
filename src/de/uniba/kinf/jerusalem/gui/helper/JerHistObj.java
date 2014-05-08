package de.uniba.kinf.jerusalem.gui.helper;

import de.uniba.kinf.jerusalem.gui.model.JerModel;

/**
 * Represents state of selection in GUI: {@link JerModel} and ID.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerHistObj {
        private final int id;
        private final JerModel model;

        public JerHistObj(final JerModel jm, final int iD) {
                this.model = jm;
                this.id = iD;
        }

        public int getId() {
                return id;
        }

        public JerModel getModel() {
                return model;
        }
}
