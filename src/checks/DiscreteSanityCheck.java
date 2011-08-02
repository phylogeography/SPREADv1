package checks;

import gui.InteractiveTableModel;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import jebl.evolution.graphs.Node;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.trees.RootedTree;
import utils.Utils;

public class DiscreteSanityCheck {

	private boolean notNull = false;

	public boolean check(String treeFilename, String stateAttName,
			InteractiveTableModel table) throws IOException, ImportException,
			ParseException {

		NexusImporter importer = new NexusImporter(new FileReader(treeFilename));
		RootedTree tree = (RootedTree) importer.importNextTree();

		String uniqueState;

		double nodeCount = Utils.getNodeCount(tree);
		double unannotatedNodeCount = 0;

		Set<String> uniqueTreeStates = new HashSet<String>();
		for (Node node : tree.getNodes()) {
			if (!tree.isRoot(node)) {

				uniqueState = (String) node.getAttribute(stateAttName);
				uniqueTreeStates.add(uniqueState);

				if (uniqueState == null) {
					unannotatedNodeCount++;
				}// unannotated internal nodes check

			}// END: root check
		}// END: node loop

		if (unannotatedNodeCount == nodeCount) {
			notNull = false;
			throw new RuntimeException("Attribute, " + stateAttName
					+ ", missing from node");

		} else if (unannotatedNodeCount == 0) {
			notNull = true;

		} else if (unannotatedNodeCount < nodeCount) {
			notNull = true;
			// TODO show unannotated branches dialog
			System.out.println("Spread detected unannotated branches "
					+ "and will continue by skipping them. Consider "
					+ "annotating all of the branches of your tree.");
		} else {
			notNull = false;
			throw new RuntimeException("Bad juju");
		}

		fitLocations(table, uniqueTreeStates);

		return notNull;
	}// END: check()

	private void fitLocations(InteractiveTableModel table,
			Set<String> uniqueTreeStates) {

		Object[] uniqueTreeStatesArray = uniqueTreeStates.toArray();
		for (int i = 0; i < table.getRowCount(); i++) {

			String state = null;

			for (int j = 0; j < uniqueTreeStatesArray.length; j++) {

				String name = String.valueOf(table.getValueAt(i, 0));

				if (name.toLowerCase().equals(
						((String) uniqueTreeStatesArray[j]).toLowerCase())) {

					state = name;

				}// END: if location and discrete states match

			}// END: unique discrete states loop

			if (state == null) { // if none matches
				System.out.println("Location "
						+ String.valueOf(table.getValueAt(i, 0))
						+ " does not fit any of the discrete states");
			}

		}// END: locations loop
	}// END: fitLocations()

}// END: class
