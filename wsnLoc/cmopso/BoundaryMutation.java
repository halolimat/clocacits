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
