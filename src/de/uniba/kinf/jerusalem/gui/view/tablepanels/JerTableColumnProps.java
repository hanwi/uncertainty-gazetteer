package de.uniba.kinf.jerusalem.gui.view.tablepanels;

import de.uniba.kinf.jerusalem.gui.model.JerTableModel;

/**
 * Bundles information about specific table column of {@link JerTableModel}.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerTableColumnProps {
        private String identifier;
        private JerTableModel tableModel;
        private boolean visible;

        public JerTableColumnProps(final String name, final boolean visible1,
                        final JerTableModel model) {
                identifier = name;
                this.visible = visible1;
                tableModel = model;
        }

        public String getIdent() {
                return identifier;
        }

        public JerTableModel getModel() {
                return tableModel;
        }

        public boolean isVisible() {
                return visible;
        }

        public void setIdent(final String name) {
                this.identifier = name;
        }

        public void setModel(final JerTableModel model) {
                this.tableModel = model;
        }

        public void setVisible(final boolean visible1) {
                this.visible = visible1;
        }

}
