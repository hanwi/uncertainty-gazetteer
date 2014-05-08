package de.uniba.kinf.jerusalem.gui.helper;

import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Provides access to {@link ResourceBundle} specified in properties file.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerResourceBundleAccessor {
        private static ResourceBundle resourcebundle;

        /**
         * @param str
         *                key for Resourcebundle.
         * @return corresponding String in Resourcebundle.
         */
        public static String get(final String str) {
                return resourcebundle.getString(str);
        }

        public JerResourceBundleAccessor(final Properties properties) {
                final String baseName = properties
                                .getProperty("resourceBundle_baseName")
                                + "_"
                                + properties.getProperty("language_acronym")
                                + "_"
                                + properties.getProperty("country_acronym");
                resourcebundle = ResourceBundle.getBundle(baseName);
        }

}
