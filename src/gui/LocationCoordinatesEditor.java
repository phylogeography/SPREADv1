package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
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

import utils.ReadLocations;
import utils.Utils;

public class LocationCoordinatesEditor {
	
	// TODO: this should be a JWindow, than it can accept a parent
	// Frame
	private JFrame frame;

	// Icons
	private ImageIcon loadIcon;
	private ImageIcon saveIcon;

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

	// Locations data
	private ReadLocations data;

	public LocationCoordinatesEditor() {

		// Setup frame
		frame = new JFrame();

		// Setup icons
		loadIcon = CreateImageIcon("/icons/locations.png");
		saveIcon = CreateImageIcon("/icons/save.png");

		// Setup Main Menu buttons
		load = new JButton("Load", loadIcon);
		save = new JButton("Save", saveIcon);
		done = new JButton("Done");

		// Add Main Menu buttons listeners
		load.addActionListener(new ListenOpenLocations());
		save.addActionListener(new ListenSaveLocationCoordinates());

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
		if (!tableModel.hasEmptyRow()) {
			tableModel.addEmptyRow();
		}

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

		// Setup frame
		frame.setJMenuBar(menu);
		frame.getContentPane().add(scrollPane);

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
					data = new ReadLocations(locationsFilename);

					for (int i = 0; i < data.nrow; i++) {

						tableModel.insertRow(i, new TableRecord(
								data.locations[i], String
										.valueOf(data.coordinates[i][0]),
								String.valueOf(data.coordinates[i][1])));

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

	public void launch() {

		// Display Frame
		// frame.setAlwaysOnTop(true);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setSize(new Dimension(300, 300));
		frame.setMinimumSize(new Dimension(100, 100));
		frame.setResizable(true);
		frame.setVisible(true);
	}// END: launch

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

	private ImageIcon CreateImageIcon(String path) {
		URL imgURL = this.getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path + "\n");
			return null;
		}
	}// END: CreateImageIcon

}// /END: class
