package test.templates;

import org.junit.Test;

import junit.framework.TestCase;
import templates.SpatialStatsToTerminal;

public class SpatialStatsToTerminalTest extends TestCase {
	
	
	@Test
	public static void testSpatialStatsToTerminal() throws Exception {
		boolean FIRST_ANALYSIS = false;
		
		SpatialStatsToTerminal spatialStatsToTerminal = new SpatialStatsToTerminal();
	

		if(FIRST_ANALYSIS) {
			
			spatialStatsToTerminal.setAnalysisType(SpatialStatsToTerminal.FIRST_ANALYSIS);
			
			spatialStatsToTerminal.setTreePath(TestUtils.getResourcePath("/data/Cent_ITS_broad.tree"));
			
			spatialStatsToTerminal.setNumberOfIntervals(10);
			
		} else {
			
			spatialStatsToTerminal.setAnalysisType(SpatialStatsToTerminal.SECOND_ANALYSIS);
			
			spatialStatsToTerminal.setCustomSliceHeightsPath(TestUtils.getResourcePath("src/data/treeslice_small.txt"));
//			spatialStatsToTerminal.setCustomSliceHeightsPath("/home/filip/Dropbox/SPREAD_dev/CustomTimeSlicing/zeroSlice.txt");
		}	
		
		spatialStatsToTerminal.setTreesPath(TestUtils.getResourcePath("src/data/Cent_ITS_small.trees"));

		spatialStatsToTerminal.setBurnIn(0);
		
		spatialStatsToTerminal.setLocationAttributeName("coords");
		
		spatialStatsToTerminal.setRateAttributeName("rate");

		spatialStatsToTerminal.setPrecisionAttName("precision");

		spatialStatsToTerminal.setUseTrueNoise(false);
		
		spatialStatsToTerminal.calculate();
	
	}//END: main
	
}//END: class
