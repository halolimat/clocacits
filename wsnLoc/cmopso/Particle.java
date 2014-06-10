/*------------------------------------------------------------------------
 * 
 * this file is part of the CMOPSO method. 
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

import java.util.ArrayList;

class Particle {
	
	public ArrayList<Double> positionsMatrix;
	public ArrayList<Double> bestPositionsMatrix;

	public ArrayList<Double> velocitiesMatrix;

	public FloodResult fitness;
	public FloodResult bestFitness;

	public double crowdingDistance;

	public Particle(ArrayList<Double> position, FloodResult fitness,
			ArrayList<Double> velocity, ArrayList<Double> bestPosition,
			FloodResult bestFitness, double crowdingDistance) {

		this.positionsMatrix = new ArrayList<Double>();
		this.positionsMatrix = position;

		this.bestPositionsMatrix = new ArrayList<Double>();
		this.bestPositionsMatrix = bestPosition;

		this.velocitiesMatrix = new ArrayList<Double>();
		this.velocitiesMatrix = velocity;

		this.fitness = fitness;
		this.bestFitness = bestFitness;
	}
	
	public double getObjective(int i){
		
		double objectiveValue = 0;
		
		if (i == 0){
			objectiveValue = fitness.time;
		}
		
		else if(i == 1){
			objectiveValue = fitness.localizedNodesNumber;
		}
		
		else{
			objectiveValue = fitness.energyConsumption;
		}
		
		return objectiveValue;
	}
	
	public void setCrowdingDistance(double crowdingDistance){
		this.crowdingDistance = crowdingDistance;
	}
	
	public double getCrowdingDistance(){
		return crowdingDistance;
	}
}