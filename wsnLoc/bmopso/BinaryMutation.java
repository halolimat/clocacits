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
