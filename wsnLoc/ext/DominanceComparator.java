/*------------------------------------------------------------------------
 * 
 * this file implement a Dominance Comparator for the MOPSO. 
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

package wsnLoc.ext;

import java.util.Comparator;

@SuppressWarnings("rawtypes")
public class DominanceComparator implements Comparator{
	
	public int compare(Object o1, Object o2) {
		
		discFloodResult firstParticleFitness = (discFloodResult) o1;
		discFloodResult SecondParticleFitness = (discFloodResult) o2;
		
		double Particle_1_obj_1_value, Particle_1_obj_2_value;
		double Particle_2_obj_1_value, Particle_2_obj_2_value;
		
		Particle_1_obj_1_value = firstParticleFitness.time;
		Particle_1_obj_2_value = firstParticleFitness.localizedNodesNumber;
		
		Particle_2_obj_1_value = SecondParticleFitness.time;
		Particle_2_obj_2_value = SecondParticleFitness.localizedNodesNumber;
		
		double particle_1_fitness = Particle_1_obj_2_value/ Particle_1_obj_1_value;
		double particle_2_fitness = Particle_2_obj_2_value/ Particle_2_obj_1_value;
		
		if (particle_1_fitness < particle_2_fitness) {
			return 1; // solution2 dominate
		}
			
		else if (particle_1_fitness > particle_2_fitness) {
			return -1; // solution1 dominate
		}
			
		else {
			return 0; // No one dominate the other
		}
	}
}
