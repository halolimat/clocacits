package wsnLoc.cmopso;

public class Constants {

	//Constant parameters.. parameter study
	
	public static int numberOfParticles = 50;
	public static int numberOfIterations = 50;
	
	public static int minimumTransmissionRange = 64; // meters
	public static int maximumTransmissionRange = 132; // 132.22
	
	public static double c1 = 1.49445; // cognitive/local weight
	public static double c2 = 1.49445; // social/global weight
	
	//inertia weight
	public static double w = 0.1;
	
	public static int numberOfObjectives = 4;
	
	public static double mutationPercentage = 0.20;
	public static int mutationValue = minimumTransmissionRange;
	
}