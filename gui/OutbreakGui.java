package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import templates.ContinuousTreeToKML;
import templates.DiscreteTreeToKML;

public class OutbreakGui {

	// Current date
	private Calendar calendar = Calendar.getInstance();
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",
			Locale.US);

	// Icons
	private ImageIcon nuclearIcon = createImageIcon("/icons/nuclear.png");
	private ImageIcon treeIcon = createImageIcon("/icons/tree.png");
	private ImageIcon quitIcon = createImageIcon("/icons/close.png");
	private ImageIcon helpIcon = createImageIcon("/icons/help.png");
	private ImageIcon locationsIcon = createImageIcon("/icons/locations.png");

	// Frame
	private JFrame Frame = new JFrame("TestlabOutbreak");
	private JTabbedPane tabbedPane = new JTabbedPane();

	/**
	 * Main menu
	 * */
	// Menubar
	private JMenuBar mainMenu = new JMenuBar();

	// Buttons with options
	private JSeparator Separator = new JSeparator(JSeparator.VERTICAL);
	private JButton Help = new JButton("Help", helpIcon);
	private JButton Quit = new JButton("Quit", quitIcon);

	/**
	 * Continuous Model
	 * */
	private String TreeFilenameCm = null;

	// Text fields
	private JTextField coordinatesNameParserCm = new JTextField("location", 5);
	private static JTextField HPDParserCm = new JTextField("80", 2);
	private JTextField mrsdStringParserCm = new JTextField(formatter
			.format(calendar.getTime()), 8);
	private JComboBox eraParserCm;
	private JTextField numberOfIntervalsParserCm = new JTextField("100", 5);
	private JTextField maxAltMappingParserCm = new JTextField("5000000", 10);
	private JTextField kmlPathParserCm = new JTextField(
			"/home/filip/Pulpit/output.kml", 17);

	// Buttons for tab
	private JButton GenerateCm = new JButton("Generate", nuclearIcon);
	private JButton OpenTreeCm = new JButton("Open", treeIcon);

	// Status Bar for tab
	JTextArea textAreaCm;

	/**
	 * Discrete Model
	 * */
	private String TreeFilenameDm = null;
	private String LocationsFilenameDm = null;

	// Text fields
	private JTextField stateAttNameParserDm = new JTextField("states", 5);
	private JTextField mrsdStringParserDm = new JTextField(formatter
			.format(calendar.getTime()), 8);
	private JComboBox eraParserDm;
	private JTextField numberOfIntervalsParserDm = new JTextField("100", 5);
	private JTextField maxAltMappingParserDm = new JTextField("5000000", 10);
	private JTextField kmlPathParserDm = new JTextField(
			"/home/filip/Pulpit/output.kml", 17);
	// private JTextField LocationFilePathParserDm = new JTextField(
	// "/home/filip/locationCoordinates", 17);

	// Buttons for tab
	private JButton GenerateDm = new JButton("Generate", nuclearIcon);
	private JButton OpenTreeDm = new JButton("Open", treeIcon);
	private JButton OpenLocationsDm = new JButton("Open", locationsIcon);

	// Status Bar for tab
	JTextArea textAreaDm;

	public OutbreakGui() {

		// Setup Main Frame
		Frame.getContentPane().setLayout(new BorderLayout());
		Frame.add(tabbedPane);
		Frame.setJMenuBar(mainMenu);
		Frame.addWindowListener(new ListenCloseWdw());

		// Setup Main Menu
		mainMenu.add(Separator);
		mainMenu.add(Help);
		mainMenu.add(Quit);

		// Add Menu buttons listeners
		Quit.addActionListener(new ListenMenuQuit());
		Help.addActionListener(new ListenMenuHelp());

		/**
		 * Continuous Model Tab
		 * */
		JPanel continuousModelTab = new JPanel();
		continuousModelTab.setLayout(new GridLayout(9, 2));
		tabbedPane.addTab("Continuous Model", continuousModelTab);

		OpenTreeCm.addActionListener(new ListenOpenTreeCm());
		GenerateCm.addActionListener(new ListenGenerateCm());

		JPanel panel0Cm = new JPanel();
		panel0Cm.setBorder(new TitledBorder("Load tree file:"));
		panel0Cm.add(OpenTreeCm);
		continuousModelTab.add(panel0Cm);

		JPanel panel1Cm = new JPanel();
		panel1Cm.setBorder(new TitledBorder("Coordinate attribute name:"));
		panel1Cm.add(coordinatesNameParserCm);
		continuousModelTab.add(panel1Cm);

		JPanel panel2Cm = new JPanel();
		panel2Cm.setOpaque(false);
		JLabel label2 = new JLabel("%");
		panel2Cm.setBorder(new TitledBorder("HPD:"));
		panel2Cm.add(HPDParserCm);
		panel2Cm.add(label2);
		label2.setLabelFor(panel2Cm);
		continuousModelTab.add(panel2Cm);

		JPanel panel3Cm = new JPanel();
		panel3Cm.setBorder(new TitledBorder("Most recent sampling date:"));
		String eraCm[] = { "AD", "BC" };
		eraParserCm = new JComboBox(eraCm);
		panel3Cm.add(mrsdStringParserCm);
		panel3Cm.add(eraParserCm);
		continuousModelTab.add(panel3Cm);

		JPanel panel4Cm = new JPanel();
		panel4Cm.setBorder(new TitledBorder("Number of intervals:"));
		panel4Cm.add(numberOfIntervalsParserCm);
		continuousModelTab.add(panel4Cm);

		JPanel panel5Cm = new JPanel();
		panel5Cm.setBorder(new TitledBorder("Maximal altitude:"));
		panel5Cm.add(maxAltMappingParserCm);
		continuousModelTab.add(panel5Cm);

		JPanel panel6Cm = new JPanel();
		panel6Cm.setBorder(new TitledBorder("KML name:"));
		panel6Cm.add(kmlPathParserCm);
		continuousModelTab.add(panel6Cm);

		JPanel panel7Cm = new JPanel();
		panel7Cm.setBorder(new TitledBorder("Generate KML:"));
		panel7Cm.add(GenerateCm);
		continuousModelTab.add(panel7Cm);

		// TODO: make scroll pane work
		JPanel panel8Cm = new JPanel();
		// panel8.setLayout(new BorderLayout());
		textAreaCm = new JTextArea(4, 20);
		textAreaCm.setEditable(true);
		JScrollPane scrollPaneCm = new JScrollPane(textAreaCm);
		panel8Cm.add(scrollPaneCm, BorderLayout.CENTER);
		panel8Cm.add(textAreaCm);
		continuousModelTab.add(panel8Cm);

		/**
		 * Discrete Model Tab
		 * */
		JPanel discreteModelTab = new JPanel();
		discreteModelTab.setOpaque(false);
		discreteModelTab.setLayout(new GridLayout(0, 1));
		tabbedPane.addTab("Discrete Model", discreteModelTab);

		OpenTreeDm.addActionListener(new ListenOpenTreeDm());
		GenerateDm.addActionListener(new ListenGenerateDm());
		OpenLocationsDm.addActionListener(new ListenOpenLocationsDm());

		JPanel panel0Dm = new JPanel();
		panel0Dm.setBorder(new TitledBorder("Load tree file:"));
		panel0Dm.add(OpenTreeDm);
		discreteModelTab.add(panel0Dm);

		// TODO: open button here
		JPanel panel1Dm = new JPanel();
		panel1Dm.setBorder(new TitledBorder("Load locations file:"));
		panel1Dm.add(OpenLocationsDm);
		discreteModelTab.add(panel1Dm);

		JPanel panel2Dm = new JPanel();
		panel2Dm.setBorder(new TitledBorder("State attribute name:"));
		panel2Dm.add(stateAttNameParserDm);
		discreteModelTab.add(panel2Dm);

		JPanel panel3Dm = new JPanel();
		panel3Dm.setBorder(new TitledBorder("Most recent sampling date:"));
		String eraDm[] = { "AD", "BC" };
		eraParserDm = new JComboBox(eraDm);
		panel3Dm.add(mrsdStringParserDm);
		panel3Dm.add(eraParserDm);
		discreteModelTab.add(panel3Dm);

		JPanel panel4Dm = new JPanel();
		panel4Dm.setBorder(new TitledBorder("Number of intervals:"));
		panel4Dm.add(numberOfIntervalsParserDm);
		discreteModelTab.add(panel4Dm);

		JPanel panel5Dm = new JPanel();
		panel5Dm.setBorder(new TitledBorder("Maximal altitude:"));
		panel5Dm.add(maxAltMappingParserDm);
		discreteModelTab.add(panel5Dm);

		JPanel panel6Dm = new JPanel();
		panel6Dm.setBorder(new TitledBorder("KML name:"));
		panel6Dm.add(kmlPathParserDm);
		discreteModelTab.add(panel6Dm);

		JPanel panel7Dm = new JPanel();
		panel7Dm.setBorder(new TitledBorder("Generate KML:"));
		panel7Dm.add(GenerateDm);
		discreteModelTab.add(panel7Dm);

		// TODO: make scroll pane work
		JPanel panel8Dm = new JPanel();
		// panel8.setLayout(new BorderLayout());
		textAreaDm = new JTextArea(4, 20);
		textAreaDm.setEditable(true);
		JScrollPane scrollPaneDm = new JScrollPane(textAreaDm);
		panel8Dm.add(scrollPaneDm, BorderLayout.CENTER);
		panel8Dm.add(textAreaDm);
		discreteModelTab.add(panel8Dm);

	}

	private class ListenMenuHelp implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String helpText = "TODO";
			textAreaCm.setText(helpText);
			textAreaDm.setText(helpText);
		}
	}

	public class ListenMenuQuit implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	public class ListenOpenTreeCm implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				JFileChooser chooser = new JFileChooser();

				chooser.showOpenDialog(chooser);
				File file = chooser.getSelectedFile();
				TreeFilenameCm = file.getAbsolutePath();

				textAreaCm.setText("Opened " + TreeFilenameCm);

			} catch (Exception e1) {
				e1.printStackTrace();
				textAreaCm.setText("Could not Open!");
			}
		}
	}

	public class ListenGenerateCm implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			try {

				ContinuousTreeToKML template = new ContinuousTreeToKML();
				String mrsdString = mrsdStringParserCm.getText() + " "
						+ (eraParserCm.getSelectedIndex() == 0 ? "AD" : "BC");
				template.setHPD(HPDParserCm.getText() + "%");
				template.setCoordinatesName(coordinatesNameParserCm.getText());
				template.setMaxAltitudeMapping(Double
						.valueOf(maxAltMappingParserCm.getText()));
				template.setMrsdString(mrsdString);
				template.setNumberOfIntervals(Integer
						.valueOf(numberOfIntervalsParserCm.getText()));
				template.setKmlWriterPath(kmlPathParserCm.getText());
				template.setTreePath(TreeFilenameCm);
				template.GenerateKML();
				textAreaCm.setText("Finished in: " + template.time + " msec");
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
				textAreaCm.setText("Could not generate! Check if: \n"
						+ "* tree file is loaded \n");
			}

			catch (RuntimeException e1) {
				textAreaCm.setText("Could not generate! Check if: \n"
						+ "* proper nr of intervals is specified \n"
						+ "* proper altitude maximum is specified \n");
			}

			catch (FileNotFoundException e2) {
				textAreaCm.setText("File not found exception! Check if: \n"
						+ "* proper kml file path is specified \n");
			}

		}// END: actionPerformed
	}// END: ListenGenerate class

	public class ListenOpenTreeDm implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				JFileChooser chooser = new JFileChooser();

				chooser.showOpenDialog(chooser);
				File file = chooser.getSelectedFile();
				TreeFilenameDm = file.getAbsolutePath();

				textAreaDm.setText("Opened " + TreeFilenameDm);

			} catch (Exception e1) {
				textAreaDm.setText("Could not Open!");
			}
		}
	}

	public class ListenOpenLocationsDm implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				JFileChooser chooser = new JFileChooser();

				chooser.showOpenDialog(chooser);
				File file = chooser.getSelectedFile();
				LocationsFilenameDm = file.getAbsolutePath();

				textAreaDm.setText("Opened " + LocationsFilenameDm);

			} catch (Exception e1) {
				textAreaDm.setText("Could not Open!");
			}
		}
	}

	public class ListenGenerateDm implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			try {

				DiscreteTreeToKML templateDm = new DiscreteTreeToKML();
				String mrsdString = mrsdStringParserDm.getText() + " "
						+ (eraParserDm.getSelectedIndex() == 0 ? "AD" : "BC");

				templateDm.setLocationFilePath(LocationsFilenameDm);

				templateDm.setStateAttName(stateAttNameParserDm.getText());

				templateDm.setMaxAltitudeMapping(Double
						.valueOf(maxAltMappingParserDm.getText()));
				templateDm.setMrsdString(mrsdString);
				templateDm.setNumberOfIntervals(Integer
						.valueOf(numberOfIntervalsParserDm.getText()));
				templateDm.setKmlWriterPath(kmlPathParserDm.getText());
				templateDm.setTreePath(TreeFilenameDm);
				templateDm.GenerateKML();
				textAreaDm.setText("Finished in: " + templateDm.time + " msec");

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
				textAreaDm.setText("Could not generate! Check if: \n"
						+ "* tree file is loaded \n"
						+ "* locations file is loaded \n");
			}

			catch (RuntimeException e1) {
				textAreaDm.setText("Could not generate! Check if: \n"
						+ "* proper nr of intervals is specified \n"
						+ "* proper altitude maximum is specified \n");
			}

			catch (FileNotFoundException e2) {
				textAreaDm.setText("File not found exception! Check if: \n"
						+ "* proper kml file path is specified \n");
			}
			
//			catch (ParseException e3) {
//				textAreaDm.setText("File not found exception! Check if: \n"
//						+ "* proper kml file path is specified \n");
//			}

		}// END: actionPerformed
	}// END: ListenGenerate class

	public class ListenCloseWdw extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}

	public void launchFrame() {

		// Display Frame
		Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Frame.setSize(300, 600);
		Frame.setResizable(false);
		// Frame.pack(); // size frame
		Frame.setVisible(true);

	}

	public static void main(String args[]) throws Exception {
		OutbreakGui gui = new OutbreakGui();
		gui.launchFrame();

	}

	private ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = this.getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

}