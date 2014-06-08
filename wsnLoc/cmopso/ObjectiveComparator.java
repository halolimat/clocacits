package wsnLoc.cmopso;

import java.util.Comparator;

@SuppressWarnings("rawtypes")
public class ObjectiveComparator implements Comparator{

	private int objectiveNumber;

	public ObjectiveComparator(int objectiveNumber) {
		this.objectiveNumber = objectiveNumber;
	}

	public int compare(Object o1, Object o2) {
		
		if (o1 == null)
			return 1;
		
		else if (o2 == null)
			return -1;
		
		double objetive1 = 0;
		double objetive2 = 0;
		
		if(objectiveNumber == 0){
			objetive1 = ((Particle) o1).getObjective(0);
			objetive2 = ((Particle) o2).getObjective(0);
			
			if (objetive1 < objetive2) {
				return -1;
			} else if (objetive1 > objetive2) {
				return 1;
			} else {
				return 0;
			}
		}
		
		else {
			
			objetive1 = ((Particle) o1).getObjective(1);
			objetive2 = ((Particle) o2).getObjective(1);

			//if number of localized nodes in particle 1 is more then it dominate the other one
			if (objetive1 > objetive2) {
				return -1;
			} else if (objetive1 < objetive2) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}
