/*------------------------------------------------------------------------
 * 
 * this file is part of the BMOPSO method. 
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

package wsnLoc.bmopso;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import wsnLoc.ext.*;

public class PSOClass {

	// number of Nodes
	int numberNodes;

	// number of PowerLevels
	int numberPowerLevels;

	int numberOfParticles = 100;
	int numberOfIterations = 200;

	// objective 1 = time, objective 2 = number of nodes Localized
	int numberOfObjectives = 2;

	ArrayList<Particle> swarm;
	ArrayList<Particle> leaders;

	static Random ran = null;

	@SuppressWarnings("rawtypes")
	private Comparator crowdingDistanceComparator;
	@SuppressWarnings("rawtypes")
	private Comparator dominanceComparator;
	@SuppressWarnings("rawtypes")
	private Comparator equalityComparator;
	
	private CrowdingDistanceClass crowdingDistance;
	
	private BinaryMutation binaryMutation;
		
	//constants
	double c1, c2;
	int r1,r2, minV, maxV;
		
	ArrayList<double[]> particlesFitness;

	public ArrayList<Particle> run(ArrayList<Node> nodesList) {

		//initialize
		crowdingDistanceComparator = new CrowdingDistanceComparator();
		dominanceComparator = new DominanceComparator();
		equalityComparator = new EqualityComparator();
		crowdingDistance = new CrowdingDistanceClass();
		binaryMutation = new BinaryMutation();
		
		ran = new Random();
		
		particlesFitness = new ArrayList<double[]>();

		numberNodes = nodesList.size();
		numberPowerLevels = ZigBee_powerConsumption.NumberPowerLevels;

		swarm = new ArrayList<Particle>();
		leaders = new ArrayList<Particle>();

		//constants
		c1 = 1.49445; // cognitive/local weight
		c2 = 1.49445; // social/global weight
		minV = 0;
		maxV = 1;
		
		// ---------------------------------------------------------------------------------------------

		for (int l = 0; l < numberOfParticles; ++l) {
			
			double crowdingDistance = 0;
			
			// +(positions)++++++++++++++++++++++++++++++++
			ArrayList<int[]> initPositions = initPositions();
			// +(Velocity)+++++++++++++++++++++++++++++++++++++
			ArrayList<double[]> initVelocities = initVelocities();
			
			//calculate Fitness
			discFloodResult fitness = ObjectiveFunction(initPositions, nodesList);

			//create particle
			Particle newParticle = new Particle(initPositions, fitness,	
					initVelocities, initPositions, fitness, crowdingDistance);

			// Add Particle to swarm
			swarm.add(newParticle);

			// Add Particle to Leaders Archive +++++++
			if (AddToArchive(newParticle)) {
				leaders.add(newParticle);
			}
			
			//initialize average fitness
			double[] particleFitness = new double[numberOfIterations];
			particlesFitness.add(particleFitness);
		}
		
		// sort leaders according to each objective
		// set crowding distance for each particle according to each objective
		leaders = crowdingDistance.crowdingDistanceAssignment(leaders, numberOfObjectives);

		// --------------------------------------------------------------------------------------------------
		// --------------------------------------------------------------------------------------------------

		discFloodResult newFitness;
		
		for (int iter = 0; iter < numberOfIterations; iter++) {

			for (int l = 0; l < numberOfParticles; ++l) {

				Particle currParticle = swarm.get(l);

				// 1 +(Velocity)++++++++++++++++++++++++++++
				currParticle.velocitiesMatrix = calculateNewVelocities(currParticle, l, iter);
				
				// 2 +(positions)+++++++++++++++++++++++++++
				currParticle.positionsMatrix = calculateNewPositions(currParticle);

				// 3 positions binary mutation
				swarm = binaryMutation.mopsoMutation(swarm);

				// 4 evaluate new positions
				newFitness = ObjectiveFunction(currParticle.positionsMatrix, nodesList);
				currParticle.fitness = newFitness;

				// 5 compare the particle with its best fitness
				@SuppressWarnings("unchecked")
				int flag = dominanceComparator.compare(currParticle.fitness, currParticle.bestFitness);

				if (flag != 1) {
					currParticle.bestFitness = currParticle.fitness;
					currParticle.bestPositionsMatrix = currParticle.positionsMatrix;
				}
				
				// 6 update leaders
				if (AddToArchive(currParticle)) {
					leaders.add(currParticle);
				}

				// 7 calculate crowding distances in Leaders Archive
				leaders = crowdingDistance.crowdingDistanceAssignment(leaders, numberOfObjectives);
				
				// 8 insert new fitness in the fitness list
				double[] particleFitness = particlesFitness.get(l);
				particleFitness[iter] = currParticle.fitness.energyConsumption / currParticle.fitness.time;
				particlesFitness.set(l, particleFitness);
			}
		}
		
		System.out.println();
		
		for(int i = 0 ; i < leaders.size() ; i++){
			String TAB = "\t";
			
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("SimulationOutput" + File.separator + "BestFitnessAllParticles.txt", true)));
				
				out.println(leaders.get(i).bestFitness.time + TAB + leaders.get(i).bestFitness.localizedNodesNumber + TAB + 
						leaders.get(i).bestFitness.energyConsumption);
				
				out.close();
				
			}catch (IOException e) {}
		}

		return leaders;
	}
	
	private ArrayList<int[]> initPositions(){
		
		ArrayList<int[]> initPositions = new ArrayList<int[]>();
		
		// will initially used the max transmit range for all sensors
		for (int i = 0; i < numberNodes; i++) {

			int[] initRange = new int[numberPowerLevels];

			// min
			initRange[0] = 0;
			// mid
			initRange[1] = 0;
			// max .. will be used
			initRange[2] = 1;

			initPositions.add(initRange);
		}
		
		return initPositions;
	}
	
	private ArrayList<double[]> initVelocities(){
		ArrayList<double[]> initVelocities = new ArrayList<double[]>();

		for (int i = 0; i < numberNodes; i++) {

			double[] randomVelocities = new double[numberPowerLevels];

			for (int j = 0; j < numberPowerLevels; j++) {
				randomVelocities[j] = 0.0;
			}

			initVelocities.add(randomVelocities);
		}
		
		return initVelocities;
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<double[]> calculateNewVelocities(Particle currParticle, int particleNumber, int iter){

		//choose one of the leaders randomly
		Particle ArchiveBestParticle;

		// if there is only one leader
		if(leaders.size() > 1){
			Particle firstLeader = leaders.get(ran.nextInt(leaders.size() - 1));
			Particle secondLeader = leaders.get(ran.nextInt(leaders.size() - 1));

			if (crowdingDistanceComparator.compare(firstLeader, secondLeader) < 1) {
				ArchiveBestParticle = firstLeader;
			} else {
				ArchiveBestParticle = secondLeader;
			}
		}
		
		else{
			ArchiveBestParticle = leaders.get(0);
		}
		
		// --------------------------------------------------------------------		
		
		double w = 0.1;
		
		ArrayList<double[]> newVelocitiesMatrix = new ArrayList<double[]>();
		
		// calculate velocities +++++++++++++++++++++++++++++++++++++++++++++
		for (int i = 0; i < currParticle.velocitiesMatrix.size(); i++) {

			double[] velocities = currParticle.velocitiesMatrix.get(i);
			int[] bestPositions = currParticle.bestPositionsMatrix.get(i);
			int[] postitons = currParticle.positionsMatrix.get(i);

			double[] newVelocities = new double[numberPowerLevels];

			// length - 1 => v(t+1) = w*v(t) ..
			for (int j = 0; j < velocities.length - 1; j++) {
				r1 = ran.nextInt(2);
				r2 = ran.nextInt(2);

				// velocity vector
				newVelocities[j] = (w * velocities[j + 1] + c1 * r1
						* (bestPositions[j] - postitons[j]) + c2
						* r2
						* (ArchiveBestParticle.bestPositionsMatrix
								.get(i)[j] - postitons[j]));

				if (newVelocities[j] < minV) {
					newVelocities[j] = minV;
				}

				else if (newVelocities[j] > maxV) {
					newVelocities[j] = maxV;
				}
			}

			// add the new velocities into the arrayList
			newVelocitiesMatrix.add(newVelocities);
		}
		
		return newVelocitiesMatrix;
	}
	
	private ArrayList<int[]> calculateNewPositions(Particle currParticle){
		
		ArrayList<int[]> newPositionsMatrix = new ArrayList<int[]>();
		
		// keep track of the zeros and ones in the arrayList per nodeRange
		int[] assignedRangeInPositionsMatrix = new int[numberNodes];

		for (int i = 0; i < currParticle.velocitiesMatrix.size(); i++) {
			double[] velocities = currParticle.velocitiesMatrix.get(i);

			int[] newPosition = new int[numberPowerLevels];

			// length - 1 => v(t+1) = w*v(t) ..
			for (int j = 0; j < velocities.length - 1; j++) {
				int random = ran.nextInt(2);

				// if a node is already assigned a range then wont enter
				if (assignedRangeInPositionsMatrix[j] == 0) {

					// to calculate sigmoid function
					double sig = 1 / (1 + Math.exp(-velocities[j]));

					if (sig > random) {
						newPosition[j] = 1;
					}

					else {
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

			// add the new velocities into the arrayList
			newPositionsMatrix.add(newPosition);
		}

		return checkNodeToRangeAssignment(newPositionsMatrix, assignedRangeInPositionsMatrix);
	}

	private discFloodResult ObjectiveFunction(ArrayList<int[]> positionsArrList,
			ArrayList<Node> nodesList) {

		double[] range = { ZigBee_powerConsumption.MinRange, ZigBee_powerConsumption.MidRange, ZigBee_powerConsumption.MaxRange };

		ArrayList<NodesToRange> nodesToRangeList = new ArrayList<NodesToRange>();

		for (int i = 0; i < numberNodes; i++) {
			NodesToRange node = new NodesToRange();
			node.sender = nodesList.get(i);

			int[] pos = positionsArrList.get(i);

			for (int j = 0; j < numberPowerLevels; j++) {
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
	 * this function will make sure that all nodes have a transmit power level chosen
	 * 
	 * @param list
	 *            : positions/velocity matrix
	 * @param assignedTasksArray
	 *            : the tracking array of the 0's and 1's
	 * 
	 * @return positions/velocity matrix
	 */
	private ArrayList<int[]> checkNodeToRangeAssignment(ArrayList<int[]> list,
			int[] assignedTasksArray) {

		ArrayList<int[]> newArrList = list;

		// check if task is not yet assigned
		for (int i = 0; i < list.size(); i++) {

			if (assignedTasksArray[i] == 0) {

				int x = ran.nextInt(numberPowerLevels);

				int[] positions = list.get(i);
				positions[x] = 1;

				newArrList.set(i, positions);
			}
		}

		return newArrList;
	}

	@SuppressWarnings("unchecked")
	private boolean AddToArchive(Particle solution) {

		int flag = 0;
		int i = 0;
		Particle aux; // Store an solution temporally

		while (i < leaders.size()) {

			aux = leaders.get(i);
			flag = dominanceComparator.compare(solution.bestFitness, aux.bestFitness);

			if (flag == 1) { // The solution to add is dominated
				return false; // Discard the new solution
			}

			else if (flag == -1) { // A solution in the archive is dominated
				leaders.remove(i); // Remove it from the population
			}

			else {// There is an equal solution in the population

				int res = equalityComparator.compare(solution, aux);

				// if aux solution is better
				if (res == 1) {
					return false; // Discard the new solution
				}

				// if they are identical
				else if (res == 0) {
					leaders.remove(i);
				}

				// if solution is better
				else {
					i++;
				}
			}
		}
		
		return true;
	}
}