/*------------------------------------------------------------------------
 * 
 * this file is part of the CMOPSO method. 
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

package wsnLoc.cmopso;

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
	
	private BoundaryMutation uniformMutation;
		
	//constants
	double r1,r2;
	
	public ArrayList<Particle> run(ArrayList<Node> nodesList) {

		//initialize
		crowdingDistanceComparator = new CrowdingDistanceComparator();
		dominanceComparator = new DominanceComparator();
		equalityComparator = new EqualityComparator();
		crowdingDistance = new CrowdingDistanceClass();
		uniformMutation = new BoundaryMutation();
		ran = new Random();
		
		numberNodes = nodesList.size();
		numberPowerLevels = ZigBee_powerConsumption.NumberPowerLevels;

		swarm = new ArrayList<Particle>();
		leaders = new ArrayList<Particle>();
						
		// ---------------------------------------------------------------------------------------------

		for (int l = 0; l < Constants.numberOfParticles; ++l) {
			
			double crowdingDistance = 0;
			
			// +(positions)++++++++++++++++++++++++++++++++
			ArrayList<Double> initPositions = initPositions();
			// +(Velocity)+++++++++++++++++++++++++++++++++++++
			ArrayList<Double> initVelocities = initVelocities();
			
			//calculate Fitness
			FloodResult fitness = ObjectiveFunction(initPositions, nodesList);

			//create particle
			Particle newParticle = new Particle(initPositions, fitness,	
					initVelocities, initPositions, fitness, crowdingDistance);

			// Add Particle to swarm
			swarm.add(newParticle);

			// Add Particle to Leaders Archive +++++++
			if (AddToArchive(newParticle)) {
				leaders.add(newParticle);
			}
		}
		
		// sort leaders according to each objective
		// set crowding distance for each particle according to each objective
		leaders = crowdingDistance.crowdingDistanceAssignment(leaders, Constants.numberOfObjectives);

		// --------------------------------------------------------------------------------------------------
		// --------------------------------------------------------------------------------------------------

		FloodResult newFitness;
		
		for (int iter = 0; iter < Constants.numberOfIterations; iter++) {

			for (int l = 0; l < Constants.numberOfParticles; ++l) {

				Particle currParticle = swarm.get(l);

				// 1 +(Velocity)++++++++++++++++++++++++++++
				currParticle.velocitiesMatrix = calculateNewVelocities(currParticle, l, iter);
				
				// 2 +(positions)+++++++++++++++++++++++++++
				currParticle.positionsMatrix = calculateNewPositions(currParticle);

				// 3 positions mutation
				swarm = uniformMutation.mopsoMutation(swarm);

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
				leaders = crowdingDistance.crowdingDistanceAssignment(leaders, Constants.numberOfObjectives);
			}
			
			if(iter%20 == 0){
				System.out.print("\t"+iter);
			}
		}
		
		System.out.println();
		
		for(int i = 0 ; i < leaders.size() ; i++){
			String TAB = "\t";
			
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("SimulationOutput" + File.separator + "BestFitnessAllParticles.txt", true)));
				
				double averageRangesUsed = leaders.get(i).bestFitness.sumRanges/leaders.get(i).bestFitness.localizedNodesNumber;
				
				out.println(
					//1
					(i+1) + TAB +
					//2
					leaders.get(i).bestFitness.time + TAB +
					//3
					averageRangesUsed + TAB +
					//4
					leaders.get(i).bestFitness.energyConsumption + TAB +
					//5
					leaders.get(i).bestFitness.localizedNodesNumber
				);
				
				out.close();
				
			}catch (IOException e) {}
		}

		return leaders;
	}
	
	private ArrayList<Double> initPositions(){
		
		ArrayList<Double> initPositions = new ArrayList<Double>();
		
		// will initially used the max transmit range for all sensors
		for (int i = 0; i < numberNodes; i++) {
			initPositions.add(0.0);
		}
		
		return initPositions;
	}
	
	private ArrayList<Double> initVelocities(){
	
		ArrayList<Double> initVelocities = new ArrayList<Double>();

		for (int i = 0; i < numberNodes; i++) {
			
			double lo = -1.0 * Math.abs(Constants.maximumTransmissionRange - Constants.minimumTransmissionRange);
            double hi = Math.abs(Constants.maximumTransmissionRange - Constants.minimumTransmissionRange);
            
			initVelocities.add((hi - lo) * ran.nextDouble() + lo);
		}
		
		return initVelocities;
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<Double> calculateNewVelocities(Particle currParticle, int particleNumber, int iter){

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
		
		// calculate InertiaValue
		double w = Constants.w;
		
		ArrayList<Double> newVelocitiesMatrix = new ArrayList<Double>();
		
		// calculate velocities +++++++++++++++++++++++++++++++++++++++++++++
		for (int i = 0; i < currParticle.velocitiesMatrix.size(); i++) {
			
			double velocities = currParticle.velocitiesMatrix.get(i);
			double bestPositions = currParticle.bestPositionsMatrix.get(i);
			double postitons = currParticle.positionsMatrix.get(i);

			r1 = ran.nextDouble();
			r2 = ran.nextDouble();

			// velocity vector
			double newVelocities = (w * velocities + Constants.c1 * r1
					* (bestPositions - postitons) + Constants.c2
					* r2
					* (ArchiveBestParticle.bestPositionsMatrix
							.get(i) - postitons));

			double delta = (Constants.maximumTransmissionRange - Constants.minimumTransmissionRange)/2;
			
			if (newVelocities < delta) {
				newVelocities = Constants.minimumTransmissionRange;
			}

			else if (newVelocities >= delta) {
				newVelocities = delta;
			}
			
			// add the new velocities into the arrayList
			newVelocitiesMatrix.add(newVelocities);
		}
		
		return newVelocitiesMatrix;
	}
	
	private ArrayList<Double> calculateNewPositions(Particle currParticle){
		
		ArrayList<Double> newPositionsMatrix = new ArrayList<Double>();
		
		// keep track of the zeros and ones in the arrayList per nodeRange
	
		for (int i = 0; i < currParticle.velocitiesMatrix.size(); i++) {
			
			//why not random double?
			double newPosition = ran.nextInt(Constants.maximumTransmissionRange) + currParticle.velocitiesMatrix.get(i);
			
			if (newPosition < Constants.minimumTransmissionRange){
              newPosition = Constants.minimumTransmissionRange;
			}
			
            else if (newPosition > Constants.maximumTransmissionRange){
              newPosition = Constants.maximumTransmissionRange;
            }
			
            else{
            	//take the value calculated if its within the range
            }
			
			newPositionsMatrix.add(newPosition);
		}

		return newPositionsMatrix;
	}

	private FloodResult ObjectiveFunction(ArrayList<Double> positionsArrList,
			ArrayList<Node> nodesList) {

		ArrayList<NodesToRange> nodesToRangeList = new ArrayList<NodesToRange>();

		for (int i = 0; i < numberNodes; i++) {
			NodesToRange node = new NodesToRange();
			node.sender = nodesList.get(i);
			
			node.range = positionsArrList.get(i);

			nodesToRangeList.add(node);
		}

		discLevelsFlood flooding = new discLevelsFlood();

		return flooding.Start(nodesToRangeList);
	}

	@SuppressWarnings("unchecked")
	private boolean AddToArchive(Particle solution) {

		int flag = 0;
		int i = 0;
		Particle aux; // Store a solution temporally

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