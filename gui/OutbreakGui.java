package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
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
	private String filenameCm = null;

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
	private JButton OpenCm = new JButton("Open", treeIcon);

	// Status Bar for tab
	JTextArea textAreaCm;

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

		OpenCm.addActionListener(new ListenMenuOpen());
		GenerateCm.addActionListener(new ListenGenerate());

		JPanel panel0 = new JPanel();
		panel0.setBorder(new TitledBorder("Load tree file:"));
		panel0.add(OpenCm);
		continuousModelTab.add(panel0);

		JPanel panel1 = new JPanel();
		panel1.setBorder(new TitledBorder("Coordinate attribute name:"));
		panel1.add(coordinatesNameParserCm);
		continuousModelTab.add(panel1);

		JPanel panel2 = new JPanel();
		panel2.setOpaque(false);
		JLabel label2 = new JLabel("%");
		panel2.setBorder(new TitledBorder("HPD:"));
		panel2.add(HPDParserCm);
		panel2.add(label2);
		label2.setLabelFor(panel2);
		continuousModelTab.add(panel2);

		JPanel panel3 = new JPanel();
		panel3.setBorder(new TitledBorder("Most recent sampling date:"));
		String era[] = { "AD", "BC" };
		eraParserCm = new JComboBox(era);
		panel3.add(mrsdStringParserCm);
		panel3.add(eraParserCm);
		continuousModelTab.add(panel3);

		JPanel panel4 = new JPanel();
		panel4.setBorder(new TitledBorder("Number of intervals:"));
		panel4.add(numberOfIntervalsParserCm);
		continuousModelTab.add(panel4);

		JPanel panel5 = new JPanel();
		panel5.setBorder(new TitledBorder("Maximal altitude:"));
		panel5.add(maxAltMappingParserCm);
		continuousModelTab.add(panel5);

		JPanel panel6 = new JPanel();
		panel6.setBorder(new TitledBorder("KML name:"));
		panel6.add(kmlPathParserCm);
		continuousModelTab.add(panel6);

		JPanel panel7 = new JPanel();
		panel7.setBorder(new TitledBorder("Generate KML:"));
		panel7.add(GenerateCm);
		continuousModelTab.add(panel7);

		// TODO: make scroll pane work
		JPanel panel8 = new JPanel();
		// panel8.setLayout(new BorderLayout());
		textAreaCm = new JTextArea(4, 20);
		textAreaCm.setEditable(true);
		JScrollPane scrollPane = new JScrollPane(textAreaCm);
		panel8.add(scrollPane, BorderLayout.CENTER);
		panel8.add(textAreaCm);
		continuousModelTab.add(panel8);

		/**
		 * Discrete Model Tab
		 * */
		JPanel discreteModelTab = new JPanel();
		discreteModelTab.setOpaque(false);
		tabbedPane.addTab("Discrete Model", discreteModelTab);
	}

	private class ListenMenuHelp implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			textAreaCm.setText("TODO");
		}
	}

	public class ListenMenuQuit implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	public class ListenMenuOpen implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				JFileChooser chooser = new JFileChooser();

				chooser.showOpenDialog(chooser);
				File file = chooser.getSelectedFile();
				filenameCm = file.getAbsolutePath();

				textAreaCm.setText("Opened " + filenameCm);

			} catch (Exception e1) {
				e1.printStackTrace();
				textAreaCm.setText("Could not Open!");
			}
		}
	}

	public class ListenGenerate implements ActionListener {
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
				template.setTreePath(filenameCm);
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

			// catch (Throwable e0) {
			// textArea.setText("FUBAR!");
			// e0.printStackTrace;
			// }

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