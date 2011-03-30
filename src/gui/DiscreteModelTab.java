package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import templates.DiscreteTreeToKML;
import templates.DiscreteTreeToProcessing;
import utils.Utils;

import com.bric.swing.ColorPicker;

@SuppressWarnings("serial")
public class DiscreteModelTab extends JPanel {

	// Sizing constants
	private final int leftPanelWidth = 200;
	private final int leftPanelHeight = 1000;

	// Current date
	private Calendar calendar;
	private SimpleDateFormat formatter;

	// Icons
	private ImageIcon nuclearIcon;
	private ImageIcon treeIcon;
	private ImageIcon locationsIcon;
	private ImageIcon processingIcon;
	private ImageIcon saveIcon;
	private ImageIcon errorIcon;

	// Colors
	private Color backgroundColor;
	private Color polygonsColor;
	private Color branchesColor;

	// Strings for paths
	private String treeFilename;
	private String locationsFilename;
	private String workingDirectory;

	// Text fields
	private JTextField stateAttNameParser;
	private JTextField mrsdStringParser;
	private JComboBox eraParser;
	private JTextField numberOfIntervalsParser;
	private JTextField maxAltMappingParser;
	private JTextField kmlPathParser;

	// Buttons
	private JButton generateKml;
	private JButton openTree;
	private JButton openLocations;
	private JButton generateProcessing;
	private JButton saveProcessingPlot;
	private JButton polygonsColorChooser;
	private JButton branchesColorChooser;

	// left tools pane
	private JPanel leftPanel;
	private JPanel tmpPanel;

	// Processing pane
	private DiscreteTreeToProcessing discreteTreeToProcessing;

	// Progress bar
	private JProgressBar progressBar;

	public DiscreteModelTab() {

		// Setup miscallenous
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		calendar = Calendar.getInstance();
		formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		backgroundColor = new Color(231, 237, 246);
		polygonsColor = new Color(50, 255, 255, 255);
		branchesColor = new Color(255, 5, 50, 255);

		// Setup icons
		nuclearIcon = CreateImageIcon("/icons/nuclear.png");
		treeIcon = CreateImageIcon("/icons/tree.png");
		locationsIcon = CreateImageIcon("/icons/locations.png");
		processingIcon = CreateImageIcon("/icons/processing.png");
		saveIcon = CreateImageIcon("/icons/save.png");
		errorIcon = CreateImageIcon("/icons/error.png");

		// Setup text fields
		stateAttNameParser = new JTextField("states", 5);
		mrsdStringParser = new JTextField(formatter.format(calendar.getTime()),
				8);
		numberOfIntervalsParser = new JTextField("100", 5);
		maxAltMappingParser = new JTextField("5000000", 5);
		kmlPathParser = new JTextField("output.kml", 10);

		// Setup buttons for tab
		generateKml = new JButton("Generate", nuclearIcon);
		openTree = new JButton("Open", treeIcon);
		openLocations = new JButton("Open", locationsIcon);
		generateProcessing = new JButton("Plot", processingIcon);
		saveProcessingPlot = new JButton("Save", saveIcon);
		polygonsColorChooser = new JButton("Setup");
		branchesColorChooser = new JButton("Setup");
		
		// Setup progress bar
		progressBar = new JProgressBar();

		/**
		 * left tools pane
		 * */
		leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setPreferredSize(new Dimension(leftPanelWidth,
				leftPanelHeight));

		openTree.addActionListener(new ListenOpenTree());
		generateKml.addActionListener(new ListenGenerateKml());
		openLocations.addActionListener(new ListenOpenLocations());
		generateProcessing.addActionListener(new ListenGenerateProcessing());
		saveProcessingPlot.addActionListener(new ListenSaveProcessingPlot());
		polygonsColorChooser
				.addActionListener(new ListenPolygonsColorChooser());
		branchesColorChooser
				.addActionListener(new ListenBranchesColorChooser());

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Load tree file:"));
		tmpPanel.add(openTree);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Load locations file:"));
		tmpPanel.add(openLocations);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Most recent sampling date:"));
		String era[] = { "AD", "BC" };
		eraParser = new JComboBox(era);
		tmpPanel.add(mrsdStringParser);
		tmpPanel.add(eraParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("State attribute name:"));
		tmpPanel.add(stateAttNameParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Number of intervals:"));
		tmpPanel.add(numberOfIntervalsParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Maximal altitude:"));
		tmpPanel.add(maxAltMappingParser);
		leftPanel.add(tmpPanel);

		// Polygons color mapping:
		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Polygons color mapping:"));
		tmpPanel.add(polygonsColorChooser);
		leftPanel.add(tmpPanel);

		// Branches color mapping:
		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Branches color mapping:"));
		tmpPanel.add(branchesColorChooser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("KML name:"));
		tmpPanel.add(kmlPathParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Generate KML / Plot tree:"));
		tmpPanel.setPreferredSize(new Dimension(leftPanelWidth, 100));
		tmpPanel.add(generateKml);
		tmpPanel.add(generateProcessing);
		tmpPanel.add(progressBar);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Save plot:"));
		tmpPanel.add(saveProcessingPlot);
		leftPanel.add(tmpPanel);

		JScrollPane leftScrollPane = new JScrollPane(leftPanel,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		leftScrollPane.setMinimumSize(new Dimension(leftPanelWidth + 40,
				leftPanelHeight));
		add(leftScrollPane, BorderLayout.CENTER);

		/**
		 * Processing pane
		 * */
		discreteTreeToProcessing = new DiscreteTreeToProcessing();
		discreteTreeToProcessing.setPreferredSize(new Dimension(2048, 1025));
		JScrollPane rightScrollPane = new JScrollPane(discreteTreeToProcessing,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(rightScrollPane, BorderLayout.CENTER);

	}

	private class ListenOpenTree implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			try {

				String[] treeFiles = new String[] { "tre", "tree" };

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Loading tree file...");
				chooser.setMultiSelectionEnabled(false);
				chooser.addChoosableFileFilter(new SimpleFileFilter(treeFiles,
						"Tree files (*.tree, *.tre)"));

				chooser.showOpenDialog(chooser);
				File file = chooser.getSelectedFile();
				treeFilename = file.getAbsolutePath();

				System.out.println("Opened " + treeFilename + "\n");

				workingDirectory = chooser.getCurrentDirectory().toString();
				System.out.println("Setted working directory to "
						+ workingDirectory + "\n");

			} catch (Exception e) {
				System.err.println("Could not Open! \n");
			}
		}
	}

	private class ListenPolygonsColorChooser implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			polygonsColor = ColorPicker.showDialog(Utils.getActiveFrame(),
					polygonsColor, true);

		}
	}

	private class ListenBranchesColorChooser implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			branchesColor = ColorPicker.showDialog(Utils.getActiveFrame(),
					branchesColor, true);

		}
	}

	private class ListenOpenLocations implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			try {

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Loading locations file...");

				chooser.showOpenDialog(chooser);
				File file = chooser.getSelectedFile();
				locationsFilename = file.getAbsolutePath();
				System.out.println("Opened " + locationsFilename + "\n");

			} catch (Exception e) {
				System.err.println("Could not Open! \n");
			}
		}
	}

	private class ListenGenerateKml implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

				// Executed in background thread
				public Void doInBackground() {

					try {

						generateKml.setEnabled(false);
						progressBar.setIndeterminate(true);

						DiscreteTreeToKML discreteTreeToKML = new DiscreteTreeToKML();

						String mrsdString = mrsdStringParser.getText()
								+ " "
								+ (eraParser.getSelectedIndex() == 0 ? "AD"
										: "BC");

						discreteTreeToKML
								.setLocationFilePath(locationsFilename);

						discreteTreeToKML.setStateAttName(stateAttNameParser
								.getText());

						discreteTreeToKML.setMaxAltitudeMapping(Double
								.valueOf(maxAltMappingParser.getText()));

						discreteTreeToKML.setMrsdString(mrsdString);

						discreteTreeToKML.setNumberOfIntervals(Integer
								.valueOf(numberOfIntervalsParser.getText()));

						discreteTreeToKML.setKmlWriterPath(workingDirectory
								.concat("/").concat(kmlPathParser.getText()));

						discreteTreeToKML.setTreePath(treeFilename);

						discreteTreeToKML.setMaxPolygonRedMapping(polygonsColor
								.getRed());

						discreteTreeToKML
								.setMaxPolygonGreenMapping(polygonsColor
										.getGreen());

						discreteTreeToKML
								.setMaxPolygonBlueMapping(polygonsColor
										.getBlue());

						discreteTreeToKML
								.setMaxPolygonOpacityMapping(polygonsColor
										.getAlpha());

						discreteTreeToKML.setMaxBranchRedMapping(branchesColor
								.getRed());

						discreteTreeToKML
								.setMaxBranchGreenMapping(branchesColor
										.getGreen());

						discreteTreeToKML.setMaxBranchBlueMapping(branchesColor
								.getBlue());

						discreteTreeToKML
								.setMaxBranchOpacityMapping(branchesColor
										.getAlpha());

						discreteTreeToKML.GenerateKML();

						System.out.println("Finished in: "
								+ discreteTreeToKML.time + " msec \n");

					} catch (Exception e) {
						e.printStackTrace();

						JOptionPane.showMessageDialog(Utils.getActiveFrame(), e
								.toString(), "Error",
								JOptionPane.ERROR_MESSAGE, errorIcon);
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

						discreteTreeToProcessing
								.setStateAttName(stateAttNameParser.getText());

						discreteTreeToProcessing
								.setLocationFilePath(locationsFilename);

						discreteTreeToProcessing.setTreePath(treeFilename);

						discreteTreeToProcessing
								.setMaxBranchRedMapping(branchesColor.getRed());

						discreteTreeToProcessing
								.setMaxBranchGreenMapping(branchesColor
										.getGreen());

						discreteTreeToProcessing
								.setMaxBranchBlueMapping(branchesColor
										.getBlue());

						discreteTreeToProcessing
								.setMaxBranchOpacityMapping(branchesColor
										.getAlpha());

						discreteTreeToProcessing.init();

					} catch (Exception e) {
						e.printStackTrace();

						JOptionPane.showMessageDialog(Utils.getActiveFrame(), e
								.toString(), "Error",
								JOptionPane.ERROR_MESSAGE, errorIcon);
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
				// System.getProperty("user.dir")

				chooser.showSaveDialog(chooser);
				File file = chooser.getSelectedFile();
				String plotToSaveFilename = file.getAbsolutePath();

				discreteTreeToProcessing.save(plotToSaveFilename);
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
