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
