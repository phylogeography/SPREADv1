package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import templates.MapBackground;
import templates.TimeSlicerToKML;
import templates.TimeSlicerToProcessing;
import utils.Utils;
import checks.TimeSlicerSanityCheck;

import com.bric.swing.ColorPicker;

@SuppressWarnings("serial")
public class TimeSlicerTab extends JPanel {

	// Sizing constants
	private final int leftPanelWidth = 260;
	private final int leftPanelHeight = 1000;
	private final int spinningPanelHeight = 20;
	private final int mapImageWidth = MapBackground.mapImageWidth;
	private final int mapImageHeight = MapBackground.mapImageHeight;
	private final Dimension minimumDimension = new Dimension(0, 0);

	// Icons
	private ImageIcon nuclearIcon;
	private ImageIcon treeIcon;
	private ImageIcon treesIcon;
	private ImageIcon processingIcon;
	private ImageIcon saveIcon;
	private ImageIcon errorIcon;
	private ImageIcon timeSlicesIcon;

	// Colors
	private Color backgroundColor;
	private Color polygonsMaxColor;
	private Color branchesMaxColor;
	private Color polygonsMinColor;
	private Color branchesMinColor;

	// Strings for paths
	private String treeFilename;
	private String treesFilename;
	private String sliceHeightsFilename;
	private File workingDirectory;

	// Radio buttons
	private JRadioButton firstAnalysisRadioButton;
	private JRadioButton secondAnalysisRadioButton;

	// Switchers
	private int analysisType;

	// Strings for radio buttons
	private String firstAnalysis;
	private String secondAnalysis;

	// Text fields
	private JTextField burnInParser;
	private JTextField coordinatesNameParser;
	private JTextField rateAttNameParser;
	private JTextField precisionAttNameParser;
	private JTextField numberOfIntervalsParser;
	private JTextField kmlPathParser;
	private JTextField maxAltMappingParser;
	private JTextField HPDParser;
	private JTextField timescalerParser;

	// Spinners
	private DateSpinner dateSpinner;

	// Buttons
	private JButton generateKml;
	private JButton openTree;
	private JButton openTrees;
	private JButton generateProcessing;
	private JButton saveProcessingPlot;
	private JButton polygonsMaxColorChooser;
	private JButton branchesMaxColorChooser;
	private JButton polygonsMinColorChooser;
	private JButton branchesMinColorChooser;
	private JButton loadTimeSlices;

	// Sliders
	private JSlider branchesWidthParser;
	private JSlider gridSizeParser;

	// Combo boxes
	private JComboBox eraParser;

	// checkboxes
	private JCheckBox trueNoiseParser;
	private JCheckBox imputeParser;

	// left tools pane
	private JPanel leftPanel;
	private JPanel tmpPanel;
	private SpinningPanel sp;
	private JPanel tmpPanelsHolder;

	// Processing pane
	private TimeSlicerToProcessing timeSlicerToProcessing;

	// Progress bar
	private JProgressBar progressBar;

	public TimeSlicerTab() {

		// Setup miscallenous
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		GridBagConstraints c = new GridBagConstraints();

		// Setup icons
		nuclearIcon = CreateImageIcon("/icons/nuclear.png");
		treeIcon = CreateImageIcon("/icons/tree.png");
		treesIcon = CreateImageIcon("/icons/trees.png");
		processingIcon = CreateImageIcon("/icons/processing.png");
		saveIcon = CreateImageIcon("/icons/save.png");
		errorIcon = CreateImageIcon("/icons/error.png");
		timeSlicesIcon = CreateImageIcon("/icons/timeSlices.png");

		// Setup colors
		backgroundColor = new Color(231, 237, 246);
		polygonsMaxColor = new Color(50, 255, 255, 255);
		branchesMaxColor = new Color(255, 5, 50, 255);
		polygonsMinColor = new Color(0, 0, 0, 100);
		branchesMinColor = new Color(0, 0, 0, 255);

		// Setup strings for paths
		treeFilename = null;
		treesFilename = null;
		sliceHeightsFilename = null;
		workingDirectory = null;

		// Setup strings for radio buttons
		firstAnalysis = new String("MCC tree slice heights");
		secondAnalysis = new String("Custom slice heights"); //Custom slice heights, polygons only

		// Setup radio buttons
		firstAnalysisRadioButton = new JRadioButton(firstAnalysis); 
		secondAnalysisRadioButton = new JRadioButton(secondAnalysis);

		// Setup switchers
		analysisType = TimeSlicerToKML.FIRST_ANALYSIS;

		// Setup text fields
		burnInParser = new JTextField("500", 10);
		coordinatesNameParser = new JTextField("location", 10);
		rateAttNameParser = new JTextField("rate", 10);
		precisionAttNameParser = new JTextField("precision", 10);
		numberOfIntervalsParser = new JTextField("10", 5);
		maxAltMappingParser = new JTextField("500000", 5);
		kmlPathParser = new JTextField("output.kml", 10);
		HPDParser = new JTextField("0.8", 5);
		timescalerParser = new JTextField("1.0", 10);

		// Setup buttons
		generateKml = new JButton("Generate", nuclearIcon);
		openTree = new JButton("Open", treeIcon);
		openTrees = new JButton("Open", treesIcon);
		generateProcessing = new JButton("Plot", processingIcon);
		saveProcessingPlot = new JButton("Save", saveIcon);
		polygonsMaxColorChooser = new JButton("Setup max");
		branchesMaxColorChooser = new JButton("Setup max");
		polygonsMinColorChooser = new JButton("Setup min");
		branchesMinColorChooser = new JButton("Setup min");
		loadTimeSlices = new JButton("Load", timeSlicesIcon);

		// Setup sliders
		branchesWidthParser = new JSlider(JSlider.HORIZONTAL, 2, 10, 4);
		branchesWidthParser.setMajorTickSpacing(2);
		branchesWidthParser.setMinorTickSpacing(1);
		branchesWidthParser.setPaintTicks(true);
		branchesWidthParser.setPaintLabels(true);

		gridSizeParser = new JSlider(JSlider.HORIZONTAL, 100, 200, 100);
		gridSizeParser.setMajorTickSpacing(50);
		gridSizeParser.setMinorTickSpacing(10);
		gridSizeParser.setPaintTicks(true);
		gridSizeParser.setPaintLabels(true);

		// Setup progress bar & checkboxes
		progressBar = new JProgressBar();
		trueNoiseParser = new JCheckBox();
		imputeParser = new JCheckBox();

		/**
		 * left tools pane
		 * */
		leftPanel = new JPanel();
		leftPanel.setBackground(backgroundColor);
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

		// Action listeners
		openTree.addActionListener(new ListenOpenTree());
		openTrees.addActionListener(new ListenOpenTrees());
		generateKml.addActionListener(new ListenGenerateKml());
		generateProcessing.addActionListener(new ListenGenerateProcessing());
		saveProcessingPlot.addActionListener(new ListenSaveProcessingPlot());
		imputeParser.addActionListener(new listenImputeParser());
		polygonsMaxColorChooser
				.addActionListener(new ListenPolygonsMaxColorChooser());
		branchesMaxColorChooser
				.addActionListener(new ListenBranchesMaxColorChooser());
		polygonsMinColorChooser
				.addActionListener(new ListenPolygonsMinColorChooser());
		branchesMinColorChooser
				.addActionListener(new ListenBranchesMinColorChooser());
		loadTimeSlices.addActionListener(new ListenLoadTimeSlices());

		// ////////////////////////////
		// ---CHOOSE ANALYSIS TYPE---//
		// ////////////////////////////

		tmpPanelsHolder = new JPanel();
		tmpPanelsHolder.setLayout(new BoxLayout(tmpPanelsHolder,
				BoxLayout.Y_AXIS));

		tmpPanel = new JPanel();
		tmpPanel.setLayout(new GridLayout(3, 1));
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Choose analysis type:"));

		ButtonGroup buttonGroup = new ButtonGroup();

		firstAnalysisRadioButton.setToolTipText(firstAnalysis);
		firstAnalysisRadioButton.setActionCommand(firstAnalysis);
		firstAnalysisRadioButton
				.addActionListener(new ChooseAnalysisTypeListener());
		firstAnalysisRadioButton.setSelected(true);
		buttonGroup.add(firstAnalysisRadioButton);
		tmpPanel.add(firstAnalysisRadioButton);

		secondAnalysisRadioButton.setToolTipText(secondAnalysis);
		secondAnalysisRadioButton.setActionCommand(secondAnalysis);
		secondAnalysisRadioButton
				.addActionListener(new ChooseAnalysisTypeListener());
		buttonGroup.add(secondAnalysisRadioButton);
		tmpPanel.add(secondAnalysisRadioButton);

		tmpPanelsHolder.add(tmpPanel);

		sp = new SpinningPanel(tmpPanelsHolder, "   Analysis", new Dimension(
				leftPanelWidth, spinningPanelHeight));
		sp.showBottom(false);
		leftPanel.add(sp);

		// /////////////
		// ---INPUT---//
		// /////////////

		tmpPanelsHolder = new JPanel();
		tmpPanelsHolder.setLayout(new BoxLayout(tmpPanelsHolder,
				BoxLayout.Y_AXIS));

		tmpPanel = new JPanel();
		tmpPanel.setLayout(new GridBagLayout());
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Load slice heights / tree file:"));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		loadTimeSlices.setEnabled(false);
		tmpPanel.add(loadTimeSlices, c);
		c.gridx = 2;
		c.gridy = 0;
		tmpPanel.add(openTree, c);
		tmpPanelsHolder.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Load trees file:"));
		tmpPanel.add(openTrees);
		tmpPanelsHolder.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth, 100));
		tmpPanel.setLayout(new GridBagLayout());
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Most recent sampling date:"));
		dateSpinner = new DateSpinner();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		tmpPanel.add(dateSpinner, c);
		String era[] = { "AD", "BC" };
		eraParser = new JComboBox(era);
		c.gridx = 2;
		c.gridy = 0;
		tmpPanel.add(eraParser, c);
		tmpPanelsHolder.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Coordinate attribute name:"));
		tmpPanel.add(coordinatesNameParser);
		tmpPanelsHolder.add(tmpPanel);

		sp = new SpinningPanel(tmpPanelsHolder, "   Input", new Dimension(
				leftPanelWidth, spinningPanelHeight));
		sp.showBottom(true);
		leftPanel.add(sp);

		// ////////////////////////
		// ---BRANCHES MAPPING---//
		// ////////////////////////

		tmpPanelsHolder = new JPanel();
		tmpPanelsHolder.setLayout(new BoxLayout(tmpPanelsHolder,
				BoxLayout.Y_AXIS));

		// Branches color mapping:
		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setLayout(new GridBagLayout());
		tmpPanel.setBorder(new TitledBorder("Branches color mapping:"));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		tmpPanel.add(branchesMinColorChooser, c);
		c.gridx = 2;
		c.gridy = 0;
		tmpPanel.add(branchesMaxColorChooser, c);
		tmpPanelsHolder.add(tmpPanel);

		// Branches width:
		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Branches width:"));
		tmpPanel.add(branchesWidthParser);
		tmpPanelsHolder.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Maximal altitude mapping:"));
		tmpPanel.add(maxAltMappingParser);
		tmpPanelsHolder.add(tmpPanel);

		sp = new SpinningPanel(tmpPanelsHolder, "   Branches mapping",
				new Dimension(leftPanelWidth, spinningPanelHeight));
		sp.showBottom(false);
		leftPanel.add(sp);

		// ////////////////////////
		// ---POLYGONS MAPPING---//
		// ////////////////////////

		tmpPanelsHolder = new JPanel();
		tmpPanelsHolder.setLayout(new BoxLayout(tmpPanelsHolder,
				BoxLayout.Y_AXIS));

		// Polygons color mapping:
		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth, 100));
		tmpPanel.setLayout(new GridBagLayout());
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Polygons color mapping:"));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		tmpPanel.add(polygonsMinColorChooser, c);
		c.gridx = 2;
		c.gridy = 0;
		tmpPanel.add(polygonsMaxColorChooser);
		tmpPanelsHolder.add(tmpPanel);

		sp = new SpinningPanel(tmpPanelsHolder, "   Polygons mapping",
				new Dimension(leftPanelWidth, spinningPanelHeight));
		sp.showBottom(false);
		leftPanel.add(sp);

		// ////////////////////
		// ---COMPUTATIONS---//
		// ////////////////////

		tmpPanelsHolder = new JPanel();
		tmpPanelsHolder.setLayout(new BoxLayout(tmpPanelsHolder,
				BoxLayout.Y_AXIS));

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Impute / Use true noise:"));
		imputeParser.setSelected(true);
		tmpPanel.add(imputeParser);
		trueNoiseParser.setSelected(true);
		tmpPanel.add(trueNoiseParser);
		tmpPanelsHolder.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Rate attribute name:"));
		tmpPanel.add(rateAttNameParser);
		tmpPanelsHolder.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Precision attribute name:"));
		tmpPanel.add(precisionAttNameParser);
		tmpPanelsHolder.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Specify burn-in:"));
		tmpPanel.add(burnInParser);
		tmpPanelsHolder.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("HPD:"));
		tmpPanel.add(HPDParser);
		tmpPanelsHolder.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Time scale multiplier:"));
		tmpPanel.add(timescalerParser);
		tmpPanelsHolder.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Number of intervals:"));
		tmpPanel.add(numberOfIntervalsParser);
		tmpPanelsHolder.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Grid size:"));
		tmpPanel.add(gridSizeParser);
		tmpPanelsHolder.add(tmpPanel);

		sp = new SpinningPanel(tmpPanelsHolder, "   Computations",
				new Dimension(leftPanelWidth, spinningPanelHeight));
		sp.showBottom(false);
		leftPanel.add(sp);

		// //////////////
		// ---OUTPUT---//
		// //////////////

		tmpPanelsHolder = new JPanel();
		tmpPanelsHolder.setLayout(new BoxLayout(tmpPanelsHolder,
				BoxLayout.Y_AXIS));

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("KML name:"));
		tmpPanel.add(kmlPathParser);
		tmpPanelsHolder.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth, 100));
		tmpPanel.setLayout(new GridBagLayout());
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Generate KML / Plot map:"));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		tmpPanel.add(generateKml, c);
		c.gridx = 2;
		c.gridy = 0;
		tmpPanel.add(generateProcessing, c);
		c.ipady = 7;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 1;
		tmpPanel.add(progressBar, c);
		tmpPanelsHolder.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Save plot:"));
		tmpPanel.add(saveProcessingPlot);
		tmpPanelsHolder.add(tmpPanel);

		sp = new SpinningPanel(tmpPanelsHolder, "   Output", new Dimension(
				leftPanelWidth, spinningPanelHeight));
		sp.showBottom(false);
		leftPanel.add(sp);

		// ////////////////////////
		// ---LEFT SCROLL PANE---//
		// ////////////////////////

		JScrollPane leftScrollPane = new JScrollPane(leftPanel,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		leftScrollPane.setMinimumSize(minimumDimension);
		leftScrollPane.setMaximumSize(new Dimension(leftPanelWidth, leftPanelHeight));
		
		/**
		 * Processing pane
		 * */
		timeSlicerToProcessing = new TimeSlicerToProcessing();
		timeSlicerToProcessing.setPreferredSize(new Dimension(mapImageWidth, mapImageHeight));

		if (System.getProperty("java.runtime.name").toLowerCase().startsWith(
				"openjdk")) {

			JScrollPane rightScrollPane = new JScrollPane(
					timeSlicerToProcessing,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			rightScrollPane.setMinimumSize(minimumDimension);
			
			SplitPane splitPane = new SplitPane(JSplitPane.HORIZONTAL_SPLIT,
					leftScrollPane, rightScrollPane);
			splitPane.setDividerLocation(leftPanelWidth);

			this.add(splitPane);

		} else {

			ScrollPane rightScrollPane = new ScrollPane(
					ScrollPane.SCROLLBARS_ALWAYS);
			rightScrollPane.add(timeSlicerToProcessing);
			rightScrollPane.setMinimumSize(minimumDimension);
			
			SplitPane splitPane = new SplitPane(JSplitPane.HORIZONTAL_SPLIT,
					leftScrollPane, rightScrollPane);
			splitPane.setDividerLocation(leftPanelWidth);

			this.add(splitPane);

		}

	}// END: TimeSlicerTab

	private class listenImputeParser implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			if (imputeParser.isSelected()) {
				trueNoiseParser.setEnabled(true);
				rateAttNameParser.setEnabled(true);
				precisionAttNameParser.setEnabled(true);

			} else {
				trueNoiseParser.setEnabled(false);
				rateAttNameParser.setEnabled(false);
				precisionAttNameParser.setEnabled(false);

			}
		}
	}

	class ChooseAnalysisTypeListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			if (ev.getActionCommand() == firstAnalysis) {

				loadTimeSlices.setEnabled(false);
				openTree.setEnabled(true);
				branchesMinColorChooser.setEnabled(true);
				branchesMaxColorChooser.setEnabled(true);
				branchesWidthParser.setEnabled(true);
				maxAltMappingParser.setEnabled(true);
				numberOfIntervalsParser.setEnabled(true);

				analysisType = TimeSlicerToKML.FIRST_ANALYSIS;
				System.out.println(firstAnalysis + " analysis selected \n");

			} else if (ev.getActionCommand() == secondAnalysis) {

				loadTimeSlices.setEnabled(true);
				openTree.setEnabled(false);
				branchesMinColorChooser.setEnabled(false);
				branchesMaxColorChooser.setEnabled(false);
				branchesWidthParser.setEnabled(false);
				maxAltMappingParser.setEnabled(false);
				numberOfIntervalsParser.setEnabled(false);

				analysisType = TimeSlicerToKML.SECOND_ANALYSIS;
				System.out.println(secondAnalysis + " analysis selected \n");

			} else {
				System.err.println("Unimplemented analysis type selected");
			}

		}// END: actionPerformed
	}// END: ChooseAnalysisTypeListener

	private class ListenLoadTimeSlices implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			try {

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Loading slice heights file...");
				chooser.setCurrentDirectory(workingDirectory);

				chooser.showOpenDialog(Utils.getActiveFrame());
				File file = chooser.getSelectedFile();
				sliceHeightsFilename = file.getAbsolutePath();
				System.out.println("Opened " + sliceHeightsFilename + "\n");

				File tmpDir = chooser.getCurrentDirectory();

				if (tmpDir != null) {
					workingDirectory = tmpDir;
				}

			} catch (Exception e) {
				System.err.println("Could not load! \n");
			}

		}
	}// END: ListenLoadTimeSlices

	private class ListenOpenTree implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			try {

				String[] treeFiles = new String[] { "tre", "tree", "trees" };

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Opening tree file...");
				chooser.setMultiSelectionEnabled(false);
				chooser.addChoosableFileFilter(new SimpleFileFilter(treeFiles,
						"Tree files (*.tree(s), *.tre)"));
				chooser.setCurrentDirectory(workingDirectory);

				chooser.showOpenDialog(Utils.getActiveFrame());
				File file = chooser.getSelectedFile();
				treeFilename = file.getAbsolutePath();
				System.out.println("Opened " + treeFilename + "\n");

				File tmpDir = chooser.getCurrentDirectory();

				if (tmpDir != null) {
					workingDirectory = tmpDir;
				}

			} catch (Exception e) {
				System.err.println("Could not open! \n");
			}
		}
	}

	private class ListenOpenTrees implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			try {

				String[] treesFiles = new String[] { "trees" };

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Loading trees file...");
				chooser.setMultiSelectionEnabled(false);
				chooser.addChoosableFileFilter(new SimpleFileFilter(treesFiles,
						"Tree files (*.trees)"));
				chooser.setCurrentDirectory(workingDirectory);

				chooser.showOpenDialog(Utils.getActiveFrame());
				File file = chooser.getSelectedFile();
				treesFilename = file.getAbsolutePath();
				System.out.println("Opened " + treesFilename + "\n");

				File tmpDir = chooser.getCurrentDirectory();

				if (tmpDir != null) {
					workingDirectory = tmpDir;
				}

			} catch (Exception e) {
				System.err.println("Could not open! \n");
			}
		}
	}

	private class ListenPolygonsMinColorChooser implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			Color c = ColorPicker.showDialog(Utils.getActiveFrame(),
					"Choose minimum polygons color...", polygonsMinColor, true);

			if (c != null)
				polygonsMinColor = c;

		}
	}

	private class ListenPolygonsMaxColorChooser implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			Color c = ColorPicker.showDialog(Utils.getActiveFrame(),
					"Choose maximum polygons color...", polygonsMaxColor, true);

			if (c != null)
				polygonsMaxColor = c;

		}
	}

	private class ListenBranchesMinColorChooser implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			Color c = ColorPicker.showDialog(Utils.getActiveFrame(),
					"Choose minimum branches color...", branchesMinColor, true);

			if (c != null)
				branchesMinColor = c;

		}
	}

	private class ListenBranchesMaxColorChooser implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			Color c = ColorPicker.showDialog(Utils.getActiveFrame(),
					"Choose maximum branches color...", branchesMaxColor, true);

			if (c != null)
				branchesMaxColor = c;

		}
	}

	private class ListenGenerateKml implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			if (openTree.isEnabled() && treeFilename == null) {

				new ListenOpenTree().actionPerformed(ev);

			} else if (treesFilename == null) {

				new ListenOpenTrees().actionPerformed(ev);

			} else {

				generateKml.setEnabled(false);
				progressBar.setIndeterminate(true);

				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					// Executed in background thread
					public Void doInBackground() {

						try {

							if (new TimeSlicerSanityCheck().check(treeFilename,
									coordinatesNameParser.getText(),
									treesFilename, analysisType)) {

								TimeSlicerToKML timeSlicerToKML = new TimeSlicerToKML();

								timeSlicerToKML.setAnalysisType(analysisType);

								if (analysisType == TimeSlicerToKML.FIRST_ANALYSIS) {

									timeSlicerToKML.setTreePath(treeFilename);

									timeSlicerToKML
											.setNumberOfIntervals(Integer
													.valueOf(numberOfIntervalsParser
															.getText()));

									timeSlicerToKML
											.setMinBranchRedMapping(branchesMinColor
													.getRed());

									timeSlicerToKML
											.setMinBranchGreenMapping(branchesMinColor
													.getGreen());

									timeSlicerToKML
											.setMinBranchBlueMapping(branchesMinColor
													.getBlue());

									timeSlicerToKML
											.setMinBranchOpacityMapping(branchesMinColor
													.getAlpha());

									timeSlicerToKML
											.setMaxBranchRedMapping(branchesMaxColor
													.getRed());

									timeSlicerToKML
											.setMaxBranchGreenMapping(branchesMaxColor
													.getGreen());

									timeSlicerToKML
											.setMaxBranchBlueMapping(branchesMaxColor
													.getBlue());

									timeSlicerToKML
											.setMaxBranchOpacityMapping(branchesMaxColor
													.getAlpha());

									timeSlicerToKML
											.setBranchWidth(branchesWidthParser
													.getValue());

									timeSlicerToKML
											.setMaxAltitudeMapping(Double
													.valueOf(maxAltMappingParser
															.getText()));

								} else if (analysisType == TimeSlicerToKML.SECOND_ANALYSIS) {

									timeSlicerToKML
											.setCustomSliceHeights(sliceHeightsFilename);

								}

								timeSlicerToKML.setTreesPath(treesFilename);

								timeSlicerToKML.setHPD(Double.valueOf(HPDParser
										.getText()));

								timeSlicerToKML.setGridSize(gridSizeParser
										.getValue());

								timeSlicerToKML.setBurnIn(Integer
										.valueOf(burnInParser.getText()));

								timeSlicerToKML
										.setLocationAttName(coordinatesNameParser
												.getText());

								timeSlicerToKML
										.setRateAttName(rateAttNameParser
												.getText());

								timeSlicerToKML
										.setPrecisionAttName(precisionAttNameParser
												.getText());

								timeSlicerToKML.setTrueNoise(trueNoiseParser
										.isSelected());

								timeSlicerToKML.setImpute(imputeParser
										.isSelected());

								timeSlicerToKML
										.setMrsdString(dateSpinner.getValue()
												+ " "
												+ (eraParser.getSelectedIndex() == 0 ? "AD"
														: "BC"));

								timeSlicerToKML.setTimescaler(Double
										.valueOf(timescalerParser.getText()));

								timeSlicerToKML
										.setKmlWriterPath(workingDirectory
												.toString()
												.concat("/")
												.concat(kmlPathParser.getText()));

								timeSlicerToKML
										.setMinPolygonRedMapping(polygonsMinColor
												.getRed());

								timeSlicerToKML
										.setMinPolygonGreenMapping(polygonsMinColor
												.getGreen());

								timeSlicerToKML
										.setMinPolygonBlueMapping(polygonsMinColor
												.getBlue());

								timeSlicerToKML
										.setMinPolygonOpacityMapping(polygonsMinColor
												.getAlpha());

								timeSlicerToKML
										.setMaxPolygonRedMapping(polygonsMaxColor
												.getRed());

								timeSlicerToKML
										.setMaxPolygonGreenMapping(polygonsMaxColor
												.getGreen());

								timeSlicerToKML
										.setMaxPolygonBlueMapping(polygonsMaxColor
												.getBlue());

								timeSlicerToKML
										.setMaxPolygonOpacityMapping(polygonsMaxColor
												.getAlpha());

								timeSlicerToKML.GenerateKML();

								System.out.println("Finished in: "
										+ timeSlicerToKML.time + " msec \n");

							}// END: check

						} catch (final OutOfMemoryError e) {

							SwingUtilities.invokeLater(new Runnable() {

								public void run() {

									e.printStackTrace();

									String msg = String.format(
											"Unexpected problem: %s", e
													.toString());

									JOptionPane.showMessageDialog(Utils
											.getActiveFrame(), msg
											+ "\nIncrease Java Heap Space",
											"Error", JOptionPane.ERROR_MESSAGE,
											errorIcon);

								}
							});

						} catch (final Exception e) {

							SwingUtilities.invokeLater(new Runnable() {

								public void run() {

									e.printStackTrace();

									String msg = String.format(
											"Unexpected problem: %s", e
													.toString());

									JOptionPane.showMessageDialog(Utils
											.getActiveFrame(), msg, "Error",
											JOptionPane.ERROR_MESSAGE,
											errorIcon);

								}
							});

						}

						return null;
					}// END: doInBackground()

					// Executed in event dispatch thread
					public void done() {

						generateKml.setEnabled(true);
						progressBar.setIndeterminate(false);

						System.out.println("Generated "
								+ workingDirectory.toString().concat("/")
										.concat(kmlPathParser.getText()));

						System.gc();

					}
				};

				worker.execute();

			}// END: if not loaded

		}// END: actionPerformed
	}// END: ListenGenerateKml

	private class ListenGenerateProcessing implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			if (openTree.isEnabled() && treeFilename == null) {

				new ListenOpenTree().actionPerformed(ev);

			} else if (treesFilename == null) {

				new ListenOpenTrees().actionPerformed(ev);

			} else {

				generateProcessing.setEnabled(false);
				progressBar.setIndeterminate(true);

				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					// Executed in background thread
					public Void doInBackground() {

						try {

							if (new TimeSlicerSanityCheck().check(treeFilename,
									coordinatesNameParser.getText(),
									treesFilename, analysisType)) {

								timeSlicerToProcessing
										.setAnalysisType(analysisType);

								if (analysisType == TimeSlicerToProcessing.FIRST_ANALYSIS) {

									timeSlicerToProcessing
											.setMccTreePath(treeFilename);

									timeSlicerToProcessing
											.setNumberOfIntervals(Integer
													.valueOf(numberOfIntervalsParser
															.getText()));

									timeSlicerToProcessing
											.setMinBranchRedMapping(branchesMinColor
													.getRed());

									timeSlicerToProcessing
											.setMinBranchGreenMapping(branchesMinColor
													.getGreen());

									timeSlicerToProcessing
											.setMinBranchBlueMapping(branchesMinColor
													.getBlue());

									timeSlicerToProcessing
											.setMinBranchOpacityMapping(branchesMinColor
													.getAlpha());

									timeSlicerToProcessing
											.setMaxBranchRedMapping(branchesMaxColor
													.getRed());

									timeSlicerToProcessing
											.setMaxBranchGreenMapping(branchesMaxColor
													.getGreen());

									timeSlicerToProcessing
											.setMaxBranchBlueMapping(branchesMaxColor
													.getBlue());

									timeSlicerToProcessing
											.setMaxBranchOpacityMapping(branchesMaxColor
													.getAlpha());

									timeSlicerToProcessing
											.setBranchWidth(branchesWidthParser
													.getValue() / 2);

								} else if (analysisType == TimeSlicerToProcessing.SECOND_ANALYSIS) {

									timeSlicerToProcessing
											.setCustomSliceHeights(sliceHeightsFilename);

								}

								timeSlicerToProcessing
										.setTreesPath(treesFilename);

								timeSlicerToProcessing.setHPD(Double
										.valueOf(HPDParser.getText()));

								timeSlicerToProcessing
										.setGridSize(gridSizeParser.getValue());

								timeSlicerToProcessing.setBurnIn(Integer
										.valueOf(burnInParser.getText()));

								timeSlicerToProcessing
										.setCoordinatesName(coordinatesNameParser
												.getText());

								timeSlicerToProcessing
										.setRateAttName(rateAttNameParser
												.getText());

								timeSlicerToProcessing
										.setPrecisionAttName(precisionAttNameParser
												.getText());

								timeSlicerToProcessing
										.setTrueNoise(trueNoiseParser
												.isSelected());

								timeSlicerToProcessing.setImpute(imputeParser
										.isSelected());

								timeSlicerToProcessing
										.setMrsdString(dateSpinner.getValue()
												+ " "
												+ (eraParser.getSelectedIndex() == 0 ? "AD"
														: "BC"));

								timeSlicerToProcessing.setTimescaler(Double
										.valueOf(timescalerParser.getText()));

								timeSlicerToProcessing
										.setMinPolygonRedMapping(polygonsMinColor
												.getRed());

								timeSlicerToProcessing
										.setMinPolygonGreenMapping(polygonsMinColor
												.getGreen());

								timeSlicerToProcessing
										.setMinPolygonBlueMapping(polygonsMinColor
												.getBlue());

								timeSlicerToProcessing
										.setMinPolygonOpacityMapping(polygonsMinColor
												.getAlpha());

								timeSlicerToProcessing
										.setMaxPolygonRedMapping(polygonsMaxColor
												.getRed());

								timeSlicerToProcessing
										.setMaxPolygonGreenMapping(polygonsMaxColor
												.getGreen());

								timeSlicerToProcessing
										.setMaxPolygonBlueMapping(polygonsMaxColor
												.getBlue());

								timeSlicerToProcessing
										.setMaxPolygonOpacityMapping(polygonsMaxColor
												.getAlpha());

								timeSlicerToProcessing.analyzeTrees();

								timeSlicerToProcessing.init();

								System.out.println("Finished. \n");

							}// END: check

						} catch (final OutOfMemoryError e) {

							SwingUtilities.invokeLater(new Runnable() {

								public void run() {

									e.printStackTrace();

									String msg = String.format(
											"Unexpected problem: %s", e
													.toString());

									JOptionPane.showMessageDialog(Utils
											.getActiveFrame(), msg
											+ "\nIncrease Java Heap Space",
											"Error", JOptionPane.ERROR_MESSAGE,
											errorIcon);

								}
							});

						} catch (final Exception e) {

							SwingUtilities.invokeLater(new Runnable() {

								public void run() {

									e.printStackTrace();

									String msg = String.format(
											"Unexpected problem: %s", e
													.toString());

									JOptionPane.showMessageDialog(Utils
											.getActiveFrame(), msg, "Error",
											JOptionPane.ERROR_MESSAGE,
											errorIcon);

								}
							});

						}

						return null;
					}// END: doInBackground()

					// Executed in event dispatch thread
					public void done() {

						generateProcessing.setEnabled(true);
						progressBar.setIndeterminate(false);

						System.gc();

					}// END: done
				};

				worker.execute();

			}// END: if not loaded

		}// END: actionPerformed
	}// END: ListenGenerateProcessing

	private class ListenSaveProcessingPlot implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			try {

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Saving as png file...");

				chooser.showSaveDialog(Utils.getActiveFrame());
				File file = chooser.getSelectedFile();
				String plotToSaveFilename = file.getAbsolutePath();

				timeSlicerToProcessing.save(plotToSaveFilename);
				System.out.println("Saved " + plotToSaveFilename + "\n");

			} catch (Exception e0) {
				System.err.println("Could not save! \n");
			}

		}// END: actionPerformed
	}// END: class

	private ImageIcon CreateImageIcon(String path) {
		java.net.URL imgURL = this.getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path + "\n");
			return null;
		}
	}

}// END class

