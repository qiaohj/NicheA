/******************************************************************************
 * Huijie Qiao
 *
 * Project:  MMWeb
 * Purpose:  
 * Created date: Jul 17, 2012 2:22:35 PM
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


package org.ku.vsstat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.dataset.SpeciesObject;

import com.mysql.jdbc.Connection;

/**
 * @author Huijie Qiao
 *
 */
public class CreateResultTableWithAllThreshold {
	
	/**
	 * @param srs
	 * @return
	 * @throws SQLException 
	 */
	private String getResult(ResultSet srs) throws SQLException {
		String a = String.format("%s,%d,%s,%d,%s,%f,%s,%d,%s,%f,%f,%f,%f,%f,%d,%d,%d,%d,%d,%d", 
				srs.getString("vs_size"),
				srs.getInt("vs_no"),
				srs.getString("scenario"),
				srs.getInt("replication_no"),
				srs.getString("algorithm"),
				srs.getFloat("threshold"),
				srs.getString("niche_type"),
				srs.getInt("threshold_type"),
				srs.getString("environment_group"),
				srs.getFloat("Kappa"),
				srs.getFloat("AUC"),
				srs.getFloat("PCC"),
				srs.getFloat("sensitivity"),
				srs.getFloat("specificity"),
				srs.getInt("a1"),
				srs.getInt("a2"),
				srs.getInt("a3"),
				srs.getInt("a4"),
				srs.getInt("a5"),
				srs.getInt("a6"))
				+ Const.LineBreak;
		return a;
	}
	
	private HashMap<String, HashMap<Integer, TreeMap<Integer, Float>>> getValue(float threshold, HashMap<String, Integer> headlist) throws SQLException{
		HashMap<String, HashMap<Integer, TreeMap<Integer, Float>>> record = new HashMap<String, HashMap<Integer,TreeMap<Integer,Float>>>();
		Connection con= (Connection) DriverManager.getConnection("jdbc:mysql://159.226.67.79:3306/mmwebdbtest?useUnicode=true&amp;characterEncoding=utf-8","root","mikania");
		PreparedStatement stmt = null;
		if (threshold<0){
			stmt = con.prepareStatement("SELECT vp_size,vp_no,scenario,replication_no,algorithm,niche_type,environment_group,max(Kappa) as Kappa " +
					"FROM vp_result WHERE absence_type='bg' and algorithm<>? and algorithm<>? and algorithm<>? " +
					"group by vp_size,vp_no,scenario,replication_no,algorithm,niche_type,environment_group");
		}else{
			stmt = con.prepareStatement("SELECT vp_size,vp_no,scenario,replication_no,algorithm,niche_type,environment_group,max(Kappa) as Kappa " +
					"FROM vp_result WHERE absence_type='bg' and algorithm<>? and algorithm<>? and algorithm<>? and threshold=? " +
					"group by vp_size,vp_no,scenario,replication_no,algorithm,niche_type,environment_group");
			stmt.setFloat(4, threshold);
		}
		stmt.setString(1, "CSMBS");
		stmt.setString(2, "CTA");
		stmt.setString(3, "ENFA");
		ResultSet srs = stmt.executeQuery();
		while (srs.next()){
			String algorithm = srs.getString("algorithm");
			String niche_type = (srs.getString("scenario").equals("HD"))?"FN/RN/RD":srs.getString("niche_type");
			String environment_group = srs.getString("environment_group");
			if (niche_type.equals("RN")){
				niche_type = "RD";
			}
			if (niche_type.equals("RNN")){
				niche_type = "RN";
			}
			int index = headlist.get(
					srs.getString("vp_size") + 
					" - " + srs.getInt("vp_no") + 
					" - " + srs.getString("scenario") + 
					" - " + niche_type + 
					" - " + environment_group).intValue();
			
			HashMap<Integer, TreeMap<Integer, Float>> record_part = record.get(algorithm);
			if (record_part==null){
				record_part = new HashMap<Integer, TreeMap<Integer,Float>>();
			}
			TreeMap<Integer, Float> sb_part = record_part.get(srs.getInt("replication_no"));
			if (sb_part==null){
				sb_part = new TreeMap<Integer, Float>();
			}
			sb_part.put(index, srs.getFloat("Kappa"));
			record_part.put(srs.getInt("replication_no"), sb_part);
			record.put(algorithm, record_part);
		}
		srs.close();
		stmt.close();
		con.close();
		return record;
	}
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
				if (f.getName().equalsIgnoreCase("value.txt")&&(!f.getAbsolutePath().contains("out"))&&(!f.getAbsolutePath().contains("background"))){
					ll.add(f);
				}
			}
		}
		return ll;
	}
	private String dealVPLabel(File f){
		String a = f.getAbsolutePath().replace("/", "-").replace("-Users-huijieqiao-data-", "").replace("value.txt", "");
		return a.substring(0, a.length()-1) + ".";
	}
	
	@Test
	public void createTableGoA() throws SQLException, IOException{
		ArrayList<String> algorithmlist_background = new ArrayList<String>();
		algorithmlist_background.add("GARP");
		algorithmlist_background.add("GARP_BS");
		algorithmlist_background.add("MAXENT_JAVA");
		
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
		String folder = "/Users/huijieqiao/data/vp";
		ArrayList<File> allValues = getValues(folder);
		for (File f : allValues){
			System.out.println(dealVPLabel(f));
			SpeciesObject sp = new SpeciesObject(f.getAbsolutePath());
			vslist.put(dealVPLabel(f), sp);
		}
		System.out.println("Finished to Init vs list");
		Connection con= (Connection) DriverManager.getConnection("jdbc:mysql://159.226.67.79:3306/mmwebdbtest?useUnicode=true&amp;characterEncoding=utf-8","root","mikania");
		PreparedStatement stmt_threshold_method = con.prepareStatement("select distinct threshold_method from vp_result where threshold_method<>0 and threshold_method<>10");
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
					"  `specificity`,A1,A2,A3,A4,A5,A6 " +
					" from vp_result where threshold_method=? and absence_type='bg' " +
					" order by niche_type, vs_size, vs_no, scenario, replication_partkappa, " +
					" replication_no, algorithm, threshold, threshold_type, environment_group");
	
			stmt_head.setInt(1, threshold_method);
			ResultSet srs = stmt_head.executeQuery();
			HashMap<String, String> pca_ka = new HashMap<String, String>(); 
			HashMap<String, String> a1_6label = new HashMap<String, String>(); 
			HashMap<String, String> pca_kgo = new HashMap<String, String>(); 
			HashMap<String, String> pca_kgnn = new HashMap<String, String>(); 
			HashMap<String, String> nopca_ka = new HashMap<String, String>(); 
			HashMap<String, String> nopca_kgo = new HashMap<String, String>(); 
			HashMap<String, String> nopca_kgnn = new HashMap<String, String>(); 
			
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
					if (srs.getString("niche_type").equals("FN")){
						pca_ka.put(label, String.format("%f,%f,%f,%f,%f,%f", 
								srs.getFloat("threshold"),
								srs.getFloat("Kappa"),
								srs.getFloat("AUC"),
								srs.getFloat("PCC"),
								srs.getFloat("sensitivity"),
								srs.getFloat("specificity")));
						a1_6label.put(label, a1_6);
					}
					if (srs.getString("niche_type").equals("RN")){
						pca_kgo.put(label, String.format("%f,%f,%f,%f,%f,%f", 
								srs.getFloat("threshold"),
								srs.getFloat("Kappa"),
								srs.getFloat("AUC"),
								srs.getFloat("PCC"),
								srs.getFloat("sensitivity"),
								srs.getFloat("specificity")));
					}
					if (srs.getString("niche_type").equals("RNN")){
						pca_kgnn.put(label, String.format("%f,%f,%f,%f,%f,%f", 
								srs.getFloat("threshold"),
								srs.getFloat("Kappa"),
								srs.getFloat("AUC"),
								srs.getFloat("PCC"),
								srs.getFloat("sensitivity"),
								srs.getFloat("specificity")));
					}
				}
				if (srs.getString("environment_group").equals("nopca")){
					if (srs.getString("niche_type").equals("FN")){
						nopca_ka.put(label, String.format("%f,%f,%f,%f,%f,%f", 
								srs.getFloat("threshold"),
								srs.getFloat("Kappa"),
								srs.getFloat("AUC"),
								srs.getFloat("PCC"),
								srs.getFloat("sensitivity"),
								srs.getFloat("specificity")));
					}
					if (srs.getString("niche_type").equals("RN")){
						nopca_kgo.put(label, String.format("%f,%f,%f,%f,%f,%f", 
								srs.getFloat("threshold"),
								srs.getFloat("Kappa"),
								srs.getFloat("AUC"),
								srs.getFloat("PCC"),
								srs.getFloat("sensitivity"),
								srs.getFloat("specificity")));
					}
					if (srs.getString("niche_type").equals("RNN")){
						nopca_kgnn.put(label, String.format("%f,%f,%f,%f,%f,%f", 
								srs.getFloat("threshold"),
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
					" from vp_result where threshold_method=? and absence_type='bg' " +
					" order by vs_size, vs_no, scenario, replication_no,replication_partkappa, algorithm");
			stmt_head.setInt(1, threshold_method);
			srs = stmt_head.executeQuery();
			StringBuilder pca_background = new StringBuilder();
			StringBuilder pca_presence_ansence = new StringBuilder();
			StringBuilder pca_presence_only = new StringBuilder();
			StringBuilder pca_all = new StringBuilder();
			StringBuilder nopca_background = new StringBuilder();
	//		StringBuilder nopca_presence_ansence = new StringBuilder();
			StringBuilder nopca_presence_only = new StringBuilder();
			StringBuilder nopca_all = new StringBuilder();
			StringBuilder all = new StringBuilder();
			pca_background.append("vs_size,vs_no,scenario,replication_no,replication_partkappa,algorithm,points,volume," +
					"threshold_A,Kappa_A,AUC_A,PCC_A,sensitivity_A,specificity_A," +
					"threshold_Go,Kappa_Go,AUC_Go,PCC_Go,sensitivity_Go,specificity_Go," +
					"threshold_Gnn,Kappa_Gnn,AUC_Gnn,PCC_Gnn,sensitivity_Gnn,specificity_Gnn,A1,A2,A3,A4,A5,A6" + Const.LineBreak);
			pca_presence_ansence.append("vs_size,vs_no,scenario,replication_no,replication_partkappa,algorithm,points,volume," +
					"threshold_A,Kappa_A,AUC_A,PCC_A,sensitivity_A,specificity_A," +
					"threshold_Go,Kappa_Go,AUC_Go,PCC_Go,sensitivity_Go,specificity_Go," +
					"threshold_Gnn,Kappa_Gnn,AUC_Gnn,PCC_Gnn,sensitivity_Gnn,specificity_Gnn,A1,A2,A3,A4,A5,A6" + Const.LineBreak);
			pca_presence_only.append("vs_size,vs_no,scenario,replication_no,replication_partkappa,algorithm,points,volume," +
					"threshold_A,Kappa_A,AUC_A,PCC_A,sensitivity_A,specificity_A," +
					"threshold_Go,Kappa_Go,AUC_Go,PCC_Go,sensitivity_Go,specificity_Go," +
					"threshold_Gnn,Kappa_Gnn,AUC_Gnn,PCC_Gnn,sensitivity_Gnn,specificity_Gnn,A1,A2,A3,A4,A5,A6" + Const.LineBreak);
			pca_all.append("vs_size,vs_no,scenario,replication_no,replication_partkappa,algorithm,points,volume," +
					"threshold_A,Kappa_A,AUC_A,PCC_A,sensitivity_A,specificity_A," +
					"threshold_Go,Kappa_Go,AUC_Go,PCC_Go,sensitivity_Go,specificity_Go," +
					"threshold_Gnn,Kappa_Gnn,AUC_Gnn,PCC_Gnn,sensitivity_Gnn,specificity_Gnn,algorithm_group,A1,A2,A3,A4,A5,A6" + Const.LineBreak);
			nopca_background.append("vs_size,vs_no,scenario,replication_no,replication_partkappa,algorithm,points,volume," +
					"threshold_A,Kappa_A,AUC_A,PCC_A,sensitivity_A,specificity_A," +
					"threshold_Go,Kappa_Go,AUC_Go,PCC_Go,sensitivity_Go,specificity_Go," +
					"threshold_Gnn,Kappa_Gnn,AUC_Gnn,PCC_Gnn,sensitivity_Gnn,specificity_Gnn,A1,A2,A3,A4,A5,A6" + Const.LineBreak);
	//		nopca_presence_ansence.append("vs_size,vs_no,scenario,replication_no,algorithm,points,volume," +
	//				"threshold_A,Kappa_A,AUC_A,PCC_A,sensitivity_A,specificity_A," +
	//				"threshold_Go,Kappa_Go,AUC_Go,PCC_Go,sensitivity_Go,specificity_Go," +
	//				"threshold_Gnn,Kappa_Gnn,AUC_Gnn,PCC_Gnn,sensitivity_Gnn,specificity_Gnn" + Const.LineBreak);
			nopca_presence_only.append("vs_size,vs_no,scenario,replication_no,replication_partkappa,algorithm,points,volume," +
					"threshold_A,Kappa_A,AUC_A,PCC_A,sensitivity_A,specificity_A," +
					"threshold_Go,Kappa_Go,AUC_Go,PCC_Go,sensitivity_Go,specificity_Go," +
					"threshold_Gnn,Kappa_Gnn,AUC_Gnn,PCC_Gnn,sensitivity_Gnn,specificity_Gnn,A1,A2,A3,A4,A5,A6" + Const.LineBreak);
			nopca_all.append("vs_size,vs_no,scenario,replication_no,replication_partkappa,algorithm,points,volume," +
					"threshold_A,Kappa_A,AUC_A,PCC_A,sensitivity_A,specificity_A," +
					"threshold_Go,Kappa_Go,AUC_Go,PCC_Go,sensitivity_Go,specificity_Go," +
					"threshold_Gnn,Kappa_Gnn,AUC_Gnn,PCC_Gnn,sensitivity_Gnn,specificity_Gnn,algorithm_group,A1,A2,A3,A4,A5,A6" + Const.LineBreak);
			all.append("vs_size,vs_no,scenario,replication_no,replication_partkappa,algorithm,points,volume," +
					"threshold_A_pca,Kappa_A_pca,AUC_A_pca,PCC_A_pca,sensitivity_A_pca,specificity_A_pca," +
					"threshold_Go_pca,Kappa_Go_pca,AUC_Go_pca,PCC_Go_pca,sensitivity_Go_pca,specificity_Go_pca," +
					"threshold_Gnn_pca,Kappa_Gnn_pca,AUC_Gnn_pca,PCC_Gnn_pca,sensitivity_Gnn_pca,specificity_Gnn_pca," +
					"threshold_A_nopca,Kappa_A_nopca,AUC_A_nopca,PCC_A_nopca,sensitivity_A_nopca,specificity_A_nopca," +
					"threshold_Go_nopca,Kappa_Go_nopca,AUC_Go_nopca,PCC_Go_nopca,sensitivity_Go_nopca,specificity_Go_nopca," +
					"threshold_Gnn_nopca,Kappa_Gnn_nopca,AUC_Gnn_nopca,PCC_Gnn_nopca,sensitivity_Gnn_nopca,specificity_Gnn_nopca," +
					"algorithm_group,A1,A2,A3,A4,A5,A6" + Const.LineBreak);
			int i = 0;
			while (srs.next()){
				if (Math.round((float)i/100f)==(float)i/100f){
					System.out.println(String.format("%s:%d %d/%d", "Threshold Method", threshold_method, i, 30000));
				}
				i++;
				String label = getLabel(srs);
				SpeciesObject vp = getSpecies(srs, vslist);
				String baseInfo = String.format("%s,%d,%f", label.replace("-", ","), vp.getAllRecordCount(), vp.getMve().getVolume(true));
				String AInfo_pca = pca_ka.get(label);
				String GoInfo_pca = pca_kgo.get(label);
				String GnnInfo_pca = pca_kgnn.get(label);
				String AInfo_nopca = nopca_ka.get(label);
				if (srs.getString("scenario").equals("HD")){
					GoInfo_pca = AInfo_pca;
					GnnInfo_pca = AInfo_pca;
				}
				if (AInfo_nopca==null){
					AInfo_nopca = "-9999,-9999,-9999,-9999,-9999,-9999";
					if (srs.getString("algorithm").equals("MA")){
						AInfo_nopca = AInfo_pca;
					}
				}
				String GoInfo_nopca = nopca_kgo.get(label);
				if (GoInfo_nopca==null){
					GoInfo_nopca = "-9999,-9999,-9999,-9999,-9999,-9999";
					if (srs.getString("algorithm").equals("MA")){
						GoInfo_nopca = GoInfo_pca;
					}
				}
				String GnnInfo_nopca = nopca_kgnn.get(label);
				if (GnnInfo_nopca==null){
					GnnInfo_nopca = "-9999,-9999,-9999,-9999,-9999,-9999";
					if (srs.getString("algorithm").equals("MA")){
						GnnInfo_nopca = GnnInfo_pca;
					}
				}
				if (algorithmlist_background.contains(srs.getString("algorithm"))){
					pca_background.append(String.format("%s,%s,%s,%s,%s", baseInfo, AInfo_pca, GoInfo_pca, GnnInfo_pca, a1_6label.get(label)) + Const.LineBreak);
					nopca_background.append(String.format("%s,%s,%s,%s,%s", baseInfo, AInfo_nopca, GoInfo_nopca, GnnInfo_nopca, a1_6label.get(label)) + Const.LineBreak);
					pca_all.append(String.format("%s,%s,%s,%s,background,%s", baseInfo, AInfo_pca, GoInfo_pca, GnnInfo_pca, a1_6label.get(label)) + Const.LineBreak);
					nopca_all.append(String.format("%s,%s,%s,%s,background,%s", baseInfo, AInfo_nopca, GoInfo_nopca, GnnInfo_nopca, a1_6label.get(label)) + Const.LineBreak);
					all.append(String.format("%s,%s,%s,%s,%s,%s,%s,background,%s", baseInfo, AInfo_pca, GoInfo_pca, GnnInfo_pca,AInfo_nopca, GoInfo_nopca, GnnInfo_nopca, a1_6label.get(label)) + Const.LineBreak);
				}
				if (algorithmlist_precence_absence.contains(srs.getString("algorithm"))){
					pca_presence_ansence.append(String.format("%s,%s,%s,%s,%s", baseInfo, AInfo_pca, GoInfo_pca, GnnInfo_pca, a1_6label.get(label)) + Const.LineBreak);
	//				nopca_presence_ansence.append(String.format("%s,%s,%s,%s", baseInfo, AInfo_nopca, GoInfo_nopca, GnnInfo_nopca) + Const.LineBreak);
					pca_all.append(String.format("%s,%s,%s,%s,presence_absence,%s", baseInfo, AInfo_pca, GoInfo_pca, GnnInfo_pca, a1_6label.get(label)) + Const.LineBreak);
	//				nopca_all.append(String.format("%s,%s,%s,%s,presence_absence", baseInfo, AInfo_nopca, GoInfo_nopca, GnnInfo_nopca) + Const.LineBreak);
	//				all.append(String.format("%s,%s,%s,%s,%s,%s,%s,presence_absence", baseInfo, AInfo_pca, GoInfo_pca, GnnInfo_pca,AInfo_nopca, GoInfo_nopca, GnnInfo_nopca));
				}
				if (algorithmlist_presence_only.contains(srs.getString("algorithm"))){
					pca_presence_only.append(String.format("%s,%s,%s,%s,%s", baseInfo, AInfo_pca, GoInfo_pca, GnnInfo_pca, a1_6label.get(label)) + Const.LineBreak);
					nopca_presence_only.append(String.format("%s,%s,%s,%s,%s", baseInfo, AInfo_nopca, GoInfo_nopca, GnnInfo_nopca, a1_6label.get(label)) + Const.LineBreak);
					pca_all.append(String.format("%s,%s,%s,%s,presence_only,%s", baseInfo, AInfo_pca, GoInfo_pca, GnnInfo_pca, a1_6label.get(label)) + Const.LineBreak);
					nopca_all.append(String.format("%s,%s,%s,%s,presence_only,%s", baseInfo, AInfo_nopca, GoInfo_nopca, GnnInfo_nopca, a1_6label.get(label)) + Const.LineBreak);
					all.append(String.format("%s,%s,%s,%s,%s,%s,%s,presence_only,%s", baseInfo, AInfo_pca, GoInfo_pca, GnnInfo_pca,AInfo_nopca, GoInfo_nopca, GnnInfo_nopca, a1_6label.get(label) ) + Const.LineBreak);
				}
				
			}
			
			CommonFun.writeFile(pca_background.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/pca_background_" + threshold_method + ".csv");
			CommonFun.writeFile(pca_presence_ansence.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/pca_presence_absence_" + threshold_method + ".csv");
			CommonFun.writeFile(pca_presence_only.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/pca_presence_only_" + threshold_method + ".csv");
			CommonFun.writeFile(pca_all.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/pca_all_" + threshold_method + ".csv");
			CommonFun.writeFile(nopca_background.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/nopca_background_" + threshold_method + ".csv");
			CommonFun.writeFile(nopca_presence_only.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/nopca_presence_only_" + threshold_method + ".csv");
			CommonFun.writeFile(nopca_all.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/nopca_all_" + threshold_method + ".csv");
			CommonFun.writeFile(all.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/all_" + threshold_method + ".csv");
		}

	}
	/**
	 * @param srs
	 * @param string
	 * @param vslist 
	 * @return
	 * @throws SQLException 
	 */
	private SpeciesObject getSpecies(ResultSet srs, HashMap<String, SpeciesObject> vslist) throws SQLException {
		String label = String.format("vp-%s-%d-%s-in.", srs.getString("vs_size"), srs.getInt("vs_no"), srs.getString("scenario"));
		label = srs.getString("scenario").equals("HD")?label.replace("-HD-in", ""):label;
		for (String key : vslist.keySet()){
			if (key.contains(label)){
				return vslist.get(key);
			}
		}
		System.out.println("Error to get " + label);
		return null;
	}
	/**
	 * @param srs
	 * @return
	 * @throws SQLException 
	 */
	private String getLabel(ResultSet srs) throws SQLException {
		
		return srs.getString("vs_size") + "-" + srs.getInt("vs_no") + "-" 
			+ srs.getString("scenario") + "-" + srs.getString("replication_no") + "-" 
			+ srs.getString("replication_partkappa") + "-"
			+ srs.getString("algorithm");
	}
	
	
	@Test
	public void findErrorTask() throws SQLException, FileNotFoundException, JDOMException, IOException{
		Connection con= (Connection) DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/mmwebdbtest?useUnicode=true&amp;characterEncoding=utf-8","root","mikania");
		PreparedStatement stmt = con.prepareStatement("SELECT * FROM t_task");
		ResultSet srs = stmt.executeQuery();
		while (srs.next()){
			String requestFile = srs.getString("algconfigFile");
//			System.out.println(requestFile);
			SAXBuilder sb = new SAXBuilder();
			Document doc = sb.build(new FileInputStream(requestFile));
			Element rootElement = doc.getRootElement();
			String occurrences_group = rootElement.getChildText("occurrences_group");
			if (!occurrences_group.equals(srs.getString("SpeciesName"))){
				System.out.println("taskid = '" + srs.getString("SpeciesName") + "' or ");
			}
			
		}
	}
	@Test
	public void deleteResult() throws SQLException{
		Connection con= (Connection) DriverManager.getConnection("jdbc:mysql://159.226.67.79:3306/mmwebdbtest?useUnicode=true&amp;characterEncoding=utf-8","root","mikania");
//		PreparedStatement stmt = con.prepareStatement("select SpeciesName from t_task where SpeciesName='vp-big-1-CB-in.0.MA' or SpeciesName='vp-big-1-CB-in.0.MAXENT_JAVA'");
		PreparedStatement stmt = con.prepareStatement("select distinct taskid SpeciesName from vp_result_marker where taskid like '%.MA'");
		
		ResultSet srs = stmt.executeQuery();
		while (srs.next()){
			System.out.println(srs.getString("SpeciesName"));
			PreparedStatement stmt_update = con.prepareStatement("delete from vp_result where taskid=?");
			stmt_update.setString(1, srs.getString("SpeciesName"));
			stmt_update.execute();
			stmt_update = con.prepareStatement("delete from vp_result_marker where taskid=?");
			stmt_update.setString(1, srs.getString("SpeciesName"));
			stmt_update.execute();
			
			stmt_update = con.prepareStatement("update t_task set taskstatus='FF' where SpeciesName=?");
			stmt_update.setString(1, srs.getString("SpeciesName"));
			stmt_update.execute();
		}
	}
	
	@Test
	public void fixMA() throws SQLException{
		Connection con= (Connection) DriverManager.getConnection("jdbc:mysql://159.226.67.79:3306/mmwebdbtest?useUnicode=true&amp;characterEncoding=utf-8","root","mikania");
		PreparedStatement stmt = con.prepareStatement("SELECT * FROM vp_result WHERE algorithm='MA' and Kappa=0 and (threshold_method=2 or threshold_method=12)");
		PreparedStatement stmt_update = con.prepareStatement("UPDATE vp_result set threshold=?, Kappa=?, `Kappa.sd`=?, " +
				"AUC=?, `AUC.sd`=?, PCC=?, `PCC.sd`=?, sensitivity=?, `sensitivity.sd`=?," +
				"specificity=?, `specificity.sd`=?" +
				"WHERE id=?");
		PreparedStatement stmt_search = con.prepareStatement("SELECT * FROM vp_result " +
				"WHERE replication_partkappa=? and taskid=? and threshold_method=? " +
				"and niche_type=?");
		ResultSet srs = stmt.executeQuery();
		int i = 0;
		while (srs.next()){
			System.out.println(++i + "/2827");
			stmt_search.setInt(1, srs.getInt("replication_partkappa"));
			stmt_search.setString(2, srs.getString("taskid"));
			if (srs.getInt("threshold_method")==2){
				stmt_search.setInt(3, 1);
			}else{
				stmt_search.setInt(3, 11);
			}
			stmt_search.setString(4, srs.getString("niche_type"));
			ResultSet srs_search = stmt_search.executeQuery();
			while (srs_search.next()){
				stmt_update.setFloat(1, 0.01f);
				stmt_update.setFloat(2, srs_search.getFloat("Kappa"));
				stmt_update.setFloat(3, srs_search.getFloat("Kappa.sd"));
				stmt_update.setFloat(4, srs_search.getFloat("AUC"));
				stmt_update.setFloat(5, srs_search.getFloat("AUC.sd"));
				stmt_update.setFloat(6, srs_search.getFloat("PCC"));
				stmt_update.setFloat(7, srs_search.getFloat("PCC.sd"));
				stmt_update.setFloat(8, srs_search.getFloat("sensitivity"));
				stmt_update.setFloat(9, srs_search.getFloat("sensitivity.sd"));
				stmt_update.setFloat(10, srs_search.getFloat("specificity"));
				stmt_update.setFloat(11, srs_search.getFloat("specificity.sd"));
				stmt_update.setInt(12, srs.getInt("id"));
				stmt_update.execute();
			}
			srs_search.close();
		}
		
	}
	
}
