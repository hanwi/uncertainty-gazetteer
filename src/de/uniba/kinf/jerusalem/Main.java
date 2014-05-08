package de.uniba.kinf.jerusalem;

import java.awt.Color;
import java.awt.geom.NoninvertibleTransformException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Level;

import javax.swing.UnsupportedLookAndFeelException;

import de.uniba.kinf.jerusalem.gui.controller.JerMainController;
import de.uniba.kinf.jerusalem.gui.helper.JerLogger;
import de.uniba.kinf.jerusalem.gui.helper.JerPropsHelper;

/**
 * Program entry point.
 * 
 * @author Hanno Wierichs
 * 
 */
public class Main {

        /**
         * Set DEBUG to false if deploy.
         */
        public static final boolean DEBUG = false;
        public static final Color DEFCOLOROK = Color.WHITE;
        public static final Color DEFCOLORPROBLEM = Color.ORANGE;
        public static final int DEFUNITINCREMENT = 16;

        /**
         * @param args
         *                if DEBUG: args[0] indicate location of properties
         *                file.
         */
        public static void main(final String[] args) {
                try {
                        final JerPropsHelper jph = new JerPropsHelper(args);
                        new JerLogger(jph.getProperties(), Level.INFO);
                        new JerMainController(jph);
                } catch (ClassNotFoundException | InstantiationException
                                | IllegalAccessException | SQLException
                                | ParseException | IOException
                                | UnsupportedLookAndFeelException
                                | NoninvertibleTransformException e) {
                        System.err.println(e.getMessage());
                        JerLogger.getLogger().severe(e.toString());
                        System.exit(-1);
                }
        }
}
