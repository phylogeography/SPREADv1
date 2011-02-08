package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSeparator;

public class OutbreakGui {

	// Initialize all swing objects

	// Frame
	private JFrame Frame = new JFrame("TestlabOutbreak"); // create Frame

	// Buttons with options
	private JSeparator Separator = new JSeparator(JSeparator.VERTICAL);
	private JButton Help = new JButton("Help");
	private JButton Quit = new JButton("Quit");

	// Menubar
	private JMenuBar mb = new JMenuBar();

	// Icon
	ImageIcon treeIcon = createImageIcon("/images/tree.png");

	/** Constructor for the GUI */
	public OutbreakGui() {

		// Set menubar
		Frame.setJMenuBar(mb);

		// Build Menus
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
	}

	private class ListenMenuQuit implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
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

	protected JComponent makePanel(String text) {
		JPanel panel = new JPanel(false);
		JLabel filler = new JLabel(text);
		filler.setHorizontalAlignment(JLabel.CENTER);
		panel.setLayout(new GridLayout(1, 1));
		panel.add(filler);
		return panel;
	}

	public static void main(String args[]) throws Exception {
		OutbreakGui gui = new OutbreakGui();
		gui.launchFrame();

		// ReadNexus nex = new
		// ReadNexus("/home/filip/Americas.104.YFV.E.GTRUCLN_BSSVS_dis.byregion_18_MCC.tre");

	}

}