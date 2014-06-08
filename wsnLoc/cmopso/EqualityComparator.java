package wsnLoc.cmopso;

import java.util.Comparator;

@SuppressWarnings("rawtypes")
public class EqualityComparator implements Comparator{
	
	public int compare(Object object1, Object object2) {
		
		Particle solution1 = (Particle) object1;
		Particle solution2 = (Particle) object2;

		double averageRangesUsedS1 = solution1.fitness.sumRanges/solution1.fitness.localizedNodesNumber;
		double averageRangesUsedS2 = solution2.fitness.sumRanges/solution2.fitness.localizedNodesNumber;
		
		//if energy consumption of solution1 is less than solution 2 then 1 wins
		if (averageRangesUsedS1 < averageRangesUsedS2) {
			return -1; // solution1 dominate
		}
			
		else if (averageRangesUsedS1 > averageRangesUsedS2) {
			return 1; // solution2 dominate
		}
			
		else {
			//the two solutions are typical
			return 0; // No one dominate the other
		}
	}
}
