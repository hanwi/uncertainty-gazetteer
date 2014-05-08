package de.uniba.kinf.jerusalem.gui.helper;

/**
 * Indicates type of message.
 * 
 * @author Hanno Wierichs
 * 
 */
public enum JerMsgType {
        /**
         * Sent by MainModel: db has changed
         */
        DB_UPDATE,
        /**
         * Sent by MainModel or MapComponent
         */
        GEOMETRY,
        /**
         * Sent by MainModel: Selected model/id has changed
         */
        SELECTION,
        /**
         * Sent by MainModel: @JerTabbedPane: show specified tab
         */
        SHOWTAB,
        /**
         * Sent by MainModel: open tab, do some action, then return to
         * initiating tab
         */
        TAB,
        /**
         * Sent by JerSelectionDialog: Table column visibility changed
         */
        TABLE_COLUMNS
}
