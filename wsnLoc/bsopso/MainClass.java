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

import java.io.IOException;
import java.util.ArrayList;

import wsnLoc.ext.*;

public class MainClass {

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		
		@SuppressWarnings("unused")
		ZigBee_powerConsumption a = new ZigBee_powerConsumption();
		
		ArrayList<Node> nodesList = new SensorsLocations().ReadLocations("200_sensor_40_anchor_1000_area_2.wsnLoc");
		//getPSONodesPowerLevels(nodesList);
		noPSO(nodesList);
	}
	
	public static void getPSONodesPowerLevels(ArrayList<Node> nodesList){
		PSOClass a = new PSOClass();
		
		for(int i = 0 ; i < 50 ; i++){
			System.out.println("\n"+(i+1));
			a.run(nodesList);
		}
		
	}
	
	public static void noPSO(ArrayList<Node> nodesList){
		
		ArrayList<NodesToRange> nodesToRangeList = new ArrayList<NodesToRange>();
		
		for(int i = 0 ; i < nodesList.size() ; i++){
			NodesToRange node = new NodesToRange();
			node.sender = nodesList.get(i);
			
			node.range = ZigBee_powerConsumption.MaxRange;
			//node.range = ZigBee.MidRange;
			//node.range = ZigBee.MinRange;
			
			nodesToRangeList.add(node);
		}
		
		discPowerLevelsFlood flooding = new discPowerLevelsFlood();
		discFloodResult floodResult = flooding.Start(nodesToRangeList);
		
		System.out.println("Localized Nodes = "+floodResult.localizedNodesNumber);
		System.out.println("Time = "+floodResult.time);
		System.out.println("Min Ranges = "+floodResult.minRange);
		System.out.println("Mid Ranges = "+floodResult.midRange);
		System.out.println("Max Ranges = "+floodResult.MaxRange);
		System.out.println("energy = " + floodResult.energyConsumption);
	}
}
