package wsnLoc.cmopso;

import java.util.ArrayList;

class Particle {

	//public ArrayList<int[]> positionsMatrix;
	//public ArrayList<int[]> bestPositionsMatrix;
	
	public ArrayList<Double> positionsMatrix;
	public ArrayList<Double> bestPositionsMatrix;

	//public ArrayList<double[]> velocitiesMatrix;
	public ArrayList<Double> velocitiesMatrix;

	public FloodResult fitness;
	public FloodResult bestFitness;

	public double crowdingDistance;

	//public Particle(ArrayList<int[]> position, FloodResult fitness,
	//		ArrayList<double[]> velocity, ArrayList<int[]> bestPosition,
	//		FloodResult bestFitness, double crowdingDistance) {
	public Particle(ArrayList<Double> position, FloodResult fitness,
			ArrayList<Double> velocity, ArrayList<Double> bestPosition,
			FloodResult bestFitness, double crowdingDistance) {

		//this.positionsMatrix = new ArrayList<int[]>();
		this.positionsMatrix = new ArrayList<Double>();
		this.positionsMatrix = position;

		//this.bestPositionsMatrix = new ArrayList<int[]>();
		this.bestPositionsMatrix = new ArrayList<Double>();
		this.bestPositionsMatrix = bestPosition;

		//this.velocitiesMatrix = new ArrayList<double[]>();
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

/*
package multiobj_pso;

import java.util.ArrayList;

class Particle {

	public ArrayList<int[]> positionsMatrix;
	public ArrayList<int[]> bestPositionsMatrix;

	public ArrayList<double[][]> velocitiesMatrix;

	public FloodResult fitness;
	public FloodResult bestFitness;

	public double crowdingDistance;

	public Particle(ArrayList<int[]> position, FloodResult fitness,
			ArrayList<double[][]> velocity, ArrayList<int[]> bestPosition,
			FloodResult bestFitness, double crowdingDistance) {

		this.positionsMatrix = new ArrayList<int[]>();
		this.positionsMatrix = position;

		this.bestPositionsMatrix = new ArrayList<int[]>();
		this.bestPositionsMatrix = bestPosition;

		this.velocitiesMatrix = new ArrayList<double[][]>();
		this.velocitiesMatrix = velocity;

		this.fitness = fitness;
		this.bestFitness = bestFitness;
	}
	
	public double getObjective(int i){
		
		double objectiveValue = 0;
		
		if (i == 0){
			objectiveValue = fitness.time;
		}
		
		else {
			objectiveValue = fitness.localizedNodesNumber;
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
*/
