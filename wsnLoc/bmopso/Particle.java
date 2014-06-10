/*------------------------------------------------------------------------
 * 
 * this file is part of the BMOPSO method. 
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

package wsnLoc.bmopso;

import java.util.ArrayList;
import wsnLoc.ext.*;

class Particle {

	public ArrayList<int[]> positionsMatrix;
	public ArrayList<int[]> bestPositionsMatrix;

	public ArrayList<double[]> velocitiesMatrix;

	public discFloodResult fitness;
	public discFloodResult bestFitness;

	public double crowdingDistance;

	public Particle(ArrayList<int[]> position, discFloodResult fitness,
			ArrayList<double[]> velocity, ArrayList<int[]> bestPosition,
			discFloodResult bestFitness, double crowdingDistance) {

		this.positionsMatrix = new ArrayList<int[]>();
		this.positionsMatrix = position;

		this.bestPositionsMatrix = new ArrayList<int[]>();
		this.bestPositionsMatrix = bestPosition;

		this.velocitiesMatrix = new ArrayList<double[]>();
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