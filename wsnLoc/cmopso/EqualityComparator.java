/*------------------------------------------------------------------------
 * 
 * this file implement a Equality Comparator for the CMOPSO. 
 *  
 * Copyright (c) 2013-2014, Hussein S. Al-Olimat.
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
