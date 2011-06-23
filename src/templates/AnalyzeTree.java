package templates;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import math.MultivariateNormalDistribution;

import jebl.evolution.graphs.Node;
import jebl.evolution.trees.RootedTree;
import structure.Coordinates;
import utils.SpreadDate;
import utils.Utils;

public class AnalyzeTree implements Runnable {

	private RootedTree currentTree;
	private String precisionString;
	private String coordinatesName;
	private String rateString;
	private int numberOfIntervals;
	private double treeRootHeight;
	private double timescaler;
	private SpreadDate mrsd;
	private ConcurrentMap<Double, List<Coordinates>> slicesMap;
	private boolean useTrueNoise;

	public AnalyzeTree(RootedTree currentTree, String precisionString,
			String coordinatesName, String rateString, int numberOfIntervals,
			double treeRootHeight, double timescaler, SpreadDate mrsd,
			ConcurrentMap<Double, List<Coordinates>> slicesMap,
			boolean useTrueNoise) throws ParseException {

		this.currentTree = currentTree;
		this.precisionString = precisionString;
		this.coordinatesName = coordinatesName;
		this.rateString = rateString;
		this.numberOfIntervals = numberOfIntervals;
		this.treeRootHeight = treeRootHeight;
		this.timescaler = timescaler;
		// TODO this is causing troubles with concurrency
		this.mrsd = mrsd;// new SpreadDate("2011-06-23 AD");
		this.slicesMap = slicesMap;
		this.useTrueNoise = useTrueNoise;

	}

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

					double nodeHeight = currentTree.getHeight(node);
					double parentHeight = currentTree.getHeight(parentNode);

					double[] location = Utils.getDoubleArrayNodeAttribute(node,
							coordinatesName);

					double[] parentLocation = Utils
							.getDoubleArrayNodeAttribute(parentNode,
									coordinatesName);

					double rate = Utils
							.getDoubleNodeAttribute(node, rateString);

					for (int i = 0; i <= numberOfIntervals; i++) {

						double sliceHeight = treeRootHeight
								- (treeRootHeight / (double) numberOfIntervals)
								* ((double) i);

						if (nodeHeight < sliceHeight
								&& sliceHeight <= parentHeight) {

							int days = (int) (sliceHeight * timescaler);
							double sliceTime = mrsd.minus(days);

							// grow map entry if key exists
							if (slicesMap.containsKey(sliceTime)) {

								double[] imputedLocation = imputeValue(
										location, parentLocation, sliceHeight,
										nodeHeight, parentHeight, rate,
										useTrueNoise, currentTreeNormalization,
										precisionArray);

								slicesMap.get(sliceTime).add(
										new Coordinates(imputedLocation[1],
												imputedLocation[0], 0.0));

								// start new entry if no such key in the map
							} else {

								List<Coordinates> coords = new ArrayList<Coordinates>();

								double[] imputedLocation = imputeValue(
										location, parentLocation, sliceHeight,
										nodeHeight, parentHeight, rate,
										useTrueNoise, currentTreeNormalization,
										precisionArray);

								coords.add(new Coordinates(imputedLocation[1],
										imputedLocation[0], 0.0));

								// slicesMap.putIfAbsent(sliceTime, coords);
								slicesMap.put(sliceTime, coords);

							}// END: key check
						}// END: sliceTime check
					}// END: numberOfIntervals loop
				}
			}// END: node loop

		} catch (Exception e) {
			e.printStackTrace();
		}

	}// END: run

	private double[] imputeValue(double[] location, double[] parentLocation,
			double sliceHeight, double nodeHeight, double parentHeight,
			double rate, boolean trueNoise, double treeNormalization,
			double[] precisionArray) {

		int dim = (int) Math.sqrt(1 + 8 * precisionArray.length) / 2;
		double[][] precision = new double[dim][dim];
		int c = 0;
		for (int i = 0; i < dim; i++) {
			for (int j = i; j < dim; j++) {
				precision[j][i] = precision[i][j] = precisionArray[c++]
						* treeNormalization;
			}
		}

		dim = location.length;
		double[] nodeValue = new double[2];
		double[] parentValue = new double[2];

		for (int i = 0; i < dim; i++) {

			nodeValue[i] = location[i];
			parentValue[i] = parentLocation[i];

		}

		final double scaledTimeChild = (sliceHeight - nodeHeight) * rate;
		final double scaledTimeParent = (parentHeight - sliceHeight) * rate;
		final double scaledWeightTotal = (1.0 / scaledTimeChild)
				+ (1.0 / scaledTimeParent);

		if (scaledTimeChild == 0)
			return location;

		if (scaledTimeParent == 0)
			return parentLocation;

		// Find mean value, weighted average
		double[] mean = new double[dim];
		double[][] scaledPrecision = new double[dim][dim];

		for (int i = 0; i < dim; i++) {
			mean[i] = (nodeValue[i] / scaledTimeChild + parentValue[i]
					/ scaledTimeParent)
					/ scaledWeightTotal;

			if (trueNoise) {
				for (int j = i; j < dim; j++)
					scaledPrecision[j][i] = scaledPrecision[i][j] = precision[i][j]
							* scaledWeightTotal;
			}
		}

		if (trueNoise) {
			mean = MultivariateNormalDistribution
					.nextMultivariateNormalPrecision(mean, scaledPrecision);
		}

		double[] result = new double[dim];
		for (int i = 0; i < dim; i++)
			result[i] = mean[i];

		return result;
	}// END: ImputeValue

}// END: AnalyzeTree