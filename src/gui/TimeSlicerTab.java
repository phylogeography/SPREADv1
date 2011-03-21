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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import templates.TimeSlicerToKML;
import templates.TimeSlicerToProcessing;

@SuppressWarnings("serial")
public class TimeSlicerTab extends JPanel {

	// Current date
	private Calendar calendar;
	private SimpleDateFormat formatter;

	// Icons
	private ImageIcon nuclearIcon;
	private ImageIcon treeIcon;
	private ImageIcon processingIcon;
	private ImageIcon saveIcon;

	// Strings for paths
	private String mccTreeFilename;
	private String treesFilename;

	// Text fields
	private JTextField burnInParser;
	private JTextField locationAttNameParser;
	private JTextField rateAttNameParser;
	private JTextField precisionAttNameParser;
	private JTextField mrsdStringParser;
	private JComboBox eraParser;
	private JTextField numberOfIntervalsParser;
	private JTextField kmlPathParser;

	// Buttons for tab
	private JButton generateKml;
	private JButton openTree;
	private JButton openTrees;
	private JButton generateProcessing;
	private JButton saveProcessingPlot;

	// checkbox
	private JCheckBox trueNoiseParser;

	// left tools pane
	private JPanel leftPanel;

	// Processing pane
	private JPanel rightPanel;
	private TimeSlicerToProcessing timeSlicerToProcessing;

	// Progress bar
	private JProgressBar progressBar;

	public TimeSlicerTab() {

		// Setup miscallenous
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		calendar = Calendar.getInstance();
		formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

		// Setup icons
		nuclearIcon = CreateImageIcon("/icons/nuclear.png");
		treeIcon = CreateImageIcon("/icons/tree.png");
		processingIcon = CreateImageIcon("/icons/processing.png");
		saveIcon = CreateImageIcon("/icons/save.png");

		// Setup text fields
		burnInParser = new JTextField("4990", 10);
		locationAttNameParser = new JTextField("location", 10);
		rateAttNameParser = new JTextField("rate", 10);
		precisionAttNameParser = new JTextField("precision", 10);
		mrsdStringParser = new JTextField(formatter.format(calendar.getTime()),
				8);
		numberOfIntervalsParser = new JTextField("10", 5);
		kmlPathParser = new JTextField("/home/filip/Pulpit/output.kml", 15);

		// Setup buttons for tab
		generateKml = new JButton("Generate", nuclearIcon);
		openTree = new JButton("Open", treeIcon);
		openTrees = new JButton("Open");
		generateProcessing = new JButton("Plot", processingIcon);
		saveProcessingPlot = new JButton("Save", saveIcon);

		// Setup progress bar & checkbox
		progressBar = new JProgressBar();
		trueNoiseParser = new JCheckBox();

		/**
		 * left tools pane
		 * */
		leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));// PAGE_AXIS
		leftPanel.setPreferredSize(new Dimension(230, 810));

		openTree.addActionListener(new ListenOpenTree());
		openTrees.addActionListener(new ListenOpenTrees());
		generateKml.addActionListener(new ListenGenerateKml());
		generateProcessing.addActionListener(new ListenGenerateProcessing());
		saveProcessingPlot.addActionListener(new ListenSaveProcessingPlot());

		JPanel panel0 = new JPanel();
		panel0.setBorder(new TitledBorder("Load tree file:"));
		panel0.add(openTree);
		leftPanel.add(panel0);

		JPanel panel1 = new JPanel();
		panel1.setBorder(new TitledBorder("Load trees file:"));
		panel1.add(openTrees);
		leftPanel.add(panel1);

		JPanel panel2 = new JPanel();
		panel2.setBorder(new TitledBorder("Specify burn-in:"));
		panel2.add(burnInParser);
		leftPanel.add(panel2);

		JPanel panel3 = new JPanel();
		panel3.setBorder(new TitledBorder("Location attribute name:"));
		panel3.add(locationAttNameParser);
		leftPanel.add(panel3);

		JPanel panel4 = new JPanel();
		panel4.setBorder(new TitledBorder("Rate attribute name:"));
		panel4.add(rateAttNameParser);
		leftPanel.add(panel4);

		JPanel panel5 = new JPanel();
		panel5.setBorder(new TitledBorder("Precision attribute name:"));
		panel5.add(precisionAttNameParser);
		leftPanel.add(panel5);

		JPanel panel6 = new JPanel();
		panel6.setBorder(new TitledBorder("Use true noise:"));
		panel6.add(trueNoiseParser);
		leftPanel.add(panel6);

		JPanel panel7 = new JPanel();
		panel7.setBorder(new TitledBorder("Most recent sampling date:"));
		String era[] = { "AD", "BC" };
		eraParser = new JComboBox(era);
		panel7.add(mrsdStringParser);
		panel7.add(eraParser);
		leftPanel.add(panel7);

		JPanel panel8 = new JPanel();
		panel8.setBorder(new TitledBorder("Number of intervals:"));
		panel8.add(numberOfIntervalsParser);
		leftPanel.add(panel8);

		JPanel panel9 = new JPanel();
		panel9.setBorder(new TitledBorder("KML name:"));
		panel9.add(kmlPathParser);
		leftPanel.add(panel9);

		JPanel panel10 = new JPanel();
		panel10.setBorder(new TitledBorder("Generate KML / Plot tree:"));
		panel10.setPreferredSize(new Dimension(230, 90));
		panel10.add(generateKml);
		panel10.add(generateProcessing);
		panel10.add(progressBar);
		leftPanel.add(panel10);

		JPanel panel11 = new JPanel();
		panel11.setBorder(new TitledBorder("Save plot:"));
		panel11.add(saveProcessingPlot);
		leftPanel.add(panel11);

		JPanel leftPanelContainer = new JPanel();
		leftPanelContainer.setLayout(new BorderLayout());
		leftPanelContainer.add(leftPanel, BorderLayout.NORTH);
		add(leftPanelContainer);

		/**
		 * Processing pane
		 * */
		timeSlicerToProcessing = new TimeSlicerToProcessing();
		rightPanel = new JPanel();
		rightPanel.setPreferredSize(new Dimension(2048, 1025));
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
		rightPanel.setBorder(new TitledBorder(""));
		rightPanel.setBackground(new Color(255, 255, 255));
		rightPanel.add(timeSlicerToProcessing);
		add(rightPanel);

	}

	private class ListenOpenTree implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			try {

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Opening tree file...");

				chooser.showOpenDialog(chooser);
				File file = chooser.getSelectedFile();
				mccTreeFilename = file.getAbsolutePath();
				System.out.println("Opened " + mccTreeFilename + "\n");

			} catch (Exception e1) {
				System.err.println("Could not Open! \n");
			}
		}
	}

	private class ListenOpenTrees implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			try {

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Loading trees file...");

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

						timeSlicerToKML.setMccTreePath(mccTreeFilename);

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

						timeSlicerToKML.setMrsdString(mrsdString);

						timeSlicerToKML.setNumberOfIntervals(Integer
								.valueOf(numberOfIntervalsParser.getText()));

						timeSlicerToKML.setKmlWriterPath(kmlPathParser
								.getText());

						timeSlicerToKML.GenerateKML();

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

						timeSlicerToProcessing.setMrsdString(mrsdString);

						timeSlicerToProcessing.setNumberOfIntervals(Integer
								.valueOf(numberOfIntervalsParser.getText()));

						timeSlicerToProcessing.AnalyzeTrees();
						timeSlicerToProcessing.init();
						System.out.println("Done! \n");

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

