package wsnLoc.bmopso;

import java.util.Comparator;

@SuppressWarnings("rawtypes")
public class CrowdingDistanceComparator implements Comparator {
	
	public int compare(Object o1, Object o2) {
		
		if (o1 == null)
			return 1;
		
		else if (o2 == null)
			return -1;

		double distance1 = ((Particle) o1).crowdingDistance;
		double distance2 = ((Particle) o2).crowdingDistance;

		if (distance1 > distance2)
			return -1;
		
		if (distance1 < distance2)
			return 1;
		
		return 0;
	}
}