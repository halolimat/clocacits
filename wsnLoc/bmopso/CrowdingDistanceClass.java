/*------------------------------------------------------------------------
 * 
 * This file is based on Distance.java file distributed with jMetal
 * library licensed under GPL. 
 *  
 * Copyright (c) 2011, Antonio J. Nebro, Juan J. Durillo
 * 
 * Edited by: Hussein S. Al-Olimat, June 2013
 * 
 *------------------------------------------------------------------------ 
 *
 * This file is part of clocacits: a set of computational intelligence 
 * methods implemented using Java for multi-objective multi-level 
 * optimization problems. 
 * 
 * clocacits contains the implementations of methods proposed in a master 
 * thesis entitled: Optimizing Cloudlet Scheduling and Wireless Sensor 
 * Localization using Computational Intelligence Techniques. 
 * Thesis by: Hussein S. Al-Olimat, the University of Toledo, July 2014. 
 * 
 * clocacits is a free library: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * clocacits is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with clocacits.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *------------------------------------------------------------------------
 */

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
