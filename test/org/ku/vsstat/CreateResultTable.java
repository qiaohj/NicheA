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
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.dataset.SpeciesObject;

import com.mysql.jdbc.Connection;

/**
 * @author Huijie Qiao
 *
 */
public class CreateResultTable {
	@Test
	public void createAllTable() throws SQLException, IOException{
		Connection con= (Connection) DriverManager.getConnection("jdbc:mysql://159.226.67.79:3306/mmwebdbtest?useUnicode=true&amp;characterEncoding=utf-8","root","mikania");
		//Table head
//		PreparedStatement stmt_head = con.prepareStatement("select * from (select `vp_size` vs_size,  `vp_no` `vs_no`, `scenario`," +
//				"  `replication_no`," +
//				"  `algorithm`," +
//				"  `threshold`," +
//				"  `niche_type`," +
//				"  0 `threshold_type`," +
//				"  `environment_group`,  `Kappa`," +
//				"  `AUC`," +
//				"  `PCC`," +
//				"  `sensitivity`," +
//				"  `specificity`" +
//				" from vp_result where threshold_method=0 and absence_type='bg'" +
//				" union select `vp_size` vs_size,  `vp_no` `vs_no`, `scenario`," +
//				"  `replication_no`,  `algorithm`,  `threshold`,  `niche_type`," +
//				"  1 `threshold_type`, `environment_group`,  max(`Kappa`)  Kappa," +
//				"  `AUC`,  `PCC`,  `sensitivity`,  `specificity`" +
//				" from vp_result where absence_type='bg' " +
//				"group by `vp_size`,  `vp_no`, `scenario`,  `replication_no`,  `algorithm`," +
//				"  `niche_type`,  `environment_group`  ) " +
//				" t where replication_no<10 order by vs_size, vs_no, scenario, replication_no, algorithm, threshold, niche_type, threshold_type, environment_group");
		PreparedStatement stmt_head = con.prepareStatement("select `vp_size` vs_size,  `vp_no` `vs_no`, `scenario`," +
				"  `replication_no`," +
				"  `algorithm`," +
				"  `threshold`," +
				"  `niche_type`," +
				"  threshold_method `threshold_type`," +
				"  `environment_group`,  `Kappa`," +
				"  `AUC`," +
				"  `PCC`," +
				"  `sensitivity`," +
				"  `specificity`" +
				" from vp_result where threshold_method=2 and absence_type='bg' order by vs_size, vs_no, scenario, replication_no, algorithm, threshold, niche_type, threshold_type, environment_group");
		ResultSet srs = stmt_head.executeQuery();
		
		StringBuilder sb = new StringBuilder();
		sb.append("vs_size,vs_no,scenario,replication_no,algorithm,threshold,niche_type,threshold_type,environment_group,Kappa,AUC,PCC,sensitivity,specificity" + Const.LineBreak);
		
		StringBuilder sb1 = new StringBuilder();
		sb1.append("vs_size,vs_no,scenario,replication_no,algorithm,threshold,niche_type,threshold_type,environment_group,Kappa,AUC,PCC,sensitivity,specificity" + Const.LineBreak);
		StringBuilder sb2 = new StringBuilder();
		sb2.append("vs_size,vs_no,scenario,replication_no,algorithm,threshold,niche_type,threshold_type,environment_group,Kappa,AUC,PCC,sensitivity,specificity" + Const.LineBreak);
		StringBuilder sb3 = new StringBuilder();
		sb3.append("vs_size,vs_no,scenario,replication_no,algorithm,threshold,niche_type,threshold_type,environment_group,Kappa,AUC,PCC,sensitivity,specificity" + Const.LineBreak);
		
		ArrayList<String> algorithmlist1 = new ArrayList<String>();
		algorithmlist1.add("GARP");
		algorithmlist1.add("GARP_BS");
		algorithmlist1.add("MAXENT_JAVA");
		
		ArrayList<String> algorithmlist2 = new ArrayList<String>();
		algorithmlist2.add("BIOCLIM");
		algorithmlist2.add("ENVDIST");
		algorithmlist2.add("ENVSCORE");
		algorithmlist2.add("MA");
		
		ArrayList<String> algorithmlist3 = new ArrayList<String>();
		algorithmlist3.add("ANN_BIOMOD");
		algorithmlist3.add("GAM");
		algorithmlist3.add("GBM");
		algorithmlist3.add("GLM");
		algorithmlist3.add("MARS");
		algorithmlist3.add("RF");
		algorithmlist3.add("SRE");
		
		ArrayList<String> algorithmlist = new ArrayList<String>();
		
		for (String a : algorithmlist1){
			algorithmlist.add(a);
		}
		for (String a : algorithmlist2){
			algorithmlist.add(a);
		}
		for (String a : algorithmlist3){
			algorithmlist.add(a);
		}
		while (srs.next()){
			if (algorithmlist1.contains(srs.getString("algorithm"))){
				sb1.append(getResult(srs));
			}
			if (algorithmlist2.contains(srs.getString("algorithm"))){
				sb2.append(getResult(srs));
			}
			if (algorithmlist3.contains(srs.getString("algorithm"))){
				sb3.append(getResult(srs));
			}
			if (algorithmlist.contains(srs.getString("algorithm"))){
				sb.append(getResult(srs));
			}
		}
		CommonFun.writeFile(sb.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/allkappa.csv");
		CommonFun.writeFile(sb1.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/background.csv");
		CommonFun.writeFile(sb2.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/presence_only.csv");
		CommonFun.writeFile(sb3.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/presence_absence.csv");
	}
	/**
	 * @param srs
	 * @return
	 * @throws SQLException 
	 */
	private String getResult(ResultSet srs) throws SQLException {
		String a = String.format("%s,%d,%s,%d,%s,%f,%s,%d,%s,%f,%f,%f,%f,%f", 
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
				srs.getFloat("specificity")) + Const.LineBreak;
		return a;
	}
	@Test
	public void create() throws SQLException, IOException{
		Connection con= (Connection) DriverManager.getConnection("jdbc:mysql://159.226.67.79:3306/mmwebdbtest?useUnicode=true&amp;characterEncoding=utf-8","root","mikania");
		StringBuilder sb = new StringBuilder();
		//Table head
		PreparedStatement stmt_head = con.prepareStatement("select distinct vp_size,vp_no from vp_result order by vp_size,vp_no");
		ResultSet srs_head = stmt_head.executeQuery();
		String head = ",";
		String head2 = ",";
		String head3 = ",";
		String head4 = ",";
		HashMap<String, Integer> headlist = new HashMap<String, Integer>();
		int index = 0;
		while (srs_head.next()){
			for (int i=0;i<14;i++){
				head += "," + srs_head.getString("vp_size") + " - " + srs_head.getInt("vp_no");
			}
			head2 += ",HD,HD,CB,CB,CB,CB,CB,CB,WD,WD,WD,WD,WD,WD";
			head3 += ",FN/RN/RD,FN/RN/RD,FN,FN,RN,RN,RD,RD,FN,FN,RN,RN,RD,RD";
			head4 += ",PCA,Non-PCA,PCA,Non-PCA,PCA,Non-PCA,PCA,Non-PCA,PCA,Non-PCA,PCA,Non-PCA,PCA,Non-PCA";
			headlist.put(srs_head.getString("vp_size") + " - " + srs_head.getInt("vp_no") + " - HD - FN/RN/RD - pca", index);
			index++;
			headlist.put(srs_head.getString("vp_size") + " - " + srs_head.getInt("vp_no") + " - HD - FN/RN/RD - nopca", index);
			index++;
			headlist.put(srs_head.getString("vp_size") + " - " + srs_head.getInt("vp_no") + " - CB - FN - pca", index);
			index++;
			headlist.put(srs_head.getString("vp_size") + " - " + srs_head.getInt("vp_no") + " - CB - FN - nopca", index);
			index++;
			headlist.put(srs_head.getString("vp_size") + " - " + srs_head.getInt("vp_no") + " - CB - RN - pca", index);
			index++;
			headlist.put(srs_head.getString("vp_size") + " - " + srs_head.getInt("vp_no") + " - CB - RN - nopca", index);
			index++;
			headlist.put(srs_head.getString("vp_size") + " - " + srs_head.getInt("vp_no") + " - CB - RD - pca", index);
			index++;
			headlist.put(srs_head.getString("vp_size") + " - " + srs_head.getInt("vp_no") + " - CB - RD - nopca", index);
			index++;
			headlist.put(srs_head.getString("vp_size") + " - " + srs_head.getInt("vp_no") + " - WD - FN - pca", index);
			index++;
			headlist.put(srs_head.getString("vp_size") + " - " + srs_head.getInt("vp_no") + " - WD - FN - nopca", index);
			index++;
			headlist.put(srs_head.getString("vp_size") + " - " + srs_head.getInt("vp_no") + " - WD - RN - pca", index);
			index++;
			headlist.put(srs_head.getString("vp_size") + " - " + srs_head.getInt("vp_no") + " - WD - RN - nopca", index);
			index++;
			headlist.put(srs_head.getString("vp_size") + " - " + srs_head.getInt("vp_no") + " - WD - RD - pca", index);
			index++;
			headlist.put(srs_head.getString("vp_size") + " - " + srs_head.getInt("vp_no") + " - WD - RD - nopca", index);
			index++;
		}
		sb.append(head + Const.LineBreak);
		sb.append(head2 + Const.LineBreak);
		sb.append(head3 + Const.LineBreak);
		sb.append(head4 + Const.LineBreak);
		srs_head.close();
		stmt_head.close();
		ArrayList<String> algorithmlist = new ArrayList<String>();
		algorithmlist.add("GARP");
		algorithmlist.add("GARP_BS");
		algorithmlist.add("MAXENT_JAVA");
		
		algorithmlist.add("BIOCLIM");
		algorithmlist.add("ENVDIST");
		algorithmlist.add("ENVSCORE");
		algorithmlist.add("MA");
		
		algorithmlist.add("ANN_BIOMOD");
		algorithmlist.add("GAM");
		algorithmlist.add("GBM");
		algorithmlist.add("GLM");
		algorithmlist.add("MARS");
		algorithmlist.add("RF");
		algorithmlist.add("SRE");
		HashMap<String, HashMap<Integer, TreeMap<Integer, Float>>> result = getValue(-1, headlist);
		for (String algorithm : algorithmlist){
			for (int i=0;i<10;i++){
				sb.append(algorithm + ",");
				sb.append(i);
				TreeMap<Integer, Float> resultpart = result.get(algorithm).get(i);
				
				for (int j=0;j<index;j++){
					double kappa = -2d;
					if (resultpart!=null){
						if (resultpart.get(j)!=null){
							kappa = resultpart.get(j).doubleValue();
						}
					}
					if (kappa<-1){
						sb.append(",N/A");
					}else{
						sb.append("," + kappa);
					}
				}
				sb.append(Const.LineBreak);
			}
		}
		CommonFun.writeFile(sb.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/maxkappa.csv");
		
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
		algorithmlist_presence_only.add("ENVDIST");
		algorithmlist_presence_only.add("ENVSCORE");
		algorithmlist_presence_only.add("MA");
		
		ArrayList<String> algorithmlist_precence_absence = new ArrayList<String>();
		algorithmlist_precence_absence.add("ANN_BIOMOD");
		algorithmlist_precence_absence.add("GAM");
		algorithmlist_precence_absence.add("GBM");
		algorithmlist_precence_absence.add("GLM");
		algorithmlist_precence_absence.add("MARS");
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
		//Minimal training presence
		PreparedStatement stmt_head = con.prepareStatement("select `vp_size` vs_size,  `vp_no` `vs_no`, `scenario`," +
				"  `replication_no`," +
				"  `algorithm`," +
				"  `threshold`," +
				"  `niche_type`," +
				"  threshold_method `threshold_type`," +
				"  `environment_group`,  `Kappa`," +
				"  `AUC`," +
				"  `PCC`," +
				"  `sensitivity`," +
				"  `specificity`" +
				" from vp_result where threshold_method=2 and absence_type='bg' order by niche_type, vs_size, vs_no, scenario, replication_no, algorithm, threshold, threshold_type, environment_group");

		//Minimal training presence (quartile)
//		PreparedStatement stmt_head = con.prepareStatement("select `vp_size` vs_size,  `vp_no` `vs_no`, `scenario`," +
//				"  `replication_no`," +
//				"  `algorithm`," +
//				"  `threshold`," +
//				"  `niche_type`," +
//				"  threshold_method `threshold_type`," +
//				"  `environment_group`,  `Kappa`," +
//				"  `AUC`," +
//				"  `PCC`," +
//				"  `sensitivity`," +
//				"  `specificity`" +
//				" from vp_result where threshold_method=3 and absence_type='bg' order by niche_type, vs_size, vs_no, scenario, replication_no, algorithm, threshold, threshold_type, environment_group");
		//Maximal kappa value
//		PreparedStatement stmt_head = con.prepareStatement("select `vp_size` vs_size,  `vp_no` `vs_no`, `scenario`," +
//				"  `replication_no`," +
//				"  `algorithm`," +
//				"  `threshold`," +
//				"  `niche_type`," +
//				"  threshold_method `threshold_type`," +
//				"  `environment_group`,  max(`Kappa`) Kappa," +
//				"  `AUC`," +
//				"  `PCC`," +
//				"  `sensitivity`," +
//				"  `specificity`" +
//				" from vp_result where absence_type='bg' " + 
//				" group by niche_type, vs_size, vs_no, scenario, replication_no, algorithm, environment_group" +
//				" order by niche_type, vs_size, vs_no, scenario, replication_no, algorithm, threshold, threshold_type, environment_group");
//		
	
		ResultSet srs = stmt_head.executeQuery();
		HashMap<String, String> pca_ka = new HashMap<String, String>(); 
		HashMap<String, String> pca_kgo = new HashMap<String, String>(); 
		HashMap<String, String> pca_kgnn = new HashMap<String, String>(); 
		HashMap<String, String> nopca_ka = new HashMap<String, String>(); 
		HashMap<String, String> nopca_kgo = new HashMap<String, String>(); 
		HashMap<String, String> nopca_kgnn = new HashMap<String, String>(); 
		
		while (srs.next()){
			String label = getLabel(srs);
			if (srs.getString("environment_group").equals("pca")){
				if (srs.getString("niche_type").equals("FN")){
					pca_ka.put(label, String.format("%f,%f,%f,%f,%f,%f", 
							srs.getFloat("threshold"),
							srs.getFloat("Kappa"),
							srs.getFloat("AUC"),
							srs.getFloat("PCC"),
							srs.getFloat("sensitivity"),
							srs.getFloat("specificity")));
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
		stmt_head = con.prepareStatement("select distinct `vp_size` vs_size,  `vp_no` `vs_no`, `scenario`," +
				"  `replication_no`," +
				"  `algorithm`" +
				" from vp_result where threshold_method=2 and absence_type='bg' " +
				" order by vs_size, vs_no, scenario, replication_no, algorithm");
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
		pca_background.append("vs_size,vs_no,scenario,replication_no,algorithm,points,volume," +
				"threshold_A,Kappa_A,AUC_A,PCC_A,sensitivity_A,specificity_A," +
				"threshold_Go,Kappa_Go,AUC_Go,PCC_Go,sensitivity_Go,specificity_Go," +
				"threshold_Gnn,Kappa_Gnn,AUC_Gnn,PCC_Gnn,sensitivity_Gnn,specificity_Gnn" + Const.LineBreak);
		pca_presence_ansence.append("vs_size,vs_no,scenario,replication_no,algorithm,points,volume," +
				"threshold_A,Kappa_A,AUC_A,PCC_A,sensitivity_A,specificity_A," +
				"threshold_Go,Kappa_Go,AUC_Go,PCC_Go,sensitivity_Go,specificity_Go," +
				"threshold_Gnn,Kappa_Gnn,AUC_Gnn,PCC_Gnn,sensitivity_Gnn,specificity_Gnn" + Const.LineBreak);
		pca_presence_only.append("vs_size,vs_no,scenario,replication_no,algorithm,points,volume," +
				"threshold_A,Kappa_A,AUC_A,PCC_A,sensitivity_A,specificity_A," +
				"threshold_Go,Kappa_Go,AUC_Go,PCC_Go,sensitivity_Go,specificity_Go," +
				"threshold_Gnn,Kappa_Gnn,AUC_Gnn,PCC_Gnn,sensitivity_Gnn,specificity_Gnn" + Const.LineBreak);
		pca_all.append("vs_size,vs_no,scenario,replication_no,algorithm,points,volume," +
				"threshold_A,Kappa_A,AUC_A,PCC_A,sensitivity_A,specificity_A," +
				"threshold_Go,Kappa_Go,AUC_Go,PCC_Go,sensitivity_Go,specificity_Go," +
				"threshold_Gnn,Kappa_Gnn,AUC_Gnn,PCC_Gnn,sensitivity_Gnn,specificity_Gnn,algorithm_group" + Const.LineBreak);
		nopca_background.append("vs_size,vs_no,scenario,replication_no,algorithm,points,volume," +
				"threshold_A,Kappa_A,AUC_A,PCC_A,sensitivity_A,specificity_A," +
				"threshold_Go,Kappa_Go,AUC_Go,PCC_Go,sensitivity_Go,specificity_Go," +
				"threshold_Gnn,Kappa_Gnn,AUC_Gnn,PCC_Gnn,sensitivity_Gnn,specificity_Gnn" + Const.LineBreak);
//		nopca_presence_ansence.append("vs_size,vs_no,scenario,replication_no,algorithm,points,volume," +
//				"threshold_A,Kappa_A,AUC_A,PCC_A,sensitivity_A,specificity_A," +
//				"threshold_Go,Kappa_Go,AUC_Go,PCC_Go,sensitivity_Go,specificity_Go," +
//				"threshold_Gnn,Kappa_Gnn,AUC_Gnn,PCC_Gnn,sensitivity_Gnn,specificity_Gnn" + Const.LineBreak);
		nopca_presence_only.append("vs_size,vs_no,scenario,replication_no,algorithm,points,volume," +
				"threshold_A,Kappa_A,AUC_A,PCC_A,sensitivity_A,specificity_A," +
				"threshold_Go,Kappa_Go,AUC_Go,PCC_Go,sensitivity_Go,specificity_Go," +
				"threshold_Gnn,Kappa_Gnn,AUC_Gnn,PCC_Gnn,sensitivity_Gnn,specificity_Gnn" + Const.LineBreak);
		nopca_all.append("vs_size,vs_no,scenario,replication_no,algorithm,points,volume," +
				"threshold_A,Kappa_A,AUC_A,PCC_A,sensitivity_A,specificity_A," +
				"threshold_Go,Kappa_Go,AUC_Go,PCC_Go,sensitivity_Go,specificity_Go," +
				"threshold_Gnn,Kappa_Gnn,AUC_Gnn,PCC_Gnn,sensitivity_Gnn,specificity_Gnn,algorithm_group" + Const.LineBreak);
		all.append("vs_size,vs_no,scenario,replication_no,algorithm,points,volume," +
				"threshold_A_pca,Kappa_A_pca,AUC_A_pca,PCC_A_pca,sensitivity_A_pca,specificity_A_pca," +
				"threshold_Go_pca,Kappa_Go_pca,AUC_Go_pca,PCC_Go_pca,sensitivity_Go_pca,specificity_Go_pca," +
				"threshold_Gnn_pca,Kappa_Gnn_pca,AUC_Gnn_pca,PCC_Gnn_pca,sensitivity_Gnn_pca,specificity_Gnn_pca," +
				"threshold_A_nopca,Kappa_A_nopca,AUC_A_nopca,PCC_A_nopca,sensitivity_A_nopca,specificity_A_nopca," +
				"threshold_Go_nopca,Kappa_Go_nopca,AUC_Go_nopca,PCC_Go_nopca,sensitivity_Go_nopca,specificity_Go_nopca," +
				"threshold_Gnn_nopca,Kappa_Gnn_nopca,AUC_Gnn_nopca,PCC_Gnn_nopca,sensitivity_Gnn_nopca,specificity_Gnn_nopca," +
				"algorithm_group" + Const.LineBreak);
		while (srs.next()){
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
				pca_background.append(String.format("%s,%s,%s,%s", baseInfo, AInfo_pca, GoInfo_pca, GnnInfo_pca) + Const.LineBreak);
				nopca_background.append(String.format("%s,%s,%s,%s", baseInfo, AInfo_nopca, GoInfo_nopca, GnnInfo_nopca) + Const.LineBreak);
				pca_all.append(String.format("%s,%s,%s,%s,background", baseInfo, AInfo_pca, GoInfo_pca, GnnInfo_pca) + Const.LineBreak);
				nopca_all.append(String.format("%s,%s,%s,%s,background", baseInfo, AInfo_nopca, GoInfo_nopca, GnnInfo_nopca) + Const.LineBreak);
				all.append(String.format("%s,%s,%s,%s,%s,%s,%s,background", baseInfo, AInfo_pca, GoInfo_pca, GnnInfo_pca,AInfo_nopca, GoInfo_nopca, GnnInfo_nopca) + Const.LineBreak);
			}
			if (algorithmlist_precence_absence.contains(srs.getString("algorithm"))){
				pca_presence_ansence.append(String.format("%s,%s,%s,%s", baseInfo, AInfo_pca, GoInfo_pca, GnnInfo_pca) + Const.LineBreak);
//				nopca_presence_ansence.append(String.format("%s,%s,%s,%s", baseInfo, AInfo_nopca, GoInfo_nopca, GnnInfo_nopca) + Const.LineBreak);
				pca_all.append(String.format("%s,%s,%s,%s,presence_absence", baseInfo, AInfo_pca, GoInfo_pca, GnnInfo_pca) + Const.LineBreak);
//				nopca_all.append(String.format("%s,%s,%s,%s,presence_absence", baseInfo, AInfo_nopca, GoInfo_nopca, GnnInfo_nopca) + Const.LineBreak);
//				all.append(String.format("%s,%s,%s,%s,%s,%s,%s,presence_absence", baseInfo, AInfo_pca, GoInfo_pca, GnnInfo_pca,AInfo_nopca, GoInfo_nopca, GnnInfo_nopca));
			}
			if (algorithmlist_presence_only.contains(srs.getString("algorithm"))){
				pca_presence_only.append(String.format("%s,%s,%s,%s", baseInfo, AInfo_pca, GoInfo_pca, GnnInfo_pca) + Const.LineBreak);
				nopca_presence_only.append(String.format("%s,%s,%s,%s", baseInfo, AInfo_nopca, GoInfo_nopca, GnnInfo_nopca) + Const.LineBreak);
				pca_all.append(String.format("%s,%s,%s,%s,presence_only", baseInfo, AInfo_pca, GoInfo_pca, GnnInfo_pca) + Const.LineBreak);
				nopca_all.append(String.format("%s,%s,%s,%s,presence_only", baseInfo, AInfo_nopca, GoInfo_nopca, GnnInfo_nopca) + Const.LineBreak);
				all.append(String.format("%s,%s,%s,%s,%s,%s,%s,presence_only", baseInfo, AInfo_pca, GoInfo_pca, GnnInfo_pca,AInfo_nopca, GoInfo_nopca, GnnInfo_nopca) + Const.LineBreak);
			}
			
		}
		
//		CommonFun.writeFile(pca_background.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/pca_background_1.csv");
//		CommonFun.writeFile(pca_presence_ansence.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/pca_presence_absence_1.csv");
//		CommonFun.writeFile(pca_presence_only.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/pca_presence_only_1.csv");
//		CommonFun.writeFile(pca_all.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/pca_all_1.csv");
//		CommonFun.writeFile(nopca_background.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/nopca_background_1.csv");
//		CommonFun.writeFile(nopca_presence_only.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/nopca_presence_only_1.csv");
//		CommonFun.writeFile(nopca_all.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/nopca_all_1.csv");
//		CommonFun.writeFile(all.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/all_1.csv");
		
		CommonFun.writeFile(pca_background.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/pca_background_2.csv");
		CommonFun.writeFile(pca_presence_ansence.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/pca_presence_absence_2.csv");
		CommonFun.writeFile(pca_presence_only.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/pca_presence_only_2.csv");
		CommonFun.writeFile(pca_all.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/pca_all_2.csv");
		CommonFun.writeFile(nopca_background.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/nopca_background_2.csv");
		CommonFun.writeFile(nopca_presence_only.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/nopca_presence_only_2.csv");
		CommonFun.writeFile(nopca_all.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/nopca_all_2.csv");
		CommonFun.writeFile(all.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/all_2.csv");
		
//		CommonFun.writeFile(pca_background.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/pca_background_3.csv");
//		CommonFun.writeFile(pca_presence_ansence.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/pca_presence_absence_3.csv");
//		CommonFun.writeFile(pca_presence_only.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/pca_presence_only_3.csv");
//		CommonFun.writeFile(pca_all.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/pca_all_3.csv");
//		CommonFun.writeFile(nopca_background.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/nopca_background_3.csv");
//		CommonFun.writeFile(nopca_presence_only.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/nopca_presence_only_3.csv");
//		CommonFun.writeFile(nopca_all.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/nopca_all_3.csv");
//		CommonFun.writeFile(all.toString(), "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/VSStat/splitted/all_3.csv");

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
			+ srs.getString("algorithm");
	}
	
}
