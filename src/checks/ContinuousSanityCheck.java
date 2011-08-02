// Set<String> set =node.getAttributeNames();
// for(String name: set) {
// System.out.println(name);
// }

package checks;

import java.io.FileReader;
import java.io.IOException;

import jebl.evolution.graphs.Node;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.trees.RootedTree;
import utils.Utils;

public class ContinuousSanityCheck {

	private boolean notNull = false;

	public boolean check(String treeFilename, String coordinatesName, String HPD)
			throws IOException, ImportException {

		NexusImporter importer = new NexusImporter(new FileReader(treeFilename));
		RootedTree tree = (RootedTree) importer.importNextTree();
		String longitudeName = (coordinatesName + 2);
		String latitudeName = (coordinatesName + 1);
		String modalityName = coordinatesName + "_" + HPD + "HPD_modality";

		Double longitude = null;
		Double latitude = null;
		Integer modality;

		double nodeCount = Utils.getNodeCount(tree);
		double iternalNodeCount = nodeCount - Utils.getExternalNodeCount(tree);
		double unannotatedNodeCount = 0;
		double unannotatedIternalNodeCount = 0;

		for (Node node : tree.getNodes()) {
			if (!tree.isRoot(node)) {

				if (!tree.isExternal(node)) {

					modality = (Integer) node.getAttribute(modalityName);

					if (modality == null) {
						unannotatedIternalNodeCount++;
					}// unannotated internal nodes check

				}// END: internal node check

				longitude = (Double) node.getAttribute(longitudeName);

				latitude = (Double) node.getAttribute(latitudeName);

				if (longitude == null || latitude == null) {
					unannotatedNodeCount++;
				}// unannotated nodes check

			}// END: root check
		}// END: node loop

		if (unannotatedNodeCount == nodeCount) {
			notNull = false;
			throw new RuntimeException("Attribute, " + coordinatesName
					+ ", missing from node");

		} else if (unannotatedIternalNodeCount == iternalNodeCount) {

			notNull = false;
			throw new RuntimeException("Attribute, " + modalityName
					+ ", missing from node");

		} else if (unannotatedNodeCount == 0
				&& unannotatedIternalNodeCount == 0) {

			notNull = true;

		} else if (unannotatedNodeCount < nodeCount
				|| unannotatedIternalNodeCount < iternalNodeCount) {

			notNull = true;
			System.out.println("Spread detected unannotated branches "
					+ "and will continue by skipping them. Consider "
					+ "annotating all of the branches of your tree.");

		} else {

			notNull = false;
			throw new RuntimeException("Bad juju");

		}

		return notNull;
	}// END: check

}// END: class
