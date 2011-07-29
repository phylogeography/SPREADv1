// Set<String> set =node.getAttributeNames();
// for(String name: set) {
// System.out.println(name);
// }

//TODO count the number of unannotated branches
// if = total number of branches throw exception
// if < total number of branches show cancel-continue dialog

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
		
		Integer modality;
		Double longitude;
		Double latitude;
		Double nodeHeight;
		
		double nodeCount = Utils.getNodeCount(tree);

		for (Node node : tree.getNodes()) {
			if (!tree.isRoot(node)) {

				if (!tree.isExternal(node)) {

					 modality = (Integer) node
							.getAttribute(coordinatesName + "_" + HPD
									+ "HPD_modality");

//					if (modality == null) {
//						notNull = false;
//						break;
//					} else {
//						notNull = true;
//					}

				} else {// END: external nodes check

					longitude = (Double) node
							.getAttribute(longitudeName);

					latitude = (Double) node.getAttribute(latitudeName);

					nodeHeight = tree.getHeight(node);

				}// END: internal nodes check

//				if(longitude){
//					
//				}
				
				
			}// END: root check
		}// END: node loop
		return notNull;
	}//END: check

}//END: class
