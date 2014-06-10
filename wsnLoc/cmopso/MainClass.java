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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import wsnLoc.ext.*;

public class MainClass {

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		
		@SuppressWarnings("unused")
		ZigBee_powerConsumption a = new ZigBee_powerConsumption();
		
		ArrayList<Node> nodesList = new SensorsLocations().ReadLocations("200_sensor_40_anchor_1000_area_2.wsnLoc");
		//ArrayList<Node> nodesList = new SensorsLocations().ReadLocations("Locations3.wsnLoc");
		getPSONodesPowerLevels(nodesList);
	}
	
	public static void getPSONodesPowerLevels(ArrayList<Node> nodesList){
		
		Calendar cal = Calendar.getInstance();
    	cal.getTime();
		
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	System.out.println( sdf.format(cal.getTime()) );
		
		String TAB = "\t\t";
		
		PSOClass a = new PSOClass();
		
		try {
			
			//out.write("Time\tLocalized Nodes\tMin Ranges\tMid Ranges\tMax Ranges\tPower Consumption\n");
			
			for(int i = 0 ; i < 50 ; i++){
			
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("SimulationOutput" + File.separator + "out.txt", true)));
				
				System.out.println(i+1);
				
				ArrayList<Particle> leaders = a.run(nodesList);
				
				for(int j = 0 ; j < leaders.size() ; j++){
					
					Particle leader = leaders.get(j);
					
					double averageRangesUsed = leader.fitness.sumRanges/leader.fitness.localizedNodesNumber;
					
					out.println(
						//1
						(j+1) + TAB +
						//2
						leader.fitness.time + TAB +
						//3
						averageRangesUsed + TAB +
						//4
						leader.fitness.energyConsumption + TAB +
						//5
						leader.fitness.localizedNodesNumber
					);
				}
								
				out.close();
			}
			
		}catch (IOException e) {}
		
	}
}
