package templates;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import structure.Coordinates;
import utils.Utils;

public class CalculateSpatialStats {

	//461: Snyder
	private ConcurrentMap<Double, List<Coordinates>> slicesMap;
	
	public CalculateSpatialStats(ConcurrentMap<Double, List<Coordinates>> slicesMap) {
		
		this.slicesMap = slicesMap;
		
	}//END: Constructor
	
	public void calculate() {
		
		Utils.printHashMap(slicesMap);
		
	}//END: calculate

	
}//END: class
