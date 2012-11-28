package templates;

import java.util.ArrayList;
import java.util.List;

import jebl.evolution.graphs.Node;
import jebl.evolution.trees.RootedTree;
import utils.Utils;

public class CalculateSpatialStats implements Runnable {

	private static final boolean DEBUG = true;

	private RootedTree currentTree;
	private String coordinatesName;
	private String rateString;
	private double[] sliceHeights;
	private String precisionString;
	private boolean useTrueNoise;

	private List<Double> timesList;
	private List<Double> distancesList;

	public CalculateSpatialStats(RootedTree currentTree,
			String coordinatesName, String rateString, String precisionString,
			double[] sliceHeights, boolean useTrueNoise) {

		this.currentTree = currentTree;
		this.coordinatesName = coordinatesName;
		this.sliceHeights = sliceHeights;
		this.rateString = rateString;
		this.useTrueNoise = useTrueNoise;
		this.precisionString = precisionString;

		timesList = new ArrayList<Double>();
		distancesList = new ArrayList<Double>();

	}// END: Constructor

	@Override
	public void run() {

		try {

			// TODO: move
			// attributes parsed once per analysis
			int nSlices = sliceHeights.length;
			int lastSliceTime = nSlices - 2;

			// END: move

			// attributes parsed once per tree
			double currentTreeNormalization = Utils.getTreeLength(currentTree,
					currentTree.getRootNode());
			double[] precisionArray = Utils.getTreeDoubleArrayAttribute(
					currentTree, precisionString);

			for (Node node : currentTree.getNodes()) {
				if (!currentTree.isRoot(node)) {

					Node parentNode = currentTree.getParent(node);

					double nodeHeight = Utils.getNodeHeight(currentTree, node);
					double parentHeight = Utils.getNodeHeight(currentTree,
							parentNode);

					double branchLength = currentTree.getEdgeLength(node,
							parentNode);

					double[] location = Utils.getDoubleArrayNodeAttribute(node,
							coordinatesName);
					double[] parentLocation = Utils
							.getDoubleArrayNodeAttribute(parentNode,
									coordinatesName);

					double rate = Utils
							.getDoubleNodeAttribute(node, rateString);

					if (parentHeight <= sliceHeights[0]) {

						timesList.add(branchLength);

						double distance = Utils.rhumbDistance(location[1],// startLon
								location[0],// startLat
								parentLocation[1],// endLon
								parentLocation[0]// endLat
								);

						distancesList.add(distance);

					} else {

						// first case: 0-th slice height
						if (nodeHeight < sliceHeights[0]
								&& sliceHeights[0] <= parentHeight) {

							timesList.add(sliceHeights[0] - nodeHeight);

							double[] imputedLocation = Utils.imputeValue(
									location, parentLocation, sliceHeights[0],
									nodeHeight, parentHeight, rate,
									useTrueNoise, currentTreeNormalization,
									precisionArray);

							double distance = Utils.rhumbDistance(location[1],// startLon
									location[0],// startLat
									imputedLocation[1],// endLon
									imputedLocation[0]// endLat
									);

							distancesList.add(distance);

						} else {
							// do nothing
						}// END: 0-th slice check

						// second case: i to i+1 slice
						for (int i = 1; i <= lastSliceTime; i++) {

							if (nodeHeight < sliceHeights[i]) {

								// full branch length falls into middle slices
								if (parentHeight <= sliceHeights[i]
										&& sliceHeights[i - 1] < nodeHeight) {

									timesList.add(branchLength);

									double distance = Utils.rhumbDistance(
											location[1],// startLon
											location[0],// startLat
											parentLocation[1],// endLon
											parentLocation[0]// endLat
											);

									distancesList.add(distance);

								} else {

									double startTime = Math.max(nodeHeight,
											sliceHeights[i - 1]);
									double endTime = Math.min(parentHeight,
											sliceHeights[i]);

									if (endTime >= startTime) {

										timesList.add(endTime - startTime);

										double[] imputedLocation1 = Utils
												.imputeValue(
														location,
														parentLocation,
														startTime,
														nodeHeight,
														parentHeight,
														rate,
														useTrueNoise,
														currentTreeNormalization,
														precisionArray);

										double[] imputedLocation2 = Utils
												.imputeValue(
														location,
														parentLocation,
														endTime,
														nodeHeight,
														parentHeight,
														rate,
														useTrueNoise,
														currentTreeNormalization,
														precisionArray);

										double distance = Utils.rhumbDistance(
												imputedLocation1[1],// startLon
												imputedLocation1[0],// startLat
												imputedLocation2[1],// endLon
												imputedLocation2[0]// endLat
												);

										distancesList.add(distance);

									}// END: negative times check

								}// END: full branch in middle slice check

							}// END: i-th slice check

						}// END: i loop

						// third case: last slice height
						if (parentHeight >= sliceHeights[lastSliceTime]
								&& sliceHeights[lastSliceTime] > nodeHeight) {

							timesList.add(parentHeight
									- sliceHeights[lastSliceTime]);

							double[] imputedLocation = Utils.imputeValue(
									location, parentLocation,
									sliceHeights[lastSliceTime], nodeHeight,
									parentHeight, rate, useTrueNoise,
									currentTreeNormalization, precisionArray);

							double distance = Utils.rhumbDistance(
									imputedLocation[1],// startLon
									imputedLocation[0],// startLat
									parentLocation[1],// endLon
									parentLocation[0]// endLat
									);

							distancesList.add(distance);

						} else if (nodeHeight > sliceHeights[lastSliceTime]) {

							timesList.add(branchLength);

							double distance = Utils.rhumbDistance(location[1],// startLon
									location[0],// startLat
									parentLocation[1],// endLon
									parentLocation[0]// endLat
									);

							distancesList.add(distance);

						} else {
							// nothing to add
						}// END: last transition time check

					}// END: if branch below first transition time bail out

					if (DEBUG) {
						if (timesList.size() != distancesList.size()) {
							System.out.println("FUBAR");
						}
					}

					final double[] weights = new double[timesList.size()];
					for (int i = 0; i < timesList.size(); i++) {
						weights[i] = timesList.get(i);
					}

					final double[] distances = new double[distancesList.size()];
					for (int i = 0; i < distancesList.size(); i++) {
						distances[i] = distancesList.get(i);
					}

					if (DEBUG) {
						// System.out.println("branchLength: " + branchLength);
						// Utils.printArray(weights);
						// System.out.println("branchDistances: ");
						// Utils.printArray(distances);
						// System.out.println();
					}

				}// END: root node check
			}// END: node loop

		} catch (Exception e) {

		}// END: try-catch block

	}// END: run

}// END: class
