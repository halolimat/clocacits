package wsnLoc.bmopso;

import java.util.Comparator;

@SuppressWarnings("rawtypes")
public class DominanceComparator implements Comparator{
	
	public int compare(Object o1, Object o2) {
		
		FloodResult firstParticleFitness = (FloodResult) o1;
		FloodResult SecondParticleFitness = (FloodResult) o2;
		
		double Particle_1_obj_1_value, Particle_1_obj_2_value;
		double Particle_2_obj_1_value, Particle_2_obj_2_value;
		
		Particle_1_obj_1_value = firstParticleFitness.time;
		Particle_1_obj_2_value = firstParticleFitness.localizedNodesNumber;
		
		Particle_2_obj_1_value = SecondParticleFitness.time;
		Particle_2_obj_2_value = SecondParticleFitness.localizedNodesNumber;
		
		double particle_1_fitness = Particle_1_obj_2_value/ Particle_1_obj_1_value;
		double particle_2_fitness = Particle_2_obj_2_value/ Particle_2_obj_1_value;
		
		if (particle_1_fitness < particle_2_fitness) {
			return 1; // solution2 dominate
		}
			
		else if (particle_1_fitness > particle_2_fitness) {
			return -1; // solution1 dominate
		}
			
		else {
			return 0; // No one dominate the other
		}
	}
}
