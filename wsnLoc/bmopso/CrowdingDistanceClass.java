package wsnLoc.bmopso;

import java.util.ArrayList;
import java.util.Collections;

public class CrowdingDistanceClass {

	@SuppressWarnings("unchecked")
	public ArrayList<Particle> crowdingDistanceAssignment(ArrayList<Particle> leaders, int nObjs) {

		int size = leaders.size();

		if (size == 0)
			return leaders;

		if (size == 1) {
			leaders.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
			return leaders;
		}

		if (size == 2) {
			leaders.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
			leaders.get(1).setCrowdingDistance(Double.POSITIVE_INFINITY);
			return leaders;
		}

		ArrayList<Particle> front = new ArrayList<Particle>();

		for (int i = 0; i < size; i++) {
			front.add(leaders.get(i));
		}

		for (int i = 0; i < size; i++)
			front.get(i).setCrowdingDistance(0.0);

		double objetiveMaxn;
		double objetiveMinn;
		double distance;

		for (int i = 0; i < nObjs; i++) {

			Collections.sort(front, new ObjectiveComparator(i));

			objetiveMinn = front.get(0).getObjective(i);
			objetiveMaxn = front.get(size - 1).getObjective(i);

			// Set the crowding distance
			front.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
			front.get(size - 1).setCrowdingDistance(Double.POSITIVE_INFINITY);

			for (int j = 1; j < size - 1; j++) {
				distance = front.get(j + 1).getObjective(i)
						- front.get(j - 1).getObjective(i);
				distance = distance / (objetiveMaxn - objetiveMinn);
				distance += front.get(j).getCrowdingDistance();
				front.get(j).setCrowdingDistance(distance);
			}
		}
		
		return front;
	}	
}
