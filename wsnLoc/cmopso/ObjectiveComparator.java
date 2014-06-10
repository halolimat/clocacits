/*------------------------------------------------------------------------
 * 
 * This file is based on ObjectiveComparator.java file distributed with 
 * jMetal library licensed under GPL. 
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
