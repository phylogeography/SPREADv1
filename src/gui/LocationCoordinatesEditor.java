package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import jebl.evolution.graphs.Node;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.trees.RootedTree;
import utils.ReadLocations;
import utils.Utils;

public class LocationCoordinatesEditor {

	private InteractiveTableModel returnValue = null;

	// Window
	private JDialog window;
	private Frame owner;

	// Icons
	private ImageIcon loadIcon;
	private ImageIcon saveIcon;
	private ImageIcon doneIcon;

	// Menubar
	private JMenuBar menu;

	// Buttons with options
	private JButton load;
	private JButton save;
	private JButton done;

	// File chooser
	private JFileChooser chooser;

	// Strings for paths
	private String locationsFilename;
	private File workingDirectory;
	private File file;

	// Data, model & stuff for JTable
	private JTable table;
	private InteractiveTableModel tableModel;
	private String[] COLUMN_NAMES = { "Location", "Latitude", "Longitude", "" };

	public LocationCoordinatesEditor() {

		// Setup icons
		loadIcon = CreateImageIcon("/icons/locations.png");
		saveIcon = CreateImageIcon("/icons/save.png");
		doneIcon = CreateImageIcon("/icons/check.png");

		// Setup Main Menu buttons
		load = new JButton("Load", loadIcon);
		save = new JButton("Save", saveIcon);
		done = new JButton("Done", doneIcon);

		// Add Main Menu buttons listeners
		load.addActionListener(new ListenOpenLocations());
		save.addActionListener(new ListenSaveLocationCoordinates());
		done.addActionListener(new ListenOk());

		// Setup menu
		menu = new JMenuBar();
		menu.setLayout(new BorderLayout());
		JPanel buttonsHolder = new JPanel();
		buttonsHolder.setOpaque(false);
		buttonsHolder.add(load);
		buttonsHolder.add(save);
		buttonsHolder.add(done);
		menu.add(buttonsHolder, BorderLayout.WEST);

		// Setup table
		tableModel = new InteractiveTableModel(COLUMN_NAMES);
		tableModel.addTableModelListener(new InteractiveTableModelListener());
		table = new JTable(tableModel);
		table.setModel(tableModel);
		table.setSurrendersFocusOnKeystroke(true);

		TableColumn hidden = table.getColumnModel().getColumn(
				InteractiveTableModel.HIDDEN_INDEX);
		hidden.setMinWidth(2);
		hidden.setPreferredWidth(2);
		hidden.setMaxWidth(2);
		hidden.setCellRenderer(new InteractiveRenderer(
				InteractiveTableModel.HIDDEN_INDEX));

		JScrollPane scrollPane = new JScrollPane(table,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		// Setup window
		owner = Utils.getActiveFrame();
		window = new JDialog(owner, "Setup location coordinates...");
		window.getContentPane().add(menu, BorderLayout.NORTH);
		window.getContentPane().add(scrollPane);
		window.pack();
		window.setLocationRelativeTo(owner);

	}// END: LocationCoordinatesEditor()

	private class ListenOpenLocations implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			try {

				chooser = new JFileChooser();
				chooser.setDialogTitle("Loading location file...");
				chooser.setCurrentDirectory(workingDirectory);

				chooser.showOpenDialog(Utils.getActiveFrame());
				file = chooser.getSelectedFile();
				locationsFilename = file.getAbsolutePath();

				File tmpDir = chooser.getCurrentDirectory();

				if (tmpDir != null) {

					workingDirectory = tmpDir;
					ReadLocations data = new ReadLocations(locationsFilename);

					if (tableModel.getRowCount() < data.nrow) {
						for (int i = 0; i < data.nrow - 1; i++) {
							tableModel.addEmptyRow();
						}
					}

					for (int i = 0; i < data.nrow; i++) {

						// String name = String.valueOf(data.locations[i]);
						// String longitude =
						// String.valueOf(data.coordinates[i][0]);
						// String latitude =
						// String.valueOf(data.coordinates[i][1]);
						// tableModel.insertRow(i, new TableRecord(name,
						// longitude, latitude));

						tableModel.setValueAt(data.locations[i], i, 0);

						for (int j = 0; j < 2; j++) {

							tableModel.setValueAt(String
									.valueOf(data.coordinates[i][j]), i, j + 1);

						}// END: col loop
					}// END: row loop
				}// END: null check

			} catch (Exception e) {
				System.err.println("Could not Open! \n");
				e.printStackTrace();
			}
		}
	}// END: ListenOpenLocations

	private class ListenSaveLocationCoordinates implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			try {

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Saving as tab delimited file...");

				chooser.showSaveDialog(Utils.getActiveFrame());
				File file = chooser.getSelectedFile();
				String filename = file.getAbsolutePath();

				JTableToTDV(filename, tableModel);
				System.out.println("Saved " + filename + "\n");

			} catch (Exception e) {
				System.err.println("Could not save! \n");
			}

		}// END: actionPerformed
	}// END: ListenSaveLocationCoordinates

	private class ListenOk implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			window.setVisible(false);

			returnValue = tableModel;

			System.out.println("Loaded " + returnValue.getRowCount()
					+ " discrete locations:");
			returnValue.printTable();

		}// END: actionPerformed
	}// END: ListenSaveLocationCoordinates

	private class InteractiveTableModelListener implements TableModelListener {
		public void tableChanged(TableModelEvent ev) {

			if (ev.getType() == TableModelEvent.UPDATE) {
				int column = ev.getColumn();
				int row = ev.getFirstRow();
				table.setColumnSelectionInterval(column + 1, column + 1);
				table.setRowSelectionInterval(row, row);
			}
		}
	}// END: InteractiveTableModelListener

	@SuppressWarnings("serial")
	private class InteractiveRenderer extends DefaultTableCellRenderer {
		protected int interactiveColumn;

		public InteractiveRenderer(int interactiveColumn) {
			this.interactiveColumn = interactiveColumn;
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			Component c = super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);

			if (column == interactiveColumn && hasFocus) {
				if ((tableModel.getRowCount() - 1) == row
						&& !tableModel.hasEmptyRow()) {
					tableModel.addEmptyRow();
				}

				highlightLastRow(row);
			}

			return c;
		}
	}// END: getTableCellRendererComponent

	private void highlightLastRow(int row) {

		int lastrow = tableModel.getRowCount();
		if (row == lastrow - 1) {
			table.setRowSelectionInterval(lastrow - 1, lastrow - 1);
		} else {
			table.setRowSelectionInterval(row + 1, row + 1);
		}

		table.setColumnSelectionInterval(0, 0);
	}// END: highlightLastRow

	private void JTableToTDV(String filename, InteractiveTableModel model) {

		boolean empty = false;

		try {

			PrintWriter printWriter = new PrintWriter(filename);
			for (int i = 0; i < model.getRowCount(); i++) {
				for (int j = 0; j < model.getColumnCount() - 1; j++) {

					String s = model.getValueAt(i, j).toString();
					empty = s.trim().equals("");

					if (!empty) {
						printWriter.print(s + "\t");
					}// END: check for empty values

				}// END: col loop

				if (!empty) {
					printWriter.println("");
				}// END: check for empty values

			}// END: row loop

			printWriter.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}// END: JTableToTDV

	private Object[] getUniqueTreeStates(RootedTree tree, String stateAttName) {

		Set<String> uniqueTreeStates = new HashSet<String>();
		for (Node node : tree.getNodes()) {
			if (!tree.isRoot(node)) {

				String[] states = Utils.getStringNodeAttribute(node,
						stateAttName).split("\\+");

				for (int i = 0; i < states.length; i++) {

					uniqueTreeStates.add(states[i]);

				}

			}// END: isRoot
		}// END: node loop

		Object[] uniqueTreeStatesArray = uniqueTreeStates.toArray();

		return uniqueTreeStatesArray;
	}// END: getUniqueTreeStates

	public void launch(String treeFilename, String stateAttName,
			File workingDirectory) {

		try {

			this.workingDirectory = workingDirectory;

			RootedTree tree = (RootedTree) new NexusImporter(new FileReader(
					treeFilename)).importNextTree();

			Object[] uniqueTreeStates = getUniqueTreeStates(tree, stateAttName);

			for (int i = 0; i < uniqueTreeStates.length; i++) {

				tableModel.insertRow(i, new TableRecord(String
						.valueOf(uniqueTreeStates[i]), "", ""));

			}// END: row loop

			// Display Frame
			window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			window.setSize(new Dimension(300, 300));
			window.setMinimumSize(new Dimension(100, 100));
			window.setResizable(true);
			window.setVisible(true);

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		} catch (ImportException e) {
			e.printStackTrace();
		}

	}// END: launch

	public InteractiveTableModel getTable() {
		return tableModel;
	}

	public void launch(File workingDirectory) {

		if (!tableModel.hasEmptyRow()) {
			tableModel.addEmptyRow();
		}

		this.workingDirectory = workingDirectory;

		// Display Frame
		window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		window.setSize(new Dimension(300, 300));
		window.setMinimumSize(new Dimension(100, 100));
		window.setResizable(true);
		window.setVisible(true);
	}// END: launch

	private ImageIcon CreateImageIcon(String path) {
		URL imgURL = this.getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path + "\n");
			return null;
		}
	}// END: CreateImageIcon

	public String[] getColumnNames() {

		return COLUMN_NAMES;
	}// END: getColumnNames

}// /END: class
