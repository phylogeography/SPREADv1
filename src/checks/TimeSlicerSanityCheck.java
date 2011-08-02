package checks;

import java.io.FileNotFoundException;
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

		if (checkMccTree(treeFilename, coordinatesName)
				&& checkFirstPosteriorTree(treesFilename, coordinatesName)) {

			notNull = true;

		}

		return notNull;
	}// END: check

	private boolean checkMccTree(String treeFilename, String coordinatesName)
			throws FileNotFoundException, IOException, ImportException {

		RootedTree tree = (RootedTree) new NexusImporter(new FileReader(
				treeFilename)).importNextTree();
		boolean flag = false;
		double nodeCount = Utils.getNodeCount(tree);
		double unannotatedNodeCount = 0;

		for (Node node : tree.getNodes()) {
			if (!tree.isRoot(node)) {

				Double longitude = (Double) node
						.getAttribute(coordinatesName + 2);

				Double latitude = (Double) node.getAttribute(coordinatesName + 1);

				if (longitude == null || latitude == null) {
					unannotatedNodeCount++;
				}// unannotated nodes check

			}// END: root check
		}// END: node loop

		if (unannotatedNodeCount == nodeCount) {
			flag = false;
			throw new RuntimeException("Attribute, " + coordinatesName
					+ ", missing from node");

		} else if (unannotatedNodeCount == 0) {
			flag = true;
		}

		return flag;
	}// END: checkMccTree

	private boolean checkFirstPosteriorTree(String treesFilename,
			String coordinatesName) throws FileNotFoundException, IOException,
			ImportException {

		boolean flag = false;

		// TODO change that
		RootedTree currentTree = (RootedTree) new NexusImporter(new FileReader(
				treesFilename)).importNextTree();

		for (Node node : currentTree.getNodes()) {
			if (!currentTree.isRoot(node)) {

				Object[] location = (Object[]) Utils
						.getObjectArrayNodeAttribute(node, coordinatesName);

				if (location == null) {
					flag = false;
					break;
				} else {
					flag = true;
				}

			}// END: root check
		}// END: node loop

		return flag;
	}// END: checkFirstPosteriorTree

}// END: class
