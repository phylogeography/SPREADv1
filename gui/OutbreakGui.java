package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
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
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import templates.ContinuousTreeToKML;

public class OutbreakGui {

	private String filename;
	// Icons
	private ImageIcon nuclearIcon = createImageIcon("/icons/nuclear.png");

	// Frame
	private JFrame Frame = new JFrame("TestlabOutbreak");

	// Buttons
	private JButton open = new JButton("Open");
	private JSeparator separator = new JSeparator(JSeparator.VERTICAL);
	private JButton help = new JButton("Help");
	private JButton quit = new JButton("Quit");
	private JButton generate = new JButton("Generate", nuclearIcon);

	// Menubar
	private JMenuBar menuBar = new JMenuBar();

	// Current date
	private Calendar calendar = Calendar.getInstance();
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",
			Locale.US);

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

	// Status Bar
	JTextArea textArea;

	/**
	 * Constructor for the GUI
	 * */
	public OutbreakGui() {

		// Allows the Swing App to be closed
		Frame.addWindowListener(new ListenCloseWdw());

		// Set menubar
		Frame.setJMenuBar(menuBar);

		// Build Menus
		menuBar.add(open);
		menuBar.add(separator);
		menuBar.add(help);
		menuBar.add(quit);

		// Add Menu listeners
		quit.addActionListener(new ListenMenuQuit());
		open.addActionListener(new ListenMenuOpen());
		generate.addActionListener(new ListenGenerate());
		help.addActionListener(new ListenMenuHelp());

		Container content = Frame.getContentPane();
		content.setLayout(new GridLayout(0, 1));

		JPanel panel1 = new JPanel();
		panel1.setBorder(new TitledBorder("Coordinate attribute name:"));
		panel1.add(coordinatesNameParser);
		content.add(panel1);

		JPanel panel2 = new JPanel();
		JLabel label2 = new JLabel("%");
		panel2.setBorder(new TitledBorder("HPD:"));
		panel2.add(HPDParser);
		panel2.add(label2);
		label2.setLabelFor(panel2);
		content.add(panel2);

		JPanel panel3 = new JPanel();
		panel3.setBorder(new TitledBorder("Most recent sampling date:"));
		String era[] = { "AD", "BC" };
		eraParser = new JComboBox(era);
		panel3.add(mrsdStringParser);
		panel3.add(eraParser);
		content.add(panel3);

		JPanel panel4 = new JPanel();
		panel4.setBorder(new TitledBorder("Number of intervals:"));
		panel4.add(numberOfIntervalsParser);
		content.add(panel4);

		JPanel panel5 = new JPanel();
		panel5.setBorder(new TitledBorder("Maximal altitude:"));
		panel5.add(maxAltMappingParser);
		content.add(panel5);

		JPanel panel6 = new JPanel();
		panel6.setBorder(new TitledBorder("KML name:"));
		panel6.add(kmlPathParser);
		content.add(panel6);

		JPanel panel7 = new JPanel();
		panel7.setBorder(new TitledBorder("Generate KML:"));
		panel7.add(generate);
		content.add(panel7);

		// TODO: make scroll pane work
		JPanel panel8 = new JPanel();
		// panel8.setLayout(new BorderLayout());
		textArea = new JTextArea(4, 20);
		textArea.setEditable(true);
		JScrollPane scrollPane = new JScrollPane(textArea);
		panel8.add(scrollPane, BorderLayout.CENTER);
		panel8.add(textArea);
		content.add(panel8);

		// JPanel panel8 = new JPanel();
		// panel8.setBorder(new TitledBorder("TEST"));
		// LabelTextCombo test = new LabelTextCombo("First Name", new Font(
		// "Arial", Font.PLAIN, 13), new Font("Arial", Font.PLAIN, 13), 15);
		// panel8.add(test);
		// content.add(panel8);
	}
	
	private class ListenMenuHelp implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.out.println("TODO");
		}
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
			filename = file.getAbsolutePath();
		}
	}

	public class ListenGenerate implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				ContinuousTreeToKML template = new ContinuousTreeToKML();
				template.setHPD(HPDParser.getText()+"%");
				template.setcoordinatesName(coordinatesNameParser.getText());
				template.setmaxAltitudeMapping(Double.valueOf(maxAltMappingParser.getText()));
				
				String mrsdString = mrsdStringParser.getText()+" "+
				
				(eraParser.getSelectedIndex() == 0 ? "AD":"BC" );
				
				
				template.setmrsdString(mrsdString);
				template.setnumberOfIntervals(Integer.valueOf(numberOfIntervalsParser.getText()));
				template.setKmlWriterPath(kmlPathParser.getText());
				template.setTreePath(filename);
				template.GenerateKML();
				
				System.out.println(template.time);

			} catch (Exception e1) {
				e1.printStackTrace();
			}
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
		Frame.setSize(300, 600);
		Frame.setResizable(false);
		// Frame.pack(); // size frame
		Frame.setVisible(true);

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

	public static void main(String args[]) throws Exception {
		OutbreakGui gui = new OutbreakGui();
		gui.launchFrame();

	}// END: main

	@SuppressWarnings("serial")
	public class LabelTextCombo extends JPanel {

		private JLabel label;
		private JTextField textField;

		public LabelTextCombo(String text, Font labelFont, Font textFieldFont,
				int textFieldSize) {

			// setLayout(new FlowLayout(FlowLayout.CENTER));
			setLayout(new GridLayout(1, 2));
			label = new JLabel(text);
			label.setFont(labelFont);
			textField = new JTextField(textFieldSize);
			textField.setFont(textFieldFont);
			add(label);
			add(textField);
		}
	}

}// END: OutbreakGui class