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
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.VmSchedulerTimeSharedOverSubscription;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.util.WorkloadFileReader;

import ut.Processors;

@SuppressWarnings("unused")
public class pso_example1 {

	static FileOutputStream out;
	static PrintStream ps;
	
	static ArrayList<Vm> vmlist;

	public static void main(String[] args) throws IOException {
		
		try {
			out = new FileOutputStream("Simulation Files/simulation_output.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();

			System.exit(0);
		}
		
		ps = new PrintStream(out);
		
		vmlist = new ArrayList<Vm>();
		initSimulation();
		
		ps.close();
	}

	private static void initSimulation() {
		Log.printLine("Starting CloudSim simulation Example using PSO...");

		try {

			int num_user = 1;
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;
			CloudSim.init(num_user, calendar, trace_flag);
			
			Datacenter datacenter1 = createDatacenter("Datacenter_1");

			DatacenterBroker broker = createBroker(1);
			int brokerId = broker.getId();

			addVMs(300, brokerId, true, 0);
			
			broker.submitVmList(vmlist);

			List<Cloudlet> cloudletList = createCloudLets();

			broker.UsePSO();

			for (Cloudlet cloudlet : cloudletList) {
				cloudlet.setUserId(brokerId);
			}

			broker.submitCloudletList(cloudletList);

			CloudSim.startSimulation();

			CloudSim.stopSimulation();

			List<Cloudlet> newList = broker.getCloudletReceivedList();

			printCloudletList(newList);

			Log.printLine("CloudSimExample finished!");

		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
	}
	
	private static List<Host> createHost(int numberHosts){
		List<Host> hostList = new ArrayList<Host>();
		
		for(int i = 0 ; i < numberHosts ; i++){

			List<Pe> peList = new ArrayList<Pe>();

			int mips = Processors.Intel.Core_i7_Extreme_Edition_3960X.mips;
			int cores = Processors.Intel.Core_i7_Extreme_Edition_3960X.cores;

			for (int j = 0; j < cores; j++) {
				peList.add(new Pe(j, new PeProvisionerSimple(mips	/ cores)));
			}

			int host_ID = 1+i;
			int host_ram = 16384; //16 GB
			long host_storage = 4194304; // 4 TB
			int host_bw = 15360; // 15 GB ... Amazon EC2

			hostList.add(new Host(host_ID, new RamProvisionerSimple(host_ram),
					new BwProvisionerSimple(host_bw), host_storage,
					//peList, new VmSchedulerSpaceShared(peList)));
					peList, new VmSchedulerTimeShared(peList)));
					//peList, new VmSchedulerTimeSharedOverSubscription(peList)));
		}
				
		return hostList;
	}

	private static Datacenter createDatacenter(String name) {

		List<Host> hostList = new ArrayList<Host>();
		
		hostList = createHost(40);
		
		String arch = "x86";
		String os = "Linux";
		String vmm = "Xen";
		double time_zone = 10.0;
		double cost = 3.0;
		double costPerMem = 0.05;
		double costPerStorage = 0.001;
		double costPerBw = 0.0;
		
		LinkedList<Storage> storageList = new LinkedList<Storage>();

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				arch, os, vmm, hostList, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);

		Datacenter datacenter = null;
		
		try {
			datacenter = new Datacenter(name, characteristics, 
								new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}

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

	private static void addVMs(int VMNr, int brokerId, boolean timeSharedScheduling, int mips) {

		mips = Processors.Intel.Pentium_4_Extreme_Edition.mips;
		//mips = Processors.AMD.Athlon_FX_57.mips;
		long size = 10000;
		int ram = 512;
		long bw = 1000;
		int pesNumber = 1;
		String vmm = "Xen";

		for (int i = 0; i < VMNr; i++) {

			Vm vm;
			
			int VM_ID = vmlist.size();
			
			if (timeSharedScheduling) {
				vm = new Vm(VM_ID, brokerId, mips, pesNumber, ram, bw, size, vmm,
						new CloudletSchedulerTimeShared());
			}

			else {
				vm = new Vm(VM_ID, brokerId, mips, pesNumber, ram, bw, size, vmm,
						new CloudletSchedulerSpaceShared());
			}

			vmlist.add(vm);
		}
	}

	private static List<Cloudlet> createCloudLets()
			throws FileNotFoundException {

		List<Cloudlet> cloudletList;

		WorkloadFileReader workloadFileReader = new WorkloadFileReader(
				"Simulation Files/NASA-iPSC-1993-3.1-cln.swf", 1);

		cloudletList = workloadFileReader.generateWorkload();

		return cloudletList;
	}

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

	private static void printCloudletList(List<Cloudlet> list) throws IOException {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "\t";

		ps.println("Cloudlet_ID" + indent + "STATUS" + indent
				+ "DataCenter_ID" + indent + "VM_ID" + indent + "Time" + indent
				+ "Start_Time" + indent + "Finish_Time");
		
		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);

			ps.print(cloudlet.getCloudletId() + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				ps.print("SUCCESS" + indent);

				ps.println(cloudlet.getResourceId() + indent
						+ cloudlet.getVmId() + indent
						+ dft.format(cloudlet.getActualCPUTime()) + indent
						+ dft.format(cloudlet.getExecStartTime()) + indent
						+ dft.format(cloudlet.getFinishTime()));
			}
		}
	}
}