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
