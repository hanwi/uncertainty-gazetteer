package de.uniba.kinf.jerusalem.gui.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import de.uniba.kinf.jerusalem.Main;
import de.uniba.kinf.jerusalem.gui.helper.JerResourceBundleAccessor;
import de.uniba.kinf.jerusalem.gui.model.JerMainModel;
import de.uniba.kinf.jerusalem.gui.view.workpanels.helper.JerItem;

/**
 * Sets layout and datahandling for analysis.
 * 
 * @author Hanno Wierichs
 * 
 */

public class JerAnalysisDialog extends JDialog {

        private static final int MAXIMUMYEAR = Calendar.getInstance().get(
                        Calendar.YEAR);
        private static final int MINIMUMYEAR = Calendar.getInstance().get(
                        Calendar.YEAR) - 10000;
        /**
     * 
     */
        private static final long serialVersionUID = 1153764594949124897L;

        @SuppressWarnings("unchecked")
        protected static JerItem[] setLiValues(final List<Object> colLi) {
                final List<JerItem> li = new ArrayList<>();
                for (int i = 0; i < colLi.size(); i++) {
                        final List<Object> rowLi = (List<Object>) colLi.get(i);
                        if (rowLi.size() == 1) {
                                // -1 signals no id... not necessary in this
                                // case... also needed
                                // for makeSQL()
                                final JerItem jci = new JerItem(-1,
                                                rowLi.get(0));
                                li.add(jci);
                        }
                        if (rowLi.size() == 2) {
                                final JerItem jci = new JerItem(
                                                (Integer) rowLi.get(0),
                                                rowLi.get(1));
                                li.add(jci);
                        }
                }
                return li.toArray(new JerItem[0]);
        }

        // private JList<JerItem> authorLi;
        // private JScrollPane document_listScroller;
        // private JList<JerItem> documentLi;
        // private JScrollPane author_listScroller;
        private final JPanel centerPanel;
        private final ChartPanel chartPanel;
        private final JPanel dataPanel;
        private final JerMainModel mainModel;
        private SpinnerNumberModel modelEnd;
        private SpinnerNumberModel modelStart;
        private SpinnerNumberModel modelTrust;
        private JScrollPane origin_listScroller;
        private JList<JerItem> originLi;
        private JScrollPane place_listScroller;
        private JList<JerItem> placeLi;
        private JScrollPane religion_listScroller;
        private JList<JerItem> religionLi;
        private final JPanel selectPanel;
        private JScrollPane show_listScroller;
        private JList<JerItem> showLi;
        private JSpinner spinnerBegin;
        private JSpinner spinnerEnd;
        private JSpinner spinnerTrust;
        private String sqlStrAnalysis;
        private JScrollPane topos_listScroller;

        private JList<JerItem> toposLi;

        public JerAnalysisDialog(final JerMainModel mainMod) {

                sqlStrAnalysis = "";
                mainModel = mainMod;
                setModal(true);
                setTitle(JerResourceBundleAccessor.get("analysis"));
                setResizable(false);
                centerPanel = new JPanel();
                selectPanel = new JPanel();
                selectPanel.setLayout(new GridLayout(1, 4));
                selectPanel.setBackground(Main.DEFCOLORPROBLEM);
                dataPanel = new JPanel();
                dataPanel.setLayout(new GridLayout(3, 2));
                chartPanel = new ChartPanel(createChart(
                                new DefaultPieDataset(), ""));
                chartPanel.setMouseWheelEnabled(true);
                chartPanel.setPreferredSize(new Dimension(600, 300));

                createSpinner();
                createDataLists();

                makeLayout();
                setContentPane(centerPanel);
                pack();
                setVisible(true);

        }

        private void analyse() {

                if (showLi.getSelectionModel().isSelectionEmpty()) {
                        return;
                } else {

                        final JerItem elementToShow = showLi.getSelectedValue();

                        final int begin_year = modelStart.getNumber()
                                        .intValue();
                        final int end_year = modelEnd.getNumber().intValue();
                        final int trustVal = modelTrust.getNumber().intValue();

                        final String timeRestrictions = "AND ( (a.BEGIN_YEAR  >= "
                                        + begin_year
                                        + ") AND (a.END_YEAR <=  "
                                        + end_year
                                        + ") AND (d.BEGIN_YEAR  >= "
                                        + begin_year
                                        + ") AND (d.END_YEAR <=  "
                                        + end_year
                                        + ")  AND (p.BEGIN_YEAR  >= "
                                        + begin_year
                                        + ") AND (p.END_YEAR <=  "
                                        + end_year + ") )";
                        final String trustRestrictions = " AND (d.TRUST >= "
                                        + trustVal + ") ";
                        // final String authorRestrictions = makeSQL("a.",
                        // authorLi.getSelectedValuesList(), "AUTHOR_ID");
                        final String originRestrictions = makeSQL("a.",
                                        originLi.getSelectedValuesList(),
                                        "ORIGIN");
                        final String religionRestrictions = makeSQL("a.",
                                        religionLi.getSelectedValuesList(),
                                        "RELIGIOUS_DENOMINATION");
                        // final String documentRestrictions = makeSQL("d.",
                        // documentLi.getSelectedValuesList(), "DOCUMENT_ID");
                        final String placeRestrictions = makeSQL("p.",
                                        placeLi.getSelectedValuesList(),
                                        "PLACE_ID");
                        final String toposRestrictions = makeSQL("t.",
                                        toposLi.getSelectedValuesList(),
                                        "TOPOS_ID");
                        final DefaultPieDataset result = new DefaultPieDataset();

                        final String val3 =
                        // authorRestrictions +
                        originRestrictions
                                        + religionRestrictions
                                        // documentRestrictions
                                        + placeRestrictions + toposRestrictions
                                        + timeRestrictions
                                        + religionRestrictions
                                        + trustRestrictions;
                        switch (elementToShow.getId()) {
                        // case 1:
                        // putDataIntoDataset("a.NAME", "a.AUTHOR_ID", val3,
                        // result);
                        // break;
                        case 1:
                                putDataIntoDataset("a.ORIGIN", "a.AUTHOR_ID",
                                                val3, result);
                                break;
                        case 2:
                                putDataIntoDataset("a.RELIGIOUS_DENOMINATION",
                                                "a.AUTHOR_ID", val3, result);
                                break;
                        // case 4:
                        // putDataIntoDataset("d.TITLE", "d.DOCUMENT_ID", val3,
                        // result);
                        // break;
                        case 3:
                                putDataIntoDataset("p.NAME", "p.PLACE_ID",
                                                val3, result);
                                break;
                        case 4:
                                putDataIntoDataset("t.NAME", "t.TOPOS_ID",
                                                val3, result);
                                break;
                        default:
                                break;
                        }
                        makeDatasetAndChart(
                                        elementToShow.getIdent()
                                                        + "; "
                                                        + JerResourceBundleAccessor.get("jeranalysisdialgo_range")
                                                        + ": " + begin_year
                                                        + "-" + end_year,
                                        result);
                }
        }

        private JFreeChart createChart(final PieDataset dataset,
                        final String title) {
                final JFreeChart chart = ChartFactory.createPieChart(title,
                                dataset, false, true, false);
                final PiePlot plot = (PiePlot) chart.getPlot();
                plot.setBackgroundPaint(null);
                plot.setInteriorGap(0.05);
                plot.setOutlineVisible(false);
                plot.setBaseSectionOutlinePaint(Color.WHITE);
                plot.setSectionOutlinesVisible(true);
                plot.setBaseSectionOutlineStroke(new BasicStroke(2.0f));
                return chart;
        }

        private void createDataLists() {
                final JerItem[] showAr = {
                                // new JerItem(1,
                                // JerResourceBundleAccessor.get("author")),
                                new JerItem(1,
                                                JerResourceBundleAccessor
                                                                .get("ORIGIN")),
                                new JerItem(
                                                2,
                                                JerResourceBundleAccessor
                                                                .get("RELIGIOUS_DENOMINATION")),
                                // new JerItem(4,
                                // JerResourceBundleAccessor.get("jerdocumentworkpanel")),
                                new JerItem(
                                                3,
                                                JerResourceBundleAccessor
                                                                .get("jerplaceworkpanel")),
                                new JerItem(
                                                4,
                                                JerResourceBundleAccessor
                                                                .get("jertoposworkpanel")) };

                showLi = getValsForList(showAr,
                                ListSelectionModel.SINGLE_SELECTION);
                // authorLi =
                // getValsForList(setLiValues(mainModel.getAllAuthors()),
                // ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                originLi = getValsForList(
                                setLiValues(mainModel.getAllOrigin()),
                                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                religionLi = getValsForList(
                                setLiValues(mainModel.getAllReligion()),
                                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                // documentLi =
                // getValsForList(setLiValues(mainModel.getAllDocuments()),
                // ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                placeLi = getValsForList(setLiValues(mainModel.getAllPlaces()),
                                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                toposLi = getValsForList(setLiValues(mainModel.getAllTopoi()),
                                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                setAllLiSelected();

                show_listScroller = new JScrollPane(showLi);
                // author_listScroller = new JScrollPane(authorLi);
                origin_listScroller = new JScrollPane(originLi);
                religion_listScroller = new JScrollPane(religionLi);
                // document_listScroller = new JScrollPane(documentLi);
                place_listScroller = new JScrollPane(placeLi);
                topos_listScroller = new JScrollPane(toposLi);
        }

        private void createSpinner() {
                // final HashMap<String, Object> values = mainModel
                // .getMinBeginValueForTable();

                int initStartVal = Calendar.getInstance().get(Calendar.YEAR) - 1000;
                int initEndVal = Calendar.getInstance().get(Calendar.YEAR) - 500;
                // if (!values.isEmpty()) {
                //
                // initStartVal = (int) values.get("MIN_BEGIN_YEAR");
                // System.out.println(initEndVal);
                // System.out.println(initEndVal);
                // initEndVal = (int) values.get("MAX_END_YEAR");
                // }
                modelStart = new SpinnerNumberModel(initStartVal, MINIMUMYEAR,
                                MAXIMUMYEAR, 1);
                modelEnd = new SpinnerNumberModel(initEndVal, MINIMUMYEAR,
                                MAXIMUMYEAR, 1);
                modelTrust = new SpinnerNumberModel(1, 1, 5, 1);

                spinnerBegin = new JSpinner(modelStart);
                spinnerEnd = new JSpinner(modelEnd);
                spinnerTrust = new JSpinner(modelTrust);

                spinnerBegin.setEditor(new JSpinner.NumberEditor(spinnerBegin,
                                "#"));
                spinnerEnd.setEditor(new JSpinner.NumberEditor(spinnerEnd, "#"));
                spinnerTrust.setEditor(new JSpinner.NumberEditor(spinnerTrust,
                                "#"));

                spinnerBegin.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(final ChangeEvent e) {
                                analyse();
                        }
                });
                spinnerEnd.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(final ChangeEvent e) {
                                analyse();
                        }
                });

                spinnerTrust.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(final ChangeEvent e) {
                                analyse();
                        }
                });

        }

        public String getSqlDataForAnalysis() {
                return sqlStrAnalysis;
        }

        protected JList<JerItem> getValsForList(final JerItem[] a,
                        final int liSelModel) {
                final JList<JerItem> li = new JList<>(a);
                li.setSelectionMode(liSelModel);
                li.addListSelectionListener(new ListSelectionListener() {

                        @Override
                        public void valueChanged(final ListSelectionEvent e) {
                                if (!e.getValueIsAdjusting()) {
                                        analyse();
                                }
                        }
                });
                li.setLayoutOrientation(JList.HORIZONTAL_WRAP);
                li.setVisibleRowCount(-1);
                return li;
        }

        private void makeDatasetAndChart(final String title,
                        final DefaultPieDataset result) {
                final JFreeChart chart = createChart(result, title);
                chartPanel.setChart(chart);
        }

        private void makeLayout() {
                // dataPanel.add(author_listScroller);
                dataPanel.add(origin_listScroller);
                dataPanel.add(religion_listScroller);
                // dataPanel.add(document_listScroller);
                dataPanel.add(place_listScroller);
                dataPanel.add(topos_listScroller);

                selectPanel.add(show_listScroller);
                selectPanel.add(spinnerBegin);
                selectPanel.add(spinnerEnd);
                selectPanel.add(spinnerTrust);

                final GroupLayout groupLayoutReg = new GroupLayout(centerPanel);
                centerPanel.setLayout(groupLayoutReg);

                groupLayoutReg.setAutoCreateGaps(true);
                groupLayoutReg.setAutoCreateContainerGaps(true);

                groupLayoutReg.setHorizontalGroup(groupLayoutReg
                                .createSequentialGroup()
                                .addGroup(groupLayoutReg.createParallelGroup()
                                                .addComponent(selectPanel)
                                                .addComponent(dataPanel))
                                .addComponent(chartPanel));

                groupLayoutReg.setVerticalGroup(groupLayoutReg
                                .createParallelGroup(
                                                GroupLayout.Alignment.BASELINE)
                                .addGroup(groupLayoutReg
                                                .createSequentialGroup()
                                                .addComponent(selectPanel)
                                                .addComponent(dataPanel))
                                .addComponent(chartPanel));
        }

        private String makeSQL(final String prefix, final List<JerItem> li,
                        final String str) {
                final StringBuffer restrictions = new StringBuffer();
                for (final JerItem item : li) {
                        if (item.getId() != -1) {
                                restrictions.append("OR (" + prefix + str
                                                + " = " + item.getId() + ") ");
                        }
                        if (item.getId() == -1) {
                                restrictions.append("OR ( " + str + " = '"
                                                + item.getIdent() + "') ");
                        }
                }
                if (restrictions.length() >= 2) {
                        restrictions.delete(0, 2);
                        return " AND (" + restrictions.toString() + ")";
                } else {
                        return " ";
                }
        }

        @SuppressWarnings("unchecked")
        private void putDataIntoDataset(final String val1, final String val2,
                        final String val3, final DefaultPieDataset result) {
                sqlStrAnalysis = " SELECT "
                                + val1
                                + ", COUNT(DISTINCT "
                                + val2
                                + " ) FROM JERUSALEM.AUTHORS AS a, JERUSALEM.DOCUMENTS AS d "
                                + ",JERUSALEM.ENTRIES AS e, JERUSALEM.TOPOS_IN_ENTRY AS tie, JERUSALEM.PLACES AS p, JERUSALEM.TOPOI AS t "
                                + "WHERE (a.AUTHOR_ID= d.AUTHOR_ID "
                                + "AND d.DOCUMENT_ID=e.DOCUMENT_ID AND e.ENTRY_ID=tie.ENTRY_ID AND p.PLACE_ID=tie.PLACE_ID AND tie.TOPOS_ID=t.TOPOS_ID"
                                + val3 + " ) GROUP BY " + val1;
                final List<Object> li = mainModel.getDataForAnalysis(this);
                for (int i = 0; i < li.size(); i++) {
                        final List<Object> valueList = (List<Object>) li.get(i);
                        if (!valueList.isEmpty()) {
                                result.setValue(valueList.get(0) + "",
                                                (int) valueList.get(1));
                        }
                }

        }

        private void setAllLiSelected() {
                showLi.setSelectedIndex(1);
                // authorLi.setSelectionInterval(0,
                // authorLi.getModel().getSize() - 1);
                originLi.setSelectionInterval(0,
                                originLi.getModel().getSize() - 1);
                religionLi.setSelectionInterval(0, religionLi.getModel()
                                .getSize() - 1);
                // documentLi.setSelectionInterval(0,
                // documentLi.getModel().getSize() -
                // 1);
                placeLi.setSelectionInterval(0,
                                placeLi.getModel().getSize() - 1);
                toposLi.setSelectionInterval(0,
                                toposLi.getModel().getSize() - 1);
        }
}
