package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class OutbreakGui {

	// Dimension
	Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

	// Icons
	private ImageIcon quitIcon = CreateImageIcon("/icons/close.png");
	private ImageIcon helpIcon = CreateImageIcon("/icons/help.png");
	private ImageIcon clearIcon = CreateImageIcon("/icons/clear.png");

	// Frame
	private JFrame frame = new JFrame("TestlabOutbreak");
	private JTabbedPane tabbedPane = new JTabbedPane();

	// Menubar
	private JMenuBar mainMenu = new JMenuBar();

	// Buttons with options
	private JSeparator separator = new JSeparator(JSeparator.VERTICAL);
	private JButton help = new JButton("Help", helpIcon);
	private JButton quit = new JButton("Quit", quitIcon);
	private JButton clear = new JButton("Clear Terminals", clearIcon);

	// Tabs
	private ContinuousModelTab continuousModelTab;
	private DiscreteModelTab discreteModelTab;
	private RateIndicatorBFTab rateIndicatorBFTab;
	private TimeSlicerTab timeSlicerTab;

	public OutbreakGui() {

		// Setup Main Frame
		frame.getContentPane().setLayout(new BorderLayout());
		frame.add(tabbedPane);
		frame.setJMenuBar(mainMenu);
		frame.addWindowListener(new ListenCloseWdw());
		JScrollPane scrollPane = new JScrollPane(tabbedPane);
		frame.add(scrollPane, BorderLayout.CENTER);

		// Setup Main Menu
		mainMenu.add(separator);
		mainMenu.add(clear);
		mainMenu.add(help);
		mainMenu.add(quit);

		// Add Menu buttons listeners
		quit.addActionListener(new ListenMenuQuit());
		help.addActionListener(new ListenMenuHelp());
		clear.addActionListener(new ListenMenuClearTerminals());

		// add Continuous Model Tab
		continuousModelTab = new ContinuousModelTab();
		tabbedPane.add("Continuous Model", continuousModelTab);

		// add Time Slicer tab
		timeSlicerTab = new TimeSlicerTab();
		tabbedPane.add("Time Slicer", timeSlicerTab);

		// add Discrete Model Tab
		discreteModelTab = new DiscreteModelTab();
		tabbedPane.add("Discrete Model", discreteModelTab);

		// add Discrete Model Tab
		rateIndicatorBFTab = new RateIndicatorBFTab();
		tabbedPane.add("Rate Indicator BF test", rateIndicatorBFTab);

	}

	private class ListenMenuHelp implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String helpText = "TODO \n";
			discreteModelTab.setText(helpText);
			continuousModelTab.setText(helpText);
			rateIndicatorBFTab.setText(helpText);
			timeSlicerTab.setText(helpText);
		}
	}

	private class ListenMenuClearTerminals implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			discreteModelTab.clearTerminal();
			continuousModelTab.clearTerminal();
			rateIndicatorBFTab.clearTerminal();
			timeSlicerTab.clearTerminal();
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
		frame.setSize(new Dimension(dimension.width - 100,
				dimension.height - 100));
		frame.setMinimumSize(new Dimension(230, 100));
		frame.setResizable(true);
		frame.setVisible(true);
	}

	public static void main(String args[]) {
		// Schedule a job for the Event Dispatching Thread:
		// creating and showing application's GUI
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				OutbreakGui gui = new OutbreakGui();
				gui.launchFrame();
			}
		});
	}

	private ImageIcon CreateImageIcon(String path) {
		java.net.URL imgURL = this.getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path + "\n");
			return null;
		}
	}

}