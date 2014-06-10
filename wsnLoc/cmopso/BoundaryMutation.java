/*------------------------------------------------------------------------
 * 
 * this file implement a boundary mutation method for the CMOPSO. 
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
import java.util.Random;

public class BoundaryMutation {

	static Random ran;
	
	// mutation to 15% of the particles
	public ArrayList<Particle> mopsoMutation(ArrayList<Particle> swarm) {
		
		ran = new Random();
		
		if(Constants.mutationPercentage > 0){
		
			int modValue = (int)( swarm.size() / (swarm.size() * Constants.mutationPercentage)); 

			for (int i = 0; i < swarm.size(); i++) {
				if ((i % modValue) == 0) {
					swarm.set(i, executeMutation(swarm.get(i)));
				}
			}
		}

		return swarm;
	}

	private Particle executeMutation(Particle particle) {

		/*
		for (int i = 0; i < particle.positionsMatrix.size(); i++) {
			if (ran.nextDouble() < 1/particle.positionsMatrix.size()) {
		        double rand = ran.nextDouble();
		        double tmp = (rand - 0.5)*0.5;
		                                
		        tmp += particle.positionsMatrix.get(i);
		                
		        if (tmp < PSOClass.minimumTransmissionRange)
		          tmp = PSOClass.minimumTransmissionRange;
		        else if (tmp > PSOClass.maximumTransmissionRange)
		          tmp = PSOClass.maximumTransmissionRange;
		        
		        particle.positionsMatrix.set(i, tmp);
			}
		}
		*/
		
		for (int i = 0; i < particle.positionsMatrix.size()/2; i++) {
			particle.positionsMatrix.set(ran.nextInt(particle.positionsMatrix.size()), (double) Constants.mutationValue);
		}
				
		return particle;
	}

}
