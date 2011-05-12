package checks;

import java.io.FileReader;
import java.io.IOException;

import utils.Utils;

import jebl.evolution.graphs.Node;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.trees.RootedTree;

public class TimeSlicerSanityCheck {

	private boolean notNull = false;

	public boolean check(String treeFilename, String coordinatesName,
			String treesFilename) throws IOException, ImportException {

		NexusImporter treeImporter = new NexusImporter(new FileReader(
				treeFilename));
		RootedTree tree = (RootedTree) treeImporter.importNextTree();

		for (Node node : tree.getNodes()) {
			if (!tree.isRoot(node)) {

				Float longitude = (float) Utils.getDoubleNodeAttribute(node,
						coordinatesName + 1);

				Float latitude = (float) Utils.getDoubleNodeAttribute(node,
						coordinatesName + 1);

				if (longitude == null || latitude == null) {
					notNull = false;
					break;
				} else {
					notNull = true;
				}

			}

		}// END: node loop

		NexusImporter treesImporter = new NexusImporter(new FileReader(
				treesFilename));
		RootedTree currentTree = (RootedTree) treesImporter.importNextTree();

		for (Node node : currentTree.getNodes()) {

			if (!currentTree.isRoot(node)) {

				Object[] location = (Object[]) Utils.getArrayNodeAttribute(
						node, coordinatesName);

				if (location == null) {
					notNull = false;
					break;
				} else {
					notNull = true;
				}
			}
		}// END: node loop

		return notNull;
	}

}
