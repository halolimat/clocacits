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

import java.util.ArrayList;

import wsnLoc.ext.*;

public class contPowerLevelsFlood {

	public FloodResult Start(ArrayList<NodesToRange> nodesToRangeList) {

		ArrayList<SensorsReachedSensors> nodesReach = new ArrayList<SensorsReachedSensors>();

		int numberOfNodesLocalized = 0;

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

				// to count the anchors as localized to be able to calculate the
				// average distances used
				numberOfNodesLocalized++;
			}

			a.broadcasted = false;

			nodesReach.add(a);
		}

		boolean notDone = true;

		int time = 0;

		double sumRanges = 0;
		double sumPowerConsumption = 0;

		ZigBee_powerConsumption zigBeeObj = new ZigBee_powerConsumption();

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
						if (distance <= nodesToRangeList.get(i).range
								&& !nodesReach.get(j).localized) {

							if (!nodesReach.get(j).reachedSensors
									.contains(sender)) {
								nodesReach.get(j).reachedSensors.add(sender);
							}

							notDone = true;
						}

						// if the node was able to contact 3 localized nodes it
						// will be localized
						if (nodesReach.get(j).reachedSensors.size() >= 3
								&& !nodesReach.get(j).localized) {
							nodesReach.get(j).localized = true;
							numberOfNodesLocalized++;
						}
					}

					sumRanges += nodesToRangeList.get(i).range;
					sumPowerConsumption += zigBeeObj
							.calculatePowerConsumption(nodesToRangeList.get(i).range);
				}

				time++;
			}
		}

		FloodResult result = new FloodResult();
		result.localizedNodesNumber = numberOfNodesLocalized;
		result.time = time;
		result.sumRanges = sumRanges;
		result.energyConsumption = sumPowerConsumption;

		return result;
	}
}