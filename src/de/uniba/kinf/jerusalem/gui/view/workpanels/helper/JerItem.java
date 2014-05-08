package de.uniba.kinf.jerusalem.gui.view.workpanels.helper;

/**
 * Represents item in {@link JerComboBox}.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerItem {
        private final Object ident;
        private final int itemID;

        public JerItem(final int id, final Object identifier) {
                this.ident = identifier;
                this.itemID = id;
        }

        public int getId() {
                return itemID;
        }

        public Object getIdent() {
                return this.ident;
        }

        @Override
        public String toString() {
                return ident + "";
        }

}
