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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import templates.ContinuousTreeToKML;
import templates.ContinuousTreeToProcessing;

@SuppressWarnings("serial")
public class ContinuousModelTab extends JPanel {

	private String defaultKmlPath = "/home/filip/Pulpit/output.kml";
	
	// Current date
	private Calendar calendar;
	private SimpleDateFormat formatter;

	// Icons
	private ImageIcon nuclearIcon;
	private ImageIcon treeIcon;
	private ImageIcon processingIcon;
	private ImageIcon saveIcon;

	// Strings for paths
	private String treeFilename;

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

	// Left tools pane
	private JPanel leftPanel;
	private JPanel tmpPanel;

	// Processing pane
	private JPanel rightPanel;
	private ContinuousTreeToProcessing continuousTreeToProcessing;

	// Progress bar
	private JProgressBar progressBar;

	public ContinuousModelTab() {

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
		coordinatesNameParser = new JTextField("location", 5);
		HPDParser = new JTextField("95", 2);
		mrsdStringParser = new JTextField(formatter.format(calendar.getTime()),
				8);
		numberOfIntervalsParser = new JTextField("100", 5);
		maxAltMappingParser = new JTextField("5000000", 5);
		kmlPathParser = new JTextField(defaultKmlPath, 15);

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
		leftPanel.setPreferredSize(new Dimension(230, 610));

		openTree.addActionListener(new ListenOpenTree());
		generateKml.addActionListener(new ListenGenerateKml());
		generateProcessing.addActionListener(new ListenGenerateProcessing());
		saveProcessingPlot.addActionListener(new ListenSaveProcessingPlot());

		tmpPanel = new JPanel();
		tmpPanel.setBorder(new TitledBorder("Load tree file:"));
		tmpPanel.add(openTree);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBorder(new TitledBorder("Most recent sampling date:"));
		String era[] = { "AD", "BC" };
		eraParser = new JComboBox(era);
		tmpPanel.add(mrsdStringParser);
		tmpPanel.add(eraParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setBorder(new TitledBorder("Coordinate attribute name:"));
		tmpPanel.add(coordinatesNameParser);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel();
		tmpPanel.setOpaque(false);
		JLabel tmplabel = new JLabel("%");
		tmpPanel.setBorder(new TitledBorder("HPD:"));
		tmpPanel.add(HPDParser);
		tmpPanel.add(tmplabel);
		tmplabel.setLabelFor(tmpPanel);
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
		tmpPanel.setPreferredSize(new Dimension(230, 90));
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
		continuousTreeToProcessing = new ContinuousTreeToProcessing();
		rightPanel = new JPanel();
		rightPanel.setPreferredSize(new Dimension(2048, 1025));
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
		rightPanel.setBorder(new TitledBorder(""));
		rightPanel.setBackground(new Color(255, 255, 255));
		rightPanel.add(continuousTreeToProcessing);
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

	private class ListenGenerateKml implements ActionListener {
		public void actionPerformed(ActionEvent e) {

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

						continuousTreeToKML.setMrsdString(mrsdString);

						continuousTreeToKML.setNumberOfIntervals(Integer
								.valueOf(numberOfIntervalsParser.getText()));

						continuousTreeToKML.setKmlWriterPath(kmlPathParser
								.getText());

						continuousTreeToKML.setTreePath(treeFilename);

						continuousTreeToKML.GenerateKML();

						System.out.println("Finished in: "
								+ continuousTreeToKML.time + " msec \n");

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

						continuousTreeToProcessing.setTreePath(treeFilename);
						continuousTreeToProcessing
								.setCoordinatesName(coordinatesNameParser
										.getText());
						continuousTreeToProcessing.setHPD(HPDParser.getText()
								+ "%");
						continuousTreeToProcessing.init();
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

				chooser.showSaveDialog(chooser);
				File file = chooser.getSelectedFile();
				String plotToSaveFilename = file.getAbsolutePath();

				continuousTreeToProcessing.save(plotToSaveFilename);
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
			System.err.println("Couldn't find file: \n" + path + "\n");
			return null;
		}
	}

}// END class

