package wsnLoc.cmopso;

import java.util.Random;

public class SAInertiaWeight {

	Random ran = new Random();
	
	/**
	 * will calculate the RIW according to (A new particle swarm optimization
	 * algorithm with random inertia weight and evolution strategy: paper)
	 * 
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
				
				double bestFitness = currParticle.bestFitness.energyConsumption
						/ currParticle.bestFitness.time;

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
