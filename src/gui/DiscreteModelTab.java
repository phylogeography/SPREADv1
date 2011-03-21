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
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import templates.DiscreteTreeToKML;
import templates.DiscreteTreeToProcessing;

@SuppressWarnings("serial")
public class DiscreteModelTab extends JPanel {

	// Current date
	private Calendar calendar;
	private SimpleDateFormat formatter;

	// Icons
	private ImageIcon nuclearIcon;
	private ImageIcon treeIcon;
	private ImageIcon locationsIcon;
	private ImageIcon processingIcon;
	private ImageIcon saveIcon;

	// Strings for paths
	private String treeFilename;
	private String locationsFilename;

	// Text fields
	private JTextField stateAttNameParser;
	private JTextField mrsdStringParser;
	private JComboBox eraParser;
	private JTextField numberOfIntervalsParser;
	private JTextField maxAltMappingParser;
	private JTextField kmlPathParser;

	// Buttons for tab
	private JButton generateKml;
	private JButton openTree;
	private JButton openLocations;
	private JButton generateProcessing;
	private JButton saveProcessingPlot;

	// left tools pane
	private JPanel leftPanel;
	private JPanel tmpPanel;

	// Processing pane
	private JPanel rightPanel;
	private DiscreteTreeToProcessing discreteTreeToProcessing;

	// Progress bar
	private JProgressBar progressBar;

	public DiscreteModelTab() {

		// Setup miscallenous
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		calendar = Calendar.getInstance();
		formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

		// Setup icons
		nuclearIcon = CreateImageIcon("/icons/nuclear.png");
		treeIcon = CreateImageIcon("/icons/tree.png");
		locationsIcon = CreateImageIcon("/icons/locations.png");
		processingIcon = CreateImageIcon("/icons/processing.png");
		saveIcon = CreateImageIcon("/icons/save.png");

		// Setup text fields
		stateAttNameParser = new JTextField("states", 5);
		mrsdStringParser = new JTextField(formatter.format(calendar.getTime()),
				8);
		numberOfIntervalsParser = new JTextField("100", 5);
		maxAltMappingParser = new JTextField("5000000", 5);
		kmlPathParser = new JTextField("/home/filip/Pulpit/output.kml", 15);

		// Setup buttons for tab
		generateKml = new JButton("Generate", nuclearIcon);
		openTree = new JButton("Open", treeIcon);
		openLocations = new JButton("Open", locationsIcon);
		generateProcessing = new JButton("Plot", processingIcon);
		saveProcessingPlot = new JButton("Save", saveIcon);

		// Setup progress bar
		progressBar = new JProgressBar();

		/**
		 * left tools pane
		 * */
		leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));// PAGE_AXIS
		leftPanel.setPreferredSize(new Dimension(230, 610));

		openTree.addActionListener(new ListenOpenTree());
		generateKml.addActionListener(new ListenGenerateKml());
		openLocations.addActionListener(new ListenOpenLocations());
		generateProcessing.addActionListener(new ListenGenerateProcessing());
		saveProcessingPlot.addActionListener(new ListenSaveProcessingPlot());

		tmpPanel = new JPanel();
		tmpPanel.setBorder(new TitledBorder("Load tree file:"));
		tmpPanel.add(openTree);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBorder(new TitledBorder("Load locations file:"));
		tmpPanel.add(openLocations);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBorder(new TitledBorder("Most recent sampling date:"));
		String era[] = { "AD", "BC" };
		eraParser = new JComboBox(era);
		tmpPanel.add(mrsdStringParser);
		tmpPanel.add(eraParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBorder(new TitledBorder("State attribute name:"));
		tmpPanel.add(stateAttNameParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBorder(new TitledBorder("Number of intervals:"));
		tmpPanel.add(numberOfIntervalsParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBorder(new TitledBorder("Maximal altitude:"));
		tmpPanel.add(maxAltMappingParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBorder(new TitledBorder("KML name:"));
		tmpPanel.add(kmlPathParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBorder(new TitledBorder("Generate KML / Plot tree:"));
		tmpPanel.setPreferredSize(new Dimension(230, 100));
		tmpPanel.add(generateKml);
		tmpPanel.add(generateProcessing);
		tmpPanel.add(progressBar);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBorder(new TitledBorder("Save plot:"));
		tmpPanel.add(saveProcessingPlot);
		leftPanel.add(tmpPanel);

		JPanel leftPanelContainer = new JPanel();
		leftPanelContainer.setLayout(new BorderLayout());
		leftPanelContainer.add(leftPanel, BorderLayout.NORTH);
		add(leftPanelContainer);

		/**
		 * Processing pane
		 * */
		discreteTreeToProcessing = new DiscreteTreeToProcessing();
		rightPanel = new JPanel();
		rightPanel.setPreferredSize(new Dimension(2048, 1025));
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
		rightPanel.setBorder(new TitledBorder(""));
		rightPanel.setBackground(new Color(255, 255, 255));
		rightPanel.add(discreteTreeToProcessing);
		add(rightPanel);

	}

	private class ListenOpenTree implements ActionListener {
		public void actionPerformed(ActionEvent e) {

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

			} catch (Exception e1) {
				System.err.println("Could not Open! \n");
			}
		}
	}

	private class ListenOpenLocations implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			try {

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Loading locations file...");

				chooser.showOpenDialog(chooser);
				File file = chooser.getSelectedFile();
				locationsFilename = file.getAbsolutePath();
				System.out.println("Opened " + locationsFilename + "\n");

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
						discreteTreeToKML.setKmlWriterPath(kmlPathParser
								.getText());
						discreteTreeToKML.setTreePath(treeFilename);
						discreteTreeToKML.GenerateKML();
						System.out.println("Finished in: "
								+ discreteTreeToKML.time + " msec");

					} catch (Exception e) {
						e.printStackTrace();
						System.err.println("FUBAR \n");
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

						discreteTreeToProcessing
								.setStateAttName(stateAttNameParser.getText());
						discreteTreeToProcessing
								.setLocationFilePath(locationsFilename);
						discreteTreeToProcessing.setTreePath(treeFilename);
						discreteTreeToProcessing.init();

					} catch (Exception e) {
						e.printStackTrace();
						System.err.println("FUBAR \n");
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
		public void actionPerformed(ActionEvent e) {

			try {

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Saving as png file...");
				// System.getProperty("user.dir")

				chooser.showSaveDialog(chooser);
				File file = chooser.getSelectedFile();
				String plotToSaveFilename = file.getAbsolutePath();

				discreteTreeToProcessing.save(plotToSaveFilename);
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

}
