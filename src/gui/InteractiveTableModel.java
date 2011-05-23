package gui;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class InteractiveTableModel extends AbstractTableModel {

	public static final int LOCATION_INDEX = 0;
	public static final int LONGITUDE_INDEX = 1;
	public static final int LATITUDE_INDEX = 2;
	public static final int HIDDEN_INDEX = 3;

	private String[] columnNames;
	private Vector<TableRecord> dataVector;

	public InteractiveTableModel(String[] columnNames) {
		this.columnNames = columnNames;
		dataVector = new Vector<TableRecord>();
	}

	public String getColumnName(int column) {
		return columnNames[column];
	}

	public boolean isCellEditable(int row, int column) {
		if (column == HIDDEN_INDEX)
			return false;
		else
			return true;
	}

	public Class<?> getColumnClass(int column) {
		switch (column) {
		case LOCATION_INDEX:
		case LONGITUDE_INDEX:
		case LATITUDE_INDEX:
			return String.class;
		default:
			return Object.class;
		}
	}

	public Object getValueAt(int row, int column) {
		TableRecord record = (TableRecord) dataVector.get(row);
		switch (column) {
		case LOCATION_INDEX:
			return record.getLocation();
		case LONGITUDE_INDEX:
			return record.getLongitude();
		case LATITUDE_INDEX:
			return record.getLatitude();
		default:
			return new Object();
		}
	}

	public void setValueAt(Object value, int row, int column) {
		TableRecord record = (TableRecord) dataVector.get(row);
		switch (column) {
		case LOCATION_INDEX:
			record.setLocation((String) value);
			break;
		case LONGITUDE_INDEX:
			record.setLongitude((String) value);
			break;
		case LATITUDE_INDEX:
			record.setLatitude((String) value);
			break;
		default:
			System.out.println("invalid index");
		}
		fireTableCellUpdated(row, column);
	}

	public int getRowCount() {
		return dataVector.size();
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	// This appends a row
	public void addRow(TableRecord row) {
		dataVector.add(row);
		this.fireTableDataChanged();
	}

	public void delRow(int row) {
		dataVector.remove(row);
		this.fireTableDataChanged();
	}

	// This overwrites a row at specified index
	public void insertRow(int index, TableRecord row) {
		dataVector.add(index, row);
		this.fireTableDataChanged();
	}

	public boolean hasEmptyRow() {

		if (dataVector.size() == 0)
			return false;
		TableRecord tableRecord = (TableRecord) dataVector.get(dataVector
				.size() - 1);
		if (tableRecord.getLocation().trim().equals("")
				&& tableRecord.getLongitude().trim().equals("")
				&& tableRecord.getLatitude().trim().equals("")) {
			return true;
		} else
			return false;
	}

	public void addEmptyRow() {
		dataVector.add(new TableRecord());
		fireTableRowsInserted(dataVector.size() - 1, dataVector.size() - 1);
	}

}
