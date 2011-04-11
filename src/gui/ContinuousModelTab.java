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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import templates.ContinuousTreeToKML;
import templates.ContinuousTreeToProcessing;
import utils.Utils;

import com.bric.swing.ColorPicker;

@SuppressWarnings("serial")
public class ContinuousModelTab extends JPanel {

	// Sizing constants
	private final int leftPanelWidth = 200;
	private final int leftPanelHeight = 1050;

	// Icons
	private ImageIcon nuclearIcon;
	private ImageIcon treeIcon;
	private ImageIcon processingIcon;
	private ImageIcon saveIcon;
	private ImageIcon errorIcon;

	// Colors
	private Color backgroundColor;
	private Color polygonsMaxColor;
	private Color branchesMaxColor;

	// Strings for paths
	private String treeFilename;
	private File workingDirectory;

	// Labels
	private JLabel tmpLabel;

	// Text fields
	private JTextField coordinatesNameParser;
	private JTextField HPDParser;
	private JComboBox eraParser;
	private JTextField numberOfIntervalsParser;
	private JTextField maxAltMappingParser;
	private JTextField kmlPathParser;

	// Spinners
	private DateSpinner spinnerDate;

	// Buttons
	private JButton generateKml;
	private JButton openTree;
	private JButton generateProcessing;
	private JButton saveProcessingPlot;
	private JButton polygonsMaxColorChooser;
	private JButton branchesMaxColorChooser;

	// Sliders
	private JSlider branchesWidthParser;

	// Left tools pane
	private JPanel leftPanel;
	private JPanel tmpPanel;

	// Processing pane
	private ContinuousTreeToProcessing continuousTreeToProcessing;

	// Progress bar
	private JProgressBar progressBar;

	public ContinuousModelTab() {

		// Setup miscallenous
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		backgroundColor = new Color(231, 237, 246);
		polygonsMaxColor = new Color(50, 255, 255, 255);
		branchesMaxColor = new Color(255, 5, 50, 255);
		GridBagConstraints c = new GridBagConstraints();

		// Setup icons
		nuclearIcon = CreateImageIcon("/icons/nuclear.png");
		treeIcon = CreateImageIcon("/icons/tree.png");
		processingIcon = CreateImageIcon("/icons/processing.png");
		saveIcon = CreateImageIcon("/icons/save.png");
		errorIcon = CreateImageIcon("/icons/error.png");

		// Setup text fields
		coordinatesNameParser = new JTextField("location", 10);
		HPDParser = new JTextField("95", 2);
		numberOfIntervalsParser = new JTextField("100", 10);
		maxAltMappingParser = new JTextField("5000000", 10);
		kmlPathParser = new JTextField("output.kml", 10);

		// Setup buttons
		generateKml = new JButton("Generate", nuclearIcon);
		openTree = new JButton("Open", treeIcon);
		generateProcessing = new JButton("Plot", processingIcon);
		saveProcessingPlot = new JButton("Save", saveIcon);
		polygonsMaxColorChooser = new JButton("Setup");
		branchesMaxColorChooser = new JButton("Setup");

		// Setup sliders
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
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setPreferredSize(new Dimension(leftPanelWidth,
				leftPanelHeight));

		// Listeners
		openTree.addActionListener(new ListenOpenTree());
		generateKml.addActionListener(new ListenGenerateKml());
		generateProcessing.addActionListener(new ListenGenerateProcessing());
		saveProcessingPlot.addActionListener(new ListenSaveProcessingPlot());
		polygonsMaxColorChooser
				.addActionListener(new ListenPolygonsMaxColorChooser());
		branchesMaxColorChooser
				.addActionListener(new ListenBranchesMaxColorChooser());

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Load tree file:"));
		tmpPanel.add(openTree);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setLayout(new GridBagLayout());
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Most recent sampling date:"));
		spinnerDate = new DateSpinner();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		tmpPanel.add(spinnerDate, c);
		String era[] = { "AD", "BC" };
		eraParser = new JComboBox(era);
		c.gridx = 2;
		c.gridy = 0;
		tmpPanel.add(eraParser, c);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Coordinate attribute name:"));
		tmpPanel.add(coordinatesNameParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpLabel = new JLabel("%");
		tmpPanel.setBorder(new TitledBorder("HPD:"));
		tmpPanel.add(HPDParser);
		tmpPanel.add(tmpLabel);
		tmpLabel.setLabelFor(tmpPanel);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Number of intervals:"));
		tmpPanel.add(numberOfIntervalsParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Maximal altitude mapping:"));
		tmpPanel.add(maxAltMappingParser);
		leftPanel.add(tmpPanel);

		// Polygons color mapping:
		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Polygons color mapping:"));
		tmpPanel.add(polygonsMaxColorChooser);
		leftPanel.add(tmpPanel);

		// Branches color mapping:
		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Branches color mapping:"));
		tmpPanel.add(branchesMaxColorChooser);
		leftPanel.add(tmpPanel);

		// Branches width:
		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Branches width:"));
		tmpPanel.add(branchesWidthParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("KML name:"));
		tmpPanel.add(kmlPathParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setMaximumSize(new Dimension(leftPanelWidth + 60, 100));
		tmpPanel.setLayout(new GridBagLayout());
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Generate KML / Plot tree:"));
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
		continuousTreeToProcessing = new ContinuousTreeToProcessing();
		continuousTreeToProcessing.setPreferredSize(new Dimension(2048, 1025));

		if (System.getProperty("java.runtime.name").toLowerCase().startsWith(
				"openjdk")) {

			JScrollPane rightScrollPane = new JScrollPane(
					continuousTreeToProcessing,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			add(rightScrollPane, BorderLayout.CENTER);

		} else {

			ScrollPane rightScrollPane = new ScrollPane(
					ScrollPane.SCROLLBARS_ALWAYS);
			rightScrollPane.add(continuousTreeToProcessing);
			add(rightScrollPane, BorderLayout.CENTER);

		}

	}// END: continuousModelTab

	private class ListenOpenTree implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			try {

				String[] treeFiles = new String[] { "tre", "tree" };

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Loading tree file...");
				chooser.setMultiSelectionEnabled(false);
				chooser.addChoosableFileFilter(new SimpleFileFilter(treeFiles,
						"Tree files (*.tree, *.tre)"));

				chooser.showOpenDialog(Utils.getActiveFrame());
				File file = chooser.getSelectedFile();
				treeFilename = file.getAbsolutePath();
				System.out.println("Opened " + treeFilename + "\n");

				workingDirectory = chooser.getCurrentDirectory();
				System.out.println("Setted working directory to "
						+ workingDirectory.toString() + "\n");

			} catch (Exception e) {
				System.err.println("Could not Open! \n");
			}
		}
	}

	private class ListenPolygonsMaxColorChooser implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			Color c = ColorPicker.showDialog(Utils.getActiveFrame(),
					"Choose polygons color...", polygonsMaxColor, true);

			if (c != null)
				polygonsMaxColor = c;

		}
	}

	private class ListenBranchesMaxColorChooser implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			Color c = ColorPicker.showDialog(Utils.getActiveFrame(),
					branchesMaxColor, true);

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

						ContinuousTreeToKML continuousTreeToKML = new ContinuousTreeToKML();

						String mrsdString = spinnerDate.getValue()
								+ " "
								+ (eraParser.getSelectedIndex() == 0 ? "AD"
										: "BC");

						continuousTreeToKML.setHPD(HPDParser.getText() + "%");

						continuousTreeToKML
								.setCoordinatesName(coordinatesNameParser
										.getText());

						continuousTreeToKML.setMaxAltitudeMapping(Double
								.valueOf(maxAltMappingParser.getText()));

						continuousTreeToKML
								.setMaxPolygonRedMapping(polygonsMaxColor.getRed());

						continuousTreeToKML
								.setMaxPolygonGreenMapping(polygonsMaxColor
										.getGreen());

						continuousTreeToKML
								.setMaxPolygonBlueMapping(polygonsMaxColor
										.getBlue());

						continuousTreeToKML
								.setMaxPolygonOpacityMapping(polygonsMaxColor
										.getAlpha());

						continuousTreeToKML
								.setMaxBranchRedMapping(branchesMaxColor.getRed());

						continuousTreeToKML
								.setMaxBranchGreenMapping(branchesMaxColor
										.getGreen());

						continuousTreeToKML
								.setMaxBranchBlueMapping(branchesMaxColor
										.getBlue());

						continuousTreeToKML
								.setMaxBranchOpacityMapping(branchesMaxColor
										.getAlpha());

						continuousTreeToKML.setBranchWidth(branchesWidthParser
								.getValue());

						continuousTreeToKML.setMrsdString(mrsdString);

						continuousTreeToKML.setNumberOfIntervals(Integer
								.valueOf(numberOfIntervalsParser.getText()));

						continuousTreeToKML.setKmlWriterPath(

						workingDirectory.toString().concat("/").concat(
								kmlPathParser.getText()));

						continuousTreeToKML.setTreePath(treeFilename);

						continuousTreeToKML.GenerateKML();

						System.out.println("Finished in: "
								+ continuousTreeToKML.time + " msec \n");

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

						continuousTreeToProcessing.setTreePath(treeFilename);

						continuousTreeToProcessing
								.setCoordinatesName(coordinatesNameParser
										.getText());

						continuousTreeToProcessing.setHPD(HPDParser.getText()
								+ "%");

						continuousTreeToProcessing
								.setMaxPolygonRedMapping(polygonsMaxColor.getRed());

						continuousTreeToProcessing
								.setMaxPolygonGreenMapping(polygonsMaxColor
										.getGreen());

						continuousTreeToProcessing
								.setMaxPolygonBlueMapping(polygonsMaxColor
										.getBlue());

						continuousTreeToProcessing
								.setMaxPolygonOpacityMapping(polygonsMaxColor
										.getAlpha());

						continuousTreeToProcessing
								.setMaxBranchRedMapping(branchesMaxColor.getRed());

						continuousTreeToProcessing
								.setMaxBranchGreenMapping(branchesMaxColor
										.getGreen());

						continuousTreeToProcessing
								.setMaxBranchBlueMapping(branchesMaxColor
										.getBlue());

						continuousTreeToProcessing
								.setMaxBranchOpacityMapping(branchesMaxColor
										.getAlpha());

						continuousTreeToProcessing
								.setBranchWidth(branchesWidthParser.getValue() / 2);

						continuousTreeToProcessing.init();

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
					System.out.println("Finished. \n");

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

				continuousTreeToProcessing.save(plotToSaveFilename);
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
			System.err.println("Couldn't find file: \n" + path + "\n");
			return null;
		}
	}

}// END class

