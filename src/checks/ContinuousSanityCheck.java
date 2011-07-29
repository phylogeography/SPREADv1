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
	private String longitudeName;
	private String latitudeName;

	public boolean check(String treeFilename, String coordinatesName, String HPD)
			throws IOException, ImportException {

		NexusImporter importer = new NexusImporter(new FileReader(treeFilename));
		RootedTree tree = (RootedTree) importer.importNextTree();
		longitudeName = (coordinatesName + 2);
		latitudeName = (coordinatesName + 1);

		for (Node node : tree.getNodes()) {
			if (!tree.isRoot(node)) {
				if (!tree.isExternal(node)) {

					System.out.println("HERE");
					// Set<String> set =node.getAttributeNames();
					// for(String name: set) {
					// System.out.println(name);
					// }
					System.out.println();

					Double longitude = Utils.getDoubleNodeAttribute(node,
							longitudeName);
					Double latitude = Utils.getDoubleNodeAttribute(node,
							latitudeName);
					Double nodeHeight = tree.getHeight(node);

//					Node parentNode = tree.getParent(node);
//
//					Double parentLongitude = (Double) parentNode
//							.getAttribute(longitudeName);
//					Double parentLatitude = (Double) parentNode
//							.getAttribute(latitudeName);
//
//					Double parentHeight = tree.getHeight(parentNode);

					Integer modality = Utils.getIntegerNodeAttribute(node,
							coordinatesName + "_" + HPD + "HPD_modality");

					if (modality == null) {
						notNull = false;
						break;
					} else {
						notNull = true;
					}
				}
			}
		}// END: node loop
		return notNull;
	}

}
