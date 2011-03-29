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

@SuppressWarnings("serial")
public class ContinuousModelTab extends JPanel {

	// Sizing constants
	private final int leftPanelWidth = 230;
	private final int leftPanelHeight = 1500;

	// Current date
	private Calendar calendar;
	private SimpleDateFormat formatter;

	// Icons
	private ImageIcon nuclearIcon;
	private ImageIcon treeIcon;
	private ImageIcon processingIcon;
	private ImageIcon saveIcon;
	private ImageIcon errorIcon;

	// Colors
	private Color backgroundColor;

	// Strings for paths
	private String treeFilename;
	private String workingDirectory;

	// Labels
	private JLabel tmpLabel;

	// Text fields
	private JTextField coordinatesNameParser;
	private JTextField HPDParser;
	private JTextField mrsdStringParser;
	private JComboBox eraParser;
	private JTextField numberOfIntervalsParser;
	private JTextField maxAltMappingParser;
	private JTextField kmlPathParser;

	// Buttons for tab
	private JButton generateKml;
	private JButton openTree;
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

		calendar = Calendar.getInstance();
		formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

		// Setup icons
		nuclearIcon = CreateImageIcon("/icons/nuclear.png");
		treeIcon = CreateImageIcon("/icons/tree.png");
		processingIcon = CreateImageIcon("/icons/processing.png");
		saveIcon = CreateImageIcon("/icons/save.png");
		errorIcon = CreateImageIcon("/icons/error.png");

		// Setup text fields
		coordinatesNameParser = new JTextField("location", 5);
		HPDParser = new JTextField("95", 2);
		mrsdStringParser = new JTextField(formatter.format(calendar.getTime()),
				8);
		numberOfIntervalsParser = new JTextField("100", 5);
		maxAltMappingParser = new JTextField("5000000", 5);
		kmlPathParser = new JTextField("output.kml", 10);

		// Setup buttons for tab
		generateKml = new JButton("Generate", nuclearIcon);
		openTree = new JButton("Open", treeIcon);
		generateProcessing = new JButton("Plot", processingIcon);
		saveProcessingPlot = new JButton("Save", saveIcon);

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
		generateProcessing.addActionListener(new ListenGenerateProcessing());
		saveProcessingPlot.addActionListener(new ListenSaveProcessingPlot());

		tmpPanel = new JPanel();
		tmpPanel.setBackground(backgroundColor);
		tmpPanel.setBorder(new TitledBorder("Load tree file:"));
		tmpPanel.add(openTree);
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
		tmpPanel.setPreferredSize(new Dimension(leftPanelWidth, 420));
		tmpPanel.setBorder(new TitledBorder("Polygons color mapping:"));

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
		tmpPanel.setPreferredSize(new Dimension(leftPanelWidth, 420));
		tmpPanel.setBorder(new TitledBorder("Branches color mapping:"));

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
		continuousTreeToProcessing = new ContinuousTreeToProcessing();
		continuousTreeToProcessing.setPreferredSize(new Dimension(2048, 1025));
		JScrollPane rightScrollPane = new JScrollPane(
				continuousTreeToProcessing,
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

	private class ListenGenerateKml implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

				// Executed in background thread
				public Void doInBackground() {

					try {

						generateKml.setEnabled(false);
						progressBar.setIndeterminate(true);

						ContinuousTreeToKML continuousTreeToKML = new ContinuousTreeToKML();

						String mrsdString = mrsdStringParser.getText()
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
								.setMaxPolygonRedMapping(redPolygonSlider
										.getValue());

						continuousTreeToKML
								.setMaxPolygonGreenMapping(greenPolygonSlider
										.getValue());

						continuousTreeToKML
								.setMaxPolygonBlueMapping(bluePolygonSlider
										.getValue());

						continuousTreeToKML
								.setMaxPolygonOpacityMapping(opacityPolygonSlider
										.getValue());

						continuousTreeToKML
								.setMaxBranchRedMapping(redBranchSlider
										.getValue());

						continuousTreeToKML
								.setMaxBranchGreenMapping(greenBranchSlider
										.getValue());

						continuousTreeToKML
								.setMaxBranchBlueMapping(blueBranchSlider
										.getValue());

						continuousTreeToKML
								.setMaxBranchOpacityMapping(opacityBranchSlider
										.getValue());

						continuousTreeToKML.setMrsdString(mrsdString);

						continuousTreeToKML.setNumberOfIntervals(Integer
								.valueOf(numberOfIntervalsParser.getText()));

						continuousTreeToKML.setKmlWriterPath(

						workingDirectory.concat("/").concat(
								kmlPathParser.getText()));

						continuousTreeToKML.setTreePath(treeFilename);

						continuousTreeToKML.GenerateKML();

						System.out.println("Finished in: "
								+ continuousTreeToKML.time + " msec \n");

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

						continuousTreeToProcessing.setTreePath(treeFilename);

						continuousTreeToProcessing
								.setCoordinatesName(coordinatesNameParser
										.getText());

						continuousTreeToProcessing.setHPD(HPDParser.getText()
								+ "%");

						continuousTreeToProcessing
								.setMaxPolygonRedMapping(redPolygonSlider
										.getValue());

						continuousTreeToProcessing
								.setMaxPolygonGreenMapping(greenPolygonSlider
										.getValue());

						continuousTreeToProcessing
								.setMaxPolygonBlueMapping(bluePolygonSlider
										.getValue());

						continuousTreeToProcessing
								.setMaxPolygonOpacityMapping(opacityPolygonSlider
										.getValue());

						continuousTreeToProcessing
								.setMaxBranchRedMapping(redBranchSlider
										.getValue());

						continuousTreeToProcessing
								.setMaxBranchGreenMapping(greenBranchSlider
										.getValue());

						continuousTreeToProcessing
								.setMaxBranchBlueMapping(blueBranchSlider
										.getValue());

						continuousTreeToProcessing
								.setMaxBranchOpacityMapping(opacityBranchSlider
										.getValue());

						continuousTreeToProcessing.init();

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

				chooser.showSaveDialog(chooser);
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

