package gui;

import java.awt.BorderLayout;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import templates.RateIndicatorBFToKML;
import templates.RateIndicatorBFToProcessing;
import utils.Utils;

import com.bric.swing.ColorPicker;

@SuppressWarnings("serial")
public class RateIndicatorBFTab extends JPanel {

	// Sizing constants
	private final int leftPanelWidth = 200;
	private final int leftPanelHeight = 1050;

	// Icons
	private ImageIcon nuclearIcon;
	private ImageIcon logIcon;
	private ImageIcon locationsIcon;
	private ImageIcon processingIcon;
	private ImageIcon saveIcon;
	private ImageIcon errorIcon;

	// Colors
	private Color backgroundColor;
	private Color branchesMaxColor;
	private Color branchesMinColor;

	// Strings for paths
	private String logFilename;
	private String locationsFilename;
	private File workingDirectory;

	// Text fields
	private JTextField numberOfIntervalsParser;
	private JTextField maxAltMappingParser;
	private JTextField bfCutoffParser;
	private JTextField kmlPathParser;

	// Buttons
	private JButton openLog;
	private JButton openLocations;
	private JButton generateKml;
	private JButton generateProcessing;
	private JButton saveProcessingPlot;
	private JButton branchesMaxColorChooser;
	private JButton branchesMinColorChooser;

	// Sliders
	private JSlider burnInParser;
	private JSlider branchesWidthParser;

	// Combo boxes
	private JComboBox meanPoissonPriorParser;
	private JComboBox poissonPriorOffsetParser;

	// left tools pane
	private JPanel leftPanel;
	private JPanel tmpPanel;

	// Processing pane
	private RateIndicatorBFToProcessing rateIndicatorBFToProcessing;

	// Progress bar
	private JProgressBar progressBar;

	public RateIndicatorBFTab() {

		// Setup miscallenous
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		backgroundColor = new Color(231, 237, 246);
		branchesMaxColor = new Color(255, 5, 50, 255);
		branchesMinColor = new Color(0, 0, 0, 255);
		GridBagConstraints c = new GridBagConstraints();

		// Setup icons
		nuclearIcon = CreateImageIcon("/icons/nuclear.png");
		logIcon = CreateImageIcon("/icons/log.png");
		locationsIcon = CreateImageIcon("/icons/locations.png");
		processingIcon = CreateImageIcon("/icons/processing.png");
		saveIcon = CreateImageIcon("/icons/save.png");
		errorIcon = CreateImageIcon("/icons/error.png");

		// Setup text fields
		numberOfIntervalsParser = new JTextField("100", 10);
		maxAltMappingParser = new JTextField("500000", 10);
		bfCutoffParser = new JTextField("3.0", 5);
		kmlPathParser = new JTextField("output.kml", 10);

		// Setup buttons
		openLog = new JButton("Open", logIcon);
		openLocations = new JButton("Open", locationsIcon);
		generateKml = new JButton("Generate", nuclearIcon);
		generateProcessing = new JButton("Plot", processingIcon);
		saveProcessingPlot = new JButton("Save", saveIcon);
		branchesMaxColorChooser = new JButton("Setup max");
		branchesMinColorChooser = new JButton("Setup min");

		// Setup sliders
		burnInParser = new JSlider(JSlider.HORIZONTAL, 0, 100, 10);
		burnInParser.setMajorTickSpacing(20);
		burnInParser.setMinorTickSpacing(10);
		burnInParser.setPaintTicks(true);
		burnInParser.setPaintLabels(true);
		branchesWidthParser = new JSlider(JSlider.HORIZONTAL, 2, 10, 4);
		branchesWidthParser.setMajorTickSpacing(2);
		branchesWidthParser.setMinorTickSpacing(1);
		branchesWidthParser.setPaintTicks(true);
		branchesWidthParser.setPaintLabels(true);

		// Setup progress bar
		progressBar = new JProgressBar();

		/**
		 * left tools pane
		 * */
		leftPanel = new JPanel();
		leftPanel.setBackground(backgroundColor);
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
		leftPanel.setPreferredSize(new Dimension(leftPanelWidth,
				leftPanelHeight));

		// Listeners
		openLog.addActionListener(new ListenOpenLog());
		generateKml.addActionListener(new ListenGenerateKml());
		openLocations.addActionListener(new ListenOpenLocations());
		generateProcessing.addActionListener(new ListenGenerateProcessing());
		saveProcessingPlot.addActionListener(new ListenSaveProcessingPlot());
		branchesMaxColorChooser
				.addActionListener(new ListenBranchesMaxColorChooser());
		branchesMinColorChooser
				.addActionListener(new ListenBranchesMinColorChooser());

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Load log file:"));
		tmpPanel.add(openLog);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Load locations file:"));
		tmpPanel.add(openLocations);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Specify burn-in %:"));
		tmpPanel.add(burnInParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setLayout(new GridLayout(1, 2));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Poisson prior mean / offset:"));
		String[] meanPoissonPrior = { "log(2)", "" };
		meanPoissonPriorParser = new JComboBox(meanPoissonPrior);
		meanPoissonPriorParser.setEditable(true);
		tmpPanel.add(meanPoissonPriorParser);
		String[] poissonPriorOffset = { "n-1", "" };
		poissonPriorOffsetParser = new JComboBox(poissonPriorOffset);
		poissonPriorOffsetParser.setEditable(true);
		tmpPanel.add(poissonPriorOffsetParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Bayes Factor cut-off:"));
		tmpPanel.add(bfCutoffParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Number of intervals:"));
		tmpPanel.add(numberOfIntervalsParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Maximal altitude:"));
		tmpPanel.add(maxAltMappingParser);
		leftPanel.add(tmpPanel);

		// Branches color mapping:
		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setLayout(new GridBagLayout());
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Rates color mapping:"));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		tmpPanel.add(branchesMinColorChooser, c);
		c.gridx = 2;
		c.gridy = 0;
		tmpPanel.add(branchesMaxColorChooser, c);
		leftPanel.add(tmpPanel);

		// Branches width:
		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Rates width:"));
		tmpPanel.add(branchesWidthParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("KML name:"));
		tmpPanel.add(kmlPathParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
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
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Save plot:"));
		tmpPanel.add(saveProcessingPlot);
		leftPanel.add(tmpPanel);

		JScrollPane leftScrollPane = new JScrollPane(leftPanel,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		leftScrollPane.setMinimumSize(new Dimension(leftPanelWidth + 60,
				leftPanelHeight));
		add(leftScrollPane, BorderLayout.CENTER);

		/**
		 * Processing pane
		 * */
		rateIndicatorBFToProcessing = new RateIndicatorBFToProcessing();
		rateIndicatorBFToProcessing.setPreferredSize(new Dimension(2048, 1025));

		if (System.getProperty("java.runtime.name").toLowerCase().startsWith(
				"openjdk")) {

			JScrollPane rightScrollPane = new JScrollPane(
					rateIndicatorBFToProcessing,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			add(rightScrollPane, BorderLayout.CENTER);

		} else {

			ScrollPane rightScrollPane = new ScrollPane(
					ScrollPane.SCROLLBARS_ALWAYS);
			rightScrollPane.add(rateIndicatorBFToProcessing);
			add(rightScrollPane, BorderLayout.CENTER);

		}

	}// END: RateIndicatorBFTab

	private class ListenOpenLog implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			try {

				String[] logFiles = new String[] { "log" };

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Opening log file...");
				chooser.setMultiSelectionEnabled(false);
				chooser.addChoosableFileFilter(new SimpleFileFilter(logFiles,
						"Log files (*.log)"));
				chooser.setCurrentDirectory(workingDirectory);

				chooser.showOpenDialog(Utils.getActiveFrame());
				File file = chooser.getSelectedFile();
				logFilename = file.getAbsolutePath();
				System.out.println("Opened " + logFilename + "\n");

				if (workingDirectory == null) {
					workingDirectory = chooser.getCurrentDirectory();
				}

			} catch (Exception e) {
				System.err.println("Could not Open! \n");
			}
		}
	}

	private class ListenOpenLocations implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			try {

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Loading locations file...");
				chooser.setCurrentDirectory(workingDirectory);

				chooser.showOpenDialog(Utils.getActiveFrame());
				File file = chooser.getSelectedFile();
				locationsFilename = file.getAbsolutePath();
				System.out.println("Opened " + locationsFilename + "\n");

				if (workingDirectory == null) {
					workingDirectory = chooser.getCurrentDirectory();
				}

			} catch (Exception e1) {
				System.err.println("Could not Open! \n");
			}
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

			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

				// Executed in background thread
				public Void doInBackground() {

					try {

						generateKml.setEnabled(false);
						progressBar.setIndeterminate(true);

						RateIndicatorBFToKML rateIndicatorBFToKML = new RateIndicatorBFToKML();

						rateIndicatorBFToKML.setLogFilePath(logFilename,
								burnInParser.getValue() / 100.0);

						rateIndicatorBFToKML.setBfCutoff(Double
								.valueOf(bfCutoffParser.getText()));

						rateIndicatorBFToKML
								.setLocationFilePath(locationsFilename);

						rateIndicatorBFToKML.setMaxAltitudeMapping(Double
								.valueOf(maxAltMappingParser.getText()));

						rateIndicatorBFToKML.setNumberOfIntervals(Integer
								.valueOf(numberOfIntervalsParser.getText()));

						rateIndicatorBFToKML.setKmlWriterPath(workingDirectory
								.toString().concat("/").concat(
										kmlPathParser.getText()));

						rateIndicatorBFToKML
								.setMinBranchRedMapping(branchesMinColor
										.getRed());

						rateIndicatorBFToKML
								.setMinBranchGreenMapping(branchesMinColor
										.getGreen());

						rateIndicatorBFToKML
								.setMinBranchBlueMapping(branchesMinColor
										.getBlue());

						rateIndicatorBFToKML
								.setMinBranchOpacityMapping(branchesMinColor
										.getAlpha());

						rateIndicatorBFToKML
								.setMaxBranchRedMapping(branchesMaxColor
										.getRed());

						rateIndicatorBFToKML
								.setMaxBranchGreenMapping(branchesMaxColor
										.getGreen());

						rateIndicatorBFToKML
								.setMaxBranchBlueMapping(branchesMaxColor
										.getBlue());

						rateIndicatorBFToKML
								.setMaxBranchOpacityMapping(branchesMaxColor
										.getAlpha());

						rateIndicatorBFToKML.setBranchWidth(branchesWidthParser
								.getValue());

						if (meanPoissonPriorParser.getSelectedIndex() == 0) {
							rateIndicatorBFToKML.setDefaultMeanPoissonPrior();

						} else {
							rateIndicatorBFToKML.setUserMeanPoissonPrior(Double
									.valueOf(meanPoissonPriorParser
											.getSelectedItem().toString()));
						}

						if (poissonPriorOffsetParser.getSelectedIndex() == 0) {
							rateIndicatorBFToKML.setDefaultPoissonPriorOffset();

						} else {
							rateIndicatorBFToKML
									.setUserPoissonPriorOffset(Double
											.valueOf(poissonPriorOffsetParser
													.getSelectedItem()
													.toString()));
						}

						rateIndicatorBFToKML.GenerateKML();

						System.out.println("Finished in: "
								+ RateIndicatorBFToKML.time + " msec \n");

					} catch (Exception e) {
						e.printStackTrace();

						String msg = String.format("Unexpected problem: %s", e
								.toString());

						JOptionPane.showMessageDialog(Utils.getActiveFrame(),
								msg, "Error", JOptionPane.ERROR_MESSAGE,
								errorIcon);
					}

					return null;
				}// END: doInBackground()

				// Executed in event dispatch thread
				public void done() {
					generateKml.setEnabled(true);
					progressBar.setIndeterminate(false);

					System.out.println("Generated "
							+ workingDirectory.toString().concat("/").concat(
									kmlPathParser.getText()));
				}
			};

			worker.execute();
		}
	}// END: ListenGenerateKml

	private class ListenGenerateProcessing implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

				// Executed in background thread
				public Void doInBackground() {

					try {

						generateProcessing.setEnabled(false);
						progressBar.setIndeterminate(true);

						rateIndicatorBFToProcessing.setLogFilePath(logFilename,
								burnInParser.getValue() / 100.0);

						rateIndicatorBFToProcessing.setBfCutoff(Double
								.valueOf(bfCutoffParser.getText()));

						rateIndicatorBFToProcessing
								.setLocationFilePath(locationsFilename);

						rateIndicatorBFToProcessing
								.setMinBranchRedMapping(branchesMinColor
										.getRed());

						rateIndicatorBFToProcessing
								.setMinBranchGreenMapping(branchesMinColor
										.getGreen());

						rateIndicatorBFToProcessing
								.setMinBranchBlueMapping(branchesMinColor
										.getBlue());

						rateIndicatorBFToProcessing
								.setMinBranchOpacityMapping(branchesMinColor
										.getAlpha());

						rateIndicatorBFToProcessing
								.setMaxBranchRedMapping(branchesMaxColor
										.getRed());

						rateIndicatorBFToProcessing
								.setMaxBranchGreenMapping(branchesMaxColor
										.getGreen());

						rateIndicatorBFToProcessing
								.setMaxBranchBlueMapping(branchesMaxColor
										.getBlue());

						rateIndicatorBFToProcessing
								.setMaxBranchOpacityMapping(branchesMaxColor
										.getAlpha());

						rateIndicatorBFToProcessing
								.setBranchWidth(branchesWidthParser.getValue() / 2);

						if (meanPoissonPriorParser.getSelectedIndex() == 0) {
							rateIndicatorBFToProcessing
									.setDefaultMeanPoissonPrior();

						} else {
							rateIndicatorBFToProcessing
									.setUserMeanPoissonPrior(Double
											.valueOf(meanPoissonPriorParser
													.getSelectedItem()
													.toString()));
						}

						if (poissonPriorOffsetParser.getSelectedIndex() == 0) {
							rateIndicatorBFToProcessing
									.setDefaultPoissonPriorOffset();

						} else {
							rateIndicatorBFToProcessing
									.setUserPoissonPriorOffset(Double
											.valueOf(poissonPriorOffsetParser
													.getSelectedItem()
													.toString()));
						}

						rateIndicatorBFToProcessing.init();

						System.out.println("Finished. \n");

					} catch (Exception e) {
						e.printStackTrace();

						String msg = String.format("Unexpected problem: %s", e
								.toString());

						JOptionPane.showMessageDialog(Utils.getActiveFrame(),
								msg, "Error", JOptionPane.ERROR_MESSAGE,
								errorIcon);
					}

					return null;
				}// END: doInBackground()

				// Executed in event dispatch thread
				public void done() {
					generateProcessing.setEnabled(true);
					progressBar.setIndeterminate(false);
				}
			};

			worker.execute();
		}
	}// END: ListenGenerateProcessing

	private class ListenSaveProcessingPlot implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			try {

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Saving as png file...");

				chooser.showSaveDialog(Utils.getActiveFrame());
				File file = chooser.getSelectedFile();
				String plotToSaveFilename = file.getAbsolutePath();

				rateIndicatorBFToProcessing.save(plotToSaveFilename);
				System.out.println("Saved " + plotToSaveFilename + "\n");

			} catch (Exception e) {
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

}
