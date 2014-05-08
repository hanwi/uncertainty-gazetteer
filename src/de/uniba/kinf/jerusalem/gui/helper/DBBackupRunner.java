package de.uniba.kinf.jerusalem.gui.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import de.uniba.kinf.jerusalem.gui.model.JerMainModel;

/**
 * Backups database; information of backuplocation, interval, maxNumber of
 * backups, cutoff time via properties file.
 * 
 * @author Hanno Wierichs
 * 
 */
public class DBBackupRunner extends Thread {

        private static final Logger LOGGER = JerLogger.getLogger();
        private final String backupLocation;
        private final JerMainModel mainModel;

        public DBBackupRunner(final JerMainModel jerMainModel) {
                mainModel = jerMainModel;
                backupLocation = mainModel.getProps().getProperty(
                                "backup_dir_absolute_path");
        }

        // if number of files higher than maxnumberbackups: delete all files
        // except
        // most recent file and most recent file before cutoff, then backup
        // database
        @SuppressWarnings({ "boxing" })
        private void backUpDatabase() {
                final int maxNumberBackups = Integer.parseInt(mainModel
                                .getProps().getProperty("backup_max_files"));
                final int cutoffMins = Integer.parseInt(mainModel.getProps()
                                .getProperty("backup_cutoff_mins"));
                final SimpleDateFormat dateFo = new SimpleDateFormat(
                                "yyyy-MM-dd-H-mm-ss-SS");

                final File dir = new File(backupLocation);
                final File[] filAr = dir.listFiles();

                if (filAr.length >= maxNumberBackups) {
                        // try to convert filenames into dates... get correctly
                        // formatted
                        // date directories
                        final List<Date> daObjLi = new ArrayList<>();
                        for (final File str : filAr) {
                                try {
                                        daObjLi.add(dateFo.parse(str.getName()));
                                } catch (final ParseException e) {
                                        LOGGER.severe(e.toString());
                                        e.printStackTrace();
                                        mainModel.getJerInfoMsgHandler()
                                                        .showMsg(JerResourceBundleAccessor
                                                                        .get("backup_problem"));
                                }
                        }
                        // check if number of correctly formatted directories is
                        // higher than
                        // maxNumberBackups, sort them
                        if ((daObjLi.size() >= maxNumberBackups)
                                        && !daObjLi.isEmpty()) {
                                Collections.sort(daObjLi);

                                final Date cutoffDate = new Date(
                                                System.currentTimeMillis()
                                                                - (-cutoffMins * 60 * 1000));
                                Date posOldestVal = daObjLi.get(0);
                                Date posMostRecentVal = daObjLi.get(0);

                                // get most recent date before cutoff
                                for (int i = 0; i < daObjLi.size(); i++) {
                                        final Date dat = daObjLi.get(i);
                                        if (dat.before(cutoffDate)) {
                                                posOldestVal = dat;
                                        }
                                        if (dat.after(posOldestVal)) {
                                                posMostRecentVal = dat;
                                        }
                                }
                                daObjLi.remove(posOldestVal);
                                if (!posMostRecentVal.equals(posOldestVal)) {
                                        daObjLi.remove(posMostRecentVal);
                                }

                                for (final Date d : daObjLi) {
                                        try {
                                                Files.walkFileTree(
                                                                Paths.get(dir.getAbsolutePath()
                                                                                + File.separator
                                                                                + dateFo.format(d)),
                                                                new DeleteDirVisitor());
                                        } catch (final IOException e) {
                                                LOGGER.info(e.toString());
                                                e.printStackTrace();
                                                mainModel.getJerInfoMsgHandler()
                                                                .showMsg(JerResourceBundleAccessor
                                                                                .get("backup_problem"));
                                        }
                                }

                        }
                }

                try (CallableStatement cs = mainModel
                                .getConnection()
                                .prepareCall("CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)")) {
                        final String backupFile = backupLocation
                                        + File.separator
                                        + dateFo.format(System
                                                        .currentTimeMillis());
                        cs.setString(1, backupFile);
                        cs.execute();
                } catch (final SQLException e) {
                        LOGGER.info(e.toString());
                        e.printStackTrace();
                        mainModel.getJerInfoMsgHandler().showMsg(
                                        JerResourceBundleAccessor
                                                        .get("backup_problem"));
                }
        }

        @Override
        public final void run() {
                final int backupInterval = Integer.parseInt(mainModel
                                .getProps().getProperty(
                                                "backup_interval_in_minutes"));
                try {
                        while (true) {
                                final long slValue = backupInterval * 60000;
                                Thread.sleep(slValue);
                                backUpDatabase();
                        }
                } catch (final InterruptedException e) {
                        LOGGER.info(e.getMessage());
                        mainModel.getJerInfoMsgHandler().showMsg(
                                        JerResourceBundleAccessor
                                                        .get("backup_problem"));
                }
        }
}

class DeleteDirVisitor extends SimpleFileVisitor<Path> {

        @Override
        public FileVisitResult postVisitDirectory(final Path dir,
                        final IOException exc) throws IOException {
                if (exc == null) {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                }
                throw exc;
        }

        @Override
        public FileVisitResult visitFile(final Path file,
                        final BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
        }
}
