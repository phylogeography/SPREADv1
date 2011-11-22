package checks;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import templates.TimeSlicerToKML;
import utils.Utils;

import jebl.evolution.graphs.Node;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.trees.RootedTree;

public class TimeSlicerSanityCheck {

	private boolean notNull = false;

	public boolean check(String treeFilename, String coordinatesName,
			String treesFilename, int analysisType) throws IOException,
			ImportException {

		switch (analysisType) {
		case TimeSlicerToKML.FIRST_ANALYSIS:
			if (checkMccTree(treeFilename, coordinatesName)
					&& checkFirstPosteriorTree(treesFilename, coordinatesName)) {
				notNull = true;
			}
			break;

		case TimeSlicerToKML.SECOND_ANALYSIS:
			if (checkFirstPosteriorTree(treesFilename, coordinatesName)) {
				// TODO: sanity check for slice heights here
				notNull = true;
			}
			break;

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

				Double latitude = (Double) node
						.getAttribute(coordinatesName + 1);

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

		} else if (unannotatedNodeCount < nodeCount) {

			notNull = true;
			System.out.println("Spread detected unannotated branches "
					+ "and will continue by skipping them. Consider "
					+ "annotating all of the branches of your MCC tree.");

		} else {

			notNull = false;
			throw new RuntimeException("Bad juju");

		}

		return flag;
	}// END: checkMccTree

	private boolean checkFirstPosteriorTree(String treesFilename,
			String coordinatesName) throws FileNotFoundException, IOException,
			ImportException {

		boolean flag = false;

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

	// TODO sanity check for sliceHeights (are they sorted? Is the max ==
	// tree.height?)

}// END: class
