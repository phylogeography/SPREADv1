package checks;

import java.io.FileReader;
import java.io.IOException;

import jebl.evolution.graphs.Node;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.trees.RootedTree;
import utils.Utils;

public class ContinuousSanityCheck {

	public boolean check(String treeFilename, String coordinatesName, String HPD)
			throws IOException, ImportException {

		NexusImporter importer = new NexusImporter(new FileReader(treeFilename));
		RootedTree tree = (RootedTree) importer.importNextTree();

		boolean isNull = false;

		for (Node node : tree.getNodes()) {
			if (!tree.isRoot(node)) {
				if (!tree.isExternal(node)) {

					Integer modality = Utils.getIntegerNodeAttribute(node,
							coordinatesName + "_" + HPD + "HPD_modality");

					if (modality == null) {
						isNull = false;
						break;
					} else {
						isNull = true;
					}
				}
			}
		}// END: node loop
		return isNull;
	}

}
