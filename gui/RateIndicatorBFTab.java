package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import templates.RateIndicatorBFToKML;
import templates.RateIndicatorBFToProcessing;

@SuppressWarnings("serial")
public class RateIndicatorBFTab extends JPanel {

	// Icons
	private ImageIcon nuclearIcon = CreateImageIcon("/icons/nuclear.png");
	private ImageIcon logIcon = CreateImageIcon("/icons/log.png");
	private ImageIcon locationsIcon = CreateImageIcon("/icons/locations.png");
	private ImageIcon processingIcon = CreateImageIcon("/icons/processing.png");
	private ImageIcon saveIcon = CreateImageIcon("/icons/save.png");
	
	// Strings for paths
	private String logFilename = null;
	private String locationsFilename = null;

	// Text fields
	private JTextField burnInParser = new JTextField("0.1", 5);
	private JTextField numberOfIntervalsParser = new JTextField("100", 5);
	private JTextField maxAltMappingParser = new JTextField("500000", 10);
	private JTextField bfCutoffParser = new JTextField("3.0", 5);
	private JTextField kmlPathParser = new JTextField(
			"/home/filip/Pulpit/output.kml", 17);

	// Buttons for tab
	private JButton openLog = new JButton("Open", logIcon);
	private JButton openLocations = new JButton("Open", locationsIcon);
	private JButton generateKml = new JButton("Generate", nuclearIcon);
	private JButton generateProcessing = new JButton("Plot", processingIcon);
	private JButton saveProcessingPlot = new JButton("Save", saveIcon);

	// Status Bar for tab
	private JTextArea textArea;

	// left tools pane
	private JPanel leftPanel;

	// Processing pane
	private JPanel rightPanel;
	private RateIndicatorBFToProcessing rateIndicatorBFToProcessing;

	public RateIndicatorBFTab() {

		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		/**
		 * left tools pane
		 * */
		Dimension leftPanelDimension = new Dimension(230, 600);// 300,600
		leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));// PAGE_AXIS
		leftPanel.setSize(leftPanelDimension);
		leftPanel.setMinimumSize(leftPanelDimension);
		leftPanel.setMaximumSize(leftPanelDimension);
		leftPanel.setPreferredSize(leftPanelDimension);

		openLog.addActionListener(new ListenOpenLog());
		generateKml.addActionListener(new ListenGenerateKml());
		openLocations.addActionListener(new ListenOpenLocations());
		generateProcessing.addActionListener(new ListenGenerateProcessing());
		saveProcessingPlot.addActionListener(new ListenSaveProcessingPlot());

		JPanel panel0 = new JPanel();
		panel0.setBorder(new TitledBorder("Load log file:"));
		panel0.add(openLog);
		leftPanel.add(panel0);

		JPanel panel1 = new JPanel();
		panel1.setBorder(new TitledBorder("Load locations file:"));
		panel1.add(openLocations);
		leftPanel.add(panel1);

		JPanel panel2 = new JPanel();
		panel2.setBorder(new TitledBorder("Specify burn-in:"));
		panel2.add(burnInParser);
		leftPanel.add(panel2);

		JPanel panel3 = new JPanel();
		panel3.setBorder(new TitledBorder("Bayes Factor cut-off:"));
		panel3.add(bfCutoffParser);
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
		panel7.add(generateKml);
		panel7.add(generateProcessing);
		leftPanel.add(panel7);

		JPanel panel8 = new JPanel();
		panel8.setBorder(new TitledBorder("Save plot:"));
		panel8.add(saveProcessingPlot);
		leftPanel.add(panel8);

		JPanel panel9 = new JPanel();
		textArea = new JTextArea(4, 20);
		textArea.setEditable(true);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(200, 60));
		panel9.add(scrollPane, BorderLayout.CENTER);
		leftPanel.add(panel9);

		JPanel leftPanelContainer = new JPanel();
		leftPanelContainer.setLayout(new BorderLayout());
		leftPanelContainer.add(leftPanel, BorderLayout.NORTH);
		add(leftPanelContainer);

		/**
		 * Processing pane
		 * */
		rateIndicatorBFToProcessing = new RateIndicatorBFToProcessing();
		Dimension rightPanelDimension = new Dimension(2048, 1025);
		rightPanel = new JPanel();
		rightPanel.setSize(rightPanelDimension);
		rightPanel.setMinimumSize(rightPanelDimension);
		rightPanel.setMaximumSize(rightPanelDimension);
		rightPanel.setPreferredSize(rightPanelDimension);
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
		rightPanel.setBorder(new TitledBorder(""));
		rightPanel.setBackground(new Color(255, 255, 255));
		rightPanel.add(rateIndicatorBFToProcessing);
		// JScrollPane ProcessingScrollPane = new
		// JScrollPane(continuousTreeToProcessing);
		// rightPanel.add(ProcessingScrollPane, BorderLayout.CENTER);
		add(rightPanel);

	}

	private class ListenOpenLog implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				JFileChooser chooser = new JFileChooser();

				chooser.showOpenDialog(chooser);
				File file = chooser.getSelectedFile();
				logFilename = file.getAbsolutePath();

				textArea.setText("Opened " + logFilename);

			} catch (Exception e1) {
				textArea.setText("Could not Open!");
			}
		}
	}

	private class ListenOpenLocations implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			try {

				JFileChooser chooser = new JFileChooser();
				chooser.showOpenDialog(chooser);
				File file = chooser.getSelectedFile();
				locationsFilename = file.getAbsolutePath();
				textArea.setText("Opened " + locationsFilename);
			}

			catch (Exception e1) {
				textArea.setText("Could not Open!");
			}

		}
	}

	private class ListenGenerateKml implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {

				RateIndicatorBFToKML rateIndicatorBFToKML = new RateIndicatorBFToKML();

				rateIndicatorBFToKML.setLogFilePath(logFilename, Double
						.parseDouble(burnInParser.getText()));

				rateIndicatorBFToKML.setBfCutoff(Double.valueOf(bfCutoffParser
						.getText()));

				rateIndicatorBFToKML.setLocationFilePath(locationsFilename);

				rateIndicatorBFToKML.setMaxAltitudeMapping(Double
						.valueOf(maxAltMappingParser.getText()));

				rateIndicatorBFToKML.setNumberOfIntervals(Integer
						.valueOf(numberOfIntervalsParser.getText()));

				rateIndicatorBFToKML.setKmlWriterPath(kmlPathParser.getText());

				rateIndicatorBFToKML.GenerateKML();

				textArea.setText("Finished in: " + RateIndicatorBFToKML.time
						+ " msec");

			} catch (IOException e0) {

				textArea.setText("Could not generate!");

			}

		}// END: actionPerformed
	}// END: ListenGenerate class

	private class ListenGenerateProcessing implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			rateIndicatorBFToProcessing.setLogFilePath(logFilename, Double
					.parseDouble(burnInParser.getText()));

			rateIndicatorBFToProcessing.setBfCutoff(Double
					.valueOf(bfCutoffParser.getText()));

			rateIndicatorBFToProcessing.setLocationFilePath(locationsFilename);

			rateIndicatorBFToProcessing.init();

		}// END: actionPerformed
	}// END: ListenGenerateProcessing

	private class ListenSaveProcessingPlot implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			try {

				JFileChooser chooser = new JFileChooser();
				// System.getProperty("user.dir")
				// chooser.setDialogTitle("");

				chooser.showSaveDialog(chooser);
				File file = chooser.getSelectedFile();
				String plotToSaveFilename = file.getAbsolutePath();

				rateIndicatorBFToProcessing.save(plotToSaveFilename);

				textArea.setText("Saved " + plotToSaveFilename);

			} catch (Exception e0) {
				textArea.setText("Could not save!");
			}

		}// END: actionPerformed
	}// END: class

	private ImageIcon CreateImageIcon(String path) {
		java.net.URL imgURL = this.getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			textArea.setText("Couldn't find file: " + path);
			return null;
		}
	}

	public void setText(String text) {
		textArea.setText(text);
	}

}
