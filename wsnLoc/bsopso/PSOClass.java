/*------------------------------------------------------------------------
 * 
 * this file is part of the BSOPSO method. 
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
		
		discFloodResult bestGlobalFitness = new discFloodResult();
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
			
			discFloodResult fitness = ObjectiveFunction(initPositions, nodesList);
			
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
				bestGlobalFitness = swarm[l].fitness;		            	
		        bestGlobalPositions = swarm[l].positionsMatrix;
			}
						
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
        		
        		ArrayList<double[]> newVelocitiesMatrix = new ArrayList<double[]>();
            	ArrayList<int[]> newPositionsMatrix = new ArrayList<int[]>();
            	discFloodResult newFitness;
        		
        		Particle currParticle = swarm[l];
        		
        		// calculate InertiaValue
        		double w = inertiaWeight.InertiaValue(currParticle, l, iter, particlesFitness.get(l), numberOfIterations);
        		
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
        		
        		if(newFitness.localizedNodesNumber >= currParticle.bestFitness.localizedNodesNumber){
        			currParticle.bestPositionsMatrix = newPositionsMatrix;
    	            currParticle.bestFitness = newFitness;
    			}
        		
        		if(newFitness.localizedNodesNumber >= bestGlobalFitness.localizedNodesNumber){
        			bestGlobalPositions = newPositionsMatrix;
    	            bestGlobalFitness = newFitness;
    			}
        		
        		// 8 insert new fitness in the fitness list
				double[] particleFitness = particlesFitness.get(l);
				particleFitness[iter] = currParticle.fitness.localizedNodesNumber;
				particlesFitness.set(l, particleFitness);
        	}
        }
        
        printFitnessValues(bestGlobalFitness);
	}
	
	private void printFitnessValues(discFloodResult bestGlobalFitness){
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
	
	private discFloodResult ObjectiveFunction(ArrayList<int[]> positionsArrList, ArrayList<Node> nodesList) {
		
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
		}
		
		discPowerLevelsFlood flooding = new discPowerLevelsFlood();
		
		return flooding.Start(nodesToRangeList);
	}
	
	/**
	 * this function will make sure that all nodes are assigned one of the output power levels
	 * 
	 * @param list: positions/velocity matrix
	 * @param assignedTasksArray: the tracking array of the 0's and 1's when assigning VMs to cloudlets 
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