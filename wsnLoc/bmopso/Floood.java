package wsnLoc.bmopso;

import java.util.ArrayList;

import wsnLoc.ext.*;

public class Floood {

	public FloodResult Start(ArrayList<NodesToRange> nodesToRangeList) {

		ArrayList<SensorsReachedSensors> nodesReach = new ArrayList<SensorsReachedSensors>();

		// init the nodesReach table
		for (int i = 0; i < nodesToRangeList.size(); i++) {
			NodesToRange node = nodesToRangeList.get(i);

			SensorsReachedSensors a = new SensorsReachedSensors();
			a.sensorNode = node.sender;
			a.reachedSensors = new ArrayList<Node>();
			a.distance = new ArrayList<Double>();
			a.distance.add(0.0);

			if (node.sender.type == SensorsLocations.SensorNode) {
				a.localized = false;
			}

			else {
				// to start flooding from anchors
					a.localized = true;
			}

			a.broadcasted = false;

			nodesReach.add(a);
		}

		boolean notDone = true;

		int numberOfNodesLocalized = 0;
		
		int time = 0;
		
		int mid = 0;
		int min = 0;
		int max = 0;
		
		// to make sure that all localized sensors broadcasted, to contribute in
		// localizing other nodes
		while (notDone == true) {

			notDone = false;

			for (int i = 0; i < nodesReach.size(); i++) {
				SensorsReachedSensors senderInfo = nodesReach.get(i);

				Node sender = senderInfo.sensorNode;

				// if the sensor is localized it can help in localizing other
				// sensors or wait to be localized
				if (senderInfo.localized && !senderInfo.broadcasted) {

					// to allow each node broadcast only once during the
					// localization process
					
					nodesReach.get(i).broadcasted = true;
					
					for (int j = 0; j < nodesReach.size(); j++) {
						SensorsReachedSensors recieverInfo = nodesReach.get(j);
						Node reciever = recieverInfo.sensorNode;

						int xDiff = Math.abs(sender.x - reciever.x);
						int yDiff = Math.abs(sender.y - reciever.y);

						double distance = Math.sqrt(Math.pow(xDiff, 2)
								+ Math.pow(yDiff, 2));

						// if distance is less or equal the distance chosen by
						// PSO then the receiver will receive
						if (distance <= nodesToRangeList.get(i).range && !nodesReach.get(j).localized) {
							
							if(!nodesReach.get(j).reachedSensors.contains(sender)){
								nodesReach.get(j).reachedSensors.add(sender);
							}
							
							notDone = true;
							
							//System.out.print(nodesToRangeList.get(i).sender.id + " \t" + nodesReach.get(j).sensorNode.id);
							//System.out.println("\t"+nodesToRangeList.get(i).range);
						}
						
						//if the node was able to contact 3 localized nodes it will be localized
						if(nodesReach.get(j).reachedSensors.size() >= 3 && !nodesReach.get(j).localized){
							//System.out.println("id> "+nodesReach.get(j).sensorNode.id);
							nodesReach.get(j).localized = true;
							numberOfNodesLocalized++;
						}
						
						//System.out.println("Number of transmissions = "+);
					}
					
					if(nodesToRangeList.get(i).range == ZigBee_powerConsumption.MinRange)
						min += 1;					
					if(nodesToRangeList.get(i).range == ZigBee_powerConsumption.MidRange)
						mid +=1;
					if(nodesToRangeList.get(i).range == ZigBee_powerConsumption.MaxRange)
						max +=1;					
				}
				
				time++;
			}
		}
		
		FloodResult result = new FloodResult();
		result.localizedNodesNumber = numberOfNodesLocalized;
		result.time = time;
		result.minRange = min;
		result.midRange = mid;
		result.MaxRange = max;
		
		double energyConsumption = max*ZigBee_powerConsumption.MaxRangePowerConsumption + mid*ZigBee_powerConsumption.MidRangePowerConsumption
				+min*ZigBee_powerConsumption.MinRangePowerConsumption;

		result.energyConsumption = energyConsumption;
		
		return result;
	}
}