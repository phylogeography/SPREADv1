package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;

public class OutbreakGui {

	// Icons
	private ImageIcon quitIcon = CreateImageIcon("/icons/close.png");
	private ImageIcon helpIcon = CreateImageIcon("/icons/help.png");

	// Frame
	private JFrame frame = new JFrame("TestlabOutbreak");
	private JTabbedPane tabbedPane = new JTabbedPane();

	// Menubar
	private JMenuBar mainMenu = new JMenuBar();

	// Buttons with options
	private JSeparator separator = new JSeparator(JSeparator.VERTICAL);
	private JButton help = new JButton("Help", helpIcon);
	private JButton quit = new JButton("Quit", quitIcon);

	// Tabs
	ContinuousModelTab continuousModelTab;
	DiscreteModelTab discreteModelTab;

	public OutbreakGui() {

		// Setup Main Frame
		frame.getContentPane().setLayout(new BorderLayout());
		frame.add(tabbedPane);
		frame.setJMenuBar(mainMenu);
		frame.addWindowListener(new ListenCloseWdw());

		// Setup Main Menu
		mainMenu.add(separator);
		mainMenu.add(help);
		mainMenu.add(quit);

		// Add Menu buttons listeners
		quit.addActionListener(new ListenMenuQuit());
		help.addActionListener(new ListenMenuHelp());

		// add Continuous Model Tab
		continuousModelTab = new ContinuousModelTab();
		tabbedPane.add("Continuous Model", continuousModelTab);

		// add Discrete Model Tab
		discreteModelTab = new DiscreteModelTab();
		tabbedPane.add("Discrete Model", discreteModelTab);

	}

	private class ListenMenuHelp implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String helpText = "TODO";
			discreteModelTab.setText(helpText);
			continuousModelTab.setText(helpText);
		}
	}

	public class ListenMenuQuit implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	public class ListenCloseWdw extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}

	public void launchFrame() {

		// Display Frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 600);
		frame.setResizable(false);
		// Frame.pack(); // size frame
		frame.setVisible(true);
	}

	public static void main(String args[]) throws Exception {
		OutbreakGui gui = new OutbreakGui();
		gui.launchFrame();
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

}