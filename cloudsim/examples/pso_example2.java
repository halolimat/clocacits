package cloudsim.examples;

/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 * 
 * Edited by: Hussein S. Al-Olimat
 * email: hussein.alolimat@msn.com
 * 
 * based on: example 3
 * 
 */

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.VmSchedulerTimeSharedOverSubscription;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.util.WorkloadFileReader;
import ut.Processors;

/**
 * A simple example showing how to create a datacenter with one host and run one
 * cloudlet on it.
 */
public class pso_example2 {

	static FileOutputStream out;
	static PrintStream ps;
	
	static ArrayList<Vm> vmlist;

	/**
	 * Creates main() to run this example.
	 * 
	 * @param args
	 *            the args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		try {
			out = new FileOutputStream("Simulation Files/simulation_output.txt");
		} catch (FileNotFoundException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
		ps = new PrintStream(out);
		
		
		for (int i = 0; i < 100; i++) {
			vmlist = new ArrayList<Vm>();
			initSimulation();
			
			System.out.println(i+1);
		}

		ps.close();
	}

	private static void initSimulation() {
		Log.printLine("Starting CloudSim simulation Example using PSO...");

		try {

			// <<< [1]: Initialize the CloudSim package. It should be called
			// before creating any entities. >>>

			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance(); // get calendar using
														// current time zone
			boolean trace_flag = false; // mean trace events
			CloudSim.init(num_user, calendar, trace_flag); // Initialize the
															// CloudSim library

			// <<< [2]: Create Datacenters >>>

			Datacenter datacenter1 = createDatacenter("Datacenter_1");

			// <<< [3]: Create Cloud Broker and name it Broker1 >>>
			DatacenterBroker broker = createBroker(1);
			int brokerId = broker.getId(); // gets the id of the created broker

			// <<< [4]: Create 5 virtual machines that uses time shared
			// scheduling >>>
			addVMs(4, brokerId, true, 6000);//0-3
			addVMs(6, brokerId, true, 12000);//4-9
			
			// <<< [5]: submit vm list to the broker >>>
			broker.submitVmList(vmlist);

			// <<< [6]: Read the workload file and create Cloudlets from it
			List<Cloudlet> cloudletList = createCloudLets();

			// <<< [7]: assign specific VMs to run specific cloudlets
			// --------------------------------------------

			broker.UsePSO();

			for (Cloudlet cloudlet : cloudletList) {
				// set all cloudlets to be managed by one broker.
				cloudlet.setUserId(brokerId);
			}

			// ---------------------------------------------------------------------------------------------------

			// <<< [8]: submit cloudlet list to the broker >>>
			broker.submitCloudletList(cloudletList);

			// <<< [9]: Starts the simulation >>>

			// start the simulation
			CloudSim.startSimulation();

			// stop the simulation
			CloudSim.stopSimulation();

			// <<< [10]: Print results when simulation is over

			// retrieve all recieved cloudlet list
			List<Cloudlet> newList = broker.getCloudletReceivedList();

			// print the list
			printCloudletList(newList);

			// Print the debt of each user to each datacenter
			// datacenter1.printDebts();

			Log.printLine("CloudSimExample finished!");

		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
	}

	/**
	 * Creates the datacenter.
	 * 
	 * desc: Datacenters are the resource providers in CloudSim. We need at
	 * least one of them to run a CloudSim simulation
	 * 
	 * @param name
	 *            the name of the data center
	 * 
	 * @return the datacenter
	 */
	private static Datacenter createDatacenter(String name) {

		// Here are the steps needed to create a PowerDatacenter:

		// 1. We need to create a list to store our machine
		List<Host> hostList = new ArrayList<Host>();

		// 2. create hosts, where Every Machine contains one or more PEs or
		// CPUs/Cores

		// ((( Host 1 )))---------------------------
		List<Pe> Host_1_peList = new ArrayList<Pe>();

		// get the mips value of the selected processor
		int Host_1_mips = Processors.Intel.Core_2_Extreme_X6800.mips;
		// get processor's number of cores
		int Host_1_cores = Processors.Intel.Core_2_Extreme_X6800.cores;

		// 3. Create PEs and add these into a list.
		for (int i = 0; i < Host_1_cores; i++) {
			// mips/cores => MIPS value is cumulative for all cores so we divide
			// the MIPS value among all of the cores
			Host_1_peList.add(new Pe(i, new PeProvisionerSimple(Host_1_mips
					/ Host_1_cores))); // need to store Pe id and MIPS Rating
		}

		// 4. Create Host with its id and list of PEs and add them to the list
		// of machines
		int host_1_ID = 1;
		int host_1_ram = 4096; // host memory (MB)
		long host_1_storage = 1048576; // host storage in MBs
		int host_1_bw = 10240; // bandwidth in MB/s

		hostList.add(new Host(host_1_ID, new RamProvisionerSimple(host_1_ram),
				new BwProvisionerSimple(host_1_bw), host_1_storage,
				//Host_1_peList, new VmSchedulerTimeShared(Host_1_peList)));
				Host_1_peList, new VmSchedulerTimeSharedOverSubscription(Host_1_peList)));

		// ((( /Host 1 )))---------------------------

		// ((( Host 2 )))---------------------------

		List<Pe> Host_2_peList = new ArrayList<Pe>();

		// get the mips value of the selected processor
		int Host_2_mips = Processors.Intel.Core_2_Extreme_QX6700.mips;
		// get processor's number of cores
		int Host_2_cores = Processors.Intel.Core_2_Extreme_QX6700.cores;

		// 3. Create PEs and add these into a list.
		for (int i = 0; i < Host_2_cores; i++) {
			// mips/cores => MIPS value is cumulative for all cores so we divide
			// the MIPS value among all of the cores
			Host_2_peList.add(new Pe(i, new PeProvisionerSimple(Host_2_mips
					/ Host_2_cores))); // need to store Pe id and MIPS Rating
		}

		// 4. Create Host with its id and list of PEs and add them to the list
		// of machines
		int host_2_id = 2;
		int host_2_ram = 4096; // host memory (MB)
		long host_2_storage = 1048576; // host storage in MBs
		int host_2_bw = 10240; // bandwidth in MB/s

		hostList.add(new Host(host_2_id, new RamProvisionerSimple(host_2_ram),
				new BwProvisionerSimple(host_2_bw), host_2_storage,
				//Host_2_peList, new VmSchedulerTimeShared(Host_2_peList)));
				Host_2_peList, new VmSchedulerTimeSharedOverSubscription(Host_2_peList)));

		// ((( /Host 2 )))---------------------------

		// 5. Create a DatacenterCharacteristics object that stores the
		// properties of a data center: architecture, OS, list of
		// Machines, allocation policy: time- or space-shared, time zone
		// and its price (G$/Pe time unit).
		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this
										// resource
		double costPerBw = 0.0; // the cost of using bw in this resource
		
		// we are not adding SAN devices by now
		LinkedList<Storage> storageList = new LinkedList<Storage>();

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				arch, os, vmm, hostList, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);

		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics,
					new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}

	/**
	 * Creates the broker.
	 * 
	 * @param id
	 *            : the broker id
	 * 
	 * @return the datacenter broker
	 */
	private static DatacenterBroker createBroker(int id) {
		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker" + id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Creates the virtual machines.
	 * 
	 * @param VMNr
	 *            : the number of virtual machines to create brokerId: the id of
	 *            the broker created timeSharedScheduling: to choose between the
	 *            time shared or space shared shceduling algorithms
	 * 
	 * @return list of virtual machines
	 * 
	 */
	private static void addVMs(int VMNr, int brokerId, boolean timeSharedScheduling, int mips) {

		// VM description
		//int mips = Processors.Intel.Pentium_4_Extreme_Edition.mips;
		//int mips = Processors.AMD.Athlon_FX_57.mips;
		long size = 10240; // image size (MB)
		int ram = 512; // vm memory (MB)
		long bw = 1024; // MB/s
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name

		for (int i = 0; i < VMNr; i++) {

			Vm vm;
			
			int VM_ID = vmlist.size();
			
			if (timeSharedScheduling) {
				// create VM that uses time shared scheduling to schedule Cloudlets
				vm = new Vm(VM_ID, brokerId, mips, pesNumber, ram, bw, size, vmm,
						new CloudletSchedulerTimeShared());
			}

			else {
				// create VM that uses space shared scheduling to schedule Cloudlets
				vm = new Vm(VM_ID, brokerId, mips, pesNumber, ram, bw, size, vmm,
						new CloudletSchedulerSpaceShared());
			}

			// add the VM to the vmList
			vmlist.add(vm);
		}
	}

	/**
	 * generate cloudlets from the workload file
	 * 
	 * @return list of cloudlets
	 * 
	 */
	private static List<Cloudlet> createCloudLets()
			throws FileNotFoundException {

		/** The cloudlet list. */
		List<Cloudlet> cloudletList;

		// Read Cloudlets from workload file in the swf format
		WorkloadFileReader workloadFileReader = new WorkloadFileReader(
				"Simulation Files/HPC2N-2002-2.1-cln.swf", 1);

		// generate cloudlets from workload file
		cloudletList = workloadFileReader.generateWorkload();

		return cloudletList;
	}

	/**
	 * get all user ids in case we want to consider the user id as a parameter
	 * and create a broker for every user
	 * 
	 * @param cloudletList
	 * 
	 * @return list of userIDs
	 * 
	 */
	@SuppressWarnings("unused")
	private static ArrayList<Integer> getUsersIDs(List<Cloudlet> cloudletList) {

		ArrayList<Integer> usersIDs = new ArrayList<Integer>();

		ArrayList<Integer> usersLists = new ArrayList<Integer>();

		for (Cloudlet cloudlet : cloudletList) {
			usersLists.add(cloudlet.getUserId());
		}

		HashSet<Integer> uniqueValues = new HashSet<Integer>(usersLists);

		for (int value : uniqueValues) {
			usersIDs.add(value);
		}

		return usersIDs;
	}

	/**
	 * Prints the Cloudlet objects.
	 * 
	 * @param list
	 *            list of Cloudlets
	 * @throws IOException
	 */
	private static void printCloudletList(List<Cloudlet> list) throws IOException {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "\t";

		Log.printLine();
		Log.printLine("========== OUTPUT ==========");

		Log.printLine("Cloudlet_ID" + indent + "STATUS" + indent
				+ "DataCenter_ID" + indent + "VM_ID" + indent + "Time" + indent
				+ "Start_Time" + indent + "Finish_Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);

			Log.print(cloudlet.getCloudletId() + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS" + indent);

				Log.printLine(cloudlet.getResourceId() + indent
						+ cloudlet.getVmId() + indent
						+ dft.format(cloudlet.getActualCPUTime()) + indent
						+ dft.format(cloudlet.getExecStartTime()) + indent
						+ dft.format(cloudlet.getFinishTime()));
			}
		}
		
		ps.println(list.get(size-1).getFinishTime());
	}
}