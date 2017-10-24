package org.ioz.darwinfox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.gdal.gdalconst.gdalconst;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

import Jama.Matrix;

public class Darwin {
	@Test
	public void convertNicheAForPROC() throws FileNotFoundException{
		GeoTiffObject nichea=  new GeoTiffObject("E:/Dropbox/Papers/DarwinFox/ENMResults/ASC/nichea_all.asc");
		double[] values = nichea.getValueArray();
		for (int x = 0; x<nichea.getXSize();x++){
			for (int y = 0; y<nichea.getYSize();y++){
				double value = nichea.readByXY(x, y);
				if (CommonFun.equal(value, nichea.getNoData(), 10000)){
					continue;
				}
				value = value * 100;
				if (CommonFun.equal(value, 0, 10000)){
					value = 0.001;
				}
				if (CommonFun.equal(value, -1, 10000)){
					value = 0;
				}		
				values[y * nichea.getXSize() + x] = value;
			}
		}
		GeoTiffController.createTiff("E:/Dropbox/Papers/DarwinFox/ENMResults/ASC/nichea_all_p_roc.tif", nichea.getXSize(), nichea.getYSize(), nichea.getDataset().GetGeoTransform(), values, -9999, gdalconst.GDT_Float32, nichea.getDataset().GetProjection());
	}
	@Test
	public void calculateEDistancetoCentroid() throws IOException{
		double [] a_C = new double[]{-1.6028581122921626, -1.6082699192578747, -0.5306683196368384};
		ArrayList<File> files = new ArrayList<File>();
		files.add(new File("E:/DarwinFox/V_N/GARP/GARP_Inside/value.txt"));
		files.add(new File("E:/DarwinFox/V_N/GARP/GARP_Outside/value.txt"));
		files.add(new File("E:/DarwinFox/V_N/GBM/inside/value.txt"));
		files.add(new File("E:/DarwinFox/V_N/GBM/outside/value.txt"));
		files.add(new File("E:/DarwinFox/V_N/GLM/inside/value.txt"));
		files.add(new File("E:/DarwinFox/V_N/GLM/outside/value.txt"));
		files.add(new File("E:/DarwinFox/V_N/KDE/inside/value.txt"));
		files.add(new File("E:/DarwinFox/V_N/KDE/outside/value.txt"));
		files.add(new File("E:/DarwinFox/V_N/MAXENT/inside/value.txt"));
		files.add(new File("E:/DarwinFox/V_N/MAXENT/outside/value.txt"));
		files.add(new File("E:/DarwinFox/V_N/NicheA/inside/value.txt"));
		files.add(new File("E:/DarwinFox/V_N/NicheA/outside/value.txt"));
		StringBuilder sb_result = new StringBuilder();
		sb_result.append("Algorithm,Insideout,sum,min,max,mean,sd,percentile_25,medium,percentile_75" + Const.LineBreak);
		for (File f : files){
			StringBuilder sb = new StringBuilder();
			sb.append("x,y,z,distance" + Const.LineBreak);
			String algorithm = f.getParentFile().getParentFile().getName();
			System.out.println("Current algorithm " + algorithm);
			String filename = f.getAbsolutePath();
			ArrayList<String> values_str = CommonFun.readFromFile(filename);
			int i = 0;
			double[] distances = new double[values_str.size()-1];
			for (String value_str : values_str){
				System.out.println(String.format("%s:%d/%d", algorithm, i, values_str.size()));
				String[] values_s = value_str.split(",");
				if (values_s.length==3){
					if (CommonFun.isDouble(values_s[0])){
						double[] v = new double[]{Double.valueOf(values_s[0]),
								Double.valueOf(values_s[1]), Double.valueOf(values_s[2])};
						double distance = CommonFun.getDistance(a_C, v);
						distances[i++] = distance;
						sb.append(String.format("%f,%f,%f,%f%n", v[0], v[1], v[2], distance));
					}
						
				}
			}
			DescriptiveStatistics stat = new DescriptiveStatistics(distances);
			sb_result.append(String.format("%s,%s,%f,%f,%f,%f,%f,%f,%f,%f,%n", algorithm, f.getParentFile().getName(), stat.getSum(),
					stat.getMin(), stat.getMax(), stat.getMean(), stat.getStandardDeviation(), stat.getPercentile(25),
					stat.getPercentile(50), stat.getPercentile(75)));
			CommonFun.writeFile(sb.toString(), "E:/DarwinFox/Extrapolation_based_assessment/E_" + algorithm + "_" + f.getParentFile().getName() + ".csv");
		}
		CommonFun.writeFile(sb_result.toString(), "E:/DarwinFox/Extrapolation_based_assessment/E_result.csv");
	}
	@Test
	public void calculateDistancetoCentroid() throws IOException{
		double[][] a_A = new double[3][3];
		a_A[0] = new double[]{0.14052211761133993,0.09083871370261287,0.09254092514185575};
		a_A[1] = new double[]{0.09083871370261286,0.14541048243213942,0.07606198492945189};
		a_A[2] = new double[]{0.09254092514185576,0.07606198492945192,0.14641066764534125};
		Matrix A = new Matrix(a_A);
		double[][] a_C = new double[3][1];
		a_C[0][0] = -1.6028581122921626;
		a_C[1][0] = -1.6082699192578747;
		a_C[2][0] = -0.5306683196368384;
		Matrix C = new Matrix(a_C);
		ArrayList<File> files = new ArrayList<File>();
		files.add(new File("E:/DarwinFox/V_N/GARP/GARP_Inside/value.txt"));
		files.add(new File("E:/DarwinFox/V_N/GARP/GARP_Outside/value.txt"));
		files.add(new File("E:/DarwinFox/V_N/GBM/inside/value.txt"));
		files.add(new File("E:/DarwinFox/V_N/GBM/outside/value.txt"));
		files.add(new File("E:/DarwinFox/V_N/GLM/inside/value.txt"));
		files.add(new File("E:/DarwinFox/V_N/GLM/outside/value.txt"));
		files.add(new File("E:/DarwinFox/V_N/KDE/inside/value.txt"));
		files.add(new File("E:/DarwinFox/V_N/KDE/outside/value.txt"));
		files.add(new File("E:/DarwinFox/V_N/MAXENT/inside/value.txt"));
		files.add(new File("E:/DarwinFox/V_N/MAXENT/outside/value.txt"));
		files.add(new File("E:/DarwinFox/V_N/NicheA/inside/value.txt"));
		files.add(new File("E:/DarwinFox/V_N/NicheA/outside/value.txt"));
		StringBuilder sb_result = new StringBuilder();
		sb_result.append("Algorithm,Insideout,sum,min,max,mean,sd,percentile_25,medium,percentile_75" + Const.LineBreak);
		for (File f : files){
			StringBuilder sb = new StringBuilder();
			sb.append("x,y,z,distance" + Const.LineBreak);
			String algorithm = f.getParentFile().getParentFile().getName();
			System.out.println("Current algorithm " + algorithm);
			String filename = f.getAbsolutePath();
			ArrayList<String> values_str = CommonFun.readFromFile(filename);
			int i = 0;
			double[] distances = new double[values_str.size()-1];
			for (String value_str : values_str){
				System.out.println(String.format("%s:%d/%d", algorithm, i, values_str.size()));
				String[] values_s = value_str.split(",");
				if (values_s.length==3){
					if (CommonFun.isDouble(values_s[0])){
						double[] v = new double[]{Double.valueOf(values_s[0]),
								Double.valueOf(values_s[1]), Double.valueOf(values_s[2])};
						double distance = CommonFun.distanceEllipsoid(A, C, v);
						distances[i++] = distance;
						sb.append(String.format("%f,%f,%f,%f%n", v[0], v[1], v[2], distance));
					}
						
				}
			}
			DescriptiveStatistics stat = new DescriptiveStatistics(distances);
			sb_result.append(String.format("%s,%s,%f,%f,%f,%f,%f,%f,%f,%f,%n", algorithm, f.getParentFile().getName(), stat.getSum(),
					stat.getMin(), stat.getMax(), stat.getMean(), stat.getStandardDeviation(), stat.getPercentile(25),
					stat.getPercentile(50), stat.getPercentile(75)));
			CommonFun.writeFile(sb.toString(), "E:/DarwinFox/Extrapolation_based_assessment/MVE_" + algorithm + "_" + f.getParentFile().getName() + ".csv");
		}
		CommonFun.writeFile(sb_result.toString(), "E:/DarwinFox/Extrapolation_based_assessment/MVE_result.csv");
		System.out.println(CommonFun.MatrixtoString(A));
	}
	@Test
	public void copyResults_M1(){
		File resultFolder = new File("E:/HU/Rscript/Merge");
		String baseFolder = "E:/HU/Rscript/AllResults/M1";
		for (File f : resultFolder.listFiles()){
			if (f.isDirectory()){
				String result = String.format("%s/ALL/proj_current/individual_projections/proj_current_ALL_PA1_RUN1_GLM.img", f.getAbsolutePath());
				File t = new File(String.format("%s/%s_GLM.tif", baseFolder, f.getName()));
				if (!t.exists()){
					System.out.println(t.getAbsolutePath());
					GeoTiffController.toGeoTIFF(result, t.getAbsolutePath());
				}
				
				result = String.format("%s/ALL/proj_current/individual_projections/proj_current_ALL_PA1_RUN1_MAXENT.img", f.getAbsolutePath());
				t = new File(String.format("%s/%s_MAXENT.tif", baseFolder, f.getName()));
				if (!t.exists()){
					System.out.println(t.getAbsolutePath());
					GeoTiffController.toGeoTIFF(result, t.getAbsolutePath());
				}
				
				result = String.format("%s/ALL/proj_current/individual_projections/proj_current_ALL_PA1_RUN1_RF.img", f.getAbsolutePath());
				t = new File(String.format("%s/%s_RF.tif", baseFolder, f.getName()));
				if (!t.exists()){
					System.out.println(t.getAbsolutePath());
					GeoTiffController.toGeoTIFF(result, t.getAbsolutePath());
				}
				
			}
		}
	}
	@Test
	public void copyResults(){
		String baseFolder = "E:/HU/Rscript";
		for (int i=1;i<11;i++){
			for (int vs=1;vs<=11;vs++){
				for (int j=1;j<2;j++){
					File checkStr = new File(String.format("%s/AllResults/Single/G_%d.VS_%d.R_%d.MAXENT.tif", baseFolder, i, vs, j));
					
					String result = String.format("%s/Grid_Repeat_No_%d/VS_%d/Repeat_%d/ALL/proj_current/individual_projections/proj_current_ALL_PA1_RUN1_GLM.img", baseFolder, i, vs, j);
					File f = new File(result);
					long time = f.lastModified();
					Date d = new Date(time);
					
					if ((!checkStr.exists())||(d.getDay()==1)){
						result = String.format("%s/Grid_Repeat_No_%d/VS_%d/Repeat_%d/ALL/proj_current/individual_projections/proj_current_ALL_PA1_RUN1_GLM.img", baseFolder, i, vs, j);
						System.out.println(result);
						GeoTiffController.toGeoTIFF(result, String.format("%s/AllResults/Single/G_%d.VS_%d.R_%d.GLM.tif", baseFolder, i, vs, j));
						result = String.format("%s/Grid_Repeat_No_%d/VS_%d/Repeat_%d/ALL/proj_current/individual_projections/proj_current_ALL_PA1_RUN1_MAXENT.img", baseFolder, i, vs, j);
						System.out.println(result);
						GeoTiffController.toGeoTIFF(result, String.format("%s/AllResults/Single/G_%d.VS_%d.R_%d.MAXENT.tif", baseFolder, i, vs, j));
						result = String.format("%s/Grid_Repeat_No_%d/VS_%d/Repeat_%d/ALL/proj_current/individual_projections/proj_current_ALL_PA1_RUN1_RF.img", baseFolder, i, vs, j);
						System.out.println(result);
						GeoTiffController.toGeoTIFF(result, String.format("%s/AllResults/Single/G_%d.VS_%d.R_%d.RF.tif", baseFolder, i, vs, j));
					}
				}
			}
		}
	}
	@Test
	public void checkResult(){
		String baseFolder = "E:/HU/Rscript/AllResults/Single";
		for (int i=1;i<11;i++){
			for (int vs=1;vs<=11;vs++){
				for (int j=1;j<2;j++){
					
					File result = new File(String.format("%s/G_%d.VS_%d.R_%d.RF.tif", baseFolder, i, vs, j));
					if (!result.exists()){
						System.out.println("Error:" + result.getName());
						//break;
					}
					
					result = new File(String.format("%s/G_%d.VS_%d.R_%d.GLM.tif", baseFolder, i, vs, j));
					if (!result.exists()){
						System.out.println("Error:" + result.getName());
						//break;
					}
					
					result = new File(String.format("%s/G_%d.VS_%d.R_%d.MAXENT.tif", baseFolder, i, vs, j));
					if (!result.exists()){
						System.out.println("Error:" + result.getName());
						//break;
					}
				}
			}
		}
	}
	
	@Test
	public void mergeEMN() throws IOException{
		ArrayList<String> overlapstr = CommonFun.readFromFile("E:/HU/GPLUS/VirtualSpecies/Overlap/overlap_result.csv");
		HashMap<String, OverlapObject> overlaps = new HashMap<String, OverlapObject>();
		HashSet<Integer> vsList = new HashSet<Integer>();
		for (String o : overlapstr){
			String[] os = o.split(",");
			if (os.length>=7){
				if (CommonFun.isDouble(os[1])){
					OverlapObject obj = new OverlapObject(Integer.valueOf(os[0].replace("E:\\HU\\GPLUS\\VirtualSpecies\\VS", "")), 
							Integer.valueOf(os[5].replace("E:\\HU\\GPLUS\\VirtualSpecies\\VS", "")), 
							Double.valueOf(os[1]), Double.valueOf(os[6]), Double.valueOf(os[10]));
					overlaps.put(String.format("%d.%d", obj.getVs1(), obj.getVs2()), obj);
					vsList.add(obj.getVs1());
					vsList.add(obj.getVs2());
				}
			}
		}
		String baseFolder = "E:/HU/Rscript/AllResults/Single";
		String targetFolder = "E:/HU/Rscript/AllResults/P1";
		HashSet<String> models = new HashSet<String>();
		models.add("GLM");
		models.add("MAXENT");
		models.add("RF");
		//Merge the most closed species V = P(A) + P(A|B) * P(B), where P(A|B) = V(overlap)/V(A)
		for (Integer vs1 : vsList){
			for (Integer vs2 : vsList){
				if (vs1.intValue()==vs2.intValue()){
					continue;
				}
				OverlapObject obj = overlaps.containsKey(String.format("%d.%d", vs1, vs2))?
						overlaps.get(String.format("%d.%d", vs1, vs2)):overlaps.get(String.format("%d.%d", vs2, vs1));
				double overlap = obj.getOverlap();
				if (CommonFun.equal(overlap, 0, 1000)){
					//continue;
				}

				for (int G1=10; G1<=10;G1++){
					for (int G2=1; G2<=10;G2++){
						for (int R1=1; R1<=1;R1++){
							for (int R2=1; R2<=1;R2++){
								System.out.println(String.format("VS1:%d VS2:%d G1:%d R1:%d G2:%d R2:%d", vs1, vs2, G1, R1, G2, R2));
								for (String model : models){
									File f1 = new File(String.format("%s/G_%d.VS_%d.R_%d.%s.tif", baseFolder, G1, vs1, R1, model));
									File f2 = new File(String.format("%s/G_%d.VS_%d.R_%d.%s.tif", baseFolder, G2, vs2, R2, model));
									File target1 = new File(String.format("%s/G1_%d.VS1_%d.R1_%d.G2_%d.VS2_%d.R2_%d.%s.tif", targetFolder, G1, vs1, R1, G2, vs2, R2, model));
									File target2 = new File(String.format("%s/G1_%d.VS1_%d.R1_%d.G2_%d.VS2_%d.R2_%d.%s.tif", targetFolder, G2, vs2, R2, G1, vs1, R1, model));
									
									if (target1.exists()||target2.exists()){
										continue;
									}
									GeoTiffObject geo1 = new GeoTiffObject(f1.getAbsolutePath());
									GeoTiffObject geo2 = new GeoTiffObject(f2.getAbsolutePath());
									double[] values_1_2 = new double[geo1.getXSize() * geo1.getYSize()];
									double[] values_2_1 = new double[geo1.getXSize() * geo1.getYSize()];
									for (int i=0;i<values_1_2.length;i++){
										values_1_2[i] = geo1.getNoData();
										values_2_1[i] = geo1.getNoData();
									}
									for (int x=0;x<geo1.getXSize();x++){
										for (int y=0;y<geo1.getYSize();y++){
											double v2 = geo2.readByXY(x, y);
											double v1  = geo1.readByXY(x, y);
											if (CommonFun.equal(v1, geo1.getNoData(), 1000)){
												continue;
											}
		
											values_1_2[y * geo1.getXSize() + x] = v1 + overlap/obj.getV_vs1() * v2;
											values_2_1[y * geo1.getXSize() + x] = v2 + overlap/obj.getV_vs2() * v1;
										}
									}
									GeoTiffController.createTiff(target1.getAbsolutePath(), geo1.getXSize(), geo1.getYSize(), geo1.getDataset().GetGeoTransform(), values_1_2, geo1.getNoData(), 
											gdalconst.GDT_Float32, geo1.getDataset().GetProjection());
									GeoTiffController.createTiff(target2.getAbsolutePath(), geo1.getXSize(), geo1.getYSize(), geo1.getDataset().GetGeoTransform(), values_2_1, geo1.getNoData(), 
											gdalconst.GDT_Float32, geo1.getDataset().GetProjection());
								}
							}
						}
					}
				}
				
			}
		}
	}
	
	@Test
	public void calculateCM2() throws IOException, SQLException, InterruptedException{
		GeoTiffObject mask = new GeoTiffObject("E:/HU/bioclim/clim/PCA/1.tiff");
		ArrayList<String> overlapstr = CommonFun.readFromFile("E:/HU/GPLUS/VirtualSpecies/Overlap/overlap_result.csv");
		HashMap<String, OverlapObject> overlaps = new HashMap<String, OverlapObject>();
		HashSet<Integer> vsList = new HashSet<Integer>();
		for (String o : overlapstr){
			String[] os = o.split(",");
			if (os.length>=7){
				if (CommonFun.isDouble(os[1])){
					OverlapObject obj = new OverlapObject(Integer.valueOf(os[0].replace("E:\\HU\\GPLUS\\VirtualSpecies\\VS", "")), 
							Integer.valueOf(os[5].replace("E:\\HU\\GPLUS\\VirtualSpecies\\VS", "")), 
							Double.valueOf(os[1]), Double.valueOf(os[6]), Double.valueOf(os[10]));
					overlaps.put(String.format("%d.%d", obj.getVs1(), obj.getVs2()), obj);
					vsList.add(obj.getVs1());
					vsList.add(obj.getVs2());
				}
			}
		}
		HashMap<String, HashSet<int[]>> occurrences = new HashMap<String, HashSet<int[]>>();
		HashMap<Integer, GeoTiffObject> trueDistribution = new HashMap<Integer, GeoTiffObject>();
		String baseFolder = "E:/HU/GPLUS";
		for (int i = 1; i<=11; i++){
			GeoTiffObject dis = new GeoTiffObject(String.format("%s/VirtualSpecies/VS%d/present.tiff", baseFolder, i));
			trueDistribution.put(i, dis);
		}
		ArrayList<File> allResult = new ArrayList<File>();
		
		File p1Folder = new File("E:/HU/Rscript/AllResults/M1");
		for (File f : p1Folder.listFiles()){
			if (f.getName().endsWith(".tif")){
				allResult.add(f);
			}
		}
		Connection con= (Connection) DriverManager.getConnection("jdbc:mysql://159.226.67.79:3306/hu?useUnicode=true&amp;characterEncoding=utf-8","root","mikania");
		PreparedStatement stmt_search = con.prepareStatement("select * from Confusion_matrix where VS1=? and VS2=? and G1=? and G2=? and R1=? and R2=? and Model=? and Type=2");
		PreparedStatement stmt_insert = con.prepareStatement("insert into Confusion_matrix (VS1, VS2, G1, G2, R1, R2, Model, Type) values (?,?,?,?,?,?,?,2)");
		PreparedStatement stmt_update = con.prepareStatement("update Confusion_matrix set VS1_Volume=?, VS2_Volume=?, Overlap=?, Threshold=?, TP=?, TN=?, FP=?, FN=?, TIFF=? "
				+ "where VS1=? and VS2=? and G1=? and G2=? and R1=? and R2=? and Model=? and Type=2");
		int current = 1;
		String occSample = "E:/HU/Rscript/Grid_Repeat_No_%d/VS_%d/Repeat_%d/vs_d_sample.csv";
		for (File tif : allResult){
			System.out.println("(" + current + "/" + allResult.size() + ") Handling " + tif.getAbsolutePath());
			current++;
			String[] info = tif.getName().replace(".tif", "").split("\\.");
			int G1 = -1;
			int G2 = -1;
			int R1 = -1;
			int R2 = -1;
			int VS1 = -1;
			int VS2 = -1;
			double VS1_Volume = -1;
			double VS2_Volume = -1;
			double overlap = -1;
			double threshold = Double.MAX_VALUE;
			int TP = 0;
			int TN = 0;
			int FP = 0;
			int FN = 0;
			String model = "";
			
			
			if (info.length==6){
				G1 = Integer.valueOf(info[2].replace("G1_", ""));
				VS1 = Integer.valueOf(info[0].replace("VS1_", ""));
				R1 = Integer.valueOf(info[4].replace("R1_", ""));
				G2 = Integer.valueOf(info[3].replace("G2_", ""));
				VS2 = Integer.valueOf(info[1].replace("VS2_", ""));
				String[] split2 = info[5].split("_");
				R2 = Integer.valueOf(split2[1]);
				model = split2[2];
			}
			stmt_search.setInt(1, VS1);
			stmt_search.setInt(2, VS2);
			stmt_search.setInt(3, G1);
			stmt_search.setInt(4, G2);
			stmt_search.setInt(5, R1);
			stmt_search.setInt(6, R2);
			stmt_search.setString(7, model);
			ResultSet rs = stmt_search.executeQuery();
			boolean isFinished = false;
			while (rs.next()){
				isFinished = true;
				break;
			}
			rs.close();
			if (isFinished){
				System.out.println("Handled, skip it");
				continue;
			}
			
			//insert a record, means I am working on it
			stmt_insert.setInt(1, VS1);
			stmt_insert.setInt(2, VS2);
			stmt_insert.setInt(3, G1);
			stmt_insert.setInt(4, G2);
			stmt_insert.setInt(5, R1);
			stmt_insert.setInt(6, R2);
			stmt_insert.setString(7, model);
			stmt_insert.execute();
			Thread.sleep((long) (Math.random() * 1000));			
			String occ_file = String.format(occSample, G1, VS1, R1);
			HashSet<int[]> xy = null;
			if (occurrences.containsKey(occ_file)){
				xy = occurrences.get(occ_file);
			}else{
				xy = new HashSet<int[]>();
				ArrayList<String> occurrences_str = CommonFun.readFromFile(occ_file);
				for (String occ_str : occurrences_str){
					String[] s = occ_str.split(",");
					if (s.length==9){
						if (CommonFun.isInteger(s[1])){
							int[] xyitem = new int[] {Integer.valueOf(s[1]).intValue(), Integer.valueOf(s[2]).intValue()};
							xy.add(xyitem);
						}
					}
				}
				occurrences.put(occ_file, xy);
			}
			
			//get the MTP value
			GeoTiffObject geo_result = new GeoTiffObject(tif.getAbsolutePath());
			for (int[] xyitem : xy){
				double v = geo_result.readByXY(xyitem[0], xyitem[1]);
				if (v>0){
					threshold = (threshold>v)?v:threshold;
				}
			}
			GeoTiffObject geo_true = trueDistribution.get(VS1);
			for (int x = 0; x<mask.getXSize(); x++){
				for (int y = 0; y<mask.getYSize(); y++){
					double v = mask.readByXY(x, y);
					if (!CommonFun.equal(v, mask.getNoData(), 1000)){
						double v_true = geo_true.readByXY(x, y);
						double v_result = geo_result.readByXY(x, y);
						if (CommonFun.equal(v_true, 255, 1000)){
							if (v_result>=threshold){
								TP++;
							}else{
								FP++;
							}
						}else{
							if (v_result>=threshold){
								FN++;
							}else{
								TN++;
							}
						}
					}
				}
			}
			
			if (overlaps.containsKey(String.format("%d.%d", VS1, VS2))){
				OverlapObject overlap_obj = overlaps.get(String.format("%d.%d", VS1, VS2));
				VS1_Volume = overlap_obj.getV_vs1();
				VS2_Volume = overlap_obj.getV_vs2();
				overlap = overlap_obj.getOverlap();
			}
			stmt_update.setDouble(1, VS1_Volume);
			stmt_update.setDouble(2, VS2_Volume);
			stmt_update.setDouble(3, overlap);
			stmt_update.setDouble(4, threshold);
			stmt_update.setInt(5, TP);
			stmt_update.setInt(6, TN);
			stmt_update.setInt(7, FP);
			stmt_update.setInt(8, FN);
			stmt_update.setString(9, tif.getAbsolutePath());
			stmt_update.setInt(10, VS1);
			stmt_update.setInt(11, VS2);
			stmt_update.setInt(12, G1);
			stmt_update.setInt(13, G2);
			stmt_update.setInt(14, R1);
			stmt_update.setInt(15, R2);
			stmt_update.setString(16, model);
			stmt_update.execute();
		}
		stmt_insert.close();
		stmt_search.close();
		stmt_update.close();
		con.close();
	}
	
	@Test
	public void calculateCM() throws IOException, SQLException, InterruptedException{
		GeoTiffObject mask = new GeoTiffObject("E:/HU/bioclim/clim/PCA/1.tiff");
		ArrayList<String> overlapstr = CommonFun.readFromFile("E:/HU/GPLUS/VirtualSpecies/Overlap/overlap_result.csv");
		HashMap<String, OverlapObject> overlaps = new HashMap<String, OverlapObject>();
		HashSet<Integer> vsList = new HashSet<Integer>();
		for (String o : overlapstr){
			String[] os = o.split(",");
			if (os.length>=7){
				if (CommonFun.isDouble(os[1])){
					OverlapObject obj = new OverlapObject(Integer.valueOf(os[0].replace("E:\\HU\\GPLUS\\VirtualSpecies\\VS", "")), 
							Integer.valueOf(os[5].replace("E:\\HU\\GPLUS\\VirtualSpecies\\VS", "")), 
							Double.valueOf(os[1]), Double.valueOf(os[6]), Double.valueOf(os[10]));
					overlaps.put(String.format("%d.%d", obj.getVs1(), obj.getVs2()), obj);
					vsList.add(obj.getVs1());
					vsList.add(obj.getVs2());
				}
			}
		}
		HashMap<String, HashSet<int[]>> occurrences = new HashMap<String, HashSet<int[]>>();
		HashMap<Integer, GeoTiffObject> trueDistribution = new HashMap<Integer, GeoTiffObject>();
		String baseFolder = "E:/HU/GPLUS";
		for (int i = 1; i<=11; i++){
			GeoTiffObject dis = new GeoTiffObject(String.format("%s/VirtualSpecies/VS%d/present.tiff", baseFolder, i));
			trueDistribution.put(i, dis);
		}
		ArrayList<File> allResult = new ArrayList<File>();
		File singleFolder = new File("E:/HU/Rscript/AllResults/Single");
		for (File f : singleFolder.listFiles()){
			if (f.getName().endsWith(".tif")){
				allResult.add(f);
			}
		}
		
		File p1Folder = new File("E:/HU/Rscript/AllResults/P1");
		for (File f : p1Folder.listFiles()){
			if (f.getName().endsWith(".tif")){
				allResult.add(f);
			}
		}
		Connection con= (Connection) DriverManager.getConnection("jdbc:mysql://159.226.67.79:3306/hu?useUnicode=true&amp;characterEncoding=utf-8","root","mikania");
		PreparedStatement stmt_search = con.prepareStatement("select * from Confusion_matrix where VS1=? and VS2=? and G1=? and G2=? and R1=? and R2=? and Model=? and Type=1");
		PreparedStatement stmt_insert = con.prepareStatement("insert into Confusion_matrix (VS1, VS2, G1, G2, R1, R2, Model, Type) values (?,?,?,?,?,?,?,1)");
		PreparedStatement stmt_update = con.prepareStatement("update Confusion_matrix set VS1_Volume=?, VS2_Volume=?, Overlap=?, Threshold=?, TP=?, TN=?, FP=?, FN=?, TIFF=? "
				+ "where VS1=? and VS2=? and G1=? and G2=? and R1=? and R2=? and Model=? and Type=1");
		int current = 1;
		String occSample = "E:/HU/Rscript/Grid_Repeat_No_%d/VS_%d/Repeat_%d/vs_d_sample.csv";
		for (File tif : allResult){
			System.out.println("(" + current + "/" + allResult.size() + ") Handling " + tif.getAbsolutePath());
			current++;
			String[] info = tif.getName().replace(".tif", "").split("\\.");
			int G1 = -1;
			int G2 = -1;
			int R1 = -1;
			int R2 = -1;
			int VS1 = -1;
			int VS2 = -1;
			double VS1_Volume = -1;
			double VS2_Volume = -1;
			double overlap = -1;
			double threshold = Double.MAX_VALUE;
			int TP = 0;
			int TN = 0;
			int FP = 0;
			int FN = 0;
			String model = "";
			if (info.length==4){
				G1 = Integer.valueOf(info[0].replace("G_", ""));
				VS1 = Integer.valueOf(info[1].replace("VS_", ""));
				R1 = Integer.valueOf(info[2].replace("R_", ""));
				model = info[3];
			}
			
			if (info.length==7){
				G1 = Integer.valueOf(info[0].replace("G1_", ""));
				VS1 = Integer.valueOf(info[1].replace("VS1_", ""));
				R1 = Integer.valueOf(info[2].replace("R1_", ""));
				G2 = Integer.valueOf(info[3].replace("G2_", ""));
				VS2 = Integer.valueOf(info[4].replace("VS2_", ""));
				R2 = Integer.valueOf(info[5].replace("R2_", ""));
				model = info[6];
			}
			stmt_search.setInt(1, VS1);
			stmt_search.setInt(2, VS2);
			stmt_search.setInt(3, G1);
			stmt_search.setInt(4, G2);
			stmt_search.setInt(5, R1);
			stmt_search.setInt(6, R2);
			stmt_search.setString(7, model);
			ResultSet rs = stmt_search.executeQuery();
			boolean isFinished = false;
			while (rs.next()){
				isFinished = true;
				break;
			}
			rs.close();
			if (isFinished){
				System.out.println("Handled, skip it");
				continue;
			}
			
			//insert a record, means I am working on it
			stmt_insert.setInt(1, VS1);
			stmt_insert.setInt(2, VS2);
			stmt_insert.setInt(3, G1);
			stmt_insert.setInt(4, G2);
			stmt_insert.setInt(5, R1);
			stmt_insert.setInt(6, R2);
			stmt_insert.setString(7, model);
			stmt_insert.execute();
			Thread.sleep((long) (Math.random() * 1000));			
			String occ_file = String.format(occSample, G1, VS1, R1);
			HashSet<int[]> xy = null;
			if (occurrences.containsKey(occ_file)){
				xy = occurrences.get(occ_file);
			}else{
				xy = new HashSet<int[]>();
				ArrayList<String> occurrences_str = CommonFun.readFromFile(occ_file);
				for (String occ_str : occurrences_str){
					String[] s = occ_str.split(",");
					if (s.length==9){
						if (CommonFun.isInteger(s[1])){
							int[] xyitem = new int[] {Integer.valueOf(s[1]).intValue(), Integer.valueOf(s[2]).intValue()};
							xy.add(xyitem);
						}
					}
				}
				occurrences.put(occ_file, xy);
			}
			
			//get the MTP value
			GeoTiffObject geo_result = new GeoTiffObject(tif.getAbsolutePath());
			for (int[] xyitem : xy){
				double v = geo_result.readByXY(xyitem[0], xyitem[1]);
				if (v>0){
					threshold = (threshold>v)?v:threshold;
				}
			}
			GeoTiffObject geo_true = trueDistribution.get(VS1);
			for (int x = 0; x<mask.getXSize(); x++){
				for (int y = 0; y<mask.getYSize(); y++){
					double v = mask.readByXY(x, y);
					if (!CommonFun.equal(v, mask.getNoData(), 1000)){
						double v_true = geo_true.readByXY(x, y);
						double v_result = geo_result.readByXY(x, y);
						if (CommonFun.equal(v_true, 255, 1000)){
							if (v_result>=threshold){
								TP++;
							}else{
								FN++;
							}
						}else{
							if (v_result>=threshold){
								FP++;
							}else{
								TN++;
							}
						}
					}
				}
			}
			
			if (overlaps.containsKey(String.format("%d.%d", VS1, VS2))){
				OverlapObject overlap_obj = overlaps.get(String.format("%d.%d", VS1, VS2));
				VS1_Volume = overlap_obj.getV_vs1();
				VS2_Volume = overlap_obj.getV_vs2();
				overlap = overlap_obj.getOverlap();
			}
			stmt_update.setDouble(1, VS1_Volume);
			stmt_update.setDouble(2, VS2_Volume);
			stmt_update.setDouble(3, overlap);
			stmt_update.setDouble(4, threshold);
			stmt_update.setInt(5, TP);
			stmt_update.setInt(6, TN);
			stmt_update.setInt(7, FP);
			stmt_update.setInt(8, FN);
			stmt_update.setString(9, tif.getAbsolutePath());
			stmt_update.setInt(10, VS1);
			stmt_update.setInt(11, VS2);
			stmt_update.setInt(12, G1);
			stmt_update.setInt(13, G2);
			stmt_update.setInt(14, R1);
			stmt_update.setInt(15, R2);
			stmt_update.setString(16, model);
			stmt_update.execute();
		}
		stmt_insert.close();
		stmt_search.close();
		stmt_update.close();
		con.close();
	}
	@Test
	public void addValues() throws IOException{
		GeoTiffObject pc1 = new GeoTiffObject("E:/Dropbox/Papers/DarwinFox/worldclim/PCA/1.tiff");
		GeoTiffObject pc2 = new GeoTiffObject("E:/Dropbox/Papers/DarwinFox/worldclim/PCA/2.tiff");
		GeoTiffObject pc3 = new GeoTiffObject("E:/Dropbox/Papers/DarwinFox/worldclim/PCA/3.tiff");
		ArrayList<String> lls = CommonFun.readFromFile("E:/Dropbox/Papers/DarwinFox/DarwinFox_All.csv");
		StringBuilder sb = new StringBuilder();
		HashSet<String> xys = new HashSet<String>();
		int id = 1;
		for (String llstr : lls){
			String[] ll = llstr.split(",");
			if (ll.length<4){
				continue;
			}
				
			if (CommonFun.isDouble(ll[1])&&CommonFun.isDouble(ll[2])){
				double v1 = pc1.readByLL(Double.valueOf(ll[1]).doubleValue(), Double.valueOf(ll[2]).doubleValue());
				double v2 = pc2.readByLL(Double.valueOf(ll[1]).doubleValue(), Double.valueOf(ll[2]).doubleValue());
				double v3 = pc3.readByLL(Double.valueOf(ll[1]).doubleValue(), Double.valueOf(ll[2]).doubleValue());
				sb.append(String.format("%d,%s,%f,%f,%f%n", id, llstr, v1, v2, v3));
				id++;
				int[] xy = CommonFun.LLToPosition(pc1.getDataset().GetGeoTransform(), new double[]{
					Double.valueOf(ll[1]).doubleValue(), Double.valueOf(ll[2]).doubleValue()});
				xys.add(String.format("%d,%d", xy[0], xy[1]));
			}else{
				sb.append(String.format("Object-ID,%s,PC1,PC2,PC3%n", llstr));
			}
		}
		int n_pseudo = 1000;
		int i_pseudo = 0;
		while (i_pseudo<n_pseudo){
			int x = (int) (Math.random() * pc1.getXSize());
			if (x>=pc1.getXSize()){
				continue;
			}
			
			int y = (int) (Math.random() * pc1.getYSize());
			if (y>=pc1.getYSize()){
				continue;
			}
			
			String xy_label = String.format("%d,%d", x, y);
			if (xys.contains(xy_label)){
				continue;
			}
			double v = pc1.readByXY(x, y);
			if (CommonFun.equal(v, pc1.getNoData(), 1000)){
				continue;
			}
			double[] ll_random = CommonFun.PositionToLL(pc1.getDataset().GetGeoTransform(), new int[]{x, y});
			double v1 = pc1.readByXY(x, y);
			double v2 = pc2.readByXY(x, y);
			double v3 = pc3.readByXY(x, y);
			sb.append(String.format("%d,Pseudo,%f,%f,Pseudo,%f,%f,%f,%n", id, ll_random[0], ll_random[1], v1, v2, v3));
			id++;
			i_pseudo ++;
		}
		CommonFun.writeFile(sb.toString(), "E:/Dropbox/Papers/DarwinFox/DarwinFox_Value.csv");
	}
	private HashSet<String> textTohash(ArrayList<String> occ, double[] geo){
		HashSet<String> hash = new HashSet<String>();
		for (String s : occ){
			String[] ss = s.split(",");
			if (ss.length>=3){
				if (CommonFun.isDouble(ss[1])&&(CommonFun.isDouble(ss[2]))){
					int[] xy = CommonFun.LLToPosition(geo, new double[]{Double.valueOf(ss[1]), Double.valueOf(ss[2])});
					hash.add(String.format("%d,%d", xy[0], xy[1]));
				}
			}
		}
		return hash;
	}
	@Test
	public void raster2text() throws IOException{
		GeoTiffObject enm_c = new GeoTiffObject("E:/DarwinFox/GARP/garp_c_n.tif");
		GeoTiffObject enm_n = new GeoTiffObject("E:/DarwinFox/GARP/garp_c_n.tif");
		GeoTiffObject enm_c_n = new GeoTiffObject("E:/DarwinFox/GARP/garp_c_n.tif");
		GeoTiffObject enm_all = new GeoTiffObject("E:/DarwinFox/GARP/garp_c_n.tif");
		ArrayList<String> occ_c = CommonFun.readFromFile("E:/DarwinFox/Maxent/Chiloe/1.csv");
		ArrayList<String> occ_n = CommonFun.readFromFile("E:/DarwinFox/Maxent/Nahuelbuta/1.csv");
		ArrayList<String> occ_m = CommonFun.readFromFile("E:/DarwinFox/Maxent/Middle/1.csv");
		HashSet<String> hash_c = textTohash(occ_c, enm_c.getDataset().GetGeoTransform());
		HashSet<String> hash_n = textTohash(occ_n, enm_c.getDataset().GetGeoTransform());
		HashSet<String> hash_m = textTohash(occ_m, enm_c.getDataset().GetGeoTransform());

		StringBuilder sb = new StringBuilder();
		sb.append("x,y,value_c, value_n, value_c_n, value_all, occu_l" + Const.LineBreak);
		
		for (String s : occ_c){
			String[] ss = s.split(",");
			if (ss.length>=3){
				if (CommonFun.isDouble(ss[1])&&(CommonFun.isDouble(ss[2]))){
					double v_c = enm_c.readByLL(Double.valueOf(ss[1]), Double.valueOf(ss[2]));
					double v_n = enm_n.readByLL(Double.valueOf(ss[1]), Double.valueOf(ss[2]));
					double v_c_n = enm_c_n.readByLL(Double.valueOf(ss[1]), Double.valueOf(ss[2]));
					double v_all = enm_all.readByLL(Double.valueOf(ss[1]), Double.valueOf(ss[2]));
					sb.append(String.format("%s,%s,%f,%f,%f,%f,%s%n", ss[1], ss[2], v_c, v_n, v_c_n, v_all, "Chiloe"));
				}
			}
		}
		
		for (String s : occ_n){
			String[] ss = s.split(",");
			if (ss.length>=3){
				if (CommonFun.isDouble(ss[1])&&(CommonFun.isDouble(ss[2]))){
					double v_c = enm_c.readByLL(Double.valueOf(ss[1]), Double.valueOf(ss[2]));
					double v_n = enm_n.readByLL(Double.valueOf(ss[1]), Double.valueOf(ss[2]));
					double v_c_n = enm_c_n.readByLL(Double.valueOf(ss[1]), Double.valueOf(ss[2]));
					double v_all = enm_all.readByLL(Double.valueOf(ss[1]), Double.valueOf(ss[2]));
					sb.append(String.format("%s,%s,%f,%f,%f,%f,%s%n", ss[1], ss[2], v_c, v_n, v_c_n, v_all, "Nahuelbuta"));
				}
			}
		}
		
		for (String s : occ_m){
			String[] ss = s.split(",");
			if (ss.length>=3){
				if (CommonFun.isDouble(ss[1])&&(CommonFun.isDouble(ss[2]))){
					double v_c = enm_c.readByLL(Double.valueOf(ss[1]), Double.valueOf(ss[2]));
					double v_n = enm_n.readByLL(Double.valueOf(ss[1]), Double.valueOf(ss[2]));
					double v_c_n = enm_c_n.readByLL(Double.valueOf(ss[1]), Double.valueOf(ss[2]));
					double v_all = enm_all.readByLL(Double.valueOf(ss[1]), Double.valueOf(ss[2]));
					sb.append(String.format("%s,%s,%f,%f,%f,%f,%s%n", ss[1], ss[2], v_c, v_n, v_c_n, v_all, "Middle"));
				}
			}
		}
		
//		for (int x=0;x<enm_c.getXSize();x++){
//			for (int y=0;y<enm_c.getYSize();y++){
//				double v_c = enm_c.readByXY(x, y);
//				if (!CommonFun.equal(v_c, enm_c.getNoData(), 1000)){
//					double v_n = enm_n.readByXY(x, y);
//					double v_c_n = enm_c_n.readByXY(x, y);
//					double v_all = enm_all.readByXY(x, y);
//					String label = "BG";
//					if (hash_c.contains(String.format("%d,%d", x, y))){
//						label = "Chiloe";
//					}
//					if (hash_n.contains(String.format("%d,%d", x, y))){
//						label = "Nahuelbuta";
//					}
//					if (hash_m.contains(String.format("%d,%d", x, y))){
//						label = "Middle";
//					}
//					sb.append(String.format("%d,%d,%f,%f,%f,%f,%s%n", x, y, v_c, v_n, v_c_n, v_all, label));
//				}
//			}
//		}
		CommonFun.writeFile(sb.toString(), "E:/DarwinFox/Rscript/occurrence_value_garp.csv");
		
	}
	
	@Test
	public void replaceTest(){
		String a = "a x% xxx";
		System.out.println(a.replace("x%", "y"));
		
	}
	@Test
	public void dismo_text2raster() throws IOException{
		GeoTiffObject map = new GeoTiffObject("E:/DarwinFox/dismo/C_N/1.tif");
		ArrayList<String> result = CommonFun.readFromFile("E:/DarwinFox/dismo/C_N/result.1.txt");
		double[] values = new double[map.getXSize() * map.getYSize()];
		for (int x=0;x<map.getXSize();x++){
			for (int y=0;y<map.getYSize();y++){
				double v = map.readByXY(x, y);
				if (CommonFun.equal(v, map.getNoData(), 10000)){
					values[x+y*map.getXSize()] = map.getNoData();
				}
			}
		}
		for (String s : result){
			String[] ss = s.split(",");
			if (ss.length<7){
				continue;
			}
			if (CommonFun.isDouble(ss[0])&&CommonFun.isDouble(ss[1])){
				int[] xy = CommonFun.LLToPosition(map.getDataset().GetGeoTransform(), 
						new double[]{Double.valueOf(ss[0]), Double.valueOf(ss[1])});
				double v = Double.valueOf(ss[6]).doubleValue();
				//System.out.println(v);
				values[xy[1] * map.getXSize() + xy[0]] = v;
			}
		}
		GeoTiffController.createTiff("E:/DarwinFox/dismo/C_N/result.tif", map.getXSize(), map.getYSize(), 
				map.getDataset().GetGeoTransform(), values, map.getNoData(), gdalconst.GDT_Float32, map.getDataset().GetProjection());
		
	}
	@Test
	public void mkne_text2raster() throws IOException{
		GeoTiffObject map = new GeoTiffObject("E:/DarwinFox/KDE/ALL/1.tif");
		ArrayList<String> result = CommonFun.readFromFile("E:/DarwinFox/KDE/ALL/result.1.txt");
		double[] values = new double[map.getXSize() * map.getYSize()];
		for (int x=0;x<map.getXSize();x++){
			for (int y=0;y<map.getYSize();y++){
				double v = map.readByXY(x, y);
				if (CommonFun.equal(v, map.getNoData(), 10000)){
					values[x+y*map.getXSize()] = map.getNoData();
				}
			}
		}
		for (String s : result){
			String[] ss = s.split(",");
			if (ss.length<6){
				continue;
			}
			if (CommonFun.isDouble(ss[0])&&CommonFun.isDouble(ss[1])){
				int[] xy = CommonFun.LLToPosition(map.getDataset().GetGeoTransform(), 
						new double[]{Double.valueOf(ss[0]), Double.valueOf(ss[1])});
				int v = 0;
				if (ss[5].equalsIgnoreCase("TRUE")){
					v = 1;
				}
				values[xy[1] * map.getXSize() + xy[0]] = v;
			}
		}
		GeoTiffController.createTiff("E:/DarwinFox/KDE/ALL/KDE_ALL.tif", map.getXSize(), map.getYSize(), 
				map.getDataset().GetGeoTransform(), values, map.getNoData(), gdalconst.GDT_Int32, map.getDataset().GetProjection());
		
	}
	
	@Test
	public void format_enm_result() throws FileNotFoundException{
		GeoTiffObject geo = new GeoTiffObject("E:/DarwinFox/ENMResults/origin/GLM_ALL.tif");
		double[] values = geo.getValueArray();
		for (int x=0;x<geo.getXSize();x++){
			for (int y=0;y<geo.getYSize();y++){
				double v = geo.readByXY(x, y);
				if (!CommonFun.equal(v, geo.getNoData(), 1000)){
					values[y * geo.getXSize() + x] = values[y * geo.getXSize() + x]/1000;
				}
			}
		}
		GeoTiffController.createTiff("E:/DarwinFox/ENMResults/formatted/GLM_ALL.tif", 
				geo.getXSize(), geo.getYSize(), geo.getDataset().GetGeoTransform(), values, geo.getNoData(), gdalconst.GDT_Float32, geo.getDataset().GetProjection());
	}
	
	@Test
	public void getENMValue() throws IOException{
		ArrayList<String> occur = CommonFun.readFromFile("E:/DarwinFox/occurrences/DarwinFox_All_NicheA.csv");
		StringBuilder sb = new StringBuilder();
		String head = "Species,Longitude,Latitude,populations";
		File folder = new File("E:/DarwinFox/ENMResults/formatted");
		HashMap<String, GeoTiffObject> results = new HashMap<String, GeoTiffObject>();
		for (File f : folder.listFiles()){
			if (f.isFile() && f.getAbsolutePath().endsWith(".tif")){
				String label = f.getName().replace(".tif", "");
				results.put(label, new GeoTiffObject(f.getAbsolutePath()));
			}
		}
		int i = 0;
		for (String s : occur){
			String[] ss = s.split(",");
			if (ss.length==4){
				if (CommonFun.isDouble(ss[1])&&CommonFun.isDouble(ss[2])){
					String vstr = s;
					for (String label : results.keySet()){
						if (i==0){
							head += "," + label;
						}
						vstr += "," + results.get(label).readByLL(Double.valueOf(ss[1]), Double.valueOf(ss[2]));
					}
					if (sb.length()==0){
						sb.append(head + Const.LineBreak);
					}
					sb.append(vstr + Const.LineBreak);
					i++;
				}
			}
		}
		CommonFun.writeFile(sb.toString(), "E:/DarwinFox/Figures/Table2/enm_results.csv");
	}
	@Test
	public void countTrue() throws FileNotFoundException{
		GeoTiffObject geo = new GeoTiffObject("E:/DarwinFox/ENM_Results/middle/GARP_C_N.tif");
		double threshold = 70;
		int c = 0;
		int a = 0;
		for (int x=0;x<geo.getXSize();x++){
			for (int y=0;y<geo.getYSize();y++){
				double v = geo.readByXY(x, y);
				if (CommonFun.equal(v, geo.getNoData(), 1000)){
					continue;
				}
				a++;
				
				if (v>=threshold){
					c++;
				}
			}
		}
		System.out.println(c + "/" + a + "=" + (double)c/(double)a);
	}
	
	@Test
	public void markGrid() throws IOException{
		GeoTiffObject pc1 = new GeoTiffObject("E:/HU/bioclim/clim/PCA/1.tiff");
		GeoTiffObject pc2 = new GeoTiffObject("E:/HU/bioclim/clim/PCA/2.tiff");
		GeoTiffObject pc3 = new GeoTiffObject("E:/HU/bioclim/clim/PCA/3.tiff");
		double[] values = pc1.getValueArray();
		
		int mark_number = 10;
		StringBuilder sb = new StringBuilder();
		sb.append("VSID,X,Y,Long,Lat,V1,V2,V3,GridID" + Const.LineBreak);
		int grid_x_scale = pc1.getXSize() / mark_number;
		int grid_y_scale = pc1.getYSize() / mark_number;

		for (int i=1;i<=11;i++){
			System.out.println(i);
			String base = String.format("E:/HU/GPLUS/VirtualSpecies/VS%d/", i);
			GeoTiffObject geo = new GeoTiffObject(base + "present.tiff");
			for (int x = 0;x<geo.getXSize();x++){
				for (int y = 0; y<geo.getYSize();y++){
					double v = geo.readByXY(x, y);
					int grid_x = x/grid_x_scale;
					int grid_y = y/grid_y_scale;
					int grid_id = grid_y * mark_number + grid_x;
//					System.out.println(x + "," + y + "," + grid_id);
					values[y* geo.getXSize() + x] = grid_id;
					if (CommonFun.equal(v, 255, 1000)){
						
						double[] ll = CommonFun.PositionToLL(geo.getDataset().GetGeoTransform(), new int[]{x, y});
						sb.append(String.format("%d,%d,%d,%f,%f,%f,%f,%f,%d%n", 
								i, x, y, ll[0], ll[1], 
								pc1.readByXY(x, y),
								pc2.readByXY(x, y),
								pc3.readByXY(x, y),
								grid_id));
					}
				}
				//break;
			}
			//break;
		}
		CommonFun.writeFile(sb.toString(), "E:/HU/GPLUS/VirtualSpecies/all.csv");
		GeoTiffController.createTiff("E:/HU/GPLUS/VirtualSpecies/grid.tiff", 
				pc1.getXSize(), pc1.getYSize(), pc1.getDataset().GetGeoTransform(), values, -9999, gdalconst.GDT_Int32, pc1.getDataset().GetProjection());
	}
	@Test
	public void getP1List() throws IOException{
		File f = new File("E:/HU/Rscript/AllResults/P1");
		StringBuilder sb = new StringBuilder();
		for (String ff : f.list()){
			sb.append(ff + Const.LineBreak);
		}
		CommonFun.writeFile(sb.toString(), "E:/HU/Rscript/AllResults/allp.csv");
		
	}
	@Test
	public void addLLtoAirport() throws SQLException{
		Connection con= (Connection) DriverManager.getConnection("jdbc:mysql://127.0.0.1:3307/flights?useUnicode=true&amp;characterEncoding=utf-8","root","mikania");
		PreparedStatement stmt_search1 = con.prepareStatement("select distinct o_airport_name from passenger_estimation where o_airport_longitude is null");
		PreparedStatement stmt_search = con.prepareStatement("select * from airports where name=?");
		PreparedStatement stmt_update1 = con.prepareStatement("update passenger_estimation set o_airport_longitude=?, o_airport_latitude=? where o_airport_name=?");
		PreparedStatement stmt_update2 = con.prepareStatement("update passenger_estimation set d_airport_longitude=?, d_airport_latitude=? where d_airport_name=?");
		ResultSet rs = stmt_search1.executeQuery();
		int index = 1;
		while (rs.next()){
			System.out.println(index++);
			stmt_search.setString(1, rs.getString("o_airport_name"));
			ResultSet rs2 = stmt_search.executeQuery();
			while (rs2.next()){
				stmt_update1.setString(1, rs2.getString("longitude_deg"));
				stmt_update2.setString(1, rs2.getString("longitude_deg"));
				stmt_update1.setString(2, rs2.getString("latitude_deg"));
				stmt_update2.setString(2, rs2.getString("latitude_deg"));
				stmt_update1.setString(3, rs2.getString("name"));
				stmt_update2.setString(3, rs2.getString("name"));
				stmt_update1.execute();
				stmt_update2.execute();
				break;
			}
			rs2.close();
		}
		rs.close();
		
	}
	@Test
	public void getTemp() throws IOException{
		ArrayList<String> llstr = CommonFun.readFromFile("E:/DarwinFox/data/DarwinFox_All.csv");
		GeoTiffObject map = new GeoTiffObject("E:/DarwinFox/worldclim/bio12.tif");
		for (String llstrs : llstr){
			String[] lls = llstrs.split(",");
			if (lls.length==4){
				if (CommonFun.isDouble(lls[1])&&(CommonFun.isDouble(lls[2]))){
					double lon = Double.valueOf(lls[1]);
					double lat = Double.valueOf(lls[2]);
					System.out.println(map.readByLL(lon, lat));
				}
			}
		}
	}
}
