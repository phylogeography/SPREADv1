package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import utils.ExceptionHandler;

public class TestlabOutbreakApp {

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
	private JMenuBar mainMenu;

	// Buttons with options
	private JButton help;
	private JButton quit;
	private JButton clear;

	// Tabs
	private ContinuousModelTab continuousModelTab;
	private DiscreteModelTab discreteModelTab;
	private RateIndicatorBFTab rateIndicatorBFTab;
	private TimeSlicerTab timeSlicerTab;
	private TerminalTab terminalTab;

	public TestlabOutbreakApp() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {

		// Setup Look & Feel
		if (System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {

			// Mac stuff
			System.setProperty("apple.awt.showGrowBox", "true");
			System.setProperty("apple.awt.brushMetalLook", "true");
			System.setProperty("apple.laf.useScreenMenuBar", "true");

			System.setProperty("apple.awt.graphics.UseQuartz", "true");
			System.setProperty("apple.awt.antialiasing", "true");
			System.setProperty("apple.awt.rendering", "VALUE_RENDER_QUALITY");

			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("apple.awt.draggableWindowBackground", "true");
			System.setProperty("apple.awt.showGrowBox", "true");

			UIManager.setLookAndFeel(ch.randelshofer.quaqua.QuaquaManager
					.getLookAndFeel());

		} else {

			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
//			 UIManager.setLookAndFeel(ch.randelshofer.quaqua.QuaquaManager
//			 .getLookAndFeel());
		}

		dimension = Toolkit.getDefaultToolkit().getScreenSize();
		Toolkit.getDefaultToolkit().setDynamicLayout(true);

		// Setup icons
		quitIcon = CreateImageIcon("/icons/close.png");
		helpIcon = CreateImageIcon("/icons/help.png");
		clearIcon = CreateImageIcon("/icons/clear.png");

		// Setup Main Frame
		frame = new JFrame("S.P.R.E.A.D.");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.addWindowListener(new ListenCloseWdw());
		frame.setIconImage(CreateImage("/icons/nuclear.png"));

		// Setup Main Menu buttons
		help = new JButton("Help", helpIcon);
		quit = new JButton("Quit", quitIcon);
		clear = new JButton("Clear Terminal", clearIcon);

		// Add Main Menu buttons listeners
		quit.addActionListener(new ListenMenuQuit());
		help.addActionListener(new ListenMenuHelp());
		clear.addActionListener(new ListenMenuClearTerminal());

		// Setup Main Menu
		mainMenu = new JMenuBar();
		mainMenu.setLayout(new BorderLayout());
		JPanel buttonsHolder = new JPanel();
		buttonsHolder.setOpaque(false);
		buttonsHolder.add(clear);
		buttonsHolder.add(help);
		buttonsHolder.add(quit);
		mainMenu.add(buttonsHolder, BorderLayout.EAST);

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
		frame.add(tabbedPane, BorderLayout.CENTER);
		frame.getContentPane().add(Box.createVerticalStrut(15),
				BorderLayout.SOUTH);
		frame.pack();

	}

	private class ListenMenuHelp implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			String helpText = "S.P.R.E.A.D. \n"
					+ "Spatial Phylogenetic Reconstruction \n"
					+ "Version 1.0, 2011 \n"
					+ "BEAST software: http://beast.bio.ed.ac.uk/Main_Page \n"
					+ "Processing libraries: http://processing.org/ \n"
					+ "\n"
					+ "* Supported date format is YYYY-MM-DD \n"
					+ "* Remember to set proper node attribute names \n"
					+ "* Color picker setups the maximal RGBA values of the colors that later get mapped to node heigths \n"
					+ "* Resulting KML file is generated in the imported tree/log file directory \n"
					+ "* You can always check the Terminal tab for what might have gone wrong with the analysis \n"
					+ "\n";

			terminalTab.setText(helpText);

		}
	}

	private class ListenMenuClearTerminal implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			terminalTab.clearTerminal();
		}
	}

	public class ListenMenuQuit implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			System.exit(0);
		}
	}

	public class ListenCloseWdw extends WindowAdapter {
		public void windowClosing(WindowEvent ev) {
			System.exit(0);
		}
	}

	public void launchFrame() {

		// Display Frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(dimension.width - 100,
				dimension.height - 100));
		frame.setMinimumSize(new Dimension(260, 100));
		frame.setResizable(true);
		frame.setVisible(true);
	}

	public static void main(String args[]) {

		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

		// Start application's GUI from Event Dispatching Thread
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {

				TestlabOutbreakApp gui;

				try {

					gui = new TestlabOutbreakApp();
					gui.launchFrame();

				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		});
	}// END: main

	private ImageIcon CreateImageIcon(String path) {
		URL imgURL = this.getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path + "\n");
			return null;
		}
	}// END: CreateImageIcon

	private Image CreateImage(String path) {
		URL imgURL = this.getClass().getResource(path);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Image img = kit.createImage(imgURL);

		if (img != null) {
			return img;
		} else {
			System.err.println("Couldn't find file: " + path + "\n");
			return null;
		}

	}// END: CreateImage

}// END: TestlabOutbreakApp