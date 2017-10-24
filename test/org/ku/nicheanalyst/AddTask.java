/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Jun 29, 2012 2:50:54 PM
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


package org.ku.nicheanalyst;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;

import org.ioz.mmweb.Algorithm;
import org.ioz.mmweb.AlgorithmParameter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;

/**
 * @author Huijie Qiao
 *
 */
public class AddTask {
	@Test
	public void postTask() throws IOException, JSONException{
		HashSet<String> alglist = new HashSet<String>();
//		alglist.add("ANN");
//		alglist.add("GAM");
//		alglist.add("GBM");
//		alglist.add("GLM");
//		alglist.add("MARS");
//		alglist.add("RF");
//		alglist.add("GARP_BS");
//		alglist.add("MAXENT_JAVA");
//		alglist.add("BIOCLIM");
//		alglist.add("ENVDIST");
//		alglist.add("ENVSCORE");
		alglist.add("MA");
		int maxcount = 2500;
		int replication = 10;
		double part = 0.8d;
		ArrayList<Algorithm> algorithms = getAlgorithms();
		String folder = "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/vp";
		ArrayList<File> allLL = getLL(folder);
		for (int i=0;i<replication;i++){
			for (File f :allLL){
				
				ArrayList<String> lllist = CommonFun.readFromFile(f.getAbsolutePath());
				String llLabel = dealVPLabel(f);
				ArrayList<String> llpart = new ArrayList<String>();
				ArrayList<String> llallclone = new ArrayList<String>();
				for (String ll : lllist){
					llallclone.add(ll);
				}
				while (llpart.size()<=((double)lllist.size() * part)&&(llpart.size()<=maxcount)){
					int index = (int) Math.floor((Math.random() * llallclone.size()));
					if (index<llallclone.size()){
						llpart.add(llallclone.get(index));
						llallclone.remove(index);
					}
				}
				StringBuilder positionsb = new StringBuilder();
				for (String ll : llpart){
					positionsb.append(ll + Const.LineBreak);
				}
				for (Algorithm algorithm : algorithms){
					if (!alglist.contains(algorithm.getAlgorithmid())){
						continue;
					}
					String occurrencesGroup = llLabel + "." + i + "." + algorithm.getAlgorithmid();
//					if (!occurrencesGroup.contains("vp-big-1.0.")){
//						continue;
//					}
					ArrayList<String> EnvironmentLayerLists = new ArrayList<String>();
					EnvironmentLayerLists.add("1f62c3cf37e869ae0137ed0833490112");
					EnvironmentLayerLists.add("1f62c3cf37e869ae0137ed083ad80113");
					EnvironmentLayerLists.add("1f62c3cf37e869ae0137ed08428d0114");
					postTask(algorithm, EnvironmentLayerLists, "huijieqiao@gmail.com",
							"1f62c3cf37e869ae0137ed0833490112", positionsb.toString(), occurrencesGroup);
//					return;
				}
			}
		}
	}
	
	
	@Test
	public void repostTask() throws IOException, JSONException{
		HashSet<String> alglist = new HashSet<String>();
//		alglist.add("BIOCLIM");
//		alglist.add("GARP");
//		alglist.add("GARP_BS");
//		alglist.add("ENVSCORE");
//		alglist.add("ENVDIST");
		alglist.add("MAXENT_JAVA");
		
		int maxcount = 2500;
		int replication = 10;
		double part = 0.8d;
		ArrayList<Algorithm> algorithms = getAlgorithms();
		String folder = "/Users/huijieqiao/data/vp";
		ArrayList<File> allLL = getLL(folder);
		for (int i=0;i<replication;i++){
			for (File f :allLL){
				
				ArrayList<String> lllist = CommonFun.readFromFile(f.getAbsolutePath());
				String llLabel = dealVPLabel(f);
				ArrayList<String> llpart = new ArrayList<String>();
				ArrayList<String> llallclone = new ArrayList<String>();
				for (String ll : lllist){
					llallclone.add(ll);
				}
				while (llpart.size()<=((double)lllist.size() * part)&&(llpart.size()<=maxcount)){
					int index = (int) Math.floor((Math.random() * llallclone.size()));
					if (index<llallclone.size()){
						llpart.add(llallclone.get(index));
						llallclone.remove(index);
					}
				}
				StringBuilder positionsb = new StringBuilder();
				for (String ll : llpart){
					positionsb.append(ll + Const.LineBreak);
				}
				for (Algorithm algorithm : algorithms){
					if (!alglist.contains(algorithm.getAlgorithmid())){
						continue;
					}
					String occurrencesGroup = llLabel + "." + i + "." + algorithm.getAlgorithmid() + ".nopca";
//					if (!occurrencesGroup.contains("vp-big-1.0.")){
//						continue;
//					}
					ArrayList<String> EnvironmentLayerLists = new ArrayList<String>();
					EnvironmentLayerLists.add("1f62c3cf37e869ae0137ed08428d0114");
					EnvironmentLayerLists.add("1f62c3cf37e869ae0137ed083ad80113");
					EnvironmentLayerLists.add("1f62c3cf37e869ae0137ed0833490112");
					postTask(algorithm, EnvironmentLayerLists, "huijieqiao@gmail.com",
							"1f62c3cf37e869ae0137ed0833490112", positionsb.toString(), occurrencesGroup);
//					return;
				}
			}
		}
	}
	
	@Test
	public void postTaskforBIOMOD() throws IOException, JSONException{
		int maxcount = 2500;
		int replication = 10;
		double part = 0.8d;
		ArrayList<Algorithm> algorithms = getAlgorithms();
		String folder = "/Users/huijieqiao/data/vp";
		ArrayList<File> allLL = getLL(folder);
		HashSet<String> algs = new HashSet<String>();
		algs.add("GLM");
		algs.add("GAM");
		algs.add("GBM");
		algs.add("ANN_BIOMOD");
		algs.add("CTA");
		algs.add("RF");
		algs.add("SRE");
		for (int i=0;i<replication;i++){
			for (File f :allLL){
				
				
				String llLabel = dealVPLabel(f);
				

				
				for (Algorithm algorithm : algorithms){
					if (algs.contains(algorithm.getAlgorithmid())){
						String occurrencesGroup = llLabel + "." + i + "." + algorithm.getAlgorithmid() + ".ra";
	//					if (!occurrencesGroup.contains("vp-big-1.0.")){
	//						continue;
	//					}
						ArrayList<String> EnvironmentLayerLists = new ArrayList<String>();
						EnvironmentLayerLists.add("1f62c3cf37e869ae0137ed08428d0114");
						EnvironmentLayerLists.add("1f62c3cf37e869ae0137ed083ad80113");
						EnvironmentLayerLists.add("1f62c3cf37e869ae0137ed0833490112");
						postTask(algorithm, EnvironmentLayerLists, "huijieqiao@gmail.com",
								"1f62c3cf37e869ae0137ed0833490112", "", occurrencesGroup);
//						return;
					}
				}
			}
		}
	}
	
	
	private void postTask(Algorithm algorithm, ArrayList<String> EnvironmentLayerLists, String Email, String MaskLayerList, 
			String PositionList, String occurrencesGroup) throws IOException{
		System.out.println("Posting " + occurrencesGroup + " @ " + algorithm.getAlgorithmid());

		// Construct data
	    StringBuilder data = new StringBuilder();
	    data.append(URLEncoder.encode("Email", "UTF-8") + "=" + URLEncoder.encode(Email, "UTF-8"));
	    for (String EnvironmentLayerList : EnvironmentLayerLists){
	    	data.append("&" + URLEncoder.encode("EnvironmentLayerList", "UTF-8") + "=" + URLEncoder.encode(EnvironmentLayerList, "UTF-8"));
	    }
	    for (AlgorithmParameter parameter : algorithm.getParameters()){
	    	data.append("&" + URLEncoder.encode(algorithm.getAlgorithmid() + "_" + parameter.getAlgorithmparameterid(), "UTF-8") + "=" + URLEncoder.encode(parameter.getDefaultvalue(), "UTF-8"));
	    }
	    data.append("&" + URLEncoder.encode("MaskLayerList", "UTF-8") + "=" + URLEncoder.encode(MaskLayerList, "UTF-8"));
	    data.append("&" + URLEncoder.encode("PositionList", "UTF-8") + "=" + URLEncoder.encode(PositionList, "UTF-8"));
	    data.append("&" + URLEncoder.encode("occurrencesGroup", "UTF-8") + "=" + URLEncoder.encode(occurrencesGroup, "UTF-8"));
	    data.append("&" + URLEncoder.encode("algcheckbox_" + algorithm.getAlgorithmid(), "UTF-8") + "=" + URLEncoder.encode(algorithm.getAlgorithmid(), "UTF-8"));
	    data.append("&" + URLEncoder.encode("paracount_" + algorithm.getAlgorithmid(), "UTF-8") + "=" + URLEncoder.encode(String.valueOf(algorithm.getParameters().size()), "UTF-8"));
	    // Send data
	    URL url = new URL("http://159.226.67.79:8180/MMWeb/TaskServlet");
	    URLConnection conn = url.openConnection();
	    conn.setDoOutput(true);
	    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	    wr.write(data.toString());
	    wr.flush();

	    // Get the response
	    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    String line;
	    while ((line = rd.readLine()) != null) {
	        System.out.println(line);
	    }
	    wr.close();
	    rd.close();

	}
	private ArrayList<Algorithm> getAlgorithms() throws IOException, JSONException{
		ArrayList<Algorithm> algorithms = new ArrayList<Algorithm>();
		URL url = new URL("http://159.226.67.79:8180/MMWeb/AlgorithmServlet");
		URLConnection connection = url.openConnection();
		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		while((line = reader.readLine()) != null) {
			builder.append(line);
		}

		JSONArray jsonarray = new JSONArray(builder.toString());
		for (int i=0;i<jsonarray.length();i++){
			Algorithm algorithm = new Algorithm();
			
			JSONObject obj = jsonarray.getJSONObject(i);
			String algorithmid = obj.getString("algorithmid");
			algorithm.setAlgorithmid(algorithmid);
			JSONArray parameters = obj.getJSONArray("parameters");
			System.out.print(algorithmid + ":");
			HashSet algparameters = new HashSet<AlgorithmParameter>();
			for (int j=0;j<parameters.length();j++){
				AlgorithmParameter algparameter = new AlgorithmParameter();
				JSONObject parameter = parameters.getJSONObject(j);
				String algorithmparameterid = parameter.getString("algorithmparameterid");
				String defaultvalue = parameter.getString("defaultvalue");
				algparameter.setAlgorithmparameterid(algorithmparameterid);
				algparameter.setDefaultvalue(defaultvalue);
				System.out.print(algorithmparameterid + "=" + defaultvalue + ";");
				algparameters.add(algparameter);
			}
			algorithm.setParameters(algparameters);
			algorithms.add(algorithm);
			System.out.println();
		}
		return algorithms;
	}
	@Test
	public void getVirtualSpeciesInfo() throws IOException{
		String folder = "/Users/huijieqiao/data/vp";
		ArrayList<File> allLL = getLL(folder);
		for (File f : allLL){
			ArrayList<String> lllist = CommonFun.readFromFile(f.getAbsolutePath());
			System.out.println(dealVPLabel(f) + ":" + lllist.size());
		}
	}
	private String dealVPLabel(File f){
		String a = f.getAbsolutePath().replace("/", "-").replace("-Users-huijieqiao-Dropbox-Papers-VirtualSpecies-", "").replace("ll.txt", "");
		return a.substring(0, a.length()-1);
	}
	/**
	 * @param folder
	 * @return
	 */
	private ArrayList<File> getLL(String folder) {
		ArrayList<File> ll = new ArrayList<File>();
		File fold = new File(folder);
		for (File f : fold.listFiles()){
			if (f.isDirectory()){
				ArrayList<File> lltemp = getLL(f.getAbsolutePath());
				for (File ff : lltemp){
					ll.add(ff);
				}
			}else{
				if (f.getName().equalsIgnoreCase("ll.txt")&&(!f.getAbsolutePath().contains("out"))&&(!f.getAbsolutePath().contains("background"))){
					ll.add(f);
				}
			}
		}
		return ll;
	}
}
