package checks;

import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import jebl.evolution.graphs.Node;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.trees.RootedTree;
import utils.Utils;

public class ContinuousSanityCheck {

	private boolean notNull = false;
	private String HPDString = null;

	public boolean check(String treeFilename, String coordinatesName)
			throws IOException, ImportException {

		NexusImporter importer = new NexusImporter(new FileReader(treeFilename));
		RootedTree tree = (RootedTree) importer.importNextTree();
		String longitudeName = (coordinatesName + 2);
		String latitudeName = (coordinatesName + 1);

		Double longitude = null;
		Double latitude = null;

		double nodeCount = Utils.getNodeCount(tree);
		double unannotatedNodeCount = 0;

		for (Node node : tree.getNodes()) {
			if (!tree.isRoot(node)) {

				longitude = (Double) node.getAttribute(longitudeName);
				latitude = (Double) node.getAttribute(latitudeName);

				if (longitude == null || latitude == null) {
					unannotatedNodeCount++;
				}// unannotated nodes check

				if (HPDString == null) {

					// we could check if(!tree.isExternal(node))
					Set<String> attSet = node.getAttributeNames();
					for (String name : attSet) {
						if (name.contains("HPD") && name.contains("modality")) {

							HPDString = name.split("_")[1];
							System.out.println("Found highest posterior "
									+ "density attribute: " + HPDString);

						}
					}
				}// END: if HPDString not defined

			}// END: root check
		}// END: node loop

		// TODO throw HPD attribute missing exception if HPDString == null
		if (unannotatedNodeCount == nodeCount) {

			notNull = false;
			throw new RuntimeException("Attribute, " + coordinatesName
					+ ", missing from node");

		} else if (unannotatedNodeCount == 0) {

			notNull = true;

		} else if (unannotatedNodeCount < nodeCount) {

			notNull = true;
			System.out.println("Spread detected unannotated branches "
					+ "and will continue by skipping them. Consider "
					+ "annotating all branches of your tree.");

		} else {

			notNull = false;
			throw new RuntimeException("Bad juju");

		}

		return notNull;
	}// END: check

	public String getHPDString() {
		return HPDString;
	}

}// END: class
