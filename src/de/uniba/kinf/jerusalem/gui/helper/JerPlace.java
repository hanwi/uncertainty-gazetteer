package de.uniba.kinf.jerusalem.gui.helper;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Represents a place that has one main location (as a JTS Geometry) and
 * additional instances (as a JTS Geometry).
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerPlace {

        private Geometry additionalInst;
        private final Geometry mainLoc;
        // -1 signals no number
        private int number = -1;

        public JerPlace(final Geometry mainLocation) {
                mainLoc = mainLocation;
        }

        public Geometry getAdditionalInst() {
                return additionalInst;
        }

        public Geometry getMainLoc() {
                return mainLoc;
        }

        public int getNumber() {
                return number;
        }

        public boolean hasAdditionalInstances() {
                return getAdditionalInst() != null;
        }

        /**
         * @return true if JerPlace has assigned ordinal number
         */
        public boolean hasNumber() {
                return getNumber() != -1;
        }

        public void setAdditionalInstances(final Geometry additionlInst) {
                additionalInst = additionlInst;
        }

        public void setNumber(final int numbr) {
                this.number = numbr;
        }

}
