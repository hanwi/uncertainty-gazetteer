package de.uniba.kinf.jerusalem.gui.helper;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Logs to log_file_absolute_path in properties file.
 * 
 * @author Hanno Wierichs
 * 
 */
public final class JerLogger {

        private static Logger logger = null;

        public static synchronized Logger getLogger() {
                return logger;
        }

        public JerLogger(final Properties properties, final Level initLevel)
                        throws IOException {
                logger = Logger.getLogger(JerLogger.class.getName());
                logger.setUseParentHandlers(false);
                logger.setLevel(initLevel);
                final File logFile = new File(
                                properties.getProperty("log_file_absolute_path"));
                final FileHandler filehandler = new FileHandler(
                                logFile.getCanonicalPath(), 102000, 1, false);
                filehandler.setLevel(Level.ALL);
                filehandler.setFormatter(new SimpleFormatter());
                logger.addHandler(filehandler);
        }

}
