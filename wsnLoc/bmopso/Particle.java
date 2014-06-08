package wsnLoc.bmopso;

import java.util.ArrayList;

class Particle {

	public ArrayList<int[]> positionsMatrix;
	public ArrayList<int[]> bestPositionsMatrix;

	public ArrayList<double[]> velocitiesMatrix;

	public FloodResult fitness;
	public FloodResult bestFitness;

	public double crowdingDistance;

	public Particle(ArrayList<int[]> position, FloodResult fitness,
			ArrayList<double[]> velocity, ArrayList<int[]> bestPosition,
			FloodResult bestFitness, double crowdingDistance) {

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