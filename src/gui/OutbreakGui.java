package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class OutbreakGui {

	// Dimension
	Dimension dimension;

	// Icons
	private ImageIcon quitIcon;
	private ImageIcon helpIcon;
	private ImageIcon clearIcon;

	// Frame
	private JFrame frame;
	private JTabbedPane tabbedPane;

	// Menubar
	private JMenuBar mainMenu = new JMenuBar();

	// Buttons with options
	private JSeparator separator;
	private JButton help;
	private JButton quit;
	private JButton clear;

	// Tabs
	private ContinuousModelTab continuousModelTab;
	private DiscreteModelTab discreteModelTab;
	private RateIndicatorBFTab rateIndicatorBFTab;
	private TimeSlicerTab timeSlicerTab;
	private TerminalTab terminalTab;

	public OutbreakGui() throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException {

		// Setup Look & Feel
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		dimension = Toolkit.getDefaultToolkit().getScreenSize();
		Toolkit.getDefaultToolkit().setDynamicLayout(true);

		// Mac stuff
		System.setProperty("apple.awt.showGrowBox", "true");
		System.setProperty("apple.awt.brushMetalLook", "true");
		System.setProperty("apple.laf.useScreenMenuBar", "true");

		// Setup icons
		quitIcon = CreateImageIcon("/icons/close.png");
		helpIcon = CreateImageIcon("/icons/help.png");
		clearIcon = CreateImageIcon("/icons/clear.png");

		// Setup Main Frame
		frame = new JFrame("TestlabOutbreak");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.addWindowListener(new ListenCloseWdw());

		// Setup Main Menu buttons
		help = new JButton("Help", helpIcon);
		quit = new JButton("Quit", quitIcon);
		clear = new JButton("Clear Terminal", clearIcon);

		// Add Main Menu buttons listeners
		quit.addActionListener(new ListenMenuQuit());
		help.addActionListener(new ListenMenuHelp());
		clear.addActionListener(new ListenMenuClearTerminal());

		// Setup Main Menu
		separator = new JSeparator(JSeparator.VERTICAL);
		separator.setOpaque(true);
		mainMenu.add(separator);
		mainMenu.add(clear);
		mainMenu.add(help);
		mainMenu.add(quit);

		// Setup Tabbed Pane
		tabbedPane = new JTabbedPane();

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

		// add Terminal Tab
		terminalTab = new TerminalTab();
		tabbedPane.add("Terminal", terminalTab);

		frame.setJMenuBar(mainMenu);
		frame.add(tabbedPane);
		JScrollPane scrollPane = new JScrollPane(tabbedPane,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		frame.add(scrollPane, BorderLayout.CENTER);
		frame.getContentPane().add(Box.createVerticalStrut(15),
				BorderLayout.SOUTH);
		frame.pack();
	}

	private class ListenMenuHelp implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String helpText = "TODO \n";
			terminalTab.setText(helpText);

		}
	}

	private class ListenMenuClearTerminal implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			terminalTab.clearTerminal();
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

		// Start application's GUI from Event Dispatching Thread
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {

				OutbreakGui gui;

				try {

					gui = new OutbreakGui();
					gui.launchFrame();

				} catch (ClassNotFoundException e) {
					e.printStackTrace();

				} catch (InstantiationException e) {
					e.printStackTrace();

				} catch (IllegalAccessException e) {
					e.printStackTrace();

				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();

				}
			}
		});
	}// END: main

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