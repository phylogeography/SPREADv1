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

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

import templates.TimeSlicerToKML;
import templates.TimeSlicerToProcessing;
import utils.Utils;

@SuppressWarnings("serial")
public class TimeSlicerTab extends JPanel {

	// Sizing constants
	private final int leftPanelWidth = 230;
	private final int leftPanelHeight = 1850;

	// Current date
	private Calendar calendar;
	private SimpleDateFormat formatter;

	// Icons
	private ImageIcon nuclearIcon;
	private ImageIcon treeIcon;
	private ImageIcon treesIcon;
	private ImageIcon processingIcon;
	private ImageIcon saveIcon;
	private ImageIcon errorIcon;
	
	// Colors
	private Color backgroundColor;

	// Strings for paths
	private String mccTreeFilename;
	private String treesFilename;
	private String workingDirectory;

	// Labels
	JLabel tmpLabel;

	// Text fields
	private JTextField burnInParser;
	private JTextField locationAttNameParser;
	private JTextField rateAttNameParser;
	private JTextField precisionAttNameParser;
	private JTextField mrsdStringParser;
	private JComboBox eraParser;
	private JTextField numberOfIntervalsParser;
	private JTextField kmlPathParser;
	private JTextField maxAltMappingParser;

	// Buttons for tab
	private JButton generateKml;
	private JButton openTree;
	private JButton openTrees;
	private JButton generateProcessing;
	private JButton saveProcessingPlot;

	// Sliders
	private JSlider redPolygonSlider;
	private JSlider greenPolygonSlider;
	private JSlider bluePolygonSlider;
	private JSlider opacityPolygonSlider;
	private JSlider redBranchSlider;
	private JSlider greenBranchSlider;
	private JSlider blueBranchSlider;
	private JSlider opacityBranchSlider;

	// checkboxes
	private JCheckBox trueNoiseParser;
	private JCheckBox imputeParser;

	// left tools pane
	private JPanel leftPanel;
	private JPanel tmpPanel;

	// Processing pane
	private TimeSlicerToProcessing timeSlicerToProcessing;

	// Progress bar
	private JProgressBar progressBar;

	public TimeSlicerTab() {

		// Setup miscallenous
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		calendar = Calendar.getInstance();
		formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		backgroundColor = new Color(231, 237, 246);
		
		// Setup icons
		nuclearIcon = CreateImageIcon("/icons/nuclear.png");
		treeIcon = CreateImageIcon("/icons/tree.png");
		treesIcon = CreateImageIcon("/icons/trees.png");
		processingIcon = CreateImageIcon("/icons/processing.png");
		saveIcon = CreateImageIcon("/icons/save.png");
		errorIcon = CreateImageIcon("/icons/error.png");

		// Setup text fields
		burnInParser = new JTextField("500", 10);
		locationAttNameParser = new JTextField("location", 10);
		rateAttNameParser = new JTextField("rate", 10);
		precisionAttNameParser = new JTextField("precision", 10);
		mrsdStringParser = new JTextField(formatter.format(calendar.getTime()),
				8);
		numberOfIntervalsParser = new JTextField("10", 5);
		maxAltMappingParser = new JTextField("500000", 5);
		kmlPathParser = new JTextField("output.kml", 10);

		// Setup buttons for tab
		generateKml = new JButton("Generate", nuclearIcon);
		openTree = new JButton("Open", treeIcon);
		openTrees = new JButton("Open", treesIcon);
		generateProcessing = new JButton("Plot", processingIcon);
		saveProcessingPlot = new JButton("Save", saveIcon);

		// Setup progress bar & checkboxes
		progressBar = new JProgressBar();
		trueNoiseParser = new JCheckBox();
		imputeParser = new JCheckBox();

		/**
		 * left tools pane
		 * */
		leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));// PAGE_AXIS
		leftPanel.setPreferredSize(new Dimension(leftPanelWidth,
				leftPanelHeight));

		openTree.addActionListener(new ListenOpenTree());
		openTrees.addActionListener(new ListenOpenTrees());
		generateKml.addActionListener(new ListenGenerateKml());
		generateProcessing.addActionListener(new ListenGenerateProcessing());
		saveProcessingPlot.addActionListener(new ListenSaveProcessingPlot());
		imputeParser.addActionListener(new listenImputeParser());

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Load tree file:"));
		tmpPanel.add(openTree);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Load trees file:"));
		tmpPanel.add(openTrees);
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
		tmpPanel.setBorder(new TitledBorder("Impute / Use true noise:"));
		tmpPanel.add(imputeParser);
		tmpPanel.add(trueNoiseParser);
		trueNoiseParser.setEnabled(false);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Rate attribute name:"));
		tmpPanel.add(rateAttNameParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Precision attribute name:"));
		tmpPanel.add(precisionAttNameParser);
		leftPanel.add(tmpPanel);

		rateAttNameParser.setEnabled(false);
		precisionAttNameParser.setEnabled(false);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Specify burn-in:"));
		tmpPanel.add(burnInParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Location attribute name:"));
		tmpPanel.add(locationAttNameParser);
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
		tmpPanel.setPreferredSize(new Dimension(leftPanelWidth, 400));
		tmpLabel = new JLabel("Polygons color mapping:");
		tmpPanel.add(tmpLabel);

		redPolygonSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 50);
		redPolygonSlider.setBorder(BorderFactory.createTitledBorder("Red"));
		redPolygonSlider.setMajorTickSpacing(50);
		redPolygonSlider.setMinorTickSpacing(25);
		redPolygonSlider.setPaintTicks(true);
		redPolygonSlider.setPaintLabels(true);
		tmpPanel.add(redPolygonSlider);

		greenPolygonSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 200);
		greenPolygonSlider.setBorder(BorderFactory.createTitledBorder("Green"));
		greenPolygonSlider.setMajorTickSpacing(50);
		greenPolygonSlider.setMinorTickSpacing(25);
		greenPolygonSlider.setPaintTicks(true);
		greenPolygonSlider.setPaintLabels(true);
		tmpPanel.add(greenPolygonSlider);

		bluePolygonSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 200);
		bluePolygonSlider.setBorder(BorderFactory.createTitledBorder("Blue"));
		bluePolygonSlider.setMajorTickSpacing(50);
		bluePolygonSlider.setMinorTickSpacing(25);
		bluePolygonSlider.setPaintTicks(true);
		bluePolygonSlider.setPaintLabels(true);
		tmpPanel.add(bluePolygonSlider);

		opacityPolygonSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 50);
		opacityPolygonSlider.setBorder(BorderFactory
				.createTitledBorder("Opacity"));
		opacityPolygonSlider.setMajorTickSpacing(50);
		opacityPolygonSlider.setMinorTickSpacing(25);
		opacityPolygonSlider.setPaintTicks(true);
		opacityPolygonSlider.setPaintLabels(true);
		tmpPanel.add(opacityPolygonSlider);

		leftPanel.add(tmpPanel);

		// Branches color mapping:
		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setPreferredSize(new Dimension(leftPanelWidth, 400));
		tmpLabel = new JLabel("Branches color mapping:");
		tmpPanel.add(tmpLabel);

		redBranchSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
		redBranchSlider.setBorder(BorderFactory.createTitledBorder("Red"));
		redBranchSlider.setMajorTickSpacing(50);
		redBranchSlider.setMinorTickSpacing(25);
		redBranchSlider.setPaintTicks(true);
		redBranchSlider.setPaintLabels(true);
		tmpPanel.add(redBranchSlider);

		greenBranchSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 5);
		greenBranchSlider.setBorder(BorderFactory.createTitledBorder("Green"));
		greenBranchSlider.setMajorTickSpacing(50);
		greenBranchSlider.setMinorTickSpacing(25);
		greenBranchSlider.setPaintTicks(true);
		greenBranchSlider.setPaintLabels(true);
		tmpPanel.add(greenBranchSlider);

		blueBranchSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 50);
		blueBranchSlider.setBorder(BorderFactory.createTitledBorder("Blue"));
		blueBranchSlider.setMajorTickSpacing(50);
		blueBranchSlider.setMinorTickSpacing(25);
		blueBranchSlider.setPaintTicks(true);
		blueBranchSlider.setPaintLabels(true);
		tmpPanel.add(blueBranchSlider);

		opacityBranchSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
		opacityBranchSlider.setBorder(BorderFactory
				.createTitledBorder("Opacity"));
		opacityBranchSlider.setMajorTickSpacing(50);
		opacityBranchSlider.setMinorTickSpacing(25);
		opacityBranchSlider.setPaintTicks(true);
		opacityBranchSlider.setPaintLabels(true);
		tmpPanel.add(opacityBranchSlider);

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
		leftScrollPane.setMinimumSize(new Dimension(leftPanelWidth,
				leftPanelHeight));
		add(leftScrollPane, BorderLayout.CENTER);

		/**
		 * Processing pane
		 * */
		timeSlicerToProcessing = new TimeSlicerToProcessing();
		timeSlicerToProcessing.setPreferredSize(new Dimension(2048, 1025));
		JScrollPane rightScrollPane = new JScrollPane(timeSlicerToProcessing,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(rightScrollPane, BorderLayout.CENTER);

	}

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

	private class ListenOpenTree implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			try {

				String[] treeFiles = new String[] { "tre", "tree" };

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Opening tree file...");
				chooser.setMultiSelectionEnabled(false);
				chooser.addChoosableFileFilter(new SimpleFileFilter(treeFiles,
						"Tree files (*.tree, *.tre)"));

				chooser.showOpenDialog(chooser);
				File file = chooser.getSelectedFile();
				mccTreeFilename = file.getAbsolutePath();
				System.out.println("Opened " + mccTreeFilename + "\n");

				workingDirectory = chooser.getCurrentDirectory().toString();
				System.out.println("Setted working directory to "
						+ workingDirectory + "\n");

			} catch (Exception e1) {
				System.err.println("Could not Open! \n");
			}
		}
	}

	private class ListenOpenTrees implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			try {

				String[] treesFiles = new String[] { "trees" };

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Loading trees file...");
				chooser.setMultiSelectionEnabled(false);
				chooser.addChoosableFileFilter(new SimpleFileFilter(treesFiles,
						"Tree files (*.trees)"));

				chooser.showOpenDialog(chooser);
				File file = chooser.getSelectedFile();
				treesFilename = file.getAbsolutePath();
				System.out.println("Opened " + treesFilename + "\n");

			} catch (Exception e1) {
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

						TimeSlicerToKML timeSlicerToKML = new TimeSlicerToKML();
						String mrsdString = mrsdStringParser.getText()
								+ " "
								+ (eraParser.getSelectedIndex() == 0 ? "AD"
										: "BC");

						timeSlicerToKML.setTreePath(mccTreeFilename);

						timeSlicerToKML.setTreesPath(treesFilename);

						timeSlicerToKML.setBurnIn(Integer.valueOf(burnInParser
								.getText()));

						timeSlicerToKML
								.setLocationAttName(locationAttNameParser
										.getText());

						timeSlicerToKML.setRateAttName(rateAttNameParser
								.getText());

						timeSlicerToKML
								.setPrecisionAttName(precisionAttNameParser
										.getText());

						timeSlicerToKML.setTrueNoise(trueNoiseParser
								.isSelected());

						timeSlicerToKML.setImpute(imputeParser.isSelected());

						timeSlicerToKML.setMrsdString(mrsdString);

						timeSlicerToKML.setNumberOfIntervals(Integer
								.valueOf(numberOfIntervalsParser.getText()));

						timeSlicerToKML.setKmlWriterPath(workingDirectory
								.concat("/").concat(kmlPathParser.getText()));

						timeSlicerToKML.setMaxAltitudeMapping(Double
								.valueOf(maxAltMappingParser.getText()));

						timeSlicerToKML
								.setMaxPolygonRedMapping(redPolygonSlider
										.getValue());

						timeSlicerToKML
								.setMaxPolygonGreenMapping(greenPolygonSlider
										.getValue());

						timeSlicerToKML
								.setMaxPolygonBlueMapping(bluePolygonSlider
										.getValue());

						timeSlicerToKML
								.setMaxPolygonOpacityMapping(opacityPolygonSlider
										.getValue());

						timeSlicerToKML.setMaxBranchRedMapping(redBranchSlider
								.getValue());

						timeSlicerToKML
								.setMaxBranchGreenMapping(greenBranchSlider
										.getValue());

						timeSlicerToKML
								.setMaxBranchBlueMapping(blueBranchSlider
										.getValue());

						timeSlicerToKML
								.setMaxBranchOpacityMapping(opacityBranchSlider
										.getValue());

						timeSlicerToKML.GenerateKML();

					} catch (Exception e) {
						e.printStackTrace();

						JOptionPane.showMessageDialog(Utils.getActiveFrame(), e
								.toString(), "Error",
								JOptionPane.ERROR_MESSAGE, errorIcon);

					} catch (OutOfMemoryError e) {
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
		public void actionPerformed(ActionEvent e) {

			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

				// Executed in background thread
				public Void doInBackground() {

					try {

						generateProcessing.setEnabled(false);
						progressBar.setIndeterminate(true);

						String mrsdString = mrsdStringParser.getText()
								+ " "
								+ (eraParser.getSelectedIndex() == 0 ? "AD"
										: "BC");

						timeSlicerToProcessing.setMccTreePath(mccTreeFilename);

						timeSlicerToProcessing.setTreesPath(treesFilename);

						timeSlicerToProcessing.setBurnIn(Integer
								.valueOf(burnInParser.getText()));

						timeSlicerToProcessing
								.setLocationAttName(locationAttNameParser
										.getText());

						timeSlicerToProcessing.setRateAttName(rateAttNameParser
								.getText());

						timeSlicerToProcessing
								.setPrecisionAttName(precisionAttNameParser
										.getText());

						timeSlicerToProcessing.setTrueNoise(trueNoiseParser
								.isSelected());

						timeSlicerToProcessing.setImpute(imputeParser
								.isSelected());

						timeSlicerToProcessing.setMrsdString(mrsdString);

						timeSlicerToProcessing.setNumberOfIntervals(Integer
								.valueOf(numberOfIntervalsParser.getText()));

						timeSlicerToProcessing
								.setMaxPolygonRedMapping(redPolygonSlider
										.getValue());

						timeSlicerToProcessing
								.setMaxPolygonGreenMapping(greenPolygonSlider
										.getValue());

						timeSlicerToProcessing
								.setMaxPolygonBlueMapping(bluePolygonSlider
										.getValue());

						timeSlicerToProcessing
								.setMaxPolygonOpacityMapping(opacityPolygonSlider
										.getValue());

						timeSlicerToProcessing
								.setMaxBranchRedMapping(redBranchSlider
										.getValue());

						timeSlicerToProcessing
								.setMaxBranchGreenMapping(greenBranchSlider
										.getValue());

						timeSlicerToProcessing
								.setMaxBranchBlueMapping(blueBranchSlider
										.getValue());

						timeSlicerToProcessing
								.setMaxBranchOpacityMapping(opacityBranchSlider
										.getValue());

						timeSlicerToProcessing.AnalyzeTrees();
						timeSlicerToProcessing.init();

					} catch (OutOfMemoryError e) {
						e.printStackTrace();

						JOptionPane.showMessageDialog(Utils.getActiveFrame(), e
								.toString(), "Error",
								JOptionPane.ERROR_MESSAGE, errorIcon);

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
					System.out.println("Done! \n");
				}// END: done
			};

			worker.execute();
		}
	}// END: ListenGenerateProcessing

	private class ListenSaveProcessingPlot implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			try {

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Saving as png file...");

				chooser.showSaveDialog(chooser);
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

