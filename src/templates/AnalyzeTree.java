package templates;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import jebl.evolution.graphs.Node;
import jebl.evolution.trees.RootedTree;
import structure.Coordinates;
import utils.ThreadLocalSpreadDate;
import utils.Utils;

public class AnalyzeTree implements Runnable {

	private static final int DaysInYear = 365;

	private RootedTree currentTree;
	private String precisionString;
	private String coordinatesName;
	private String rateString;
	private double timescaler;
	private double[] sliceHeights;
	private ThreadLocalSpreadDate mrsd;
	private ConcurrentMap<Double, List<Coordinates>> slicesMap;
	private boolean useTrueNoise;

	public AnalyzeTree(RootedTree currentTree, String precisionString,
			String coordinatesName, String rateString, double[] sliceHeights,
			double timescaler, ThreadLocalSpreadDate mrsd,
			ConcurrentMap<Double, List<Coordinates>> slicesMap,
			boolean useTrueNoise) {

		this.currentTree = currentTree;
		this.precisionString = precisionString;
		this.coordinatesName = coordinatesName;
		this.rateString = rateString;
		this.sliceHeights = sliceHeights;
		this.timescaler = timescaler;
		this.mrsd = mrsd;
		this.slicesMap = slicesMap;
		this.useTrueNoise = useTrueNoise;

	}// END: Constructor

	public void run() throws ConcurrentModificationException {

		try {

			// attributes parsed once per tree
			double currentTreeNormalization = Utils.getTreeLength(currentTree,
					currentTree.getRootNode());
			double[] precisionArray = Utils.getTreeDoubleArrayAttribute(
					currentTree, precisionString);

			for (Node node : currentTree.getNodes()) {
				if (!currentTree.isRoot(node)) {

					// attributes parsed once per node
					Node parentNode = currentTree.getParent(node);

					double nodeHeight = Utils.getNodeHeight(currentTree, node);
					double parentHeight = Utils.getNodeHeight(currentTree,
							parentNode);

					double[] location = Utils.getDoubleArrayNodeAttribute(node,
							coordinatesName);

					double[] parentLocation = Utils
							.getDoubleArrayNodeAttribute(parentNode,
									coordinatesName);

					double rate = Utils
							.getDoubleNodeAttribute(node, rateString);

					for (int i = 0; i < sliceHeights.length; i++) {

						double sliceHeight = sliceHeights[i];
						
						if (nodeHeight < sliceHeight && sliceHeight <= parentHeight) {

							double[] imputedLocation = Utils.imputeValue(location,
									parentLocation, sliceHeight, nodeHeight,
									parentHeight, rate, useTrueNoise,
									currentTreeNormalization, precisionArray);

							// calculate key
							int days = (int) (sliceHeight * DaysInYear * timescaler);
							double sliceTime = mrsd.minus(days);
							
							// grow map entry if key exists
							if (slicesMap.containsKey(sliceTime)) {

								slicesMap.get(sliceTime).add(
										new Coordinates(imputedLocation[1], // longitude
												imputedLocation[0], // latitude
												0.0 // altitude
										));

								// start new entry if no such key in the map
							} else {

								List<Coordinates> coords = new ArrayList<Coordinates>();
								coords.add(new Coordinates(imputedLocation[1], // longitude
										imputedLocation[0], // latitude
										0.0 // altitude
								));

								slicesMap.put(sliceTime, coords);

							}// END: key check
							
						}// END: sliceTime check

					}// END: numberOfIntervals loop
				}// END: root node check
			}// END: node loop

			 System.exit(-1);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}// END: run

}// END: AnalyzeTree
