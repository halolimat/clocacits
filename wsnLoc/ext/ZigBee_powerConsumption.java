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

public class ZigBee_powerConsumption {

	int NodeRecieverSensitivity = -98;
	double passLossExponent = 2.5;
	int fadeMargin = 8;
	int signalFrequency = 2405;
	
	public static double MaxRange;
	public static double MidRange;
	public static double MinRange;
	
	public static double MaxRangePowerConsumption = 3.1622776602;
	public static double MidRangePowerConsumption = 1.2589254118;
	public static double MinRangePowerConsumption = 0.5011872336;
	
	public static int NumberPowerLevels = 3;
	
	public ZigBee_powerConsumption(){
		int MaxNodeATransmitPower = 5; // dBm
		int MidNodeATransmitPower = 1;
		int MinNodeATransmitPower = -3;
		
		setMaxRange(calculateRange(MaxNodeATransmitPower));
		setMidRange(calculateRange(MidNodeATransmitPower));
		setMinRange(calculateRange(MinNodeATransmitPower));
	}
	
	private void setMaxRange(double MaxRange){
		ZigBee_powerConsumption.MaxRange = MaxRange;
	}
	
	private void setMidRange(double MidRange){
		ZigBee_powerConsumption.MidRange = MidRange;
	}
	
	private void setMinRange(double MinRange){
		ZigBee_powerConsumption.MinRange = MinRange;
	}
	
	private double calculateRange(int TransmitPower){
		
		double xxx = (TransmitPower - fadeMargin - NodeRecieverSensitivity - (10 * passLossExponent * Math.log10(signalFrequency)) + 30 * passLossExponent - 32.44)/(10*passLossExponent);
		
		double range = Math.pow(10, xxx);
		
		return range;
	}
	
	public double calculatePowerConsumption(double range){
		
		double powerConsumption = 0;
		
		powerConsumption = (10*passLossExponent* Math.log10(range)) + fadeMargin + NodeRecieverSensitivity + (10 * passLossExponent * Math.log10(signalFrequency)) - (30 * passLossExponent) + 32.44;
		
		// to convert dBm to mW =>
		powerConsumption = Math.pow(10, (powerConsumption/10));
		
		return powerConsumption;
	}
}