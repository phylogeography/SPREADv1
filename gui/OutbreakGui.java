package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToolBar;

public class OutbreakGui {

	// Initialize all swing objects

	// Frame
	private JFrame Frame = new JFrame("TestlabOutbreak"); // create Frame

	// Buttons with options
	private JButton Open = new JButton("Open");
	private JSeparator Separator = new JSeparator(JSeparator.VERTICAL);
	private JButton Help = new JButton("Help");
	private JButton Quit = new JButton("Quit");

	// Menubar
	private JMenuBar mb = new JMenuBar();

	// Text fields
	JTextField coordinatesNameParser = new JTextField(10);

	/** Constructor for the GUI */
	public OutbreakGui() {

		coordinatesNameParser.setActionCommand("coordinatesNameParser");

		// Set menubar
		Frame.setJMenuBar(mb);
		Frame.add(coordinatesNameParser);

		// Build Menus
		mb.add(Open);
		mb.add(Separator);
		mb.add(Help);
		mb.add(Quit);

		// Setup Main Frame
		Frame.getContentPane().setLayout(new BorderLayout());
		// tabbedPane.addTab("Tab", treeIcon, segments);

		// Allows the Swing App to be closed
		Frame.addWindowListener(new ListenCloseWdw());

		// Add Menu listeners
		Quit.addActionListener(new ListenMenuQuit());
		Open.addActionListener(new ListenMenuOpen());
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
//		Frame.setResizable(false);
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