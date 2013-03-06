package templates;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import readers.SliceHeightsReader;

import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.io.TreeImporter;
import jebl.evolution.trees.RootedTree;
import utils.Utils;

public class SpatialStatsToTerminal {

	public long time;

	private int analysisType;
	public final static int FIRST_ANALYSIS = 1;
	public final static int SECOND_ANALYSIS = 2;

	private RootedTree tree;
	private double treeRootHeight;
	private double[] sliceHeights;
	private int numberOfIntervals;
	private TreeImporter treeImporter;
	private TreeImporter treesImporter;
	private RootedTree currentTree;
	private int burnIn;
	private boolean useTrueNoise;
	private String coordinatesName;
	private String rateString;
	private String precisionString;

	public SpatialStatsToTerminal() {
	}// END: Constructor

	public void setAnalysisType(int analysisType) {
		this.analysisType = analysisType;
	}

	public void setNumberOfIntervals(int numberOfIntervals) {
		this.numberOfIntervals = numberOfIntervals;
	}

	public void setTreePath(String path) throws FileNotFoundException {
		treeImporter = new NexusImporter(new FileReader(path));
	}

	public void setTreesPath(String path) throws FileNotFoundException {
		treesImporter = new NexusImporter(new FileReader(path));
	}

	public void setBurnIn(int burnIn) {
		this.burnIn = burnIn;
	}

	public void setLocationAttributeName(String name) {
		coordinatesName = name;
	}

	public void setRateAttributeName(String name) {
		rateString = name;
	}

	public void setPrecisionAttName(String name) {
		precisionString = name;
	}

	public void setUseTrueNoise(boolean useTrueNoise) {
		this.useTrueNoise = useTrueNoise;
	}

	public void setCustomSliceHeightsPath(String path) {
		sliceHeights = new SliceHeightsReader(path).getSliceHeights();
	}

	public void calculate() {

		try {

			// start timing
			time = -System.currentTimeMillis();

			switch (analysisType) {
			case FIRST_ANALYSIS:
				tree = (RootedTree) treeImporter.importNextTree();
				treeRootHeight = Utils.getNodeHeight(tree, tree.getRootNode());
				sliceHeights = Utils.generateTreeSliceHeights(treeRootHeight,
						numberOfIntervals);
				break;
			case SECOND_ANALYSIS:
				break;
			}// END: switch on analysisType

			// sort them in ascending numerical order
			Arrays.sort(sliceHeights);
			
			System.out.println("Using as slice times: ");
			Utils.printArray(sliceHeights);
			System.out.println();

			// Executor for threads
			int NTHREDS = Runtime.getRuntime().availableProcessors();
			ExecutorService executor = Executors.newFixedThreadPool(NTHREDS * 2);

			int treesAssumed = 10000;
			int treesRead = 0;

			System.out.println("Analyzing trees (bar assumes 10,000 trees)");
			System.out.println("0                   25                  50                  75                 100");
			System.out.println("|---------------------|---------------------|---------------------|---------------------|");
			// System.out.println("0              25             50             75            100");
			// System.out.println("|--------------|--------------|--------------|--------------|");
			
			int stepSize = treesAssumed / 60;
			if (stepSize < 1) {
				stepSize = 1;
			}

			int totalTrees = 0;
			List<Double> treesRatesList = new ArrayList<Double>();
			while (treesImporter.hasTree()) {

				currentTree = (RootedTree) treesImporter.importNextTree();

				if (totalTrees >= burnIn) {

					CalculateTreeSpatialStats calculateTreeSpatialStats = new CalculateTreeSpatialStats(currentTree,//
							coordinatesName, //
							rateString, //
							precisionString,//
							sliceHeights, //
							useTrueNoise //
							);
					
					calculateTreeSpatialStats.run();
//					executor.submit(calculateTreeSpatialStats);

					treesRatesList.add(calculateTreeSpatialStats.getTreeRate());
					treesRead += 1;

				}// END: if burn-in

				if (totalTrees > 0 && totalTrees % stepSize == 0) {
					System.out.print("*");
					System.out.flush();
				}

				totalTrees++;

			}// END: while has trees

			// Wait until all threads are finished
			executor.shutdown();
			while (!executor.isTerminated()) {
			}

			if ((totalTrees - burnIn) <= 0.0) {
				throw new RuntimeException("Burnt too many trees!");
			} else {
				System.out.println("\nAnalyzed " + treesRead
						+ " trees with burn-in of " + burnIn
						+ " for the total of " + totalTrees + " trees");
			}
			
//			System.out.println("rate statistic: ");
			
			
			// stop timing
			time += System.currentTimeMillis();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ImportException e) {
			e.printStackTrace();
		}// END: try-catch block

	}// END: calculate

}// END: class
