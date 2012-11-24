package app;


import gui.ContinuousModelTab;
import gui.DiscreteModelTab;
import gui.RateIndicatorBFTab;
import gui.TerminalTab;
import gui.TimeSlicerTab;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import utils.ExceptionHandler;

public class SpreadApp {

    /**
     * Version string: assumed to be in format x.x.x
     */
    private static final String VERSION = "1.0.5";
    private static final String DATE_STRING = "2012";

	// Dimension
	private Dimension dimension;

	// Icons
	public static ImageIcon quitIcon;
	public static ImageIcon helpIcon;
	public static ImageIcon clearIcon;
	public static ImageIcon nuclearIcon;
	public static ImageIcon treeIcon;
	public static ImageIcon processingIcon;
	public static ImageIcon saveIcon;
	public static ImageIcon errorIcon;
	public static ImageIcon locationsIcon;
	public static ImageIcon loadIcon;
	public static ImageIcon doneIcon;
	public static ImageIcon logIcon;
	public static ImageIcon timeSlicesIcon;
	public static ImageIcon treesIcon;
	
	// Frame
	private JFrame frame;
	private JTabbedPane tabbedPane;

	// Menubar
	private JToolBar mainMenu;

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
	
	public SpreadApp() throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException {

		boolean lafLoaded = false;

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

			UIManager.put("SystemFont", new Font("Lucida Grande", Font.PLAIN,
					13));
			UIManager.put("SmallSystemFont", new Font("Lucida Grande",
					Font.PLAIN, 11));

			try {

				// UIManager.setLookAndFeel(UIManager
				// .getSystemLookAndFeelClassName());

				UIManager
						.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
				lafLoaded = true;

			} catch (Exception e) {
				//
			}

		} else {

			try {

				UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
				lafLoaded = true;

			} catch (Exception e) {
				//
			}

		}

		if (!lafLoaded) {

			try {

				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				System.out.println("Specified l&f not found. Loading system default l&f");
				
			} catch (Exception e) {

				e.printStackTrace();

			}
		}

		dimension = Toolkit.getDefaultToolkit().getScreenSize();
		Toolkit.getDefaultToolkit().setDynamicLayout(true);

		// Setup icons
		quitIcon = CreateImageIcon("/icons/close.png");
		helpIcon = CreateImageIcon("/icons/help.png");
		clearIcon = CreateImageIcon("/icons/clear.png");
		nuclearIcon = CreateImageIcon("/icons/nuclear.png");
		treeIcon = CreateImageIcon("/icons/tree.png");
		processingIcon = CreateImageIcon("/icons/processing.png");
		saveIcon = CreateImageIcon("/icons/save.png");
		errorIcon = CreateImageIcon("/icons/error.png");
		locationsIcon = CreateImageIcon("/icons/locations.png");
		loadIcon = CreateImageIcon("/icons/locations.png");
		doneIcon = CreateImageIcon("/icons/check.png");
		logIcon = CreateImageIcon("/icons/log.png");
		timeSlicesIcon = CreateImageIcon("/icons/timeSlices.png");
		treesIcon = CreateImageIcon("/icons/trees.png");
		 
		// Setup Main Frame
		frame = new JFrame("SPREAD");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.addWindowListener(new ListenCloseWdw());
		frame.setIconImage(CreateImage("/icons/spread.png"));

		// Setup Main Menu buttons
		help = new JButton("Help", helpIcon);
		quit = new JButton("Quit", quitIcon);
		clear = new JButton("Clear Terminal", clearIcon);

		// Add Main Menu buttons listeners
		quit.addActionListener(new ListenMenuQuit());
		help.addActionListener(new ListenMenuHelp());
		clear.addActionListener(new ListenMenuClearTerminal());

		// Setup Main Menu
		mainMenu = new JToolBar();
		mainMenu.setFloatable(false);
		mainMenu.setLayout(new BorderLayout());
		JPanel buttonsHolder = new JPanel();
		buttonsHolder.setOpaque(false);
		buttonsHolder.add(clear);
		buttonsHolder.add(help);
		buttonsHolder.add(quit);
		mainMenu.add(buttonsHolder, BorderLayout.EAST);

		// Setup Tabbed Pane
		tabbedPane = new JTabbedPane();

		// add Discrete Model Tab
		discreteModelTab = new DiscreteModelTab();
		tabbedPane.add("Discrete Tree", discreteModelTab);

		// add rateIndicatorBF Tab
		rateIndicatorBFTab = new RateIndicatorBFTab();
		tabbedPane.add("Discrete Bayes Factors", rateIndicatorBFTab);

		// add Continuous Model Tab
		continuousModelTab = new ContinuousModelTab();
		tabbedPane.add("Continuous Tree", continuousModelTab);

		// add Time Slicer tab
		timeSlicerTab = new TimeSlicerTab();
		tabbedPane.add("Time Slicer", timeSlicerTab);

		// add Terminal Tab
		terminalTab = new TerminalTab();
		tabbedPane.add("Terminal", terminalTab);

		frame.add(mainMenu, BorderLayout.NORTH);
		frame.add(tabbedPane, BorderLayout.CENTER);
		frame.getContentPane().add(Box.createVerticalStrut(15),
				BorderLayout.SOUTH);
		frame.pack();

	}

	private class ListenMenuHelp implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			String helpText = "\n"
					+ "\t SPREAD \n"
					+ "Spatial Phylogenetic Reconstruction of Evolutionary Dynamics \n"
					+ " \t Version " + VERSION + ", " + DATE_STRING + "\n"
					+ "Filip Bielejec, Andrew Rambaut, Marc A. Suchard & Philippe Lemey \n"
					+ "\n"
					+ "SPREAD: www.phylogeography.org/SPREAD \n"
					+ "BEAST software: http://beast.bio.ed.ac.uk/Main_Page \n"
					+ "PROCESSING libraries: http://processing.org/ \n"
					+ "\n"
					+ "Citing SPREAD: \n"
					+ "Bielejec F., Rambaut A., Suchard M.A & Lemey P. SPREAD: Spatial Phylogenetic Reconstruction of Evolutionary Dynamics. Bioinformatics, 2011. doi:10.1093 \n"
//					+ "\n"
					+ "BibTeX entry: \n"
					+ "@article{Bielejec11092011, \n"
					+ "\t author = {Bielejec, Filip and Rambaut, Andrew and Suchard, Marc A. and Lemey, Philippe}, \n"
					+ "\t title = {SPREAD: Spatial phylogenetic reconstruction of evolutionary dynamics}, \n"
					+ "\t year = {2011}, \n"
					+ "\t doi = {10.1093/bioinformatics/btr481}, \n"
					+ "\t URL = {http://bioinformatics.oxfordjournals.org/content/early/2011/09/11/bioinformatics.btr481.abstract}, \n"
					+ "\t eprint = {http://bioinformatics.oxfordjournals.org/content/early/2011/09/11/bioinformatics.btr481.full.pdf+html}, \n"
					+ "\t journal = {Bioinformatics} \n" 
					+ "} \n";

			terminalTab.setText(helpText);
			tabbedPane.setSelectedIndex(4);

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

				SpreadApp gui;

				try {

					gui = new SpreadApp();
					gui.launchFrame();

				} catch (UnsupportedClassVersionError e) {
					
					System.err.println("Your Java Runtime Environment is too old. Please update");
					e.printStackTrace();

				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedLookAndFeelException e) {
					// TODO Auto-generated catch block
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

}// END: SpreadApp
