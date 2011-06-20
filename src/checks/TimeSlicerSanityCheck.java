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

		RootedTree tree = (RootedTree) new NexusImporter(new FileReader(
				treeFilename)).importNextTree();

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

		RootedTree currentTree = (RootedTree) new NexusImporter(new FileReader(
				treesFilename)).importNextTree();

		for (Node node : currentTree.getNodes()) {

			if (!currentTree.isRoot(node)) {

				Object[] location = (Object[]) Utils.getObjectArrayNodeAttribute(
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
