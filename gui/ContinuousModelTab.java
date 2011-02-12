package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import templates.ContinuousTreeToKML;
import templates.ContinuousTreeToProcessing;

@SuppressWarnings("serial")
public class ContinuousModelTab extends JPanel {

	// Current date
	private Calendar calendar = Calendar.getInstance();
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",
			Locale.US);

	// Icons
	private ImageIcon nuclearIcon = CreateImageIcon("/icons/nuclear.png");
	private ImageIcon treeIcon = CreateImageIcon("/icons/tree.png");

	// Strings for paths
	private String treeFilename = null;

	// Text fields
	private JTextField coordinatesNameParser = new JTextField("location", 5);
	private static JTextField HPDParser = new JTextField("80", 2);
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
	private JButton generateProcessing = new JButton("Plot");

	// Status Bar for tab
	private JTextArea textArea;

	// Processing
	private ContinuousTreeToProcessing continuousTreeToProcessing;

	public ContinuousModelTab() {

		continuousTreeToProcessing = new ContinuousTreeToProcessing();

		setLayout(new GridLayout(0, 1));

		openTree.addActionListener(new ListenOpenTree());
		generateKml.addActionListener(new ListenGenerateKml());
		generateProcessing.addActionListener(new ListenGenerateProcessing());

		JPanel panel0 = new JPanel();
		panel0.setBorder(new TitledBorder("Load tree file:"));
		panel0.add(openTree);
		add(panel0);

		JPanel panel1 = new JPanel();
		panel1.setBorder(new TitledBorder("Coordinate attribute name:"));
		panel1.add(coordinatesNameParser);
		add(panel1);

		JPanel panel2 = new JPanel();
		panel2.setOpaque(false);
		JLabel label2 = new JLabel("%");
		panel2.setBorder(new TitledBorder("HPD:"));
		panel2.add(HPDParser);
		panel2.add(label2);
		label2.setLabelFor(panel2);
		add(panel2);

		JPanel panel3 = new JPanel();
		panel3.setBorder(new TitledBorder("Most recent sampling date:"));
		String era[] = { "AD", "BC" };
		eraParser = new JComboBox(era);
		panel3.add(mrsdStringParser);
		panel3.add(eraParser);
		add(panel3);

		JPanel panel4 = new JPanel();
		panel4.setBorder(new TitledBorder("Number of intervals:"));
		panel4.add(numberOfIntervalsParser);
		add(panel4);

		JPanel panel5 = new JPanel();
		panel5.setBorder(new TitledBorder("Maximal altitude:"));
		panel5.add(maxAltMappingParser);
		add(panel5);

		JPanel panel6 = new JPanel();
		panel6.setBorder(new TitledBorder("KML name:"));
		panel6.add(kmlPathParser);
		add(panel6);

		JPanel panel7 = new JPanel();
		panel7.setBorder(new TitledBorder("Generate KML / Plot tree:"));
		panel7.add(generateKml);
		panel7.add(generateProcessing);
		add(panel7);

		// TODO: make scroll pane work
		JPanel panel8 = new JPanel();
		textArea = new JTextArea(4, 20);
		textArea.setEditable(true);
		JScrollPane scrollPane = new JScrollPane(textArea);
		panel8.add(scrollPane, BorderLayout.CENTER);
		panel8.add(textArea);
		add(panel8);

		// // TODO: add PApplet
		// JPanel panel9Cm = new JPanel();
		// panel9Cm.add(continuousTreeToProcessing);
		// add(panel9Cm);

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
				// e1.printStackTrace();
				textArea.setText("Could not Open!");
			}
		}
	}

	private class ListenGenerateKml implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			try {

				ContinuousTreeToKML continuousTreeToKML = new ContinuousTreeToKML();
				String mrsdString = mrsdStringParser.getText() + " "
						+ (eraParser.getSelectedIndex() == 0 ? "AD" : "BC");
				continuousTreeToKML.setHPD(HPDParser.getText() + "%");
				continuousTreeToKML.setCoordinatesName(coordinatesNameParser
						.getText());
				continuousTreeToKML.setMaxAltitudeMapping(Double
						.valueOf(maxAltMappingParser.getText()));
				continuousTreeToKML.setMrsdString(mrsdString);
				continuousTreeToKML.setNumberOfIntervals(Integer
						.valueOf(numberOfIntervalsParser.getText()));
				continuousTreeToKML.setKmlWriterPath(kmlPathParser.getText());
				continuousTreeToKML.setTreePath(treeFilename);
				continuousTreeToKML.GenerateKML();
				textArea.setText("Finished in: " + continuousTreeToKML.time
						+ " msec");
			}

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

			catch (NullPointerException e0) {
				textArea.setText("Could not generate! Check if: \n"
						+ "* tree file is loaded \n");
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

				// continuousTreeToProcessing = new
				// ContinuousTreeToProcessing();
				continuousTreeToProcessing
						.setCoordinatesName(coordinatesNameParser.getText());
				continuousTreeToProcessing.setTreePath(treeFilename);

				continuousTreeToProcessing.init();
			}

			catch (FileNotFoundException e1) {
				e1.printStackTrace();
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

}// END class

