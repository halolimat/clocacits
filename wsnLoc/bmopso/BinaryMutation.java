/*------------------------------------------------------------------------
 * 
 * this file implement a binary mutation method for the BMOPSO. 
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

public class BinaryMutation {

	// mutation to 15% of the particles
	public ArrayList<Particle> mopsoMutation(ArrayList<Particle> swarm) {

		for (int i = 0; i < swarm.size(); i++) {
			if ((i % 6) == 0) {
				swarm.set(i, executeMutation(swarm.get(i)));
			}
		}
		
		return swarm;
	}

	private Particle executeMutation(Particle particle) {

		ArrayList<int[]> NodePositionsMatrix = particle.positionsMatrix;

		for (int i = 0; i < NodePositionsMatrix.size(); i++) {

			int[] positions = NodePositionsMatrix.get(i);

			// flip binary
			for (int j = 0; j < positions.length; j++) {

				if (positions[j] == 1) {
					positions[j] = 0;
				}

				else {
					positions[j] = 1;
				}
			}

			NodePositionsMatrix.set(i, positions);
		}

		particle.positionsMatrix = NodePositionsMatrix;

		return particle;
	}

}
