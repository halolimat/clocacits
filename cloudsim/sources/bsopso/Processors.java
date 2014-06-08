package cloudsim.sources.bsopso;

/*
 * Title:        CloudSim Toolkit
 * 
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 *               
 * License:      GPL - http://www.gnu.org/copyleft/gpl.html
 * 
 * Implemented by: Hussein S. Al-Olimat
 * email: hussein.alolimat@msn.com
 * 
 * class description: this class have a list of some processors;
 * where each processor's MIPS and number of cores are defined
 * 
 * Values Source: http://en.wikipedia.org/wiki/Instructions_per_second#Timeline_of_instructions_per_second
 * 
 */

public class Processors {
	public static class Intel{
		
		public class Core_i7_Extreme_Edition_3960X{
			public static final int mips = 177730;
			public static final int cores = 6;
		}
		
		public class Core_i7_Extreme_Edition_980X{
			public static final int mips = 147600;
			public static final int cores = 6;
		}
		
		public class Core_i7_2600k{
			public static final int mips = 128300;
			public static final int cores = 4;
		}
		
		public class Core_i7_875K{
			public static final int mips = 92100;
			public static final int cores = 4;
		}
		
		public class Core_i7_920{
			public static final int mips = 82300;
			public static final int cores = 4;
		}
		
		public class Core_2_Extreme_QX9770{
			public static final int mips = 59455;
			public static final int cores = 4;
		}
		
		public class Core_2_Extreme_QX6700{
			public static final int mips = 49161;
			public static final int cores = 4;
		}
		
		public class Core_2_Extreme_X6800{
			public static final int mips = 27079;
			public static final int cores = 2;
		}
		
		public class Pentium_4_Extreme_Edition{
			public static final int mips = 9726;
			public static final int cores = 1;
		}
	}
	
	public class AMD{
		public class FX_8150_Eight_core{
			public static final int mips = 108890;
			public static final int cores = 8;
		}
		
		public class Phenom_II_X6_1100T{
			public static final int mips = 78440;
			public static final int cores = 6;
		}
		
		public class Phenom_II_X4_940_Black_Edition{
			public static final int mips = 42820;
			public static final int cores = 4;
		}
		
		public class Athlon_FX_60_Dual_core{
			public static final int mips = 18938;
			public static final int cores = 2;
		}
		
		public class Athlon_64_3800_X2_Dual_core{
			public static final int mips = 14564;
			public static final int cores = 2;
		}
		
		public class Athlon_FX_57{
			public static final int mips = 12000;
			public static final int cores = 1;
		}
		
		public class E_350_Dual_core{
			public static final int mips = 10000;
			public static final int cores = 2;
		}
	}
}

/**

1,Intel Core i7 Extreme Edition 3960X (Hex core),3.33,GHz,177730,IPS
2,Intel Core i7 Extreme Edition 980X (Hex core),3.33,GHz,147600,MIPS
3,Intel Core i7 2600K,3.4,GHz,128300,MIPS
4,AMD FX-8150 (Eight core),3.6,GHz,108890,MIPS
5,Intel Core i7 875K,2.93,GHz,92100,MIPS
6,Intel Core i7 920 (Quad core),2.66 (Turbo 2.93),GHz,82300,MIPS
7,AMD Phenom II X6 1100T,3.3,GHz,78440,MIPS
8,Intel Core 2 Extreme QX9770 (Quad core),3.2,GHz,59455,MIPS
9,Intel Core 2 Extreme QX6700 (Quad core),2.66,GHz,49161,MIPS
10,AMD Phenom II X4 940 Black Edition,3,GHz,42820,MIPS
11,Intel Core 2 Extreme X6800 (Dual core),2.93,GHz,27079,MIPS
12,Xbox360 IBM -Xenon- (Triple core),3.2,GHz,19200,MIPS
13,AMD Athlon FX-60 (Dual core),2.6,GHz,18938,MIPS
14,AMD Athlon 64 3800+ X2 (Dual core),2,GHz,14564,MIPS
15,Samsung Exynos 5250 (Cortex-A15-like Dual core),2,GHz,14000,MIPS
16,AMD Athlon FX-57,2.8,GHz,12000,MIPS
17,PS3 Cell BE(PPEonly),3.2,GHz,10240,MIPS
18,AMD E-350 (Dual core),1.6,GHz,10000,MIPS
19,Qualcomm Krait (Cortex A15-like Dual core),1.5,GHz,9900,MIPS
20,Pentium 4 Extreme Edition,3.2,GHz,9726,MIPS

*/