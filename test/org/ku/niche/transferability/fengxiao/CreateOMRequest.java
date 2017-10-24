package org.ku.niche.transferability.fengxiao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.gdal.gdalconst.gdalconst;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

public class CreateOMRequest {
	@Test
	public void createVE() throws FileNotFoundException{
		HashMap<Integer, double[]> centrals = new HashMap<Integer, double[]>();
		centrals.put(1, new double[]{-2, -1.5, -1.25});
		centrals.put(2, new double[]{-2, -1.5, 2.25});
		centrals.put(3, new double[]{-2, 3.5, -1.25});
		centrals.put(4, new double[]{-2, 3.5, 2.25});
		centrals.put(5, new double[]{4, -1.5, -1.25});
		centrals.put(6, new double[]{4, -1.5, 2.25});
		centrals.put(7, new double[]{4, 3.5, -1.25});
		centrals.put(8, new double[]{4, 3.5, 2.25});
		centrals.put(9, new double[]{1, 1, 0.5});
		
		GeoTiffObject mask = new GeoTiffObject("/home/huijieqiao/New_Experiments/bio_2.5m_Eurasia/PCA/PC1.tif");
		
		for (Integer key : centrals.keySet()){
			double[] central = centrals.get(key);
			double[] values = new double[mask.getXSize() * mask.getYSize()];
			for (int i=0;i<values.length;i++){
				values[i] = -9999;
			}
			
			for (int i=0;i<10000;i++){
				values[i] = -10 + 20d * (double)i / 10000d; 
			}
			
			for (int i=10000;i<20000;i++){
				values[i] = central[1]; 
			}
			
			for (int i=20000;i<30000;i++){
				values[i] = central[2]; 
				
			}
			
			GeoTiffController.createTiff(
					String.format("/home/huijieqiao/New_Experiments/ve/PC1_VS%d.tif", key), mask.getXSize(), mask.getYSize(), 
					mask.getDataset().GetGeoTransform(), values, -9999, gdalconst.GDT_Float32, mask.getDataset().GetProjection());
			
			for (int i=0;i<values.length;i++){
				values[i] = -9999;
			}
			
			for (int i=0;i<10000;i++){
				values[i] = central[0]; 
			}
			
			for (int i=10000;i<20000;i++){
				values[i] = -10 + 20d * (double)(i-10000) / 10000d; 
			}
			
			for (int i=20000;i<30000;i++){
				values[i] = central[2]; 
				
			}
			
			GeoTiffController.createTiff(
					String.format("/home/huijieqiao/New_Experiments/ve/PC2_VS%d.tif", key), mask.getXSize(), mask.getYSize(), 
					mask.getDataset().GetGeoTransform(), values, -9999, gdalconst.GDT_Float32, mask.getDataset().GetProjection());
			
			
			for (int i=0;i<values.length;i++){
				values[i] = -9999;
			}
			
			for (int i=0;i<10000;i++){
				values[i] = central[0]; 
			}
			
			for (int i=10000;i<20000;i++){
				values[i] = central[1]; 
			}
			
			
			for (int i=20000;i<30000;i++){
				values[i] = -10 + 20d * (double)(i-20000) / 10000d; 
				
			}
			
			GeoTiffController.createTiff(
					String.format("/home/huijieqiao/New_Experiments/ve/PC3_VS%d.tif", key), mask.getXSize(), mask.getYSize(), 
					mask.getDataset().GetGeoTransform(), values, -9999, gdalconst.GDT_Float32, mask.getDataset().GetProjection());
			
		}
	}
	
	@Test
	public void createVEResponseCurve() throws IOException{
		HashSet<String> models = new HashSet<String>();
		models.add("enfa");
		models.add("garp");
		models.add("kde_large_bw_tif");
		models.add("kde_small_bw_tif");
		models.add("MA");
		models.add("MA2");
		
		for (String model : models){
			System.out.println(model);
			StringBuilder sb = new StringBuilder();
			sb.append("var,PC1,PC2,PC3" + Const.LineBreak);
			GeoTiffObject geo = new GeoTiffObject("/Users/huijieqiao/temp/raw_projection_ve/Large_9_region_1_rep_1_" + model + ".tif");
			double[] values = geo.getValueArray();
			for (int i=0;i<10000;i++){
				double v = -10 + 20d * (double)i / 10000d;
				double pc1 = values[i];
				double pc2 = values[i + 10000];
				double pc3 = values[i + 20000];
				sb.append(String.format("%.3f,%f,%f,%f%n", v, pc1, pc2, pc3));
			}
			CommonFun.writeFile(sb.toString(), 
					String.format("/Users/huijieqiao/Dropbox/Papers/NicheTransferability/New_Figures/fig_responseCurve_Large_9_region_1_/Large_9_region_1_rep_1_%s.csv",
							model.replace("_tif", "")));
		}
		
	}
	@Test
	public void createCopyCommand() throws IOException{
		StringBuilder sb_cmd = new StringBuilder();
		String baseFolder = "/home/huijieqiao/Dropbox/Papers/NicheTransferability/New_Experiments/spp_training_pa_xiao/2.dataset_training_combined_openmodeller/";
		for (int t=1; t<=11; t++){
			
			for (int r=1; r<=10; r++){
				sb_cmd.append(String.format("copy %s/train_%d/rep_%d/train_%d_rep_%d_enfa.tif %s %n", baseFolder, t, r, t, r, baseFolder));
				sb_cmd.append(String.format("copy %s/train_%d/rep_%d/train_%d_rep_%d_garp.tif %s %n", baseFolder, t, r, t, r, baseFolder));
			}
		}
		CommonFun.writeFile(sb_cmd.toString(), "/home/huijieqiao/New_Experiments/cp.bat");
	}
	
	@Test
	public void createRequest_Response_Curve() throws IOException{
		
		String baseFolder = "/home/huijieqiao/New_Experiments/spp_training_pa_xiao";
		File trainingOccu = new File (baseFolder + "/2.dataset_training_combined");
		String trainFolderBase = baseFolder + "/2.dataset_training_combined_openmodeller_ve";
		StringBuilder sb_cmd = new StringBuilder();
		int max_command = 50;
		int current_command = 0;
		int comment_index = 1;
		
		for (File occfile : trainingOccu.listFiles()){
			//Large_1_region_1_rep_1_train_p.csv
			//Large_1_region_1_rep_1
			if ((!occfile.getName().endsWith("train_p.csv"))){
				continue;
			}
			current_command++;
			String info = occfile.getName().replace("_train_p.csv", "");
			System.out.println(String.format("train: %s", info));
			String trainFolder = trainFolderBase + "/" + info;
			
			StringBuilder sb_garp = new StringBuilder();
			StringBuilder sb_enfa = new StringBuilder();
			StringBuilder sb_occ = new StringBuilder();
			sb_occ.append("#id	label	long	lat	abundance" + Const.LineBreak);
			ArrayList<String> occ_str = CommonFun.readFromFile(occfile.getAbsolutePath());
			int id_index = 0;
			for (String occs : occ_str){
				String[] occs_str = occs.split(",");
				if (occs_str.length==13){
					if (CommonFun.isDouble(occs_str[1])&&CommonFun.isDouble(occs_str[2])){
						id_index++;
						sb_occ.append(String.format("%d\t%s\t%f\t%f\t1%n", 
								id_index, info, Double.valueOf(occs_str[1]).doubleValue(), Double.valueOf(occs_str[2]).doubleValue()));
						
					}
				}
			}
			
			
			String occ_file_name = trainFolder + "/" + info + ".txt";
			CommonFun.writeFile(sb_occ.toString(), occ_file_name);
			String[] infos = info.split("_");
			sb_garp.append(String.format("Occurrences source = %s%n", occ_file_name));
			sb_garp.append("WKT Coord System = PROJCS[\"Eckert_IV\",GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_unknown\",SPHEROID[\"WGS84\","
					+ "6378137,298.257223563]],PRIMEM[\"Greenwich\",0],UNIT[\"Degree\",0.017453292519943295]],PROJECTION[\"Eckert_IV\"],"
					+ "PARAMETER[\"central_meridian\",105],PARAMETER[\"false_easting\",0],PARAMETER[\"false_northing\",0],UNIT[\"kilometre\",1000]]" + Const.LineBreak);
			sb_garp.append(String.format("Occurrences group = %s%n", info));
			for (int i=1;i<=3;i++){
				sb_garp.append(String.format("Map = %s/pc%d.tif%n", trainFolder.replace("_ve", ""), i));
			}
			sb_garp.append(String.format("Mask = %s/pc1.tif%n", trainFolder.replace("_ve", "")));
			sb_garp.append(String.format("Output model = %s/%s_garp.xml%n", trainFolder, info));
			sb_garp.append(String.format("Output format = /home/huijieqiao/New_Experiments/ve/PC%d.tif%n", 1));
			for (int i=1;i<=3;i++){
				sb_garp.append(String.format("Output map = /home/huijieqiao/New_Experiments/ve/PC%d_VS%s.tif%n", i, infos[1]));
			}
			sb_garp.append(String.format("Output mask = /home/huijieqiao/New_Experiments/ve/PC%d.tif%n", 1));
			sb_garp.append(String.format("Output file = %s/%s_garp.tif%n", "/home/huijieqiao/New_Experiments/raw_projection_ve", info));
			sb_garp.append("Output file type = GreyTiff100" + Const.LineBreak);
			
			sb_garp.append("Algorithm = GARP_BS" + Const.LineBreak);
			sb_garp.append("Parameter = TrainingProportion 50"
					+ Const.LineBreak);
			sb_garp.append("Parameter = TotalRuns 20" + Const.LineBreak);
			sb_garp.append("Parameter = HardOmissionThreshold 100"
					+ Const.LineBreak);
			sb_garp.append("Parameter = ModelsUnderOmissionThreshold 20"
					+ Const.LineBreak);
			sb_garp.append("Parameter = CommissionThreshold 50"
					+ Const.LineBreak);
			sb_garp.append("Parameter = CommissionSampleSize 10000"
					+ Const.LineBreak);
			sb_garp.append("Parameter = MaxThreads 1" + Const.LineBreak);
			sb_garp.append("Parameter = MaxGenerations 400"
					+ Const.LineBreak);
			sb_garp.append("Parameter = ConvergenceLimit 0.01"
					+ Const.LineBreak);
			sb_garp.append("Parameter = PopulationSize 50"
					+ Const.LineBreak);
			sb_garp.append("Parameter = Resamples 2500" + Const.LineBreak);
			CommonFun.writeFile(sb_garp.toString(), String.format("%s/request_garp.txt", trainFolder));
			

			sb_enfa.append(String.format("Occurrences source = %s%n", occ_file_name));
			sb_enfa.append("WKT Coord System = PROJCS[\"Eckert_IV\",GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_unknown\",SPHEROID[\"WGS84\","
					+ "6378137,298.257223563]],PRIMEM[\"Greenwich\",0],UNIT[\"Degree\",0.017453292519943295]],PROJECTION[\"Eckert_IV\"],"
					+ "PARAMETER[\"central_meridian\",105],PARAMETER[\"false_easting\",0],PARAMETER[\"false_northing\",0],UNIT[\"kilometre\",1000]]" + Const.LineBreak);
			sb_enfa.append(String.format("Occurrences group = %s%n", info));
			for (int i=1;i<=3;i++){
				sb_enfa.append(String.format("Map = %s/pc%d.tif%n", trainFolder.replace("_ve", ""), i));
			}
			sb_enfa.append(String.format("Mask = %s/pc1.tif%n", trainFolder.replace("_ve", "")));
			sb_enfa.append(String.format("Output model = %s/%s_enfa.xml%n", trainFolder, info));
			sb_enfa.append(String.format("Output format = /home/huijieqiao/New_Experiments/ve/PC%d.tif%n", 1));
			for (int i=1;i<=3;i++){
				sb_enfa.append(String.format("Output map = /home/huijieqiao/New_Experiments/ve/PC%d_VS%s.tif%n", i, infos[1]));
			}
			sb_enfa.append(String.format("Output mask = /home/huijieqiao/New_Experiments/ve/PC%d.tif%n", 1));
			sb_enfa.append(String.format("Output file = %s/%s_enfa.tif%n", "/home/huijieqiao/New_Experiments/raw_projection_ve", info));
			sb_enfa.append("Output file type = GreyTiff100" + Const.LineBreak);
			
			sb_enfa.append("Algorithm = ENFA" + Const.LineBreak);
			sb_enfa.append("Parameter = NumberOfBackgroundPoints 10000"
					+ Const.LineBreak);
			sb_enfa.append("Parameter = NumberOfRetries 5"
					+ Const.LineBreak);
			sb_enfa.append("Parameter = DiscardMethod 2" + Const.LineBreak);
			sb_enfa.append("Parameter = RetainComponents 2"
					+ Const.LineBreak);
			sb_enfa.append("Parameter = RetainVariation 0.75"
					+ Const.LineBreak);
			sb_enfa.append("Parameter = DisplayLoadings 0"
					+ Const.LineBreak);
			sb_enfa.append("Parameter = VerboseDebug 0" + Const.LineBreak);
			CommonFun.writeFile(sb_enfa.toString(), String.format("%s/request_enfa.txt", trainFolder));
			
			sb_cmd.append("om_console " + String.format("%s/request_enfa.txt", trainFolder) + Const.LineBreak);
			sb_cmd.append("om_console " + String.format("%s/request_garp.txt", trainFolder) + Const.LineBreak);
		
			if (current_command>=max_command){
				current_command = 0;
				
				CommonFun.writeFile(sb_cmd.toString(), String.format("%s/command_train_response_curve_%d.sh", baseFolder, comment_index));
				sb_cmd = new StringBuilder();
				comment_index++;
			}
		}
		CommonFun.writeFile(sb_cmd.toString(), String.format("%s/command_train_response_curve_%d.sh", baseFolder, comment_index));
	}
	
	@Test
	public void createRequest() throws IOException{
		
		String baseFolder = "/home/huijieqiao/New_Experiments/spp_training_pa_xiao";
		File trainingOccu = new File (baseFolder + "/2.dataset_training_combined");
		String trainFolderBase = baseFolder + "/2.dataset_training_combined_openmodeller";
		StringBuilder sb_cmd = new StringBuilder();
		int max_command = 50;
		int current_command = 0;
		int comment_index = 1;
		
		for (File occfile : trainingOccu.listFiles()){
			//Large_1_region_1_rep_1_train_p.csv
			//Large_1_region_1_rep_1
			if ((!occfile.getName().endsWith("train_p.csv"))){
				continue;
			}
			current_command++;
			String info = occfile.getName().replace("_train_p.csv", "");
			System.out.println(String.format("train: %s", info));
			String trainFolder = trainFolderBase + "/" + info;
			
			StringBuilder sb_garp = new StringBuilder();
			StringBuilder sb_enfa = new StringBuilder();
			StringBuilder sb_occ = new StringBuilder();
			sb_occ.append("#id	label	long	lat	abundance" + Const.LineBreak);
			ArrayList<String> occ_str = CommonFun.readFromFile(occfile.getAbsolutePath());
			int id_index = 0;
			for (String occs : occ_str){
				String[] occs_str = occs.split(",");
				if (occs_str.length==13){
					if (CommonFun.isDouble(occs_str[1])&&CommonFun.isDouble(occs_str[2])){
						id_index++;
						sb_occ.append(String.format("%d\t%s\t%f\t%f\t1%n", 
								id_index, info, Double.valueOf(occs_str[1]).doubleValue(), Double.valueOf(occs_str[2]).doubleValue()));
						
					}
				}
			}
			
			
			String occ_file_name = trainFolder + "/" + info + ".txt";
			CommonFun.writeFile(sb_occ.toString(), occ_file_name);
			
			sb_garp.append(String.format("Occurrences source = %s%n", occ_file_name));
			sb_garp.append("WKT Coord System = PROJCS[\"Eckert_IV\",GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_unknown\",SPHEROID[\"WGS84\","
					+ "6378137,298.257223563]],PRIMEM[\"Greenwich\",0],UNIT[\"Degree\",0.017453292519943295]],PROJECTION[\"Eckert_IV\"],"
					+ "PARAMETER[\"central_meridian\",105],PARAMETER[\"false_easting\",0],PARAMETER[\"false_northing\",0],UNIT[\"kilometre\",1000]]" + Const.LineBreak);
			sb_garp.append(String.format("Occurrences group = %s%n", info));
			for (int i=1;i<=3;i++){
				sb_garp.append(String.format("Map = %s/pc%d.tif%n", trainFolder, i));
			}
			sb_garp.append(String.format("Mask = %s/pc1.tif%n", trainFolder));
			sb_garp.append(String.format("Output model = %s/%s_garp.xml%n", trainFolder, info));
			sb_garp.append(String.format("Output format = /home/huijieqiao/New_Experiments/bio_2.5m_Eurasia/PCA/PC%d.tif%n", 1));
			for (int i=1;i<=3;i++){
				sb_garp.append(String.format("Output map = /home/huijieqiao/New_Experiments/bio_2.5m_Eurasia/PCA/PC%d.tif%n", i));
			}
			sb_garp.append(String.format("Output mask = /home/huijieqiao/New_Experiments/bio_2.5m_Eurasia/PCA/PC%d.tif%n", 1));
			sb_garp.append(String.format("Output file = %s/%s_garp.tif%n", "/home/huijieqiao/New_Experiments/om_output", info));
			sb_garp.append("Output file type = GreyTiff100" + Const.LineBreak);
			
			sb_garp.append("Algorithm = GARP_BS" + Const.LineBreak);
			sb_garp.append("Parameter = TrainingProportion 50"
					+ Const.LineBreak);
			sb_garp.append("Parameter = TotalRuns 20" + Const.LineBreak);
			sb_garp.append("Parameter = HardOmissionThreshold 100"
					+ Const.LineBreak);
			sb_garp.append("Parameter = ModelsUnderOmissionThreshold 20"
					+ Const.LineBreak);
			sb_garp.append("Parameter = CommissionThreshold 50"
					+ Const.LineBreak);
			sb_garp.append("Parameter = CommissionSampleSize 10000"
					+ Const.LineBreak);
			sb_garp.append("Parameter = MaxThreads 1" + Const.LineBreak);
			sb_garp.append("Parameter = MaxGenerations 400"
					+ Const.LineBreak);
			sb_garp.append("Parameter = ConvergenceLimit 0.01"
					+ Const.LineBreak);
			sb_garp.append("Parameter = PopulationSize 50"
					+ Const.LineBreak);
			sb_garp.append("Parameter = Resamples 2500" + Const.LineBreak);
			CommonFun.writeFile(sb_garp.toString(), String.format("%s/request_garp.txt", trainFolder));
			

			sb_enfa.append(String.format("Occurrences source = %s%n", occ_file_name));
			sb_enfa.append("WKT Coord System = PROJCS[\"Eckert_IV\",GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_unknown\",SPHEROID[\"WGS84\","
					+ "6378137,298.257223563]],PRIMEM[\"Greenwich\",0],UNIT[\"Degree\",0.017453292519943295]],PROJECTION[\"Eckert_IV\"],"
					+ "PARAMETER[\"central_meridian\",105],PARAMETER[\"false_easting\",0],PARAMETER[\"false_northing\",0],UNIT[\"kilometre\",1000]]" + Const.LineBreak);
			sb_enfa.append(String.format("Occurrences group = %s%n", info));
			for (int i=1;i<=3;i++){
				sb_enfa.append(String.format("Map = %s/pc%d.tif%n", trainFolder, i));
			}
			sb_enfa.append(String.format("Mask = %s/pc1.tif%n", trainFolder));
			sb_enfa.append(String.format("Output model = %s/%s_enfa.xml%n", trainFolder, info));
			sb_enfa.append(String.format("Output format = /home/huijieqiao/New_Experiments/bio_2.5m_Eurasia/PCA/PC%d.tif%n", 1));
			for (int i=1;i<=3;i++){
				sb_enfa.append(String.format("Output map = /home/huijieqiao/New_Experiments/bio_2.5m_Eurasia/PCA/PC%d.tif%n", i));
			}
			sb_enfa.append(String.format("Output mask = /home/huijieqiao/New_Experiments/bio_2.5m_Eurasia/PCA/PC%d.tif%n", 1));
			sb_enfa.append(String.format("Output file = %s/%s_enfa.tif%n", "/home/huijieqiao/New_Experiments/om_output", info));
			sb_enfa.append("Output file type = GreyTiff100" + Const.LineBreak);
			
			sb_enfa.append("Algorithm = ENFA" + Const.LineBreak);
			sb_enfa.append("Parameter = NumberOfBackgroundPoints 10000"
					+ Const.LineBreak);
			sb_enfa.append("Parameter = NumberOfRetries 5"
					+ Const.LineBreak);
			sb_enfa.append("Parameter = DiscardMethod 2" + Const.LineBreak);
			sb_enfa.append("Parameter = RetainComponents 2"
					+ Const.LineBreak);
			sb_enfa.append("Parameter = RetainVariation 0.75"
					+ Const.LineBreak);
			sb_enfa.append("Parameter = DisplayLoadings 0"
					+ Const.LineBreak);
			sb_enfa.append("Parameter = VerboseDebug 0" + Const.LineBreak);
			CommonFun.writeFile(sb_enfa.toString(), String.format("%s/request_enfa.txt", trainFolder));
			
			sb_cmd.append("om_console " + String.format("%s/request_enfa.txt", trainFolder) + Const.LineBreak);
			sb_cmd.append("om_console " + String.format("%s/request_garp.txt", trainFolder) + Const.LineBreak);
		
			if (current_command>=max_command){
				current_command = 0;
				
				CommonFun.writeFile(sb_cmd.toString(), String.format("%s/command_train_%d.sh", baseFolder, comment_index));
				sb_cmd = new StringBuilder();
				comment_index++;
			}
		}
		CommonFun.writeFile(sb_cmd.toString(), String.format("%s/command_train_%d.sh", baseFolder, comment_index));
	}
	@Test
	public void createOccurrence() throws IOException{
		StringBuilder sb = new StringBuilder();
		String baseFolder = "/home/huijieqiao/New_Experiments";
		sb.append("#id	label	long	lat	abundance" + Const.LineBreak);
		for (int t=1; t<=11; t++){
			for (int r=1; r<=10; r++){
				System.out.println(String.format("train: %d, rep: %d", t, r));
				
				ArrayList<String> occstr = CommonFun.readFromFile(String.format("%s/train_%d/rep_%d/presence.csv", baseFolder, t, r));
				int occ_count = 0;
				for (String occ_ll : occstr){
					String[] occs = occ_ll.split(",");
					
					if (occs.length==7){
						if (CommonFun.isDouble(occs[0])&&(CommonFun.isDouble(occs[1]))){
							if (occs[6].equals("1")){
								occ_count ++;
								sb.append(String.format("%d	train_%d_rep_%d	%s	%s	1%n", occ_count, t, r, occs[0], occs[1]));
							}
						}
					}
				}
				CommonFun.writeFile(sb.toString(), String.format("%s/train_%d/rep_%d/occurrence_om.csv", baseFolder, t, r));
			}
		}
	}
	@Test
	public void createTrainLayer() throws IOException{
		ArrayList<String> bgstr = CommonFun.readFromFile("E:/Dropbox/Papers/NicheTransferability/experiments/bg_coords.csv");
		GeoTiffObject pc1 = new GeoTiffObject("/home/huijieqiao/New_Experiments/bio_2.5m_Eurasia/PCA/PC1.tif");
		GeoTiffObject pc2 = new GeoTiffObject("/home/huijieqiao/New_Experiments/bio_2.5m_Eurasia/PCA/PC2.tif");
		GeoTiffObject pc3 = new GeoTiffObject("/home/huijieqiao/New_Experiments/bio_2.5m_Eurasia/PCA/PC3.tif");
		double[] pc1_v = new double[pc1.getXSize() * pc1.getYSize()];
		double[] pc2_v = new double[pc1.getXSize() * pc1.getYSize()];
		double[] pc3_v = new double[pc1.getXSize() * pc1.getYSize()];
		
		
		String baseFolder = "/home/huijieqiao/New_Experiments";
		for (int t=1; t<=11; t++){
			for (int r=1; r<=10; r++){
				System.out.println(String.format("train: %d, rep: %d", t, r));
				for (int i=0; i<pc1_v.length;i++){
					pc1_v[i] = -9999;
					pc2_v[i] = -9999;
					pc3_v[i] = -9999;
				}
				ArrayList<String> occstr = CommonFun.readFromFile(String.format("%s/train_%d/rep_%d/presence.csv", baseFolder, t, r));
				int occ_count = 0;
				for (String occ_ll : occstr){
					String[] occs = occ_ll.split(",");
					
					if (occs.length==7){
						if (CommonFun.isDouble(occs[0])&&(CommonFun.isDouble(occs[1]))){
							if (occs[6].equals("1")){
								double[] ll = new double[]{Double.valueOf(occs[0]).doubleValue(), Double.valueOf(occs[1]).doubleValue()};
								int[] xy = CommonFun.LLToPosition(pc1.getDataset().GetGeoTransform(), ll);
								pc1_v[xy[1] * pc1.getXSize() + xy[0]] = pc1.getValueArray()[xy[1] * pc1.getXSize() + xy[0]];
								pc2_v[xy[1] * pc1.getXSize() + xy[0]] = pc2.getValueArray()[xy[1] * pc1.getXSize() + xy[0]];
								pc3_v[xy[1] * pc1.getXSize() + xy[0]] = pc3.getValueArray()[xy[1] * pc1.getXSize() + xy[0]];
								//System.out.println(pc1.getValueArray()[xy[1] * pc1.getXSize() + xy[0]]);
								occ_count++;
							}
						}
					}
				}
				int bg_count = 0;
				for (String bg_ll : bgstr){
					String[] bgs = bg_ll.split(",");
					if (bgs.length==3){
						if (CommonFun.isDouble(bgs[1])&&(CommonFun.isDouble(bgs[2]))){
							double[] ll = new double[]{Double.valueOf(bgs[1]).doubleValue(), Double.valueOf(bgs[2]).doubleValue()};
							int[] xy = CommonFun.LLToPosition(pc1.getDataset().GetGeoTransform(), ll);
							pc1_v[xy[1] * pc1.getXSize() + xy[0]] = pc1.getValueArray()[xy[1] * pc1.getXSize() + xy[0]];
							pc2_v[xy[1] * pc1.getXSize() + xy[0]] = pc2.getValueArray()[xy[1] * pc1.getXSize() + xy[0]];
							pc3_v[xy[1] * pc1.getXSize() + xy[0]] = pc3.getValueArray()[xy[1] * pc1.getXSize() + xy[0]];
							bg_count++;
						}
					}
				}
				System.out.println(String.format("Occ count: %d, bg count: %d", occ_count, bg_count));
				GeoTiffController.createTiff(String.format("%s/train_%d/rep_%d/PC%d.tif", baseFolder, t, r, 1), pc1.getXSize(), pc1.getYSize(), pc1.getDataset().GetGeoTransform(), 
						pc1_v, -9999, gdalconst.GDT_Float32, pc1.getDataset().GetProjection());
				GeoTiffController.createTiff(String.format("%s/train_%d/rep_%d/PC%d.tif", baseFolder, t, r, 2), pc1.getXSize(), pc1.getYSize(), pc1.getDataset().GetGeoTransform(), 
						pc2_v, -9999, gdalconst.GDT_Float32, pc1.getDataset().GetProjection());
				GeoTiffController.createTiff(String.format("%s/train_%d/rep_%d/PC%d.tif", baseFolder, t, r, 3), pc1.getXSize(), pc1.getYSize(), pc1.getDataset().GetGeoTransform(), 
						pc3_v, -9999, gdalconst.GDT_Float32, pc1.getDataset().GetProjection());

				
			}
		}
	}
}
