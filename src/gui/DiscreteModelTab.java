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
		maxAltMappingParser = new JTextField("5000000", 10);
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

		JPanel panel0 = new JPanel();
		panel0.setBorder(new TitledBorder("Load tree file:"));
		panel0.add(openTree);
		leftPanel.add(panel0);

		JPanel panel1 = new JPanel();
		panel1.setBorder(new TitledBorder("Load locations file:"));
		panel1.add(openLocations);
		leftPanel.add(panel1);

		JPanel panel2 = new JPanel();
		panel2.setBorder(new TitledBorder("State attribute name:"));
		panel2.add(stateAttNameParser);
		leftPanel.add(panel2);

		JPanel panel3 = new JPanel();
		panel3.setBorder(new TitledBorder("Most recent sampling date:"));
		String era[] = { "AD", "BC" };
		eraParser = new JComboBox(era);
		panel3.add(mrsdStringParser);
		panel3.add(eraParser);
		leftPanel.add(panel3);

		JPanel panel4 = new JPanel();
		panel4.setBorder(new TitledBorder("Number of intervals:"));
		panel4.add(numberOfIntervalsParser);
		leftPanel.add(panel4);

		JPanel panel5 = new JPanel();
		panel5.setBorder(new TitledBorder("Maximal altitude:"));
		panel5.add(maxAltMappingParser);
		leftPanel.add(panel5);

		JPanel panel6 = new JPanel();
		panel6.setBorder(new TitledBorder("KML name:"));
		panel6.add(kmlPathParser);
		leftPanel.add(panel6);

		JPanel panel7 = new JPanel();
		panel7.setBorder(new TitledBorder("Generate KML / Plot tree:"));
		panel7.setPreferredSize(new Dimension(230, 90));
		panel7.add(generateKml);
		panel7.add(generateProcessing);
		panel7.add(progressBar);
		leftPanel.add(panel7);

		JPanel panel8 = new JPanel();
		panel8.setBorder(new TitledBorder("Save plot:"));
		panel8.add(saveProcessingPlot);
		leftPanel.add(panel8);

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

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Loading tree file...");

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
