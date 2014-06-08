package wsnLoc.bmopso;

import java.util.Comparator;

@SuppressWarnings("rawtypes")
public class EqualityComparator implements Comparator{
	
	public int compare(Object object1, Object object2) {
		
		Particle solution1 = (Particle) object1;
		Particle solution2 = (Particle) object2;

		//if energy consumption of solution1 is less than solution 2 then 1 wins
		if (solution1.fitness.energyConsumption < solution2.fitness.energyConsumption) {
			return -1; // solution1 dominate
		}
			
		else if (solution1.fitness.energyConsumption > solution2.fitness.energyConsumption) {
			return 1; // solution2 dominate
		}
			
		else {
			//the two solutions are typical
			return 0; // No one dominate the other
		}
	}
}
