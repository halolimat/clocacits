/*------------------------------------------------------------------------
 * 
 * this file implement the BSOPSO to reschedule cloudlets to be executed
 * on specific VMs. BSOPSO used simulated annealing to randomly update the
 * inertia weight for better performance.

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

package cloudsim.sources.bsopso;

import java.util.ArrayList;

class Particle {
	
	public ArrayList<int[]> positionsMatrix; // equivalent to x-Values and/or solution
	public ArrayList<int[]> bestPositionsMatrix; // best position found so far by this Particle
	
	public ArrayList<double[]> velocitiesMatrix;
	
	public double fitness;
	public double bestFitness;	

	public Particle(ArrayList<int[]> position, double fitness, ArrayList<double[]> velocity,
			ArrayList<int[]> bestPosition, double bestFitness) {
		
		this.positionsMatrix = new ArrayList<int[]>();
		this.positionsMatrix = position;
		
		this.bestPositionsMatrix = new ArrayList<int[]>();
		this.bestPositionsMatrix = bestPosition;
		
		this.velocitiesMatrix = new ArrayList<double[]>();
		this.velocitiesMatrix = velocity;
		
		this.fitness = fitness;		
		this.bestFitness = bestFitness;
	}
}
