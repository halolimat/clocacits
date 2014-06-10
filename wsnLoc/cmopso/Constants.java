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