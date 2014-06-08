package wsnLoc.bsopso;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import wsnLoc.ext.*;

public class PSOClass {

	//number of Nodes
	int m;
	
	//number of PowerLevels
	int n;
	
	int numberOfParticles = 100;
	int numberOfIterations = 200;
	
	Particle[] swarm;

	static Random ran = null;
	
	ArrayList<double[]> particlesFitness;
	private SAInertiaWeight inertiaWeight;
	
	public void run(ArrayList<Node> nodesList) {

		particlesFitness = new ArrayList<double[]>();
		inertiaWeight = new SAInertiaWeight();
		
		ran = new Random();

		m = nodesList.size();
		n = 3; // ZigBee power levels

		swarm = new Particle[numberOfParticles];

		ArrayList<int[]> bestGlobalPositions = new ArrayList<int[]>();// the best positions found
		
		//double bestGlobalFitness = 0; // smaller values better
		
		FloodResult bestGlobalFitness = new FloodResult();
		bestGlobalFitness.localizedNodesNumber = 0;
		bestGlobalFitness.time = Integer.MAX_VALUE;
		
		// +++++++++++++++++++++++>

		for (int l = 0; l < swarm.length; ++l) // initialize each Particle in the swarm
		{
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			//+( positions )++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			ArrayList<int[]> initPositions = new ArrayList<int[]>();
			
			//will initially used the max transmit range for all sensors
			for (int i = 0; i < m; i++) {

				int[] initRange = new int[n];

				//min
				initRange[0] = 1;
				//mid
				initRange[1] = 0;
				//max .. will be used
				initRange[2] = 0;
				
				initPositions.add(initRange);
			}
			
			FloodResult fitness = ObjectiveFunction(initPositions, nodesList);
			
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			//+( Velocity )+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			ArrayList<double[]> initVelocities = new ArrayList<double[]>();
			int[] assignedTasksArrayInVelocityMatrix = new int[n];

			for (int i = 0; i < m; i++) {

				double[] randomPositions = new double[n];

				for (int j = 0; j < n; j++) {

					// if not assigned assign it
					if (assignedTasksArrayInVelocityMatrix[j] == 0) {
						randomPositions[j] = ran.nextInt(2);

						if (randomPositions[j] == 1) {
							assignedTasksArrayInVelocityMatrix[j] = 1;
						}
					}

					else {
						randomPositions[j] = 0;
					}
				}

				initVelocities.add(randomPositions);
			}

			swarm[l] = new Particle(initPositions, fitness, initVelocities, initPositions, fitness);
			
			// does current Particle have global best position/solution?
			if(swarm[l].fitness.localizedNodesNumber >= bestGlobalFitness.localizedNodesNumber){
			//	if(swarm[l].fitness.time <= bestGlobalFitness.time){
					//if(
					//		swarm[l].fitness.rangesFrequent[2] < bestGlobalFitness.rangesFrequent[1]
					//		|| 
					//		swarm[l].fitness.rangesFrequent[2] < bestGlobalFitness.rangesFrequent[0]
					//		|| 
					//		swarm[l].fitness.rangesFrequent[1] < bestGlobalFitness.rangesFrequent[0]
							
					//	){
						
						bestGlobalFitness = swarm[l].fitness;		            	
		            	bestGlobalPositions = swarm[l].positionsMatrix;
					//}
				}
			//}
			
			//initialize average fitness
			double[] particleFitness = new double[numberOfIterations];
			particlesFitness.add(particleFitness);
		}
		
		double c1 = 1.49445; // cognitive/local weight
        double c2 = 1.49445; // social/global weight
        int r1, r2; // cognitive and social randomizations
        
        //minimum and maximum values to have since we are working with only two values 0 and 1
        int minV = 0;
        int maxV = 1;

        //to keep an array of the average fitness per particle
        ArrayList<int[]> averageFitness = new ArrayList<int[]>();
        
        //fill the averageFitnesses with empty arrays
        for(int i = 0 ; i < swarm.length ; i++){
        	averageFitness.add(new int[numberOfIterations]);
        }
        
        for(int iter = 0 ; iter < numberOfIterations ; iter++){
        	
        	for (int l = 0; l < swarm.length; ++l){
        		
        		//calculate InertiaValue
        		//double w = InertiaValue(l,iter, averageFitness);
            	
            	ArrayList<double[]> newVelocitiesMatrix = new ArrayList<double[]>();
            	ArrayList<int[]> newPositionsMatrix = new ArrayList<int[]>();
            	FloodResult newFitness;
        		
        		Particle currParticle = swarm[l];
        		
        		// calculate InertiaValue
        		double w = inertiaWeight.InertiaValue(currParticle, l, iter, particlesFitness.get(l), numberOfIterations);
        		
        		//double w = 0.1;
        		
        		for(int i = 0; i < currParticle.velocitiesMatrix.size() ; i++){
        			double[] vmVelocities = currParticle.velocitiesMatrix.get(i);
        			int[] vmBestPositions = currParticle.bestPositionsMatrix.get(i);
        			int[] vmPostitons = currParticle.positionsMatrix.get(i);
        			
        			int[] vmGlobalbestPositions = bestGlobalPositions.get(i);
        			
        			double[] newVelocities = new double[n];
        			
        			//length - 1 => v(t+1) = w*v(t) ..
                    for(int j = 0 ; j < vmVelocities.length - 1 ; j++){
                    	r1 = ran.nextInt(2);
                        r2 = ran.nextInt(2);
                    	
                        //velocity vector
                        newVelocities[j] =  (w * vmVelocities[j+1] + c1 * r1 * (vmBestPositions[j] - vmPostitons[j]) + c2 * r2 * (vmGlobalbestPositions[j] - vmPostitons[j]));
                        	
                        if (newVelocities[j] < minV){
                        	newVelocities[j] = minV;
                        }
                        	
                        else if (newVelocities[j] > maxV){
                        	newVelocities[j] = maxV;
                        }
                    }
                    
                    //add the new velocities into the arrayList
                    newVelocitiesMatrix.add(newVelocities);
        		}
        		
        		currParticle.velocitiesMatrix = newVelocitiesMatrix;
        		
        		/*
        		for(int i = 0 ; i < newVelocitiesMatrix.size() ; i ++){
        			for(int j = 0 ; j < newVelocitiesMatrix.get(i).length ; j++){
        				System.out.print(newVelocitiesMatrix.get(i)[j] + "\t");
        			}
        			System.out.println();
        		}
        		*/
        		
        		//----->>>>> Done with velocities -----------------------------------------------------------------------------
        		
        		//to keep track of the zeros and ones in the arrayList per nodeRange
    			int[] assignedRangeInPositionsMatrix = new int[m];
        		
        		for(int i = 0; i < currParticle.velocitiesMatrix.size() ; i++){
        			double[] vmVelocities = currParticle.velocitiesMatrix.get(i);
        			
        			int[] newPosition = new int[n];
        			
        			//length - 1 => v(t+1) = w*v(t) ..
                    for(int j = 0 ; j < vmVelocities.length - 1 ; j++){
                    	int random = ran.nextInt(2);
                    	
                    	//if a node is already assigned a range then wont enter
                        if (assignedRangeInPositionsMatrix[j] == 0) {
    						
                        	//to calculate sigmoid function
                        	double sig = 1/(1+Math.exp(-vmVelocities[j]));
                        	
                        	if(sig > random){
                        		newPosition[j] = 1;
                        	}
                        	
                        	else{
                        		newPosition[j] = 0;
                        	}
                        	
                        	if (newPosition[j] == 1) {
                        		assignedRangeInPositionsMatrix[i] = 1;
    						}
    					}

    					else {
    						newPosition[j] = 0;
    					}
                    }
                    
                    //add the new velocities into the arrayList
                    newPositionsMatrix.add(newPosition);
        		}
        		
        		newPositionsMatrix = checkNodeToRangeAssignment(newPositionsMatrix, assignedRangeInPositionsMatrix);
        		
        		currParticle.positionsMatrix = newPositionsMatrix;
        		
        		//-------> done with new positions
        		
        		newFitness = ObjectiveFunction(newPositionsMatrix, nodesList);
        		currParticle.fitness = newFitness;
        		
        		/////////////////-------------- multi-objective here
        		// add newFitness > .. and if bestPositionsMatrix has minimum max
        		
        		//if the new fitness is better than what we already found
        		//-> set the new fitness as the best fitness so far
        		if(newFitness.localizedNodesNumber >= currParticle.bestFitness.localizedNodesNumber){
        			//if(newFitness.time <= bestGlobalFitness.time){		
        			//	if(
    				//			newFitness.rangesFrequent[2] < currParticle.bestFitness.rangesFrequent[1]
    				//			|| 
    				//			newFitness.rangesFrequent[2] < currParticle.bestFitness.rangesFrequent[0]
    				//			|| 
    				//			newFitness.rangesFrequent[1] < currParticle.bestFitness.rangesFrequent[0]
    							
    				//	){
        					currParticle.bestPositionsMatrix = newPositionsMatrix;
    	                    currParticle.bestFitness = newFitness;
        			//	}
        			//}
    			}
        		
        		//if the new fitness is better than all solution found by all particles
        		//-> set the new fitness as the global fitness
        		if(newFitness.localizedNodesNumber >= bestGlobalFitness.localizedNodesNumber){
    			//	if(newFitness.time <= bestGlobalFitness.time){
    				//	if(
    				//			newFitness.rangesFrequent[2] < bestGlobalFitness.rangesFrequent[1]
    				//			|| 
    				//			newFitness.rangesFrequent[2] < bestGlobalFitness.rangesFrequent[0]
    				//			|| 
    				//			newFitness.rangesFrequent[1] < bestGlobalFitness.rangesFrequent[0]
    							
    				//	){
    						
    						bestGlobalPositions = newPositionsMatrix;
    	                    bestGlobalFitness = newFitness;
    					//}
    			//	}
    			}
        		
        		// 8 insert new fitness in the fitness list
				double[] particleFitness = particlesFitness.get(l);
				particleFitness[iter] = currParticle.fitness.localizedNodesNumber;
				particlesFitness.set(l, particleFitness);
        	}
        	
        	//System.out.println(iter);
        	
        	if(iter%50 == 0){
				System.out.print("\t"+iter);
			}
        }
        
        printFitnessValues(bestGlobalFitness);
	}
	
	private void printFitnessValues(FloodResult bestGlobalFitness){
		String TAB = "\t";
		
		String res = bestGlobalFitness.time + TAB + 
				bestGlobalFitness.minRange + TAB + 
				bestGlobalFitness.midRange + TAB + 
				bestGlobalFitness.MaxRange + TAB + 
				bestGlobalFitness.energyConsumption + TAB +
				bestGlobalFitness.localizedNodesNumber;
		
		
		
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("SimulationOutput" + File.separator + "out.txt", true)));
		    out.println(res);
		    out.close();
		} catch (IOException e) {}
	}
	
	
	
	/**
	 * will calculate the RIW according to 
	 * (A new particle swarm optimization algorithm with random inertia weight and evolution strategy: paper)
	 * 
	 * @param particleNumber: The particle's number; one of the possible solutions
	 * @param iterationNumber: The move number when searching the space
	 * @param averageFitnesses: The average of all fitness found so far during the first to current iteration number
	 * 
	 * @return double value of the inertia weight
	 */	
	
	/*
	
	private double InertiaValue(int particleNumber, int iterationNumber, ArrayList<int[]> averageFitness){
		
		int k = 5;
		double w = 0.0;
		
		double w_max = 0.9;
		double w_min = 0.1;
		
		double t_max = numberOfIterations;
		double t = iterationNumber;
		
		//if t is multiple of k; use RIW method
		if (t % k == 0 && t != 0) {
		     
			//annealing probability
			double p = 0;
					
			double currentFitness = averageFitness.get(particleNumber)[iterationNumber];
			double previousFitness = averageFitness.get(particleNumber)[iterationNumber - k];
			
			if(previousFitness <= currentFitness){
				p = 1;
			}
			
			else{
				//annealing temperature
				double coolingTemp_Tt = 0.0;
				
				Particle currParticle = swarm[particleNumber];
				//double bestFitness = currParticle.bestFitness.time;
				//double bestFitness = currParticle.bestFitness.localizedNodesNumber;
				double bestFitness = currParticle.bestFitness.MaxRange;
				
				double ParticleFitnessAverage = 0;
				
				int counter = 0;
				for(int i = 0 ; i < iterationNumber ; i++){
					if(averageFitness.get(particleNumber)[i] > 0){
						ParticleFitnessAverage += averageFitness.get(particleNumber)[i];
						counter++;
					}
				}
				
				ParticleFitnessAverage = ParticleFitnessAverage/counter;
				
				coolingTemp_Tt = (ParticleFitnessAverage / bestFitness) - 1;
				
				p = Math.exp(-(previousFitness - currentFitness)/coolingTemp_Tt);
				
			}	
			
			int random = ran.nextInt(2);
			
			//new inertia weight
			if(p >= random){
				w = 1 + random/2;				
			}
			
			else{
				w = 0 + random/2;
			}
		}
		
		else{

			//new inertia weight using LDIW
			double w_fraction = ( w_max - w_min ) * ( t_max - t ) / t_max;
			w = w_max - w_fraction;
		}
		
		return w;
	}

	 */

	private FloodResult ObjectiveFunction(ArrayList<int[]> positionsArrList, ArrayList<Node> nodesList) {
		
		double[] range = {ZigBee_powerConsumption.MinRange, ZigBee_powerConsumption.MidRange, ZigBee_powerConsumption.MaxRange}; 
		
		ArrayList<NodesToRange> nodesToRangeList = new ArrayList<NodesToRange>();
		
		for(int i = 0 ; i < m ; i++){
			NodesToRange node = new NodesToRange();
			node.sender = nodesList.get(i);
			
			int[] pos = positionsArrList.get(i);
			
			for (int j = 0; j < n; j++) {
				if (pos[j] == 1) {
					node.range = range[j];
				}
			}
			
			nodesToRangeList.add(node);
			//System.out.println("id:"+node.sender.id+"\trange:"+node.range);
		}
		
		Floood flooding = new Floood();
		
		return flooding.Start(nodesToRangeList);
	}
	
	/**
	 * this function will make sure that all tasks are assigned to one of the VMs
	 * 
	 * @param list: positions/velocity matrix
	 * @param assignedTasksArray: the tracking array of the 0's and 1's when assiging VMs to cloudlets 
	 * 
	 * @return positions/velocity matrix
	 */	
	private ArrayList<int[]> checkNodeToRangeAssignment(ArrayList<int[]> list, int[] assignedTasksArray) {

		ArrayList<int[]> newArrList = list;

		// check if task is not yet assigned
		for (int i = 0; i < list.size(); i++) {

			if (assignedTasksArray[i] == 0) {
				
				int x = ran.nextInt(n);

				int[] positions = list.get(i);
				positions[x] = 1;

				newArrList.set(i, positions);
			}
		}

		return newArrList;
	}
}