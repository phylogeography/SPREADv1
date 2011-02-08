package gui;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

public class OutbreakGui {

	// Initialize all swing objects

	// Frame
	private JFrame Frame = new JFrame("TestlabOutbreak");

	// Buttons with options
	private JButton Open = new JButton("Open");
	private JSeparator Separator = new JSeparator(JSeparator.VERTICAL);
	private JButton Help = new JButton("Help");
	private JButton Quit = new JButton("Quit");
	private JButton Generate = new JButton("Generate");
	
	// Menubar
	private JMenuBar mb = new JMenuBar();

	Calendar calendar = Calendar.getInstance();
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

	// Text fields
	JTextField coordinatesNameParser = new JTextField("location", 10);
	JTextField HPDParser = new JTextField("95%", 5);
	JTextField mrsdStringParser = new JTextField(formatter.format(calendar
			.getTime()), 10);
	JComboBox eraParser;
	JTextField numberOfIntervalsParser = new JTextField("100", 5);
	JTextField maxAltMappingParser = new JTextField("5000000", 5);
	JTextField kmlPathParser = new JTextField("/home/filip/Pulpit/output.kml",
			15);

	/**
	 * Constructor for the GUI
	 * */
	public OutbreakGui() {

		// Allows the Swing App to be closed
		Frame.addWindowListener(new ListenCloseWdw());

		// Set menubar
		Frame.setJMenuBar(mb);

		// Build Menus
		mb.add(Open);
		mb.add(Separator);
		mb.add(Help);
		mb.add(Quit);

		// Add Menu listeners
		Quit.addActionListener(new ListenMenuQuit());
		Open.addActionListener(new ListenMenuOpen());

		Container content = Frame.getContentPane();
		content.setLayout(new GridLayout(0, 1));

		JPanel panel1 = new JPanel();
		panel1.setBorder(new javax.swing.border.TitledBorder(
				"Coordinate attribute name:"));
		panel1.add(coordinatesNameParser);
		content.add(panel1);

		JPanel panel2 = new JPanel();
		panel2.setBorder(new javax.swing.border.TitledBorder("HPD:"));
		panel2.add(HPDParser);
		content.add(panel2);

		JPanel panel3 = new JPanel();
		panel3.setBorder(new javax.swing.border.TitledBorder(
				"Most recent sampling date:"));
		String era[] = { "AD", "BC" };
		eraParser = new JComboBox(era);
		panel3.add(mrsdStringParser);
		panel3.add(eraParser);
		content.add(panel3);

		JPanel panel4 = new JPanel();
		panel4.setBorder(new javax.swing.border.TitledBorder(
				"number of intervals:"));
		panel4.add(numberOfIntervalsParser);
		content.add(panel4);

		JPanel panel5 = new JPanel();
		panel5.setBorder(new javax.swing.border.TitledBorder(
				"maximal altitude:"));
		panel5.add(maxAltMappingParser);
		content.add(panel5);

		JPanel panel6 = new JPanel();
		panel6.setBorder(new javax.swing.border.TitledBorder("kml name:"));
		panel6.add(kmlPathParser);
		content.add(panel6);
		
		JPanel panel7 = new JPanel();
		panel7.setBorder(new javax.swing.border.TitledBorder("Generate KML:"));
		panel7.add(Generate);
		content.add(panel7);

	}

	private class ListenMenuQuit implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	public class ListenMenuOpen implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			JFileChooser chooser = new JFileChooser();

			chooser.showOpenDialog(chooser);
			File file = chooser.getSelectedFile();
			String filename = file.getAbsolutePath();

		}
	}

	private class ListenCloseWdw extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}

	private void launchFrame() {

		// Display Frame
		Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Frame.setSize(400, 500);
		// Frame.setResizable(false);
		Frame.pack(); // size frame
		Frame.setVisible(true);

	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = this.getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public static void main(String args[]) throws Exception {
		OutbreakGui gui = new OutbreakGui();
		gui.launchFrame();

	}

}