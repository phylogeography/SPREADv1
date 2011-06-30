package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import templates.DiscreteTreeToKML;
import templates.DiscreteTreeToProcessing;
import utils.Utils;
import checks.DiscreteSanityCheck;

import com.bric.swing.ColorPicker;

@SuppressWarnings("serial")
public class DiscreteModelTab extends JPanel {

	// Sizing constants
	private final int leftPanelWidth = 200;
	private final int leftPanelHeight = 1200;

	// Icons
	private ImageIcon nuclearIcon;
	private ImageIcon treeIcon;
	private ImageIcon locationsIcon;
	private ImageIcon processingIcon;
	private ImageIcon saveIcon;
	private ImageIcon errorIcon;

	// Colors
	private Color backgroundColor;
	private Color polygonsMaxColor;
	private Color branchesMaxColor;
	private Color polygonsMinColor;
	private Color branchesMinColor;

	// Locations & coordinates table
	private InteractiveTableModel table;

	// Strings for paths
	private String treeFilename;
	private File workingDirectory;

	// Text fields
	private JTextField stateAttNameParser;
	private JTextField numberOfIntervalsParser;
	private JTextField maxAltMappingParser;
	private JTextField kmlPathParser;

	// Spinners
	private DateSpinner dateSpinner;

	// Buttons
	private JButton generateKml;
	private JButton openTree;
	private JButton openLocationCoordinatesEditor;
	private JButton generateProcessing;
	private JButton saveProcessingPlot;
	private JButton polygonsMaxColorChooser;
	private JButton branchesMaxColorChooser;
	private JButton polygonsMinColorChooser;
	private JButton branchesMinColorChooser;

	// Sliders
	private JSlider branchesWidthParser;
	private JSlider polygonsRadiusMultiplierParser;

	// Combo boxes
	private JComboBox eraParser;

	// left tools pane
	private JPanel leftPanel;
	private JPanel tmpPanel;
	private SpinningPanel sp;
	private JPanel tmpPanelsHolder;

	// Processing pane
	private DiscreteTreeToProcessing discreteTreeToProcessing;

	// Progress bar
	private JProgressBar progressBar;

	public DiscreteModelTab() {

		// Setup miscallenous
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		backgroundColor = new Color(231, 237, 246);
		polygonsMaxColor = new Color(50, 255, 255, 255);
		branchesMaxColor = new Color(255, 5, 50, 255);
		polygonsMinColor = new Color(0, 0, 0, 100);
		branchesMinColor = new Color(0, 0, 0, 255);
		GridBagConstraints c = new GridBagConstraints();

		// Setup icons
		nuclearIcon = CreateImageIcon("/icons/nuclear.png");
		treeIcon = CreateImageIcon("/icons/tree.png");
		locationsIcon = CreateImageIcon("/icons/locations.png");
		processingIcon = CreateImageIcon("/icons/processing.png");
		saveIcon = CreateImageIcon("/icons/save.png");
		errorIcon = CreateImageIcon("/icons/error.png");

		// Setup text fields
		stateAttNameParser = new JTextField("states", 10);
		numberOfIntervalsParser = new JTextField("100", 10);
		maxAltMappingParser = new JTextField("5000000", 10);
		kmlPathParser = new JTextField("output.kml", 10);

		// Setup buttons for tab
		generateKml = new JButton("Generate", nuclearIcon);
		openTree = new JButton("Open", treeIcon);
		openLocationCoordinatesEditor = new JButton("Setup", locationsIcon);
		generateProcessing = new JButton("Plot", processingIcon);
		saveProcessingPlot = new JButton("Save", saveIcon);
		polygonsMaxColorChooser = new JButton("Setup max");
		branchesMaxColorChooser = new JButton("Setup max");
		polygonsMinColorChooser = new JButton("Setup min");
		branchesMinColorChooser = new JButton("Setup min");

		// Setup sliders
		branchesWidthParser = new JSlider(JSlider.HORIZONTAL, 2, 10, 4);
		branchesWidthParser.setMajorTickSpacing(2);
		branchesWidthParser.setMinorTickSpacing(1);
		branchesWidthParser.setPaintTicks(true);
		branchesWidthParser.setPaintLabels(true);
		polygonsRadiusMultiplierParser = new JSlider(JSlider.HORIZONTAL, 1, 11,
				1);
		polygonsRadiusMultiplierParser.setMajorTickSpacing(2);
		polygonsRadiusMultiplierParser.setMinorTickSpacing(1);
		polygonsRadiusMultiplierParser.setPaintTicks(true);
		polygonsRadiusMultiplierParser.setPaintLabels(true);

		// Setup progress bar
		progressBar = new JProgressBar();

		/**
		 * left tools pane
		 * */
		leftPanel = new JPanel();
		leftPanel.setBackground(backgroundColor);
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setPreferredSize(new Dimension(leftPanelWidth,
				leftPanelHeight));

		openTree.addActionListener(new ListenOpenTree());
		generateKml.addActionListener(new ListenGenerateKml());
		openLocationCoordinatesEditor
				.addActionListener(new ListenOpenLocationCoordinatesEditor());
		generateProcessing.addActionListener(new ListenGenerateProcessing());
		saveProcessingPlot.addActionListener(new ListenSaveProcessingPlot());
		polygonsMaxColorChooser
				.addActionListener(new ListenPolygonsMaxColorChooser());
		branchesMaxColorChooser
				.addActionListener(new ListenBranchesMaxColorChooser());
		polygonsMinColorChooser
				.addActionListener(new ListenPolygonsMinColorChooser());
		branchesMinColorChooser
				.addActionListener(new ListenBranchesMinColorChooser());

		// /////////////
		// ---INPUT---//
		// /////////////

		tmpPanelsHolder = new JPanel();
		tmpPanelsHolder.setLayout(new BoxLayout(tmpPanelsHolder,
				BoxLayout.Y_AXIS));

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Load tree file:"));
		tmpPanel.add(openTree);
		tmpPanelsHolder.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Setup location coordinates:"));
		tmpPanel.add(openLocationCoordinatesEditor);
		tmpPanelsHolder.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
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
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("State attribute name:"));
		tmpPanel.add(stateAttNameParser);
		tmpPanelsHolder.add(tmpPanel);

		sp = new SpinningPanel(tmpPanelsHolder, "   Input", new Dimension(
				leftPanelWidth + 60, 20));
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
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setLayout(new GridBagLayout());
		tmpPanel.setBackground(backgroundColor);
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
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Branches width:"));
		tmpPanel.add(branchesWidthParser);
		tmpPanelsHolder.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Maximal altitude:"));
		tmpPanel.add(maxAltMappingParser);
		tmpPanelsHolder.add(tmpPanel);

		sp = new SpinningPanel(tmpPanelsHolder, "   Branches mapping",
				new Dimension(leftPanelWidth + 60, 20));
		sp.showBottom(false);
		leftPanel.add(sp);

		// ////////////////////////
		// ---POLYGONS MAPPING---//
		// ////////////////////////

		tmpPanelsHolder = new JPanel();
		tmpPanelsHolder.setLayout(new BoxLayout(tmpPanelsHolder,
				BoxLayout.Y_AXIS));

		// Circles color mapping:
		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setLayout(new GridBagLayout());
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Circles color mapping:"));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		tmpPanel.add(polygonsMinColorChooser, c);
		c.gridx = 2;
		c.gridy = 0;
		tmpPanel.add(polygonsMaxColorChooser, c);
		tmpPanelsHolder.add(tmpPanel);

		// Circles radius multiplier:
		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Circles radius multiplier:"));
		tmpPanel.add(polygonsRadiusMultiplierParser);
		tmpPanelsHolder.add(tmpPanel);

		sp = new SpinningPanel(tmpPanelsHolder, "   Circles mapping",
				new Dimension(leftPanelWidth + 60, 20));
		sp.showBottom(false);
		leftPanel.add(sp);

		// ////////////////////
		// ---COMPUTATIONS---//
		// ////////////////////

		tmpPanelsHolder = new JPanel();
		tmpPanelsHolder.setLayout(new BoxLayout(tmpPanelsHolder,
				BoxLayout.Y_AXIS));

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Number of intervals:"));
		tmpPanel.add(numberOfIntervalsParser);
		tmpPanelsHolder.add(tmpPanel);

		sp = new SpinningPanel(tmpPanelsHolder, "   Computations",
				new Dimension(leftPanelWidth + 60, 20));
		sp.showBottom(false);
		leftPanel.add(sp);

		// //////////////
		// ---OUTPUT---//
		// //////////////

		tmpPanelsHolder = new JPanel();
		tmpPanelsHolder.setLayout(new BoxLayout(tmpPanelsHolder,
				BoxLayout.Y_AXIS));

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("KML name:"));
		tmpPanel.add(kmlPathParser);
		tmpPanelsHolder.add(tmpPanel);

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
		tmpPanelsHolder.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Save plot:"));
		tmpPanel.add(saveProcessingPlot);
		tmpPanelsHolder.add(tmpPanel);

		sp = new SpinningPanel(tmpPanelsHolder, "   Output", new Dimension(
				leftPanelWidth + 60, 20));
		sp.showBottom(false);
		leftPanel.add(sp);

		// ////////////////////////
		// ---LEFT SCROLL PANE---//
		// ////////////////////////

		JScrollPane leftScrollPane = new JScrollPane(leftPanel,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		leftScrollPane.setMinimumSize(new Dimension(leftPanelWidth + 60,
				leftPanelHeight));
		add(leftScrollPane, BorderLayout.CENTER);

		/**
		 * Processing pane
		 * */
		discreteTreeToProcessing = new DiscreteTreeToProcessing();
		discreteTreeToProcessing.setPreferredSize(new Dimension(2048, 1025));

		if (System.getProperty("java.runtime.name").toLowerCase().startsWith(
				"openjdk")) {

			JScrollPane rightScrollPane = new JScrollPane(
					discreteTreeToProcessing,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			add(rightScrollPane, BorderLayout.CENTER);

		} else {

			ScrollPane rightScrollPane = new ScrollPane(
					ScrollPane.SCROLLBARS_ALWAYS);
			rightScrollPane.add(discreteTreeToProcessing);
			add(rightScrollPane, BorderLayout.CENTER);

		}

	} // END: discreteModelTab

	private class ListenOpenTree implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			try {

				String[] treeFiles = new String[] { "tre", "tree" };

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Loading tree file...");
				chooser.setMultiSelectionEnabled(false);
				chooser.addChoosableFileFilter(new SimpleFileFilter(treeFiles,
						"Tree files (*.tree, *.tre)"));
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
				System.err.println("Could not Open! \n");
			}
		}
	}// END: ListenOpenTree

	private class ListenOpenLocationCoordinatesEditor implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			try {

				LocationCoordinatesEditor locationCoordinatesEditor = new LocationCoordinatesEditor();
				locationCoordinatesEditor.launch(treeFilename,
						stateAttNameParser.getText(), workingDirectory);

				table = locationCoordinatesEditor.getTable();

			} catch (NullPointerException e) {

				e.printStackTrace();

				String msg = String.format("Unexpected problem: %s", e
						.toString()
						+ "\nHave you imported the tree file?");

				JOptionPane.showMessageDialog(Utils.getActiveFrame(), msg,
						"Error", JOptionPane.ERROR_MESSAGE, errorIcon);

			} catch (RuntimeException e) {

				e.printStackTrace();

				String msg = String
						.format(
								"Unexpected problem: %s",
								e.toString()
										+ "\nHave you specified proper state attribute name?"
										+ "\nHave you set the posterior probability limit in treeAnnotator to zero?");

				JOptionPane.showMessageDialog(Utils.getActiveFrame(), msg,
						"Error", JOptionPane.ERROR_MESSAGE, errorIcon);

			}
		}
	}// END: ListenOpenLocations

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
		public void actionPerformed(ActionEvent e) {

			generateKml.setEnabled(false);
			progressBar.setIndeterminate(true);

			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

				// Executed in background thread
				public Void doInBackground() {

					try {

						if (new DiscreteSanityCheck().check(treeFilename,
								stateAttNameParser.getText(), table)) {

							DiscreteTreeToKML discreteTreeToKML = new DiscreteTreeToKML();

							discreteTreeToKML.setTable(table);

							discreteTreeToKML
									.setStateAttName(stateAttNameParser
											.getText());

							discreteTreeToKML.setMaxAltitudeMapping(Double
									.valueOf(maxAltMappingParser.getText()));

							discreteTreeToKML.setMrsdString(dateSpinner
									.getValue()
									+ " "
									+ (eraParser.getSelectedIndex() == 0 ? "AD"
											: "BC"));

							discreteTreeToKML
									.setNumberOfIntervals(Integer
											.valueOf(numberOfIntervalsParser
													.getText()));

							discreteTreeToKML.setKmlWriterPath(workingDirectory
									.toString().concat("/").concat(
											kmlPathParser.getText()));

							discreteTreeToKML.setTreePath(treeFilename);

							discreteTreeToKML
									.setMinPolygonRedMapping(polygonsMinColor
											.getRed());

							discreteTreeToKML
									.setMinPolygonGreenMapping(polygonsMinColor
											.getGreen());

							discreteTreeToKML
									.setMinPolygonBlueMapping(polygonsMinColor
											.getBlue());

							discreteTreeToKML
									.setMinPolygonOpacityMapping(polygonsMinColor
											.getAlpha());

							discreteTreeToKML
									.setMaxPolygonRedMapping(polygonsMaxColor
											.getRed());

							discreteTreeToKML
									.setMaxPolygonGreenMapping(polygonsMaxColor
											.getGreen());

							discreteTreeToKML
									.setMaxPolygonBlueMapping(polygonsMaxColor
											.getBlue());

							discreteTreeToKML
									.setMaxPolygonOpacityMapping(polygonsMaxColor
											.getAlpha());

							discreteTreeToKML
									.setPolygonsRadiusMultiplier(polygonsRadiusMultiplierParser
											.getValue());

							discreteTreeToKML
									.setMinBranchRedMapping(branchesMinColor
											.getRed());

							discreteTreeToKML
									.setMinBranchGreenMapping(branchesMinColor
											.getGreen());

							discreteTreeToKML
									.setMinBranchBlueMapping(branchesMinColor
											.getBlue());

							discreteTreeToKML
									.setMinBranchOpacityMapping(branchesMinColor
											.getAlpha());

							discreteTreeToKML
									.setMaxBranchRedMapping(branchesMaxColor
											.getRed());

							discreteTreeToKML
									.setMaxBranchGreenMapping(branchesMaxColor
											.getGreen());

							discreteTreeToKML
									.setMaxBranchBlueMapping(branchesMaxColor
											.getBlue());

							discreteTreeToKML
									.setMaxBranchOpacityMapping(branchesMaxColor
											.getAlpha());

							discreteTreeToKML
									.setBranchWidth(branchesWidthParser
											.getValue());

							discreteTreeToKML.GenerateKML();

							System.out.println("Finished in: "
									+ discreteTreeToKML.time + " msec \n");

						}// END: check

					} catch (final Exception e) {

						SwingUtilities.invokeLater(new Runnable() {

							public void run() {

								e.printStackTrace();

								String msg = String.format(
										"Unexpected problem: %s", e.toString());

								JOptionPane.showMessageDialog(Utils
										.getActiveFrame(), msg, "Error",
										JOptionPane.ERROR_MESSAGE, errorIcon);

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
							+ workingDirectory.toString().concat("/").concat(
									kmlPathParser.getText()));
				}
			};

			worker.execute();
		}
	}// END: ListenGenerateKml

	private class ListenGenerateProcessing implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			generateProcessing.setEnabled(false);
			progressBar.setIndeterminate(true);

			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

				// Executed in background thread
				public Void doInBackground() {

					// TODO This work should be activated after each option
					// changes (automatic responsive plotting); will make for a
					// much slicker program

					try {

						if (new DiscreteSanityCheck().check(treeFilename,
								stateAttNameParser.getText(), table)) {

							// TODO Should only be done with state data changes,
							// not with each draw
							discreteTreeToProcessing
									.setStateAttName(stateAttNameParser
											.getText());

							// TODO Should only be done when changed,
							// not with each draw
							discreteTreeToProcessing.setTable(table);

							// TODO Should only be done when changed,
							// not with each draw
							discreteTreeToProcessing.setTreePath(treeFilename);

							discreteTreeToProcessing
									.setNumberOfIntervals(Integer
											.valueOf(numberOfIntervalsParser
													.getText()));

							discreteTreeToProcessing
									.setMinPolygonRedMapping(polygonsMinColor
											.getRed());

							discreteTreeToProcessing
									.setMinPolygonGreenMapping(polygonsMinColor
											.getGreen());

							discreteTreeToProcessing
									.setMinPolygonBlueMapping(polygonsMinColor
											.getBlue());

							discreteTreeToProcessing
									.setMinPolygonOpacityMapping(polygonsMinColor
											.getAlpha());

							discreteTreeToProcessing
									.setMaxPolygonRedMapping(polygonsMaxColor
											.getRed());

							discreteTreeToProcessing
									.setMaxPolygonGreenMapping(polygonsMaxColor
											.getGreen());

							discreteTreeToProcessing
									.setMaxPolygonBlueMapping(polygonsMaxColor
											.getBlue());

							discreteTreeToProcessing
									.setMaxPolygonOpacityMapping(polygonsMaxColor
											.getAlpha());

							discreteTreeToProcessing
									.setPolygonsRadiusMultiplier(polygonsRadiusMultiplierParser
											.getValue());

							discreteTreeToProcessing
									.setMinBranchRedMapping(branchesMinColor
											.getRed());

							discreteTreeToProcessing
									.setMinBranchGreenMapping(branchesMinColor
											.getGreen());

							discreteTreeToProcessing
									.setMinBranchBlueMapping(branchesMinColor
											.getBlue());

							discreteTreeToProcessing
									.setMinBranchOpacityMapping(branchesMinColor
											.getAlpha());

							discreteTreeToProcessing
									.setMaxBranchRedMapping(branchesMaxColor
											.getRed());

							discreteTreeToProcessing
									.setMaxBranchGreenMapping(branchesMaxColor
											.getGreen());

							discreteTreeToProcessing
									.setMaxBranchBlueMapping(branchesMaxColor
											.getBlue());

							discreteTreeToProcessing
									.setMaxBranchOpacityMapping(branchesMaxColor
											.getAlpha());

							discreteTreeToProcessing
									.setBranchWidth(branchesWidthParser
											.getValue() / 2);

							discreteTreeToProcessing.init();

							System.out.println("Finished. \n");

						}// END: check

					} catch (final Exception e) {

						SwingUtilities.invokeLater(new Runnable() {

							public void run() {

								e.printStackTrace();

								String msg = String.format(
										"Unexpected problem: %s", e.toString());

								JOptionPane.showMessageDialog(Utils
										.getActiveFrame(), msg, "Error",
										JOptionPane.ERROR_MESSAGE, errorIcon);

							}
						});

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

				discreteTreeToProcessing.save(plotToSaveFilename);
				System.out.println("Saved " + plotToSaveFilename + "\n");
				// table.printTable();

			} catch (Exception e) {
				e.printStackTrace();
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
