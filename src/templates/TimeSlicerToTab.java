package templates;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import math.MultivariateNormalDistribution;

import jebl.evolution.graphs.Node;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.io.TreeImporter;
import jebl.evolution.trees.RootedTree;
import structure.Coordinates;
import utils.Utils;

public class TimeSlicerToTab {

	
	public static void main(String args[]) {
		
		try {
		
			Map<Double, List<double[]>> slicesMap = new HashMap<Double, List<double[]>>();
			
			double[] sliceHeights = new double[] {1,2,3,4,5};
			
		String path="/home/filip/Dropbox/WNV_drift_hom_het.trees";
		TreeImporter treesImporter;
			treesImporter = new NexusImporter(new FileReader(path));
		int totalTrees = 0;
		while (treesImporter.hasTree()) {

			RootedTree	currentTree = (RootedTree) treesImporter.importNextTree();

			double currentTreeNormalization = Utils.getTreeLength(currentTree,
					currentTree.getRootNode());
			
			double[] precisionArray = Utils.getTreeDoubleArrayAttribute(
					currentTree, "precision");

			for (Node node : currentTree.getNodes()) {
				if (!currentTree.isRoot(node)) {
					
					
					Node parentNode = currentTree.getParent(node);
					
					double nodeHeight = Utils.getNodeHeight(currentTree, node);
					double parentHeight = Utils.getNodeHeight(currentTree,
							parentNode);

					
					double trait1 = Utils.getDoubleNodeAttribute(node, "location.driftModels.1", 0.0);
					double parentTrait1 = Utils.getDoubleNodeAttribute(parentNode, "location.driftModels.1", 0.0);
					
					double trait2 = Utils.getDoubleNodeAttribute(node, "location.driftModels.2", 0.0);
					double parentTrait2 = Utils.getDoubleNodeAttribute(parentNode, "location.driftModels.2", 0.0);
					
					double[] trait = new double[2];
					trait[0] = trait1;
					trait[1] = trait2;
					
					double[] parentTrait = new double[2];
					parentTrait[0] = parentTrait1;
					parentTrait[1] = parentTrait2;
					
					
					double rate = Utils
							.getDoubleNodeAttribute(node, "rate");
					
					
					for (int i = 0; i < sliceHeights.length; i++) {
						
						
						double sliceHeight = sliceHeights[i];
						
						if (nodeHeight < sliceHeight && sliceHeight <= parentHeight) {
							
							System.out.println("HIT");
							
							
							double[] imputedTrait = imputeValue(trait,
									parentTrait, sliceHeight, nodeHeight,
									parentHeight, rate,
									currentTreeNormalization, precisionArray);
							
							Utils.printArray(imputedTrait);
							
							
							// grow map entry if key exists
							if (slicesMap.containsKey(sliceHeight)) {

								slicesMap.get(sliceHeight).add(
										new double[]{imputedTrait[0], // 
												imputedTrait[1] // 
										});

								// start new entry if no such key in the map
							} else {

								List<double[]> traits = new ArrayList<double[]>();
								traits.add(	new double[]{imputedTrait[0], // 
										imputedTrait[1] // 
								});

								slicesMap.put(sliceHeight, traits);

							}// END: key check

							
							
							
							
							
						}// END: sliceTime check
						
						
					}//END: slices loop
					
					
					
					
					
					
					
				}//END: root check
				}//END: nodes loop
			
			

		}//END: trees loop

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}//END: main
	
	private static double[] imputeValue(double[] trait, double[] parentTrait,
			double sliceHeight, double nodeHeight, double parentHeight,
			double rate, double treeNormalization,
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

		dim = trait.length;
		double[] nodeValue = new double[2];
		double[] parentValue = new double[2];

		for (int i = 0; i < dim; i++) {

			nodeValue[i] = trait[i];
			parentValue[i] = parentTrait[i];

		}

		final double scaledTimeChild = (sliceHeight - nodeHeight) * rate;
		final double scaledTimeParent = (parentHeight - sliceHeight) * rate;
		final double scaledWeightTotal = (1.0 / scaledTimeChild)
				+ (1.0 / scaledTimeParent);

		if (scaledTimeChild == 0)
			return trait;

		if (scaledTimeParent == 0)
			return parentTrait;

		// Find mean value, weighted average
		double[] mean = new double[dim];
		double[][] scaledPrecision = new double[dim][dim];

		for (int i = 0; i < dim; i++) {
			mean[i] = (nodeValue[i] / scaledTimeChild + parentValue[i]
					/ scaledTimeParent)
					/ scaledWeightTotal;

				for (int j = i; j < dim; j++)
					scaledPrecision[j][i] = scaledPrecision[i][j] = precision[i][j]
							* scaledWeightTotal;
		}

			mean = MultivariateNormalDistribution
					.nextMultivariateNormalPrecision(mean, scaledPrecision);

		double[] result = new double[dim];
		for (int i = 0; i < dim; i++) {
			result[i] = mean[i];
		}

		return result;
	}// END: ImputeValue

}// END: class
