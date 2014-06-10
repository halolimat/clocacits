/*------------------------------------------------------------------------
 * 
 * this file is part of the BSOPSO method.
 * 
 * this file implements the RIW method proposed by:  
 * Chong-min, L., Yue-lin, G., & Yu-hong, D. (2008). A New Particle Swarm 
 * Optimization Algorithm with Random Inertia Weight and Evolution Strategy. 
 * Journal of Communication and Computer, 5(11), 42–48. 
 * Retrieved from http://goo.gl/QPtsH
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

package wsnLoc.bsopso;

import java.util.Random;

public class SAInertiaWeight {

	Random ran = new Random();
	
	/** 
	 * @param currParticle2
	 * 
	 * @param particleNumber
	 *            : The particle's number; one of the possible solutions
	 * @param iterationNumber
	 *            : The move number when searching the space
	 * @param objectiveNumber
	 * @param averageFitnesses
	 *            : The average of all fitness found so far during the first to
	 *            current iteration number
	 * 
	 * @return double value of the inertia weight
	 */
	public double InertiaValue(Particle currParticle, int particleNumber, int iterationNumber, 
						double[] particlesFitness, int numberOfIterations) {
		
		int k = 5;
		double w = 0.0;

		double w_max = 0.9;
		double w_min = 0.1;

		double t_max = numberOfIterations;
		
		// if iterationNumber is multiple of k; use RIW method
		if (iterationNumber % k == 0 && iterationNumber != 0) {

			// annealing probability
			double p = 0;

			double currentFitness = particlesFitness[iterationNumber];
			double previousFitness = particlesFitness[iterationNumber - k];

			if (previousFitness <= currentFitness) {
				p = 1;
			}

			else {
				// annealing temperature
				double coolingTemp_Tt = 0.0;
				
				double bestFitness = currParticle.fitness.localizedNodesNumber;

				double ParticleFitnessAverage = 0;

				int counter = 0;
				for (int i = 0; i < iterationNumber; i++) {
					if (particlesFitness[i] > 0) {
						ParticleFitnessAverage += particlesFitness[i];
						counter++;
					}
				}

				ParticleFitnessAverage = ParticleFitnessAverage / counter;

				coolingTemp_Tt = (ParticleFitnessAverage / bestFitness) - 1;

				p = Math.exp(-(previousFitness - currentFitness) / coolingTemp_Tt);

			}

			int random = ran.nextInt(2);

			// new inertia weight
			if (p >= random) {
				w = 1 + random / 2;
			}

			else {
				w = 0 + random / 2;
			}
		}

		else {
			// new inertia weight using LDIW
			double w_fraction = (w_max - w_min) * (t_max - iterationNumber) / t_max;
			w = w_max - w_fraction;
		}

		return w;
	}
}
