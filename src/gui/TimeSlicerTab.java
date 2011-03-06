package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import templates.TimeSlicerToKML;

@SuppressWarnings("serial")
public class TimeSlicerTab extends JPanel {

	// Current date
	private Calendar calendar = Calendar.getInstance();
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",
			Locale.US);

	// Icons
	private ImageIcon nuclearIcon = CreateImageIcon("/icons/nuclear.png");
	private ImageIcon treeIcon = CreateImageIcon("/icons/tree.png");
	private ImageIcon processingIcon = CreateImageIcon("/icons/processing.png");
	private ImageIcon saveIcon = CreateImageIcon("/icons/save.png");

	// Strings for paths
	private String mccTreeFilename = null;
	private String treesFilename = null;

	// Text fields
	private JTextField burnInParser = new JTextField("1000", 10);
	private JTextField locationAttNameParser = new JTextField("location", 10);
	private JTextField rateAttNameParser = new JTextField("rate", 10);
	private JTextField precisionAttNameParser = new JTextField("precision", 10);
	private JTextField mrsdStringParser = new JTextField(formatter
			.format(calendar.getTime()), 8);
	private JComboBox eraParser;
	private JTextField numberOfIntervalsParser = new JTextField("10", 5);
	private JTextField kmlPathParser = new JTextField(
			"/home/filip/Pulpit/output.kml", 17);

	// Buttons for tab
	private JButton generateKml = new JButton("Generate", nuclearIcon);
	private JButton openTree = new JButton("Open", treeIcon);
	private JButton openTrees = new JButton("Open");
	private JButton generateProcessing = new JButton("Plot", processingIcon);
	private JButton saveProcessingPlot = new JButton("Save", saveIcon);

	// checkbox
	private JCheckBox trueNoiseParser = new JCheckBox();

	// Status Bar for tab
	private JTextArea textArea;

	// left tools pane
	private JPanel leftPanel;

	// Processing pane
	private JPanel rightPanel;

	// private TimeSlicerToProcessing timeSlicerToProcessing;

	public TimeSlicerTab() {

		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		/**
		 * left tools pane
		 * */
		Dimension leftPanelDimension = new Dimension(230, 800);
		leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));// PAGE_AXIS
		leftPanel.setSize(leftPanelDimension);
		leftPanel.setMinimumSize(leftPanelDimension);
		leftPanel.setMaximumSize(leftPanelDimension);
		leftPanel.setPreferredSize(leftPanelDimension);

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
		panel10.add(generateKml);
		panel10.add(generateProcessing);
		leftPanel.add(panel10);

		JPanel panel11 = new JPanel();
		panel11.setBorder(new TitledBorder("Save plot:"));
		panel11.add(saveProcessingPlot);
		leftPanel.add(panel11);

		JPanel panel12 = new JPanel();
		textArea = new JTextArea(4, 20);
		textArea.setEditable(true);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(200, 60));
		panel12.add(scrollPane, BorderLayout.CENTER);
		leftPanel.add(panel12);

		JPanel leftPanelContainer = new JPanel();
		leftPanelContainer.setLayout(new BorderLayout());
		leftPanelContainer.add(leftPanel, BorderLayout.NORTH);
		add(leftPanelContainer);

		/**
		 * Processing pane
		 * */
		// timeSlicerToProcessing = new TimeSlicerToProcessing();
		Dimension rightPanelDimension = new Dimension(2048, 1025);
		rightPanel = new JPanel();
		rightPanel.setSize(rightPanelDimension);
		rightPanel.setMinimumSize(rightPanelDimension);
		rightPanel.setMaximumSize(rightPanelDimension);
		rightPanel.setPreferredSize(rightPanelDimension);
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
		rightPanel.setBorder(new TitledBorder(""));
		rightPanel.setBackground(new Color(255, 255, 255));
		// rightPanel.add(timeSlicerToProcessing);
		add(rightPanel);

	}

	private class ListenOpenTree implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			try {

				JFileChooser chooser = new JFileChooser();

				chooser.showOpenDialog(chooser);
				File file = chooser.getSelectedFile();
				mccTreeFilename = file.getAbsolutePath();

				textArea.setText("Opened " + mccTreeFilename);

			} catch (Exception e1) {
				textArea.setText("Could not Open!");
			}
		}
	}

	private class ListenOpenTrees implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			try {

				JFileChooser chooser = new JFileChooser();

				chooser.showOpenDialog(chooser);
				File file = chooser.getSelectedFile();
				treesFilename = file.getAbsolutePath();

				textArea.setText("Opened " + treesFilename);

			} catch (Exception e1) {
				textArea.setText("Could not Open!");
			}
		}
	}

	private class ListenGenerateKml implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			try {

				TimeSlicerToKML timeSlicerToKML = new TimeSlicerToKML();
				String mrsdString = mrsdStringParser.getText() + " "
						+ (eraParser.getSelectedIndex() == 0 ? "AD" : "BC");

				timeSlicerToKML.setMccTreePath(mccTreeFilename);

				timeSlicerToKML.setTreesPath(treesFilename);

				timeSlicerToKML.setBurnIn(Integer.valueOf(burnInParser
						.getText()));

				timeSlicerToKML.setLocationAttName(locationAttNameParser
						.getText());

				timeSlicerToKML.setRateAttName(rateAttNameParser.getText());

				timeSlicerToKML.setPrecisionAttName(precisionAttNameParser
						.getText());

				timeSlicerToKML.setTrueNoise(trueNoiseParser.isSelected());

				timeSlicerToKML.setMrsdString(mrsdString);

				timeSlicerToKML.setNumberOfIntervals(Integer
						.valueOf(numberOfIntervalsParser.getText()));

				timeSlicerToKML.setKmlWriterPath(kmlPathParser.getText());

				timeSlicerToKML.GenerateKML();

				textArea.setText("Finished in: " + timeSlicerToKML.time
						+ " msec");
			}

			/**
			 * TODO: catch exception for:
			 * 
			 * missing/wrong attribute names
			 * 
			 **/

			/**
			 * TODO: catch exception for (unparseable date):
			 * 
			 * missing/wrong mrsd date
			 * */
			
			catch (OutOfMemoryError e00) {
				textArea.setText("Out of memory error!");
			}

			catch (NullPointerException e0) {
				textArea.setText("Could not generate! Check if: \n"
						+ "* tree file is loaded \n");
			}

			catch (RuntimeException e1) {
				textArea.setText("Could not generate! Check if: \n"
						+ "* proper nr of intervals is specified \n");
			}

			catch (FileNotFoundException e2) {
				textArea.setText("File not found exception! Check if: \n"
						+ "* proper kml file path is specified \n");
			}

			catch (Exception e3) {
				textArea.setText("FUBAR");
			}

		}// END: actionPerformed
	}// END: ListenGenerate class

	private class ListenGenerateProcessing implements ActionListener {
		public void actionPerformed(ActionEvent e) {

		}// END: actionPerformed
	}// END: class

	private class ListenSaveProcessingPlot implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			// try {
			//
			// JFileChooser chooser = new JFileChooser();
			// // System.getProperty("user.dir")
			// // chooser.setDialogTitle("");
			//
			// chooser.showSaveDialog(chooser);
			// File file = chooser.getSelectedFile();
			// String plotToSaveFilename = file.getAbsolutePath();
			//
			// // continuousTreeToProcessing.save(plotToSaveFilename);
			//
			// textArea.setText("Saved " + plotToSaveFilename);
			//
			// } catch (Exception e0) {
			// textArea.setText("Could not save!");
			// }

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

}// END class
