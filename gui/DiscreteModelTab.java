package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import jebl.evolution.io.ImportException;

import templates.DiscreteTreeToKML;
import templates.DiscreteTreeToProcessing;

@SuppressWarnings("serial")
public class DiscreteModelTab extends JPanel {

	// Current date
	private Calendar calendar = Calendar.getInstance();
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",
			Locale.US);

	// Icons
	private ImageIcon nuclearIcon = CreateImageIcon("/icons/nuclear.png");
	private ImageIcon treeIcon = CreateImageIcon("/icons/tree.png");
	private ImageIcon locationsIcon = CreateImageIcon("/icons/locations.png");
	private ImageIcon processingIcon = CreateImageIcon("/icons/processing.png");

	// Strings for paths
	private String treeFilename = null;
	private String locationsFilename = null;

	// Text fields
	private JTextField stateAttNameParser = new JTextField("states", 5);
	private JTextField mrsdStringParser = new JTextField(formatter
			.format(calendar.getTime()), 8);
	private JComboBox eraParser;
	private JTextField numberOfIntervalsParser = new JTextField("100", 5);
	private JTextField maxAltMappingParser = new JTextField("5000000", 10);
	private JTextField kmlPathParser = new JTextField(
			"/home/filip/Pulpit/output.kml", 17);

	// Buttons for tab
	private JButton generateKml = new JButton("Generate", nuclearIcon);
	private JButton openTree = new JButton("Open", treeIcon);
	private JButton openLocations = new JButton("Open", locationsIcon);
	private JButton generateProcessing = new JButton("Plot", processingIcon);

	// Status Bar for tab
	private JTextArea textArea;

	// left tools pane
	private JPanel leftPanel;

	// right tools panel
	private JPanel rightPanel;
	private DiscreteTreeToProcessing discreteTreeToProcessing;

	public DiscreteModelTab() {

		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		/**
		 * left tools pane
		 * */
		Dimension leftPanelDimension = new Dimension(300, 600);
		leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
		leftPanel.setSize(leftPanelDimension);
		leftPanel.setMinimumSize(leftPanelDimension);
		leftPanel.setMaximumSize(leftPanelDimension);

		openTree.addActionListener(new ListenOpenTree());
		generateKml.addActionListener(new ListenGenerateKml());
		openLocations.addActionListener(new ListenOpenLocations());
		generateProcessing.addActionListener(new ListenGenerateProcessing());

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
		panel7.add(generateKml);
		panel7.add(generateProcessing);
		leftPanel.add(panel7);

		// TODO: make scroll pane work
		JPanel panel8 = new JPanel();
		textArea = new JTextArea(4, 20);
		textArea.setEditable(true);
		JScrollPane scrollPane = new JScrollPane(textArea);
		panel8.add(scrollPane, BorderLayout.CENTER);
		panel8.add(textArea);
		leftPanel.add(panel8);

		add(leftPanel);

		/**
		 * Processing pane
		 * */
		rightPanel = new JPanel();
		discreteTreeToProcessing = new DiscreteTreeToProcessing();
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

				chooser.showOpenDialog(chooser);
				File file = chooser.getSelectedFile();
				treeFilename = file.getAbsolutePath();

				textArea.setText("Opened " + treeFilename);

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

				DiscreteTreeToKML discreteTreeToKML = new DiscreteTreeToKML();
				String mrsdString = mrsdStringParser.getText() + " "
						+ (eraParser.getSelectedIndex() == 0 ? "AD" : "BC");

				discreteTreeToKML.setLocationFilePath(locationsFilename);

				discreteTreeToKML.setStateAttName(stateAttNameParser.getText());

				discreteTreeToKML.setMaxAltitudeMapping(Double
						.valueOf(maxAltMappingParser.getText()));
				discreteTreeToKML.setMrsdString(mrsdString);
				discreteTreeToKML.setNumberOfIntervals(Integer
						.valueOf(numberOfIntervalsParser.getText()));
				discreteTreeToKML.setKmlWriterPath(kmlPathParser.getText());
				discreteTreeToKML.setTreePath(treeFilename);
				discreteTreeToKML.GenerateKML();
				textArea.setText("Finished in: " + discreteTreeToKML.time
						+ " msec");

				/**
				 * TODO: catch exception for (missing att from node):
				 * 
				 * missing/wrong coord attribute name
				 * 
				 * missing/wrong coord HPD name
				 **/

				/**
				 * TODO: catch exception for (unparseable date):
				 * 
				 * missing/wrong mrsd date
				 * */

			} catch (NullPointerException e0) {
				textArea.setText("Could not generate! Check if: \n"
						+ "* tree file is loaded \n"
						+ "* locations file is loaded \n");
			}

			catch (RuntimeException e1) {
				textArea.setText("Could not generate! Check if: \n"
						+ "* proper nr of intervals is specified \n"
						+ "* proper altitude maximum is specified \n");
			}

			catch (FileNotFoundException e2) {
				textArea.setText("File not found exception! Check if: \n"
						+ "* proper kml file path is specified \n");
			}

		}// END: actionPerformed
	}// END: ListenGenerate class

	private class ListenGenerateProcessing implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			try {

				discreteTreeToProcessing.setStateAttName(stateAttNameParser
						.getText());
				discreteTreeToProcessing.setLocationFilePath(locationsFilename);
				discreteTreeToProcessing.setTreePath(treeFilename);
				discreteTreeToProcessing.init();

			} catch (FileNotFoundException e1) {
				e1.printStackTrace();

			} catch (IOException e2) {
				e2.printStackTrace();

			} catch (ImportException e3) {
				e3.printStackTrace();
			}

		}
	}

	private ImageIcon CreateImageIcon(String path) {
		java.net.URL imgURL = this.getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public void setText(String text) {
		textArea.setText(text);
	}

}
