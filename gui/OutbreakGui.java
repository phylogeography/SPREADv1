package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;

public class OutbreakGui {

	// Initialize all swing objects

	// Frame
	private JFrame Frame = new JFrame("app"); // create Frame

	// Buttons with options
	private JButton Open = new JButton("Open");
	private JSeparator Separator = new JSeparator(JSeparator.VERTICAL);
	private JButton Help = new JButton("Help");
	private JButton Quit = new JButton("Quit");

	// Menubar
	private JMenuBar mb = new JMenuBar();

	// Text fields
	JTextField coordinatesNameParser = new JTextField("location", 10);
	JTextField HPDParser = new JTextField("95%", 5);

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
		panel1.setBorder(new javax.swing.border.TitledBorder("1"));
		JTextField textField1 = new JTextField("1");
		panel1.add(textField1);
		content.add(panel1);

		
		JPanel panel2 = new JPanel();
		panel2.setBorder(new javax.swing.border.TitledBorder("2"));
		JTextField textField2 = new JTextField("2");
		panel2.add(textField2);
		content.add(panel2);

		JPanel panel3 = new JPanel();
		panel3.setBorder(new javax.swing.border.TitledBorder("3 / 4"));
		JTextField textField3 = new JTextField("3");
		JTextField textField4 = new JTextField("4");
		panel3.add(textField3);
		panel3.add(textField4);
		content.add(panel3);
		
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