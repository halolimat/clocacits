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
		
		Floood flooding = new Floood();
		FloodResult floodResult = flooding.Start(nodesToRangeList);
		
		System.out.println("Localized Nodes = "+floodResult.localizedNodesNumber);
		System.out.println("Time = "+floodResult.time);
		System.out.println("Min Ranges = "+floodResult.minRange);
		System.out.println("Mid Ranges = "+floodResult.midRange);
		System.out.println("Max Ranges = "+floodResult.MaxRange);
		System.out.println("energy = " + floodResult.energyConsumption);
	}
}
