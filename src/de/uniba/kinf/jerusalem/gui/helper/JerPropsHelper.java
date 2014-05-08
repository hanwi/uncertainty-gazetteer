package de.uniba.kinf.jerusalem.gui.helper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import de.uniba.kinf.jerusalem.Main;

/**
 * If !DEBUG in Main: (if necessary create directory system), then copy data,
 * then set properties according to created directories, then load properties.
 * If DEBUG in Main: load properties from given location.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerPropsHelper {
        private final Properties properties = new Properties();
        private File propertiesFile;

        public JerPropsHelper(final String[] args) throws IOException {

                final String propertiesName = "config.properties";
                final File jerFolderDir = new File(getClass()
                                .getProtectionDomain().getCodeSource()
                                .getLocation().getPath(), "JerusalemData");
                final File resourcesFolderDir = new File(
                                jerFolderDir.getAbsolutePath(),
                                "JerusalemResources");
                final File propertiesFolderDir = new File(resourcesFolderDir,
                                "properties");

                propertiesFile = new File(propertiesFolderDir, propertiesName);

                if (Main.DEBUG) {
                        propertiesFile = new File(args[0]);
                        loadProperties();
                } else {
                        if (!propertiesFile.exists()
                                        && !propertiesFile.isFile()) {
                                final File logFolderDir = new File(
                                                resourcesFolderDir, "logging");
                                final File scriptsDir = new File(
                                                resourcesFolderDir, "scripts");
                                final File imgDir = new File(
                                                resourcesFolderDir, "images");
                                final File backupDir = new File(
                                                resourcesFolderDir, "backups");

                                logFolderDir.mkdirs();
                                propertiesFolderDir.mkdirs();
                                scriptsDir.mkdirs();
                                imgDir.mkdirs();
                                backupDir.mkdirs();

                                copyPropertiesToFileSystem(propertiesName);

                                loadProperties();

                                final String scriptName = properties
                                                .getProperty("script_name");
                                final String defMapStr = properties
                                                .getProperty("defaultmap");
                                final String imgName = properties
                                                .getProperty(defMapStr
                                                                + "_filename");
                                final File logFile = new File(logFolderDir,
                                                "logging.txt");
                                final File derbyLogFile = new File(
                                                logFolderDir, "derby.log");
                                final File scriptFile = new File(scriptsDir,
                                                scriptName);
                                final File imgFile = new File(imgDir, imgName);

                                copyDataToFileSystem(scriptName, scriptFile);
                                copyDataToFileSystem(imgName, imgFile);

                                final String dbHomePath = jerFolderDir
                                                + File.separator
                                                + "JerusalemDB";
                                properties.setProperty(
                                                "derby_system_home_path",
                                                dbHomePath);
                                final String dbPath = dbHomePath
                                                + File.separator + "DB";
                                properties.setProperty("DB_absolute_path",
                                                dbPath);
                                properties.setProperty(
                                                "DB_SQL_file_absolute_path",
                                                scriptFile.getCanonicalPath());
                                final String nameDefMap = properties
                                                .getProperty("defaultmap");
                                properties.setProperty(nameDefMap
                                                + "_absolute_path",
                                                imgFile.getCanonicalPath());
                                properties.setProperty(
                                                "log_file_absolute_path",
                                                logFile.getCanonicalPath());
                                properties.setProperty(
                                                "derby_stream_error_file_path",
                                                derbyLogFile.getCanonicalPath());
                                properties.setProperty(
                                                "backup_dir_absolute_path",
                                                backupDir.getCanonicalPath());
                                saveProperties();
                        }
                        loadProperties();
                }
        }

        private void copyDataToFileSystem(final String name, final File file)
                        throws IOException {
                try (InputStream is = getClass().getClassLoader()
                                .getResourceAsStream(name)) {
                        Files.copy(is, file.toPath(),
                                        StandardCopyOption.REPLACE_EXISTING);
                }
        }

        private void copyPropertiesToFileSystem(final String propName)
                        throws IOException {
                try (BufferedInputStream inStream = new BufferedInputStream(
                                getClass().getClassLoader()
                                                .getResourceAsStream(propName))) {
                        properties.load(inStream);
                        saveProperties();
                }
        }

        public final Properties getProperties() {
                return properties;
        }

        private void loadProperties() throws IOException {
                try (BufferedInputStream inStream = new BufferedInputStream(
                                new FileInputStream(propertiesFile))) {
                        properties.load(inStream);
                }
        }

        /**
         * Save properties into properties file.
         * 
         * @throws IOException
         */
        public final void saveProperties() throws IOException {
                try (BufferedOutputStream outStream = new BufferedOutputStream(
                                new FileOutputStream(propertiesFile))) {
                        properties.store(outStream, "");
                }
        }

}
