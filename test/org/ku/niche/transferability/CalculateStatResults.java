/******************************************************************************
 * Huijie Qiao
 *
 * Project:  NicheA
 * Purpose:  
 * Created date: Dec 19, 2012 4:42:51 PM
 * Author:   Huijie Qiao
 *
 ******************************************************************************
 * Copyright (c) 2012, Huijie Qiao
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 ****************************************************************************/


package org.ku.niche.transferability;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoid;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoidResult;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.ConfigInfo;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.MathUtility;
import org.ku.nicheanalyst.dataset.SpeciesData;
import org.ku.nicheanalyst.dataset.SpeciesObject;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

import quickhull3d.Point3d;
import quickhull3d.QuickHull3D;
import rjava.rcaller.RCaller;
import rjava.rcaller.RCode;

import Jama.Matrix;

import com.mysql.jdbc.Connection;

/**
 * @author Huijie Qiao
 *
 */
public class CalculateStatResults {
	private ArrayList<File> getValues(String folder) {
		ArrayList<File> ll = new ArrayList<File>();
		File fold = new File(folder);
		for (File f : fold.listFiles()){
			if (f.isDirectory()){
				ArrayList<File> lltemp = getValues(f.getAbsolutePath());
				for (File ff : lltemp){
					ll.add(ff);
				}
			}else{
				if (f.getName().equalsIgnoreCase("value.txt")&&(!f.getAbsolutePath().contains("background"))){
					ll.add(f);
				}
			}
		}
		return ll;
	}
	private SpeciesObject getSpecies(ResultSet srs, HashMap<String, SpeciesObject> vslist) throws SQLException {
		String label = String.format("VS%d_%s.", srs.getInt("vs_no"), srs.getString("vs_size"));
		for (String key : vslist.keySet()){
			if (key.contains(label)){
				return vslist.get(key);
			}
		}
		System.out.println("Error to get " + label);
		return null;
	}
	private String dealVPLabel(File f){
		String a = f.getAbsolutePath().replace("/", "-").replace("-Users-huijieqiao-Dropbox-Papers-NicheTransferability-VS-", "").replace("value.txt", "");
		return a.substring(0, a.length()-1) + ".";
	}
	private String getLabel(ResultSet srs) throws SQLException {
		
		return srs.getString("vs_size") + "-" + srs.getInt("vs_no") + "-" 
			+ srs.getString("scenario") + "-" + srs.getString("replication_no") + "-" 
			+ srs.getString("replication_partkappa") + "-"
			+ srs.getString("algorithm");
	}
	
	@Test
	public void createTableGoA() throws SQLException, IOException{
		ArrayList<String> algorithmlist_background = new ArrayList<String>();
		algorithmlist_background.add("GARP");
		algorithmlist_background.add("GARP_BS");
		algorithmlist_background.add("MAXENT_JAVA");
		algorithmlist_background.add("MVE");
		algorithmlist_background.add("CH");
		ArrayList<String> algorithmlist_presence_only = new ArrayList<String>();
		algorithmlist_presence_only.add("BIOCLIM");
//		algorithmlist_presence_only.add("ENVDIST");
//		algorithmlist_presence_only.add("ENVSCORE");
		algorithmlist_presence_only.add("MA");
		
		ArrayList<String> algorithmlist_precence_absence = new ArrayList<String>();
		algorithmlist_precence_absence.add("ANN_BIOMOD");
		algorithmlist_precence_absence.add("GAM");
		algorithmlist_precence_absence.add("GBM");
		algorithmlist_precence_absence.add("GLM");
//		algorithmlist_precence_absence.add("MARS");
		algorithmlist_precence_absence.add("RF");
		algorithmlist_precence_absence.add("SRE");
		System.out.println("Init vs list");
		HashMap<String, SpeciesObject> vslist = new HashMap<String, SpeciesObject>();
		//vs info
		String folder = "/Users/huijieqiao/Dropbox/Papers/NicheTransferability/VS";
		ArrayList<File> allValues = getValues(folder);
		for (File f : allValues){
			System.out.println(dealVPLabel(f));
			SpeciesObject sp = new SpeciesObject(f.getAbsolutePath());
			vslist.put(dealVPLabel(f), sp);
		}
		System.out.println("Finished to Init vs list");
		if (true){
//			return;
		}
		Connection con= (Connection) DriverManager.getConnection("jdbc:mysql://159.226.67.79:3306/mmwebdbtest?useUnicode=true&amp;characterEncoding=utf-8","root","mikania");
		PreparedStatement stmt_getTSS = con.prepareStatement("UPDATE vp_result SET TSS=sensitivity+specificity-1 WHERE TSS is null");
		stmt_getTSS.execute();
		PreparedStatement stmt_threshold_method = con.prepareStatement("select distinct threshold_method from vp_result where threshold_method<>0 and threshold_method<>10 and threshold_method<>20");
		ResultSet srs_threshold_method = stmt_threshold_method.executeQuery();
		while (srs_threshold_method.next()){
			
			int threshold_method = srs_threshold_method.getInt("threshold_method");
			System.out.println("threshold_method:" + threshold_method);
			//Minimal training presence
			PreparedStatement stmt_head = con.prepareStatement("select " +
					" `vp_size` vs_size,  `vp_no` `vs_no`, `scenario`," +
					"  `replication_partkappa`," +
					"  `replication_no`," +
					"  `algorithm`," +
					"  `threshold`," +
					"  `niche_type`," +
					"  threshold_method `threshold_type`," +
					"  `environment_group`,  `Kappa`," +
					"  `AUC`," +
					"  `PCC`," +
					"  `sensitivity`," +
					"  `specificity`,A1,A2,A3,A4,A5,A6,TSS " +
					" from vp_result where threshold_method=? and absence_type='bg' " +
					" order by niche_type, vs_size, vs_no, scenario, replication_partkappa, " +
					" replication_no, algorithm, threshold, threshold_type, environment_group");
	
			stmt_head.setInt(1, threshold_method);
			ResultSet srs = stmt_head.executeQuery();
			HashMap<String, String> pca_kself = new HashMap<String, String>(); 
			HashMap<String, String> a1_6label = new HashMap<String, String>(); 
			HashMap<String, String> pca_kopposite = new HashMap<String, String>(); 
			HashMap<String, String> pca_kgfull = new HashMap<String, String>(); 
			
			while (srs.next()){
				String label = getLabel(srs);
				String a1_6 = String.format("%d,%d,%d,%d,%d,%d", 
						srs.getInt("a1"), 
						srs.getInt("a2"), 
						srs.getInt("a3"), 
						srs.getInt("a4"), 
						srs.getInt("a5"), 
						srs.getInt("a6")); 
				
				if (srs.getString("environment_group").equals("pca")){
					if (srs.getString("niche_type").equals("SELF")){
						pca_kself.put(label, String.format("%f,%f,%f,%f,%f,%f,%f", 
								srs.getFloat("threshold"),
								srs.getFloat("TSS"),
								srs.getFloat("Kappa"),
								srs.getFloat("AUC"),
								srs.getFloat("PCC"),
								srs.getFloat("sensitivity"),
								srs.getFloat("specificity")));
						a1_6label.put(label, a1_6);
					}
					if (srs.getString("niche_type").equals("OPPOSITE")){
						pca_kopposite.put(label, String.format("%f,%f,%f,%f,%f,%f,%f", 
								srs.getFloat("threshold"),
								srs.getFloat("TSS"),
								srs.getFloat("Kappa"),
								srs.getFloat("AUC"),
								srs.getFloat("PCC"),
								srs.getFloat("sensitivity"),
								srs.getFloat("specificity")));
					}
					if (srs.getString("niche_type").equals("FULL")){
						pca_kgfull.put(label, String.format("%f,%f,%f,%f,%f,%f,%f", 
								srs.getFloat("threshold"),
								srs.getFloat("TSS"),
								srs.getFloat("Kappa"),
								srs.getFloat("AUC"),
								srs.getFloat("PCC"),
								srs.getFloat("sensitivity"),
								srs.getFloat("specificity")));
					}
				}
				
			}
			System.out.println("Finished to get values");
//			stmt_head = con.prepareStatement("select count(1) a from (select distinct `vp_size` vs_size,  `vp_no` `vs_no`, `scenario`," +
//					"  `replication_no`," +
//					"  `replication_partkappa`," +
//					"  `algorithm`" +
//					" from vp_result where threshold_method=? and absence_type='bg') t ");
//			stmt_head.setInt(1, threshold_method);
//			srs = stmt_head.executeQuery();
//			int allcount = 0;
//			while (srs.next()){
//				allcount = srs.getInt("a");
//			}
			stmt_head = con.prepareStatement("select distinct `vp_size` vs_size,  `vp_no` `vs_no`, `scenario`," +
					"  `replication_no`," +
					"  `replication_partkappa`," +
					"  `algorithm`" +
					" from vp_result where threshold_method=? and (taskid not like '%6.left%' and taskid not like '%VS2.SMALL%' and taskid not like '%6.right%') and absence_type='bg' " +
					" order by vs_size, vs_no, scenario, replication_no,replication_partkappa, algorithm");
			stmt_head.setInt(1, threshold_method);
			srs = stmt_head.executeQuery();
			StringBuilder pca_background = new StringBuilder();
			StringBuilder pca_presence_ansence = new StringBuilder();
			StringBuilder pca_presence_only = new StringBuilder();
			StringBuilder pca_all = new StringBuilder();
			pca_background.append("vs_size,vs_no,scenario,replication_no,replication_partkappa,algorithm,points,volume," +
					"threshold_SELF,TSS_SELF,Kappa_SELF,AUC_SELF,PCC_SELF,sensitivity_SELF,specificity_SELF," +
					"threshold_OPPOSITE,TSS_OPPOSITE,Kappa_OPPOSITE,AUC_OPPOSITE,PCC_OPPOSITE,sensitivity_OPPOSITE,specificity_OPPOSITE," +
					"threshold_FULL,TSS_FULL,Kappa_FULL,AUC_FULL,PCC_FULL,sensitivity_FULL,specificity_FULL,A1,A2,A3,A4,A5,A6" + Const.LineBreak);
			pca_presence_ansence.append("vs_size,vs_no,scenario,replication_no,replication_partkappa,algorithm,points,volume," +
					"threshold_SELF,TSS_SELF,Kappa_SELF,AUC_SELF,PCC_SELF,sensitivity_SELF,specificity_SELF," +
					"threshold_OPPOSITE,TSS_OPPOSITE,Kappa_OPPOSITE,AUC_OPPOSITE,PCC_OPPOSITE,sensitivity_OPPOSITE,specificity_OPPOSITE," +
					"threshold_FULL,TSS_FULL,Kappa_FULL,AUC_FULL,PCC_FULL,sensitivity_FULL,specificity_FULL,A1,A2,A3,A4,A5,A6" + Const.LineBreak);
			pca_presence_only.append("vs_size,vs_no,scenario,replication_no,replication_partkappa,algorithm,points,volume," +
					"threshold_SELF,TSS_SELF,Kappa_SELF,AUC_SELF,PCC_SELF,sensitivity_SELF,specificity_SELF," +
					"threshold_OPPOSITE,TSS_OPPOSITE,Kappa_OPPOSITE,AUC_OPPOSITE,PCC_OPPOSITE,sensitivity_OPPOSITE,specificity_OPPOSITE," +
					"threshold_FULL,TSS_FULL,Kappa_FULL,AUC_FULL,PCC_FULL,sensitivity_FULL,specificity_FULL,A1,A2,A3,A4,A5,A6" + Const.LineBreak);
			pca_all.append("vs_size,vs_no,scenario,replication_no,replication_partkappa,algorithm,points,volume," +
					"threshold_SELF,TSS_SELF,Kappa_SELF,AUC_SELF,PCC_SELF,sensitivity_SELF,specificity_SELF," +
					"threshold_OPPOSITE,TSS_OPPOSITE,Kappa_OPPOSITE,AUC_OPPOSITE,PCC_OPPOSITE,sensitivity_OPPOSITE,specificity_OPPOSITE," +
					"threshold_FULL,TSS_FULL,Kappa_FULL,AUC_FULL,PCC_FULL,sensitivity_FULL,specificity_FULL,algorithm_group,A1,A2,A3,A4,A5,A6" + Const.LineBreak);			
			int i = 0;
			while (srs.next()){
				if (Math.round((float)i/100f)==(float)i/100f){
					System.out.println(String.format("%s:%d %d/%d", "Threshold Method", threshold_method, i, 30000));
				}
				i++;
				String label = getLabel(srs);
//				SpeciesObject vp = getSpecies(srs, vslist);
//				String baseInfo = String.format("%s,%d,%f", label.replace("-", ","), vp.getAllRecordCount(), vp.getMve().getVolume());
				String baseInfo = label.replace("-", ",") + ",,";
				String SELFInfo_pca = pca_kself.get(label);
				String OPPOSITEInfo_pca = pca_kopposite.get(label);
				String FULLInfo_pca = pca_kgfull.get(label);
				if (srs.getString("scenario").equals("HD")){
//					GoInfo_pca = AInfo_pca;
//					GnnInfo_pca = AInfo_pca;
				}
				if (algorithmlist_background.contains(srs.getString("algorithm"))){
					pca_background.append(String.format("%s,%s,%s,%s,%s", baseInfo, SELFInfo_pca, OPPOSITEInfo_pca, FULLInfo_pca, a1_6label.get(label)) + Const.LineBreak);
					pca_all.append(String.format("%s,%s,%s,%s,background,%s", baseInfo, SELFInfo_pca, OPPOSITEInfo_pca, FULLInfo_pca, a1_6label.get(label)) + Const.LineBreak);
				}
				if (algorithmlist_precence_absence.contains(srs.getString("algorithm"))){
					pca_presence_ansence.append(String.format("%s,%s,%s,%s,%s", baseInfo, SELFInfo_pca, OPPOSITEInfo_pca, FULLInfo_pca, a1_6label.get(label)) + Const.LineBreak);
					pca_all.append(String.format("%s,%s,%s,%s,presence_absence,%s", baseInfo, SELFInfo_pca, OPPOSITEInfo_pca, FULLInfo_pca, a1_6label.get(label)) + Const.LineBreak);
				}
				if (algorithmlist_presence_only.contains(srs.getString("algorithm"))){
					pca_presence_only.append(String.format("%s,%s,%s,%s,%s", baseInfo, SELFInfo_pca, OPPOSITEInfo_pca, FULLInfo_pca, a1_6label.get(label)) + Const.LineBreak);
					pca_all.append(String.format("%s,%s,%s,%s,presence_only,%s", baseInfo, SELFInfo_pca, OPPOSITEInfo_pca, FULLInfo_pca, a1_6label.get(label)) + Const.LineBreak);
				}
				
			}
			
			CommonFun.writeFile(pca_background.toString(), "/Users/huijieqiao/Dropbox/Papers/NicheTransferability/Stat/pca_background_" + threshold_method + ".csv");
			CommonFun.writeFile(pca_presence_ansence.toString(), "/Users/huijieqiao/Dropbox/Papers/NicheTransferability/Stat/pca_presence_absence_" + threshold_method + ".csv");
			CommonFun.writeFile(pca_presence_only.toString(), "/Users/huijieqiao/Dropbox/Papers/NicheTransferability/Stat/pca_presence_only_" + threshold_method + ".csv");
			CommonFun.writeFile(pca_all.toString(), "/Users/huijieqiao/Dropbox/Papers/NicheTransferability/Stat/pca_all_" + threshold_method + ".csv");
		}

	}
	private String dealVPLabel(String f){
		String a = f.replace("/", "-").replace("-Users-huijieqiao-Dropbox-Papers-NicheTransferability-VS", "")
			.replace("_", ".").replace("-", ".").replace(".tiff", "");
		return a.substring(1, a.length());
	}
	private int pnumber2;
	private double pnumber;
	@Test
	public void calculate() throws SQLException, IOException{
		pnumber = .0d;
		pnumber2 = 5;
		String tempfoler = "/Volumes/Disk2/temp/kappa/" + pnumber2 + "/" + Math.round(pnumber*10) +"/";
		System.out.println("INIT DATA");
		HashSet<String> backgroundxy = CommonFun.readFromFile2HashSet("/Users/huijieqiao/Dropbox/Papers/NicheTransferability/Layers/PCA/background/xy.txt");
		HashMap<String, Boolean> background = new HashMap<String, Boolean>();
		for (String xy : backgroundxy){
			background.put(xy, true);
		}
		//
		
		File folder = new File("/Users/huijieqiao/Dropbox/Papers/NicheTransferability/VS");
		HashMap<String, ResultObject> objects = new HashMap<String, ResultObject>();
		for (File f : folder.listFiles()){
			if (f.isDirectory()){
				for (File tiffile : f.listFiles()){
					if (tiffile.getName().endsWith("_right.tiff")||(tiffile.getName().endsWith("_left.tiff"))){
						System.out.println("INIT " + dealVPLabel(tiffile.getAbsolutePath()));
						ResultObject object = new ResultObject();
						object.setFull(f.getAbsolutePath() + "/present.tiff", 
								"/Users/huijieqiao/Dropbox/Papers/NicheTransferability/Layers/EnvironmentalLayers/bio5_cut.tiff");
						if (background==null){
							background = object.getFull();
						}
						object.setSelf(tiffile.getAbsolutePath());
						if (tiffile.getAbsolutePath().contains("left")){
							object.setOpposite(tiffile.getAbsolutePath().replace("left", "right"));
						}else{
							object.setOpposite(tiffile.getAbsolutePath().replace("right", "left"));
						}
						for (int i=0;i<10;i++){
							object.setExperiments(i, tiffile.getAbsolutePath().replace(".tiff", "_folder") + "/xy" + i + ".txt");
						}
						//VS2.BIG.present.4.right.0.GBM
						objects.put(dealVPLabel(tiffile.getAbsolutePath()), object);
					}
				}
			}
		}
		if (true){
//			return;
		}
		System.out.println("FINISHED INIT");
		
		Connection con= (Connection) DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/mmwebdbtest?" +
				"useUnicode=true&amp;characterEncoding=utf-8","root","mikania");
		PreparedStatement stmt = con.prepareStatement("SELECT * FROM t_task");
		PreparedStatement stmt_check = con.prepareStatement("SELECT * FROM vp_result_marker WHERE taskid=?");
		PreparedStatement stmt_insert_marker = con.prepareStatement("UPDATE vp_result_marker SET ishandled=1 where taskid=?");
		ResultSet srs = stmt.executeQuery();
		ArrayList<String> niche_types = new ArrayList<String>();
		niche_types.add("SELF");
		niche_types.add("OPPOSITE");
		niche_types.add("FULL");
		
		while (srs.next()){
			int ajvalue = 0;
			
			
			String taskid = srs.getString("SpeciesName");
			String vsLabel = getVsLabel(taskid);
			int experimentID = getExperimentID(taskid);
			stmt_check.setString(1, taskid);
			ResultSet srs_check = stmt_check.executeQuery();
			int isExist = 0;
			while (srs_check.next()){
				isExist = srs_check.getInt("ishandled");
				ajvalue = srs_check.getInt("maxFrequencyValue");
			}
			if (isExist==0){
				long end0 = System.currentTimeMillis();
				stmt_insert_marker.setString(1, taskid);
				stmt_insert_marker.execute();
//				if (true){
//					continue;
//				}
				for (String niche_type : niche_types){
					//遍历所有的threshold_method
					for (int threshold_group = 0; threshold_group<3; threshold_group++){
						
						if (threshold_group==1){
							continue;
						}
						int replication_partkappa = 1;
						if (threshold_group==1){
							replication_partkappa = 10;
						}
						for (int replication_partkappa_i = 0;replication_partkappa_i<replication_partkappa;replication_partkappa_i++){
							//准备数据
							String fname = tempfoler + "/t" + threshold_group + "." + pnumber2 + "." + 
								Math.round(pnumber*10) + "." + taskid + "." + niche_type + ".txt";
							CommonFun.rmfile(fname);
							File tempF = new File(fname);
							double minThreshold = Double.MAX_VALUE;
							FileWriter fw = new FileWriter(tempF.getAbsolutePath(), false);
				            fw.write("\"plotID\"\t\"Observed\"\tmodel" + Const.LineBreak);
							System.out.println(pnumber2 + " : " + taskid + " niche_type: " + niche_type + " " + taskid + " threshold_group:" + threshold_group + " replication_part:" + replication_partkappa_i);
							//vp-big-3-CB-in.0.MA
							HashMap<String, Boolean> xylist = null;
							//根据不同的niche选取不同的图层生成数据
							if (niche_type.equalsIgnoreCase("SELF")){
								xylist = objects.get(vsLabel).getSelf();
							}
							if (niche_type.equalsIgnoreCase("OPPOSITE")){
								xylist = objects.get(vsLabel).getOpposite();
							}
							if (niche_type.equalsIgnoreCase("FULL")){
								xylist = objects.get(vsLabel).getFull();
							}
							
							File f = new File(srs.getString("requestFile").replace("request", "report").replace(".txt", ".tiff"));
							String tiff = f.getName();
							GeoTiffObject geo = new GeoTiffObject("/Volumes/Disk2/NicheTransferabilityResults/" + tiff);
							
							
							double[] geotr = geo.getDataset().GetGeoTransform();
							ArrayList<String> trainXYList = objects.get(vsLabel).getExperiments().get(experimentID);
							double[] thresholds = new double[trainXYList.size()];
							ArrayList<String> absencelist = new ArrayList<String>();
							int i = 0;
							int absenceNumber = 0;
							int trainSize = 0;
							int presentNumber = 0;
							for (String xy : background.keySet()){
								if (!background.get(xy)){
									continue;
								}
								if (xy.split(",").length==2){
									int x = Integer.valueOf(xy.split(",")[0]);
									int y = Integer.valueOf(xy.split(",")[1]);
									double value = (geo.readByXY(x, y) - ajvalue)/100d;
									if (trainXYList.contains(xy)){
										thresholds[trainSize] = value;
										trainSize++;
										if (value>0){
											minThreshold = Math.min(minThreshold, value);
										}
									}
								}
								if (!xylist.containsKey(xy)){
									continue;
								}
								
								if (xy.split(",").length==2){
									
									int x = Integer.valueOf(xy.split(",")[0]);
									int y = Integer.valueOf(xy.split(",")[1]);
									double value = (geo.readByXY(x, y) - ajvalue)/100d;
									if (value<0){
										continue;
									}
									if (value>1){
										value = 1;
									}
									i++;
									//记录所有的threshold，用于计算minimal training presence和 quartile of training presence的值
									
									double observed = 0d;
									
									if (xylist.get(xy)){
										presentNumber++;
										observed = 1d;
									}else{
										//如果需要平衡presence和absence，当absence数目大于等于presence的时候，就不再写了
										if (threshold_group==1){
											absencelist.add(String.format("\"%d\"\t%d\t%f\t%f", i, i, observed, value));
											continue;
										}
										//如果是threshold_group==2，则记录value>0的所有值作为true negative，如果数目少于xy.size，则用0,0来补充
										if (threshold_group==2){
											if (value>0){
												absencelist.add(String.format("\"%d\"\t%d\t%f\t%f", i, i, observed, value));
											}
											continue;
										}
									}
									
									fw.write(String.format("\"%d\"\t%d\t%f\t%f", i, i, observed, value) + Const.LineBreak);
								}
							}
							absenceNumber = 0;
							if (threshold_group==1){
								while (absenceNumber<=(presentNumber)){
									if (absencelist.size()==0){
										break;
									}
									i++;
									int index = (int) (Math.random() * absencelist.size());
									if (index>=absencelist.size()){
										continue;
									}
									fw.write(absencelist.get(index) + Const.LineBreak);
									absenceNumber++;
									absencelist.remove(index);
								}
							}
							if (threshold_group==2){
								if (absencelist.size()>presentNumber){
									while (absencelist.size()>presentNumber){
										int index = (int) (Math.random() * absencelist.size());
										if (index>=absencelist.size()){
											continue;
										}
										absencelist.remove(index);
									}
								}else{
									while (absencelist.size()<=presentNumber){
										i++;
										absencelist.add(String.format("\"%d\"\t%d\t%f\t%f", i, i, 0f, 0f));
									}
								}
								for (String str : absencelist){
									fw.write(str + Const.LineBreak);
								}
							}
							fw.close();
							int[] threshold_methodss = null;
							if (threshold_group==1){
								threshold_methodss = new int[]{10, 11, 12, 13, 14, 15};
							}
							if (threshold_group==0){
								threshold_methodss = new int[]{0, 1, 2, 3, 4, 5};
							}
							if (threshold_group==2){
								threshold_methodss = new int[]{20, 21, 22, 23, 24, 25};
							}
							try{
								double threshold2 = minThreshold;
								double threshold3 = MathUtility.Quartiles(thresholds, 25);
								double threshold4 = MathUtility.Quartiles(thresholds, 10);
								double threshold5 = MathUtility.Average(thresholds, .10f);
								threshold2 = (CommonFun.equal(threshold2, 0.00001d, 10000))?0.00001d:threshold2;
								threshold3 = (CommonFun.equal(threshold3, 0.00001d, 10000))?0.00001d:threshold3;
								threshold4 = (CommonFun.equal(threshold4, 0.00001d, 10000))?0.00001d:threshold4;
								threshold5 = (CommonFun.equal(threshold5, 0.00001d, 10000))?0.00001d:threshold5;
								double[] thresholdss = new double[]{threshold2 - 0.00001f, threshold3 - 0.00001f, threshold4 - 0.00001f, threshold5 - 0.00001f};
								
								getR(tempF.getAbsolutePath(), thresholdss, tempfoler, pnumber, pnumber2, threshold_methodss);
								handleResult(taskid, niche_type, tempfoler, pnumber, pnumber2, replication_partkappa_i, thresholdss, threshold_methodss);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					
					}
				}//end 
				System.out.println("Finished in " + (System.currentTimeMillis() - end0) + " ms.");
				end0 = System.currentTimeMillis();
//				return;
				}//end if
			
			}//end next task
		}
		
	
	
	/**
	 * @param taskid
	 * @return
	 */
	private int getExperimentID(String taskid) {
		String[] s = taskid.split("\\.");
		return Integer.valueOf(s[5]);
	}
	/**
	 * @param taskid
	 * @return
	 */
	private String getVsLabel(String taskid) {
		String[] s = taskid.split("\\.");
		String r = "";
		for (int i=0;i<5;i++){
			if (i!=4){
				r += s[i] + ".";
			}else{
				r += s[i];
			}
		}
		return r;
	}
	private void getR(String filename, double[] thresholdss,
			String tempfoler, double pnumber, int pnumber2,
			int[] thresholdMethodss) throws IOException {
		ArrayList<String> filenames = new ArrayList<String>();
		for (int i=1;i<thresholdMethodss.length;i++){
			filenames.add(tempfoler + "/threshold_method." + thresholdMethodss[i] + "." + pnumber2 + "." + Math.round(pnumber*10) + ".tmp");
		}
		for (int i=1;i<10;i++){
			filenames.add(tempfoler + "/fixed."+ thresholdMethodss[0] + "." + i + "." + pnumber2 + "." + Math.round(pnumber*10) + ".tmp");
		}
		for (String filenamettt : filenames){
			CommonFun.rmfile(filenamettt);
		}
		String threshold = "";
//		System.out.println(filename);
		ArrayList<String> alg = CommonFun.readFromFile(filename);
		String[] algs = alg.get(0).split("\t"); 
		RCaller caller = new RCaller();
	    caller.setRscriptExecutable("/usr/bin/Rscript");
		RCode code = new RCode();
		code.clear();
		code.addRCode("library(PresenceAbsence)");
		code.addRCode("a<-read.table( file=\"" + filename + "\")");
		code.addRCode("t<-optimal.thresholds(a,opt.methods=\"MaxKappa\")");
		if (threshold.equalsIgnoreCase("")){
			threshold = "c(";
			for (int i=2;i<algs.length;i++){
				threshold += String.format("t[[%d]],", i);
			}
			threshold = threshold.substring(0,threshold.length()-1) + ")";
		}
//		System.out.println(threshold);
//		System.exit(0);
//		threshold = String.valueOf((Float.valueOf(threshold) - 0.00001f));
//		
//		threshold = (CommonFun.equal(Double.valueOf(threshold), 0.00001d, 1000))?"0.00001":threshold;
		code.addRCode("r<-presence.absence.accuracy(a,threshold=" +threshold +" - 0.00001)");
		code.addRCode("write.table(r,sep=\",\", file=\"" + tempfoler + "/threshold_method." + thresholdMethodss[1] + "." + pnumber2 + "." + Math.round(pnumber*10) + ".tmp" + "\")");
		for (int i=1;i<10;i++){
			code.addRCode("r<-presence.absence.accuracy(a,threshold=0." +i +")");
			code.addRCode("write.table(r,sep=\",\", file=\"" + tempfoler + "/fixed."+ thresholdMethodss[0] + "." + i + "." + pnumber2 + "." + Math.round(pnumber*10) + ".tmp" + "\")");
		}
		
		for (int i=2;i<thresholdMethodss.length;i++){
			threshold = String.valueOf(thresholdss[i - 2]);
			if (thresholdss[i - 2]<0){
				threshold = "0";
			}
			code.addRCode("r<-presence.absence.accuracy(a,threshold=" +threshold +")");
			code.addRCode("write.table(r,sep=\",\", file=\"" + tempfoler + "/threshold_method." + thresholdMethodss[i] + "." + pnumber2 + "." + Math.round(pnumber*10) + ".tmp" + "\")");
				
		}
		
		
//		StringBuilder sb = new StringBuilder();
//		System.out.print(code.getCode().toString());
		caller.setRCode(code);
		caller.runOnly();
//		ArrayList<String> value = CommonFun.readFromFile(tempfoler + "/tempresult.tmp");
//		CommonFun.writeFile(sb.toString(), tempfoler + "/" + UUID.randomUUID().toString() + ".log");
//		return value;
		
	}
	private void handleResult(String info, String niche_type, String tempfoler, double pnumber, int pnumber2, 
			int replicationPartkappaI, double[] thresholdss, int[] thresholdMethodss) throws SQLException, IOException {
		Connection con= (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/mmwebdbtest?useUnicode=true&amp;characterEncoding=utf-8","root","mikania");
		
		PreparedStatement stmt = con.prepareStatement("INSERT INTO vp_result (" +
				"vp_size, vp_no, scenario, replication_no, algorithm, " +
				"threshold,PCC,sensitivity,specificity,Kappa,AUC,`PCC.sd`,`sensitivity.sd`,`specificity.sd`,`Kappa.sd`,`AUC.sd`, " +
				"niche_type, threshold_method,taskid,absence_type,environment_group, replication_partkappa) values (" +
				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		
		String[] infosplit = info.split("\\.");
		
		//VS2.BIG.present.4.right.0.GARP_BS
		ArrayList<String> filenames = new ArrayList<String>();
		for (int i=1;i<thresholdMethodss.length;i++){
			filenames.add(tempfoler + "/threshold_method." + thresholdMethodss[i] + "." + pnumber2 + "." + Math.round(pnumber*10) + ".tmp");
		}
		for (int i=1;i<10;i++){
			filenames.add(tempfoler + "/fixed."+ thresholdMethodss[0] + "." + i + "." + pnumber2 + "." + Math.round(pnumber*10) + ".tmp");
		}
		int threshold_method;
		for (String filename : filenames){
			ArrayList<String> c = CommonFun.readFromFile(filename);
			if (c.size()<=1){
				System.out.println("Error 1");
				continue;
			}
			String[] result = c.get(1).split(",");
			if (result.length<13){
				System.out.println("Error 2");
				continue;
			}
			String[] f = new File(filename).getName().split("\\.");
			threshold_method = Integer.valueOf(f[1]);

			
			stmt.setString(1, infosplit[1]);
			stmt.setInt(2, Integer.valueOf(infosplit[0].substring(2, 3)));
			stmt.setString(3, infosplit[3] + "." + infosplit[4]);
			stmt.setInt(4, Integer.valueOf(infosplit[5]));
			stmt.setString(5, infosplit[6]);
			stmt.setFloat(6, getFloatValue(result[2]));
			stmt.setFloat(7, getFloatValue(result[3]));
			stmt.setFloat(8, getFloatValue(result[4]));
			stmt.setFloat(9, getFloatValue(result[5]));
			stmt.setFloat(10, getFloatValue(result[6]));
			stmt.setFloat(11, getFloatValue(result[7]));
			stmt.setFloat(12, getFloatValue(result[8]));
			stmt.setFloat(13, getFloatValue(result[9]));
			stmt.setFloat(14, getFloatValue(result[10]));
			stmt.setFloat(15, getFloatValue(result[11]));
			stmt.setFloat(16, getFloatValue(result[12]));
			stmt.setString(17, niche_type);
			stmt.setInt(18, threshold_method);
			stmt.setString(19, info);
			String absence_type = "bg";
			String environment_group = "pca";
			
			stmt.setString(20, absence_type);
			stmt.setString(21, environment_group);
			stmt.setInt(22, replicationPartkappaI);
			stmt.execute();
			if (niche_type.equals("RN")&&(!info.contains("-CB-in"))&&(!info.contains("-WD-in"))){
				stmt.setString(17, "FN");
				stmt.execute();
				stmt.setString(17, "RNN");
				stmt.execute();
			}
		}
		
		
	}
	public float getFloatValue(String v){
		try{
			return Float.valueOf(v);
		}catch(Exception e){
			return -9999f;
		}
	}
	@Test
	public void deleteTask() throws SQLException{
		Connection con= (Connection) DriverManager.getConnection("jdbc:mysql://159.226.67.79:3306/mmwebdbtest?useUnicode=true&amp;characterEncoding=utf-8","root","mikania");
		
		PreparedStatement stmt = con.prepareStatement("select count(1) a, taskid from vp_result group by taskid");
		ResultSet srs = stmt.executeQuery();
		PreparedStatement stmt_del = con.prepareStatement("DELETE FROM vp_result WHERE taskid=?");
		PreparedStatement stmt_update = con.prepareStatement("UPDATE vp_result_marker set ishandled=0 WHERE taskid=?");
		while (srs.next()){
			int a = srs.getInt("a");
			String taskid = srs.getString("taskid");
			if ((a!=84)&&(a!=28)){
				if ((a==56)&&(taskid.contains(".present.6"))){
					continue;
				}
				stmt_del.setString(1, taskid);
				stmt_del.execute();
				stmt_update.setString(1, taskid);
				stmt_update.execute();
			}
		}
	}
	@Test
	public void calculateOverLap() throws IOException, SQLException{
		pnumber = .0d;
		pnumber2 = 20;
		String tempfoler = "/Volumes/Disk2/temp/kappa/" + pnumber2 + "/" + Math.round(pnumber*10) +"/";
		HashSet<String> background = CommonFun.readFromFile2HashSet("/Users/huijieqiao/Dropbox/Papers/NicheTransferability/Layers/PCA/background/xy.txt");
		
		System.out.println("INIT DATA");
		HashMap<String, double[]> backgroundvalue = new HashMap<String, double[]>(); 
		ArrayList<String> xyv = CommonFun.readFromFile("/Users/huijieqiao/Dropbox/Papers/NicheTransferability/Layers/PCA/background/xy.txt");
		ArrayList<String> values = CommonFun.readFromFile("/Users/huijieqiao/Dropbox/Papers/NicheTransferability/Layers/PCA/background/value.txt");
		for (int i=0; i<xyv.size();i++){
			String value = values.get(i);
			String[] vstrs = value.split(",");
			double[] vs = {Double.valueOf(vstrs[0]).doubleValue(), Double.valueOf(vstrs[1]).doubleValue(), Double.valueOf(vstrs[2]).doubleValue()};
			backgroundvalue.put(xyv.get(i), vs);
		}
		File folder = new File("/Users/huijieqiao/Dropbox/Papers/NicheTransferability/VS");
		HashMap<String, ResultObject> objects = new HashMap<String, ResultObject>();
		for (File f : folder.listFiles()){
			if (f.isDirectory()){
				for (File tiffile : f.listFiles()){
					if (tiffile.getName().endsWith("_right.tiff")||(tiffile.getName().endsWith("_left.tiff"))){
						System.out.println("INIT " + dealVPLabel(tiffile.getAbsolutePath()));
						ResultObject object = new ResultObject();
						object.setFull(f.getAbsolutePath() + "/present.tiff",
								"/Users/huijieqiao/Dropbox/Papers/NicheTransferability/Layers/EnvironmentalLayers/bio5_cut.tiff");
						
						object.setSelf(tiffile.getAbsolutePath());
						if (tiffile.getAbsolutePath().contains("left")){
							object.setOpposite(tiffile.getAbsolutePath().replace("left", "right"));
						}else{
							object.setOpposite(tiffile.getAbsolutePath().replace("right", "left"));
						}
						for (int i=0;i<10;i++){
							object.setExperiments(i, tiffile.getAbsolutePath().replace(".tiff", "_folder") + "/xy" + i + ".txt");
						}
						//VS2.BIG.present.4.right.0.GBM
						objects.put(dealVPLabel(tiffile.getAbsolutePath()), object);
					}
				}
			}
		}
		if (true){
//			return;
		}
		System.out.println("FINISHED INIT");
		Connection con= (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/mmwebdbtest?useUnicode=true&amp;characterEncoding=utf-8","root","mikania");
		PreparedStatement stmt = con.prepareStatement("SELECT  FROM vp_result WHERE ");
		PreparedStatement stmt_check = con.prepareStatement("SELECT * FROM vp_result_marker WHERE taskid=?");
		PreparedStatement stmt_insert_marker = con.prepareStatement("INSERT INTO vp_result_marker (taskid, ishandled) values (?, 1)");
		
		ArrayList<String> niche_types = new ArrayList<String>();
		niche_types.add("SELF");
		niche_types.add("OPPOSITE");
		niche_types.add("FULL");
		
		for (String objectID : objects.keySet()){
			ResultObject object = objects.get(objectID);
			int ajvalue = 0;
			
			
			String taskid = objectID + ".MVECH";
			System.out.println(taskid);
			String vsLabel = getVsLabel(taskid);
			stmt_check.setString(1, taskid);
			ResultSet srs_check = stmt_check.executeQuery();
			
			int isExist = 0;
			
			while (srs_check.next()){
				isExist = srs_check.getInt("ishandled");
				ajvalue = srs_check.getInt("maxFrequencyValue");				
			}
			
			if (isExist==0){
				long end0 = System.currentTimeMillis();
				stmt_insert_marker.setString(1, taskid);
				stmt_insert_marker.execute();
//				if (true){
//					continue;
//				}
				for (String niche_type : niche_types){
					//遍历所有的threshold_method
					for (int threshold_group = 0; threshold_group<3; threshold_group++){
						
						if (threshold_group==1){
							continue;
						}
						int replication_partkappa = 1;
						if (threshold_group==1){
							replication_partkappa = 10;
						}
						for (int replication_partkappa_i = 0;replication_partkappa_i<replication_partkappa;replication_partkappa_i++){
							//准备数据
							String fname = tempfoler + "/t" + threshold_group + "." + pnumber2 + "." + 
								Math.round(pnumber*10) + "." + taskid.replace(".MVECH", ".MVE") + "." + niche_type + ".txt";
							CommonFun.rmfile(fname);
							File tempF = new File(fname);
							double minThreshold = Double.MAX_VALUE;
							FileWriter fw = new FileWriter(tempF.getAbsolutePath(), false);
				            fw.write("\"plotID\"\t\"Observed\"\tmodel" + Const.LineBreak);
							System.out.println(pnumber2 + " : " + taskid.replace(".MVECH", ".MVE") + " niche_type: " + niche_type + " threshold_group:" + threshold_group + " replication_part:" + replication_partkappa_i);
							HashMap<String, Boolean> xylistself = object.getSelf();
							if (xylistself.size()<=4){
								continue;
							}
							MinimumVolumeEllipsoidResult mve_self = getMVE(xylistself, backgroundvalue);
							
							quickhull3d.Point3d[] vertices_self = getVertices(xylistself, backgroundvalue);
							HashMap<String, Boolean> xylist_test = null;
							//根据不同的niche选取不同的图层生成数据
							if (niche_type.equalsIgnoreCase("SELF")){
								xylist_test = objects.get(vsLabel).getSelf();
							}
							if (niche_type.equalsIgnoreCase("OPPOSITE")){
								xylist_test = objects.get(vsLabel).getOpposite();
							}
							if (niche_type.equalsIgnoreCase("FULL")){
								xylist_test = objects.get(vsLabel).getFull();
							}
							if (xylist_test.size()<=4){
								continue;
							}
							MinimumVolumeEllipsoidResult mve_test = getMVE(xylist_test, backgroundvalue);
							quickhull3d.Point3d[] vertices_test = getVertices(xylist_test, backgroundvalue);
							
							ArrayList<String> absencelist = new ArrayList<String>();
							int i = 0;
							int absenceNumber = 0;
							int trainSize = 0;
							//计算mve
							for (String xy : background){
								if (xy.split(",").length==2){
									double[] pcavalues = backgroundvalue.get(xy);
									int value = isInEllipsoid(mve_test.getA(), mve_test.getCenter(), pcavalues[0], pcavalues[1], pcavalues[2])?1:0;
									
									i++;
									
									int observed = 0;
									if (isInEllipsoid(mve_self.getA(), mve_self.getCenter(), pcavalues[0], pcavalues[1], pcavalues[2])){
										observed = 1;
									}else{
										//如果需要平衡presence和absence，当absence数目大于等于presence的时候，就不再写了
										if (threshold_group==1){
											absencelist.add(String.format("\"%d\"\t%d\t%d\t%d", i, i, observed, value));
											continue;
										}
										//如果是threshold_group==2，则记录value>0的所有值作为true negative，如果数目少于xy.size，则用0,0来补充
										if (threshold_group==2){
											if (value>0){
												absencelist.add(String.format("\"%d\"\t%d\t%d\t%d", i, i, observed, value));
											}
											continue;
										}
									}
									
									fw.write(String.format("\"%d\"\t%d\t%d\t%d", i, i, observed, value) + Const.LineBreak);
								}
							}
							absenceNumber = 0;
							if (threshold_group==1){
								
							}
							if (threshold_group==2){
								if (absencelist.size()>xylistself.size()){
									while (absencelist.size()>xylistself.size()){
										int index = (int) (Math.random() * absencelist.size());
										if (index>=absencelist.size()){
											continue;
										}
										absencelist.remove(index);
									}
								}else{
									while (absencelist.size()<=xylistself.size()){
										i++;
										absencelist.add(String.format("\"%d\"\t%d\t%d\t%d", i, i, 0, 0));
									}
								}
								for (String str : absencelist){
									fw.write(str + Const.LineBreak);
								}
							}
							fw.close();
							int[] threshold_methodss = null;
							if (threshold_group==1){
								threshold_methodss = new int[]{10, 11, 12, 13, 14, 15};
							}
							if (threshold_group==0){
								threshold_methodss = new int[]{0, 1, 2, 3, 4, 5};
							}
							if (threshold_group==2){
								threshold_methodss = new int[]{20, 21, 22, 23, 24, 25};
							}
							try{
								double threshold2 = .5d;
								double threshold3 = .5d;
								double threshold4 = .5d;
								double threshold5 = .5d;
								double[] thresholdss = new double[]{threshold2 - 0.00001f, threshold3 - 0.00001f, threshold4 - 0.00001f, threshold5 - 0.00001f};
								
								getR(tempF.getAbsolutePath(), thresholdss, tempfoler, pnumber, pnumber2, threshold_methodss);
								for (int ii=0;ii<10;ii++){
									handleResult(taskid.replace(".MVECH", "") + "." + ii + ".MVE", niche_type, tempfoler, pnumber, pnumber2, replication_partkappa_i, thresholdss, threshold_methodss);
								}
							}catch(Exception e){
								e.printStackTrace();
							}
							
							
							//计算convex hull
							
							fname = tempfoler + "/t" + threshold_group + "." + pnumber2 + "." + 
							Math.round(pnumber*10) + "." + taskid.replace(".MVECH", ".CH") + "." + niche_type + ".txt";
							CommonFun.rmfile(fname);
							tempF = new File(fname);
							minThreshold = Double.MAX_VALUE;
							fw = new FileWriter(tempF.getAbsolutePath(), false);
				            fw.write("\"plotID\"\t\"Observed\"\tmodel" + Const.LineBreak);
							System.out.println(pnumber2 + " : " + taskid.replace(".MVECH", ".CH") + " niche_type: " + niche_type + " threshold_group:" + threshold_group + " replication_part:" + replication_partkappa_i);
							
							
							for (String xy : background){
								if (xy.split(",").length==2){
									double[] pcavalues = backgroundvalue.get(xy);
									int value = inConvexHull(new Point3d(pcavalues[0], pcavalues[1], pcavalues[2]), vertices_test)?1:0;
									
									i++;
									
									int observed = 0;
									if (inConvexHull(new Point3d(pcavalues[0], pcavalues[1], pcavalues[2]), vertices_self)){
										observed = 1;
									}else{
										//如果需要平衡presence和absence，当absence数目大于等于presence的时候，就不再写了
										if (threshold_group==1){
											absencelist.add(String.format("\"%d\"\t%d\t%d\t%d", i, i, observed, value));
											continue;
										}
										//如果是threshold_group==2，则记录value>0的所有值作为true negative，如果数目少于xy.size，则用0,0来补充
										if (threshold_group==2){
											if (value>0){
												absencelist.add(String.format("\"%d\"\t%d\t%d\t%d", i, i, observed, value));
											}
											continue;
										}
									}
									
									fw.write(String.format("\"%d\"\t%d\t%d\t%d", i, i, observed, value) + Const.LineBreak);
								}
							}
							absenceNumber = 0;
							if (threshold_group==1){
								
							}
							if (threshold_group==2){
								if (absencelist.size()>xylistself.size()){
									while (absencelist.size()>xylistself.size()){
										int index = (int) (Math.random() * absencelist.size());
										if (index>=absencelist.size()){
											continue;
										}
										absencelist.remove(index);
									}
								}else{
									while (absencelist.size()<=xylistself.size()){
										i++;
										absencelist.add(String.format("\"%d\"\t%d\t%d\t%d", i, i, 0, 0));
									}
								}
								for (String str : absencelist){
									fw.write(str + Const.LineBreak);
								}
							}
							fw.close();
							threshold_methodss = null;
							if (threshold_group==1){
								threshold_methodss = new int[]{10, 11, 12, 13, 14, 15};
							}
							if (threshold_group==0){
								threshold_methodss = new int[]{0, 1, 2, 3, 4, 5};
							}
							if (threshold_group==2){
								threshold_methodss = new int[]{20, 21, 22, 23, 24, 25};
							}
							try{
								double threshold2 = .5d;
								double threshold3 = .5d;
								double threshold4 = .5d;
								double threshold5 = .5d;
								double[] thresholdss = new double[]{threshold2 - 0.00001f, threshold3 - 0.00001f, threshold4 - 0.00001f, threshold5 - 0.00001f};
								
								getR(tempF.getAbsolutePath(), thresholdss, tempfoler, pnumber, pnumber2, threshold_methodss);
								for (int ii=0;ii<10;ii++){
									handleResult(taskid.replace(".MVECH", "") + "." + ii + ".CH", niche_type, tempfoler, pnumber, pnumber2, replication_partkappa_i, thresholdss, threshold_methodss);
								}
							}catch(Exception e){
								e.printStackTrace();
							}
							
						}
					
					}
				}//end 
				System.out.println("Finished in " + (System.currentTimeMillis() - end0) + " ms.");
				end0 = System.currentTimeMillis();
//				return;
				}//end if
			
			}//end next task
		
		
			
	}
	private quickhull3d.Point3d[] getVertices(HashMap<String, Boolean> xylist, HashMap<String, double[]> background){
		HashSet<double[]> values = getValues(xylist, background);
		quickhull3d.Point3d[] points = new quickhull3d.Point3d[values.size()];
		int ii = 0;
		for (double[] value : values){
			points[ii++] = new quickhull3d.Point3d(value[0], value[1], value[2]);
		}
		QuickHull3D hull = new QuickHull3D();
		hull.build(points);
		quickhull3d.Point3d[] vertices = hull.getVertices();
		return vertices;
	}
	private HashSet<double[]> getValues(HashMap<String, Boolean> xylist, HashMap<String, double[]> background){
		HashSet<double[]> values = new HashSet<double[]>();
		for (String xy : xylist.keySet()){
			if (xylist.get(xy)){
				values.add(background.get(xy));
			}
		}
		return values;
	}
	public MinimumVolumeEllipsoidResult getMVE(HashMap<String, Boolean> xylist, HashMap<String, double[]> background){
		HashSet<double[]> values = getValues(xylist, background);
		quickhull3d.Point3d[] points = new quickhull3d.Point3d[values.size()];
		int ii = 0;
		for (double[] value : values){
			points[ii++] = new quickhull3d.Point3d(value[0], value[1], value[2]);
		}
		QuickHull3D hull = new QuickHull3D();
		hull.build(points);
		quickhull3d.Point3d[] vertices = hull.getVertices();
		double[][] hullvalues = new double[3][vertices.length];
		for (int i=0;i<vertices.length;i++){
			hullvalues[0][i] = vertices[i].x;
			hullvalues[1][i] = vertices[i].y;
			hullvalues[2][i] = vertices[i].z;
		}
		MinimumVolumeEllipsoidResult mve = MinimumVolumeEllipsoid.getMatrix(hullvalues, 3);
//		mve.print();
		return mve;
	}
	private static boolean isin(Point3d point, Point3d[] vertices) {
		for (Point3d p : vertices){
			if (distance(p, point)<0.0001){
				return true;
			}
		}
		return false;
	}
	private static double distance(Point3d p, Point3d point) {
		return Math.sqrt(Math.pow(p.x - point.x, 2) + Math.pow(p.y - point.y, 2) + Math.pow(p.z - point.z, 2));
	}
	private static boolean inConvexHull(Point3d point, Point3d[] vertices){
		int i = 0;
		if (isin(point, vertices)){
			return true;
		}
		quickhull3d.Point3d[] points = new quickhull3d.Point3d[vertices.length + 1];
		
		for (Point3d p : vertices){
			points[i++] = p;
		}
		points[i] = point;
		QuickHull3D hull = new QuickHull3D();
		hull.build(points);
		quickhull3d.Point3d[] rvertices = hull.getVertices();
		return isin(point, rvertices);
	}
	
	private boolean isInEllipsoid(Matrix A, Matrix C, double x, double y, double z){
		double[] tt = new double[3];
		tt[0] = x;
		tt[1] = y;
		tt[2] = z;
		Matrix xM = new Matrix(tt, 3);
//		CommonFun.printMatrix(xM);
//		System.out.println("");
		Matrix tempMatrix = xM.minus(C);
		Matrix distanceMatrix = tempMatrix.transpose();
		distanceMatrix = distanceMatrix.times(A);
		distanceMatrix = distanceMatrix.times(tempMatrix);
		double distance = distanceMatrix.getArray()[0][0];
		
		if (distance<=1){
			return true;
		}else{
			return false;
		}
	}
}
