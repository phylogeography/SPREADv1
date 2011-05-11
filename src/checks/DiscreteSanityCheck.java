package checks;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import jebl.evolution.graphs.Node;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.trees.RootedTree;
import utils.ReadLocations;
import utils.Utils;

public class DiscreteSanityCheck {

	private boolean isNull = false;

	public boolean check(String treeFilename, String stateAttName, String path)
			throws IOException, ImportException, ParseException {

		NexusImporter importer = new NexusImporter(new FileReader(treeFilename));
		RootedTree tree = (RootedTree) importer.importNextTree();
		ReadLocations data = new ReadLocations(path);

		Set<String> uniqueTreeStates = new HashSet<String>();
		for (Node node : tree.getNodes()) {
			if (!tree.isRoot(node)) {

				String uniqueTreeState = Utils.getStringNodeAttribute(node,
						stateAttName);
				uniqueTreeStates.add(uniqueTreeState);

				if (uniqueTreeState == null) {
					isNull = false;
					break;
				} else {
					isNull = true;
				}

			}
		}// END: node loop

		Object[] uniqueTreeStatesArray = uniqueTreeStates.toArray();

		for (int i = 0; i < data.locations.length; i++) {

			String state = null;

			for (int j = 0; j < uniqueTreeStatesArray.length; j++) {

				if (data.locations[i].toLowerCase().equals(
						((String) uniqueTreeStatesArray[j]).toLowerCase())) {

					state = data.locations[i];

				}// END: if location and discrete states match

			}// END: unique discrete states loop

			if (state == null) { // if none matches
				System.out.println("Location " + data.locations[i]
						+ " does not fit any of the discrete states");
			}
		}// END: locations loop
		return isNull;

	}

}
