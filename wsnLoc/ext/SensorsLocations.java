/*------------------------------------------------------------------------
 * 
 * this file is part of the PSO methods for WSN Localization.
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

package wsnLoc.ext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class SensorsLocations {

	public static int AnchorNode = 1;
	public static int SensorNode = 2;
	
	public static int maxXindex = 1000;
	public static int maxYindex = 1000;
	
	public static int numberOfSensors = 200;
	public static int numberOfAnchors = 40;
	
	@SuppressWarnings("unchecked")
	public ArrayList<Node> ReadLocations(String FileName) throws IOException, ClassNotFoundException{
		FileInputStream fis = new FileInputStream("DefinedSensorLocations"+ File.separator + FileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        
        ArrayList<Node> nodesList = new ArrayList<Node>();
        
        nodesList = (ArrayList<Node>) ois.readObject();
        
        ois.close();
        
        return nodesList;
	}
	
	//R1 -- based on Time-bounded paper / no longer used .. instead ZigBee 3 ranges are used
	public int getMinimumRange(){
		
		double r1 = Math.sqrt(Math.pow(maxXindex, 2)/numberOfSensors);
		
		return (int)r1;
	}
	
	//R2
	public int getMaximumRange(){
		double r2 = Math.sqrt(Math.pow(maxXindex, 2) * 4/numberOfSensors);
		
		return (int)r2;
	}
}
