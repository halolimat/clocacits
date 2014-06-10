/*------------------------------------------------------------------------
 * 
 * this file implement the BSOPSO to reschedule cloudlets to be executed
 * on specific VMs. BSOPSO used simulated annealing to randomly update the
 * inertia weight for better performance.
 * 
 * this file also implements the RIW method proposed by:
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

package cloudsim.sources.bsopso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class CloudLetPSOScheduling {

	//number of VMs
	int m;
	
	//number of cloudlets
	int n;
	
	int numberOfParticles = 10;
	int numberOfIterations = 100;
	
	Particle[] swarm;

	static Random ran = null;
	
	/**
	 * Sums a list of numbers.
	 * 
	 * @param cloudletList
	 * @param vmList
	 * 
	 * @return array of integers with the IDs of VMs that will run each cloudlet
	 */	
	public int[] getScheduledCloudLets(List<? extends Cloudlet> cloudletList, List<? extends Vm> vmList) {
		
		ArrayList<Vm> initVMs = new ArrayList<Vm>();
		
		for(int i = 0 ; i < vmList.size() ; i++){
			
			Vm vm = vmList.get(i);
			
			if(vm.getHost() != null){
				initVMs.add(vm);
				System.out.println(">> vmID = "+vm.getId());
			}
		}
		
		int[] res = null;
		
		res = run(cloudletList, initVMs);			
				
		return res;
	}
	
	private int[] run(List<? extends Cloudlet> cloudletList, List<Vm> vmlist) {

		ran = new Random();

		// ------------------------------------------------------------------------------------------------------------------------------------
		
		m = vmlist.size();
		n = cloudletList.size();

		ArrayList<double[]> runTime = new ArrayList<double[]>();

		//will calculate the execution time each cloudlet takes if it runs on one of the VMs
		for (int i = 0; i < m; i++) {

			Vm vm = vmlist.get(i);
			
			double[] arr = new double[n];

			for (int j = 0; j < n; j++) {
				
				Cloudlet cloudlet = cloudletList.get(j);
				
				arr[j] = (double) cloudlet.getCloudletLength() / vm.getHost().getTotalAllocatedMipsForVm(vm);
			}

			runTime.add(arr);
		}
		// ------------------------------------------------------------------------------------------------------------------------------------

		swarm = new Particle[numberOfParticles];

		ArrayList<int[]> bestGlobalPositions = new ArrayList<int[]>();// the best positions found
		
		double bestGlobalFitness = Double.MAX_VALUE; // smaller values better

		// +++++++++++++++++++++++>

		for (int l = 0; l < swarm.length; ++l) // initialize each Particle in the swarm
		{
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			//+( positions )++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			ArrayList<int[]> initPositions = new ArrayList<int[]>();
			int[] assignedTasksArray = new int[n];

			for (int i = 0; i < m; i++) {

				int[] randomPositions = new int[n];

				for (int j = 0; j < n; j++) {

					// if not assigned assign it
					if (assignedTasksArray[j] == 0) {
						randomPositions[j] = ran.nextInt(2);

						if (randomPositions[j] == 1) {
							assignedTasksArray[j] = 1;
						}
					}

					else {
						randomPositions[j] = 0;
					}

				}

				initPositions.add(randomPositions);
			}

			
			// to assign unassigned tasks
			ArrayList<int[]> newPositionsMatrix = checkResourceAssignmentForNonAssignedTasks(initPositions, assignedTasksArray);

			double fitness = ObjectiveFunction(runTime, newPositionsMatrix);
			
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

			swarm[l] = new Particle(newPositionsMatrix, fitness, initVelocities, newPositionsMatrix, fitness);
			
			// does current Particle have global best position/solution?
            if (swarm[l].fitness < bestGlobalFitness)
            {
            	bestGlobalFitness = swarm[l].fitness;
            	
            	bestGlobalPositions = swarm[l].positionsMatrix;
            }
		}
		
		double c1 = 1.49445; // cognitive/local weight
        double c2 = 1.49445; // social/global weight
        int r1, r2; // cognitive and social randomizations
        
        //minimum and maximum values to have since we are working with only two values 0 and 1
        int minV = 0;
        int maxV = 1;

        //to keep an array of the average fitness per particle
        ArrayList<double[]> averageFitnesses = new ArrayList<double[]>();
        
        //fill the averageFitnesses with empty arrays
        for(int i = 0 ; i < swarm.length ; i++){
        	averageFitnesses.add(new double[numberOfIterations]);
        }
        
        for(int iter = 0 ; iter < numberOfIterations ; iter++){
        	
        	for (int l = 0; l < swarm.length; ++l){
        		
        		//calculate InertiaValue
        		double w = InertiaValue(l,iter, averageFitnesses);
            	
            	ArrayList<double[]> newVelocitiesMatrix = new ArrayList<double[]>();
            	ArrayList<int[]> newPositionsMatrix = new ArrayList<int[]>();
            	double newFitness;
        		
        		Particle currParticle = swarm[l];
        		
        		//to keep track of the zeros and ones in the arrayList per task
    			int[] assignedTasksArrayInVelocityMatrix = new int[n];
        		
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
                    	
                        if (assignedTasksArrayInVelocityMatrix[j] == 0) {
    						
                        	//velocity vector
                        	newVelocities[j] =  (w * vmVelocities[j+1] + c1 * r1 * (vmBestPositions[j] - vmPostitons[j]) + c2 * r2 * (vmGlobalbestPositions[j] - vmPostitons[j]));
                        	
                        	if (newVelocities[j] < minV){
                        		newVelocities[j] = minV;
                        	}
                        	
                            else if (newVelocities[j] > maxV){
                            	newVelocities[j] = maxV;
                            }
                        	
                        	if (newVelocities[j] == 1) {
    							assignedTasksArrayInVelocityMatrix[j] = 1;
    						}
    					}

    					else {
    						newVelocities[j] = 0;
    					}
                    }
                    
                    //add the new velocities into the arrayList
                    newVelocitiesMatrix.add(newVelocities);
        		}
        		
        		currParticle.velocitiesMatrix = newVelocitiesMatrix;
        		
        		//----->>>>> Done with velocities
        		
        		//to keep track of the zeros and ones in the arrayList per task
    			int[] assignedTasksArrayInPositionsMatrix = new int[n];
        		
        		for(int i = 0; i < currParticle.velocitiesMatrix.size() ; i++){
        			double[] vmVelocities = currParticle.velocitiesMatrix.get(i);
        			
        			int[] newPosition = new int[n];
        			
        			//length - 1 => v(t+1) = w*v(t) ..
                    for(int j = 0 ; j < vmVelocities.length - 1 ; j++){
                    	int random = ran.nextInt(2);
                    	
                        if (assignedTasksArrayInPositionsMatrix[j] == 0) {
    						
                        	//to calculate sigmoid function
                        	double sig = 1/(1+Math.exp(-vmVelocities[j]));
                        	
                        	if(sig > random){
                        		newPosition[j] = 1;
                        	}
                        	
                        	else{
                        		newPosition[j] = 0;
                        	}
                        	
                        	if (newPosition[j] == 1) {
                        		assignedTasksArrayInPositionsMatrix[j] = 1;
    						}
    					}

    					else {
    						newPosition[j] = 0;
    					}
                    }
                    
                    //add the new velocities into the arrayList
                    newPositionsMatrix.add(newPosition);
        		}
        		
        		//will check for non assigned tasks
        		newPositionsMatrix = checkResourceAssignmentForNonAssignedTasks(newPositionsMatrix, assignedTasksArrayInPositionsMatrix);
        		newPositionsMatrix = ReBalancePSO(newPositionsMatrix, runTime);
        		currParticle.positionsMatrix = newPositionsMatrix;
        		
        		//-------> done with new positions
        		
        		newFitness = ObjectiveFunction(runTime, newPositionsMatrix);
        		currParticle.fitness = newFitness;
        		
        		//if the new fitness is better than what we already found
        		//-> set the new fitness as the best fitness so far
        		if (newFitness < currParticle.bestFitness)
                {
                	currParticle.bestPositionsMatrix = newPositionsMatrix;
                    currParticle.bestFitness = newFitness;
                }

        		//if the new fitness is better than all solutions found by all particles
        		//-> set the new fitness as the global fitness
                if (newFitness < bestGlobalFitness)
                {
                	bestGlobalPositions = newPositionsMatrix;
                    bestGlobalFitness = newFitness;
                }
                
                //to add the new fitness to the average fitness array
                double[] fitnessArrayInAvgFitnesses = averageFitnesses.get(l);
                fitnessArrayInAvgFitnesses[iter] = newFitness;
                //Added .. worse solutions
                averageFitnesses.set(l, fitnessArrayInAvgFitnesses);
        	}
        }
        
       return returnVM2CloudLetArray(bestGlobalPositions);		
	}
	
	// This function will re-balance the solution found by PSO for better solutions
	private ArrayList<int[]> ReBalancePSO(ArrayList<int[]> newPositionsMatrix, ArrayList<double[]> runTime) {
		
		boolean done = false;
		int counter = 0;
		
		while(!done){
			
			double[] sum = new double[m];

			for (int i = 0; i < m; i++) {

				double[] time = runTime.get(i);
				int[] pos = newPositionsMatrix.get(i);

				int n = pos.length;

				for (int j = 0; j < n; j++) {
					if (pos[j] == 1) {
						sum[i] = sum[i] + time[j];
					}
				}
			}
			
			int heavestVMLoad = 0;
			int lightestVMLoad = 0;
			
			for(int i = 1 ; i < m; i++){
				if(sum[heavestVMLoad] < sum[i]){
					heavestVMLoad = i;
				}
				
				if(sum[lightestVMLoad] > sum[i]){
					lightestVMLoad = i;
				}
			}
			
			int[] HeavestPOS = newPositionsMatrix.get(heavestVMLoad);
			
			int[] LightestPOS = newPositionsMatrix.get(lightestVMLoad);
						
			for(int i = 0 ; i < HeavestPOS.length ; i++){
				int cloudletNumberOnHeavest = 0;
				
				if(HeavestPOS[i] == 1){
					cloudletNumberOnHeavest = i;
				}
				
				double heavestMinusThisCloudlet = sum[heavestVMLoad] - HeavestPOS[cloudletNumberOnHeavest];
				double LightestPlusThisCloudlet = sum[lightestVMLoad] + LightestPOS[cloudletNumberOnHeavest];
				
				if(heavestMinusThisCloudlet < LightestPlusThisCloudlet){
					break;
				}
				
				else{
					HeavestPOS[cloudletNumberOnHeavest] = 0;
					LightestPOS[cloudletNumberOnHeavest] = 1;					
					newPositionsMatrix.set(heavestVMLoad, HeavestPOS);
					newPositionsMatrix.set(lightestVMLoad, LightestPOS);
				}
			}
			
			//----
			
			if(counter == 3){
				done = true;
			}
			
			counter++;
		}
		
		return newPositionsMatrix;
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
	private double InertiaValue(int particleNumber, int iterationNumber, ArrayList<double[]> averageFitnesses){
		
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
					
			double currentFitness = averageFitnesses.get(particleNumber)[iterationNumber];
			double previousFitness = averageFitnesses.get(particleNumber)[iterationNumber - k];
			
			if(previousFitness <= currentFitness){
				p = 1;
			}
			
			else{
				//annealing temperature
				double coolingTemp_Tt = 0.0;
				
				Particle currParticle = swarm[particleNumber];
				double bestFitness = currParticle.bestFitness;
				
				double ParticleFitnessAverage = 0;
				
				int counter = 0;
				for(int i = 0 ; i < iterationNumber ; i++){
					if(averageFitnesses.get(particleNumber)[i] > 0){
						ParticleFitnessAverage += averageFitnesses.get(particleNumber)[i];
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

	/**
	 * will calculate the fitness value of the current particle's solution
	 * 
	 * @param runTime: the list of execution times of all cloudlets on all VMs 
	 * @param positionsArrList: the positions matrix found by current particle
	 * 
	 * @return double fitness value
	 */	
	private double ObjectiveFunction(ArrayList<double[]> runTime, ArrayList<int[]> positionsArrList) {
		
		double[] sum = new double[m];

		for (int i = 0; i < m; i++) {

			double[] time = runTime.get(i);
			int[] pos = positionsArrList.get(i);

			int n = pos.length;

			for (int j = 0; j < n; j++) {
				if (pos[j] == 1) {
					sum[i] = sum[i] + time[j];
				}
			}
		}

		double result = 0;

		// will find the highest execution time among all
		for (int i = 0; i < m; i++) {
			if (result < sum[i]) {
				result = sum[i];
			}
		}
		
		return result;
	}
	
	/**
	 * will return an integer array of the vm to cloudlet mapping
	 * 
	 * @param bestGlobalPositions: best found positions array based on the best fitness found/ minimum makespan
	 * 
	 * @return array of integers
	 */	
	private int[] returnVM2CloudLetArray(ArrayList<int[]> bestGlobalPositions){
		
		int cloudLetNumbers = n;
		
		int[] cloudLetPositions = new int[cloudLetNumbers];
		
		for(int i = 0 ; i < m ; i++){
			
			int[] vm = bestGlobalPositions.get(i);
			
			for(int j = 0 ; j < n ; j++){
				
				if(vm[j] == 1){
					cloudLetPositions[j] = i;
				}
			}
		}
		
		return cloudLetPositions;		
	}

	/**
	 * this function will make sure that all tasks are assigned to one of the VMs
	 * 
	 * @param list: positions/velocity matrix
	 * @param assignedTasksArray: the tracking array of the 0's and 1's when assiging VMs to cloudlets 
	 * 
	 * @return positions/velocity matrix
	 */	
	private ArrayList<int[]> checkResourceAssignmentForNonAssignedTasks(ArrayList<int[]> list, int[] assignedTasksArray) {

		ArrayList<int[]> newArrList = list;

		// check if task is not yet assigned
		for (int i = 0; i < assignedTasksArray.length; i++) {

			if (assignedTasksArray[i] == 0) {
				
				int x = ran.nextInt(m);

				int[] positions = newArrList.get(x);
				positions[i] = 1;

				newArrList.set(x, positions);
			}
		}

		return newArrList;
	}
}