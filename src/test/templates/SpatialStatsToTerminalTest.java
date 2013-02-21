package test.templates;

import org.junit.Test;

import junit.framework.TestCase;
import templates.SpatialStatsToTerminal;

public class SpatialStatsToTerminalTest extends TestCase {
	
	private static boolean FIRST_ANALYSIS = false;
	
	private static SpatialStatsToTerminal spatialStatsToTerminal = new SpatialStatsToTerminal();
	
	@Test
	public static void testSpatialStatsToTerminal() throws Exception {
	

		if(FIRST_ANALYSIS) {
			
			spatialStatsToTerminal.setAnalysisType(SpatialStatsToTerminal.FIRST_ANALYSIS);
			
			spatialStatsToTerminal.setTreePath("/home/filip/Dropbox/SPREAD_dev/CustomTimeSlicing/Cent_ITS_broad.tree");
			
			spatialStatsToTerminal.setNumberOfIntervals(10);
			
		} else {
			
			spatialStatsToTerminal.setAnalysisType(SpatialStatsToTerminal.SECOND_ANALYSIS);
			
			spatialStatsToTerminal.setCustomSliceHeightsPath("/home/filip/Dropbox/SPREAD_dev/CustomTimeSlicing/treeslice_small.txt");
//			spatialStatsToTerminal.setCustomSliceHeightsPath("/home/filip/Dropbox/SPREAD_dev/CustomTimeSlicing/zeroSlice.txt");
		}	
		
		spatialStatsToTerminal.setTreesPath("/home/filip/Dropbox/SPREAD_dev/CustomTimeSlicing/Cent_ITS_small.trees");

		spatialStatsToTerminal.setBurnIn(0);
		
		spatialStatsToTerminal.setLocationAttributeName("coords");
		
		spatialStatsToTerminal.setRateAttributeName("rate");

		spatialStatsToTerminal.setPrecisionAttName("precision");

		spatialStatsToTerminal.setUseTrueNoise(false);
		
		spatialStatsToTerminal.calculate();
	
	}//END: main
	
}//END: class
